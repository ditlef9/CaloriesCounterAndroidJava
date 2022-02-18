package net.frindex.caloriescounterandroidjava.synchronize_app;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import net.frindex.caloriescounterandroidjava.common.OutputString;
import net.frindex.caloriescounterandroidjava.common.WriteToErrorLog;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SynchronizeHMealPlans {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/meal_plans/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    String currentLanguage = "";
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;

    public SynchronizeHMealPlans(Context context, String currentLanguage) {
        this.context = context;
        this.currentLanguage = currentLanguage;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();



        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='meal_plans'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'meal_plans')";
            db.rawQuery(q);
        }
        else{
            currentLastIdLocal = cursorWorkoutPlansWeekly.getInt(2);
        }

        // Dates
        DateFormat dfYYYY = new SimpleDateFormat("yyyy");//format date
        String dateyyyy = dfYYYY.format(Calendar.getInstance().getTime());

        DateFormat dfMM = new SimpleDateFormat("MM");
        String dateMM = dfMM.format(Calendar.getInstance().getTime());

        DateFormat dfdd = new SimpleDateFormat("dd");
        String datedd = dfdd.format(Calendar.getInstance().getTime());

        DateFormat dfw = new SimpleDateFormat("w");
        String datew = dfw.format(Calendar.getInstance().getTime());

        DateFormat dfH = new SimpleDateFormat("H");
        String dateH = dfH.format(Calendar.getInstance().getTime());

        DateFormat dfmm = new SimpleDateFormat("mm");
        String datemm = dfmm.format(Calendar.getInstance().getTime());

        DateFormat dfss = new SimpleDateFormat("ss");
        String datess = dfss.format(Calendar.getInstance().getTime());

        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String dateyyyyMMdd = dfyyyymmdd.format(Calendar.getInstance().getTime());

        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateyyyyMMdddfhhmmss = dfhhmmss.format(Calendar.getInstance().getTime());

        // Update it
        q = "UPDATE synchronize SET " +
                "last_on_local='" + currentLastIdLocal + "', " +
                "last_on_server='0', " +
                "synchronized_year='" + dateyyyy + "', " +
                "synchronized_month='" + dateMM + "', " +
                "synchronized_day='" + datedd + "', " +
                "synchronized_week='" + datew + "', " +
                "synchronized_hour='" + dateH + "', " +
                "synchronized_minute='" + datemm + "', " +
                "synchronized_second='" + datess + "', " +
                "synchronized_date='" + dateyyyyMMdd + "', " +
                "synchronized_datetime='" + dateyyyyMMdddfhhmmss + "' " +
                "WHERE name='meal_plans'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            // Error
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeHMealPlans", "updateLastSynchronizedDate", "error", e.getMessage());

        }
        db.close();


        // Get number of exercises equipments on server
        askLastMealPlanId();
    }



    /*- 1. askLastFoodIndexIdOnServer ------------------------------------------------------- */
    public void askLastMealPlanId(){


        // Call HTTP request
        String url = apiURL + "/get_last_meal_plan_id_on_server.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerLastMealPlanIdOnServer();
            }
        });
        task.execute();
    } // askLastExerciseIndexOnServer

    public void answerLastMealPlanIdOnServer(){
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();

        // Dynamic Text
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String lastIdOnServer = cursorData.getString(1);
        String lastIdOnServerSQL = db.quoteSmart(lastIdOnServer);


        // Transfer to class variable
        try {
            currentLastIdOnServer = Integer.parseInt(lastIdOnServer);

            // Update OR insert last id on server
            String q = "UPDATE synchronize SET last_on_server=" + lastIdOnServerSQL + " WHERE name='meal_plans'";
            db.rawQuery(q);
        }
        catch(NumberFormatException nfe) {
            // Error
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeHMealPlans", "answerLastMealPlanIdOnServer", "error", nfe.getMessage());
        }

        // Close
        db.close();


        /* Start synchronization */
        askMealPlans();
    } // answerLastFoodIndexIdOnServer



    /*- 2. askMealPlans --------------------------------------------------------- */
    public void askMealPlans(){

        // Set start and stop
        int start = currentLastIdLocal;
        int stop  = start+10;


        // Update synchronize last on local to stop
        DBAdapter db = new DBAdapter(context);
        db.open();
        if(stop < currentLastIdOnServer) {
            String q = "UPDATE synchronize SET last_on_local='" + stop + "' WHERE name='meal_plans'";
            db.rawQuery(q);
        } else {
            String q = "UPDATE synchronize SET last_on_local='" + currentLastIdOnServer + "' WHERE name='meal_plans'";
            db.rawQuery(q);
        }
        db.close();


        if(currentLastIdLocal < currentLastIdOnServer) {

            // Pass new variable to currentLastIdLocal
            currentLastIdLocal = stop;

            // HTTP Request
            String url = apiURL + "/get_many_meal_plans_from_server.php?start=" + start +
                    "&stop=" + stop + "&l=" + currentLanguage;
            String stringMethod = "get";
            String stringSend = "";


            HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    answerMealPlans();
                }
            });
            task.execute();


        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeHMealPlans", "askMealPlans", "info", "Finished with meal plans");

        }
    } // askFoodIndex

    public void answerMealPlans(){

        // Dynamic Text
        DBAdapter db = new DBAdapter(context);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);
        db.close();


        if(!(dataResult.equals("")) && !(dataResult.equals("[]"))) {

            // Database
            db.open();



            try {
                JSONArray array = new JSONArray(dataResult);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);


                    // Meal plan

                    JSONObject mealPlanObject = object.getJSONObject("meal_plan");
                    String mealPlanIdSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_id")));
                    String mealPlanUserIdSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_user_id")));
                    String mealPlanLanguageSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_language")));
                    String mealPlanTitleSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_title")));
                    String mealPlanTitleCleanSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_title_clean")));
                    String mealPlanNumberOfDaysSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_number_of_days")));
                    String mealPlanIntroductionSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_introduction")));
                    String mealPlanTotalEnergyWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_energy_without_training")));
                    String mealPlanTotalFatWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_fat_without_training")));
                    String mealPlanTotalCarbyWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_carb_without_training")));
                    String mealPlanTotalProteinWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_protein_without_training")));

                    String mealPlanTotalEnergyWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_energy_with_training")));
                    String mealPlanTotalFatWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_fat_with_training")));
                    String mealPlanTotalCarbWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_carb_with_training")));
                    String mealPlanTotalProteinWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_total_protein_with_training")));

                    String mealPlanAverageKcalWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_kcal_without_training")));
                    String mealPlanAverageFatWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_fat_without_training")));
                    String mealPlanAverageCarbWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_carb_without_training")));
                    String mealPlanAverageProteinWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_protein_without_training")));

                    String mealPlanAverageKcalWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_kcal_with_training")));
                    String mealPlanAverageFatWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_fat_with_training")));
                    String mealPlanAverageCarbWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_carb_with_training")));
                    String mealPlanAverageProteinWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_average_protein_with_training")));
                    String mealPlanCreatedSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_created")));
                    String mealPlanUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_updated")));
                    String mealPlanUserIpSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_user_ip")));
                    String mealPlanImagePathSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_image_path")));
                    String mealPlanImageThumbSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_image_thumb")));
                    String mealPlanImageFileSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_image_file")));
                    String mealPlanViewsSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_views")));
                    String mealPlanViewsIpBlockSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_views_ip_block")));
                    String mealPlanLikesSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_likes")));
                    String mealPlanDislikesSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_dislikes")));
                    String mealPlanRatingSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_rating")));
                    String mealPlanRatingIpBlockSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_rating_ip_block")));
                    String mealPlanCommentsSQL = db.quoteSmart(new OutputString().outputHTML(mealPlanObject.getString("meal_plan_comments")));


                    String q = "SELECT _id FROM meal_plans WHERE meal_plan_id=" + mealPlanIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {


                        // Insert
                        q = "INSERT INTO meal_plans (_id, meal_plan_id, meal_plan_user_id, " +
                                "meal_plan_language, meal_plan_title, meal_plan_title_clean, " +
                                "meal_plan_number_of_days, meal_plan_introduction, meal_plan_total_energy_without_training, " +
                                "meal_plan_total_fat_without_training, meal_plan_total_carb_without_training, meal_plan_total_protein_without_training," +
                                " meal_plan_total_energy_with_training, meal_plan_total_fat_with_training, meal_plan_total_carb_with_training, " +
                                "meal_plan_total_protein_with_training, meal_plan_average_kcal_without_training, meal_plan_average_fat_without_training, " +
                                "meal_plan_average_carb_without_training, meal_plan_average_protein_without_training, meal_plan_average_kcal_with_training," +
                                " meal_plan_average_fat_with_training, meal_plan_average_carb_with_training, meal_plan_average_protein_with_training, " +
                                "meal_plan_created, meal_plan_updated, meal_plan_user_ip, meal_plan_image_path, meal_plan_image_thumb, " +
                                "meal_plan_image_file, " +
                                "meal_plan_views, meal_plan_views_ip_block, meal_plan_likes, " +
                                "meal_plan_dislikes, meal_plan_rating, meal_plan_rating_ip_block, " +
                                "meal_plan_comments) " +
                                "VALUES (" +
                                "NULL, " + mealPlanIdSQL + ", " + mealPlanUserIdSQL + ", " +
                                mealPlanLanguageSQL + ", " + mealPlanTitleSQL + ", " + mealPlanTitleCleanSQL + ", " +
                                mealPlanNumberOfDaysSQL + ", " + mealPlanIntroductionSQL + ", " + mealPlanTotalEnergyWithoutTrainingSQL + ", " +
                                mealPlanTotalFatWithoutTrainingSQL + ", " + mealPlanTotalCarbyWithoutTrainingSQL + ", " + mealPlanTotalProteinWithoutTrainingSQL + ", " +
                                mealPlanTotalEnergyWithTrainingSQL + ", " + mealPlanTotalFatWithTrainingSQL + ", " + mealPlanTotalCarbWithTrainingSQL + ", " +
                                mealPlanTotalProteinWithTrainingSQL + ", " + mealPlanAverageKcalWithoutTrainingSQL + ", " + mealPlanAverageFatWithoutTrainingSQL + ", " +
                                mealPlanAverageCarbWithoutTrainingSQL + ", " + mealPlanAverageProteinWithoutTrainingSQL + ", " + mealPlanAverageKcalWithTrainingSQL + ", " +
                                mealPlanAverageFatWithTrainingSQL + ", " + mealPlanAverageCarbWithTrainingSQL + ", " + mealPlanAverageProteinWithTrainingSQL + ", " +
                                mealPlanCreatedSQL + ", " + mealPlanUpdatedSQL + ", " + mealPlanUserIpSQL + ", " +
                                mealPlanImagePathSQL + ", " + mealPlanImageThumbSQL + ", " +
                                mealPlanImageFileSQL + ", " + mealPlanViewsSQL + ", " +
                                "''" + ", " + mealPlanLikesSQL + ", "  + mealPlanDislikesSQL + ", " +
                                mealPlanRatingSQL + ", "  + "''" + ", " + mealPlanCommentsSQL
                                + ")";



                        db.rawQuery(q);
                    } else {

                        // Update
                        q = "UPDATE meal_plans SET " +
                                "meal_plan_user_id=" + mealPlanUserIdSQL + ", " +
                                "meal_plan_language=" + mealPlanLanguageSQL + ", " +
                                "meal_plan_title=" + mealPlanTitleSQL + ", " +
                                "meal_plan_title_clean=" + mealPlanTitleCleanSQL + ", " +
                                "meal_plan_number_of_days=" + mealPlanNumberOfDaysSQL + ", " +
                                "meal_plan_introduction=" + mealPlanIntroductionSQL + ", " +
                                "meal_plan_total_energy_without_training=" + mealPlanTotalEnergyWithoutTrainingSQL + ", " +
                                "meal_plan_total_fat_without_training=" + mealPlanTotalFatWithoutTrainingSQL + ", " +
                                "meal_plan_total_carb_without_training=" + mealPlanTotalCarbyWithoutTrainingSQL + ", " +
                                "meal_plan_total_protein_without_training=" + mealPlanTotalProteinWithoutTrainingSQL + ", " +
                                "meal_plan_total_energy_with_training=" + mealPlanTotalEnergyWithTrainingSQL + ", " +
                                "meal_plan_total_fat_with_training=" + mealPlanTotalFatWithTrainingSQL + ", " +
                                "meal_plan_total_carb_with_training=" + mealPlanTotalCarbWithTrainingSQL + ", " +
                                "meal_plan_total_protein_with_training=" + mealPlanTotalProteinWithTrainingSQL + ", " +
                                "meal_plan_average_kcal_without_training=" + mealPlanAverageKcalWithoutTrainingSQL + ", " +
                                "meal_plan_average_fat_without_training=" + mealPlanAverageFatWithoutTrainingSQL + ", " +
                                "meal_plan_average_carb_without_training=" + mealPlanAverageCarbWithoutTrainingSQL + ", " +
                                "meal_plan_average_protein_without_training=" + mealPlanAverageProteinWithoutTrainingSQL + ", " +
                                "meal_plan_average_kcal_with_training=" + mealPlanAverageKcalWithTrainingSQL + ", " +
                                "meal_plan_average_fat_with_training=" + mealPlanAverageFatWithTrainingSQL + ", " +
                                "meal_plan_average_carb_with_training=" + mealPlanAverageCarbWithTrainingSQL + ", " +
                                "meal_plan_average_protein_with_training=" + mealPlanAverageProteinWithTrainingSQL + ", " +
                                "meal_plan_created=" + mealPlanCreatedSQL + ", " +
                                "meal_plan_updated=" + mealPlanUpdatedSQL + ", " +
                                "meal_plan_user_ip=" + mealPlanUserIpSQL + ", " +
                                "meal_plan_image_path=" + mealPlanImagePathSQL + ", " +
                                "meal_plan_image_file=" + mealPlanImageFileSQL + ", " +
                                "meal_plan_views=" + mealPlanViewsSQL + ", " +
                                "meal_plan_likes=" + mealPlanLikesSQL + ", " +
                                "meal_plan_dislikes=" + mealPlanDislikesSQL + ", " +
                                "meal_plan_rating=" + mealPlanRatingSQL + ", " +
                                "meal_plan_comments=" + mealPlanCommentsSQL + " " +
                                "WHERE meal_plan_id=" + mealPlanIdSQL;

                        db.rawQuery(q);
                    }


                    // days
                    try {
                        JSONArray daysArray = object.getJSONArray("days");
                        for (int y = 0; y < daysArray.length(); y++) {
                            JSONObject daysObject = daysArray.getJSONObject(y);


                            String dayIdSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_id")));
                            String dayMealPlanIdSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_meal_plan_id")));
                            String dayNumberSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_number")));
                            String dayEnergyWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_energy_without_training")));
                            String dayFatWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_fat_without_training")));
                            String dayCarbWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_carb_without_training")));
                            String dayProteinWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_protein_without_training")));
                            String daySumWithoutTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_sum_without_training")));
                            String dayFatWithoutTrainingPercentageSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_fat_without_training_percentage")));
                            String dayCarbWithoutTrainingPercentageSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_carb_without_training_percentage")));
                            String dayProteinWithoutTrainingPercentageSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_protein_without_training_percentage")));
                            String dayEnergyWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_energy_with_training")));
                            String dayFatWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_fat_with_training")));
                            String dayCarbWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_carb_with_training")));
                            String dayProteinWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_protein_with_training")));
                            String daySumWithTrainingSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_sum_with_training")));
                            String dayFatWithTrainingPercentageSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_fat_with_training_percentage")));
                            String dayCarbWithTrainingPercentageSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_carb_with_training_percentage")));
                            String dayProteinWithTrainingPercentageSQL = db.quoteSmart(new OutputString().outputHTML(daysObject.getString("day_protein_with_training_percentage")));

                            q = "SELECT _id FROM meal_plans_days WHERE day_id=" + dayIdSQL;
                            checkCursor = db.rawQuery(q);
                            if (checkCursor.getCount() == 0) {


                                // Insert
                                q = "INSERT INTO meal_plans_days (_id, day_id, day_meal_plan_id, " +
                                        "day_number, day_energy_without_training, day_fat_without_training, " +
                                        "day_carb_without_training, day_protein_without_training, day_sum_without_training, " +
                                        "day_fat_without_training_percentage, day_carb_without_training_percentage, day_protein_without_training_percentage, " +
                                        "day_energy_with_training, day_fat_with_training, day_carb_with_training, " +
                                        "day_protein_with_training, day_sum_with_training, day_fat_with_training_percentage, " +
                                        "day_carb_with_training_percentage, day_protein_with_training_percentage) " +
                                        "VALUES (" +
                                        "NULL, " + dayIdSQL + ", " + dayMealPlanIdSQL + ", " +
                                        dayNumberSQL + ", " + dayEnergyWithoutTrainingSQL + ", " + dayFatWithoutTrainingSQL + ", " +
                                        dayCarbWithoutTrainingSQL + ", " + dayProteinWithoutTrainingSQL + ", " + daySumWithoutTrainingSQL + ", " +
                                        dayFatWithoutTrainingPercentageSQL + ", " + dayCarbWithoutTrainingPercentageSQL + ", " + dayProteinWithoutTrainingPercentageSQL + ", " +
                                        dayEnergyWithTrainingSQL + ", " + dayFatWithTrainingSQL + ", " + dayCarbWithTrainingSQL + ", " +
                                        dayProteinWithTrainingSQL + ", " + daySumWithTrainingSQL + ", " + dayFatWithTrainingPercentageSQL + ", " +
                                        dayCarbWithTrainingPercentageSQL + ", " + dayProteinWithTrainingPercentageSQL
                                        + ")";
                                db.rawQuery(q);

                            } else {
                                // Update
                                q = "UPDATE meal_plans_days SET " +
                                        "day_meal_plan_id=" + dayMealPlanIdSQL + ", " +
                                        "day_number=" + dayNumberSQL + ", "  +
                                        "day_energy_without_training=" + dayEnergyWithoutTrainingSQL + ", " +
                                        "day_fat_without_training=" + dayFatWithoutTrainingSQL + ", " +
                                        "day_carb_without_training=" + dayCarbWithoutTrainingSQL + ", " +
                                        "day_protein_without_training=" + dayProteinWithoutTrainingSQL + ", " +
                                        "day_sum_without_training=" + daySumWithoutTrainingSQL + " , " +
                                        "day_fat_without_training_percentage=" + dayFatWithoutTrainingPercentageSQL+ ", " +
                                        "day_carb_without_training_percentage=" + dayCarbWithoutTrainingPercentageSQL + ", " +
                                        "day_protein_without_training_percentage=" + dayProteinWithoutTrainingPercentageSQL + ", " +
                                        "day_energy_with_training=" + dayEnergyWithTrainingSQL + ", " +
                                        "day_fat_with_training=" + dayFatWithTrainingSQL + ", " +
                                        "day_carb_with_training=" + dayCarbWithTrainingSQL + ", " +
                                        "day_protein_with_training=" + dayProteinWithTrainingSQL + ", " +
                                        "day_sum_with_training=" + daySumWithTrainingSQL + ", " +
                                        "day_fat_with_training_percentage=" + dayFatWithTrainingPercentageSQL + ", " +
                                        "day_carb_with_training_percentage=" + dayCarbWithTrainingPercentageSQL + ", " +
                                        "day_protein_with_training_percentage=" + dayProteinWithTrainingPercentageSQL + " " +
                                        "WHERE day_id=" + dayIdSQL;

                                db.rawQuery(q);
                            } // update
                        } // for
                    } catch (JSONException je) {
                        // Server error 500
                        WriteToErrorLog log = new WriteToErrorLog(context);
                        log.writeToErrorLog("SynchronizeHMealPlans", "answerMealPlans meal_plans_days", "error", je.getMessage());
                    } // food_index_prices


                    // entries
                    try {
                        JSONArray entriesArray = object.getJSONArray("entries");
                        for (int y = 0; y < entriesArray.length(); y++) {
                            JSONObject entriesObject = entriesArray.getJSONObject(y);


                            String entryIdSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_id")));
                            String entryMealPlanIdSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_meal_plan_id")));
                            String entryDayNumberSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_day_number")));
                            String entryMealNumberSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_meal_number")));
                            String entryWeightSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_weight")));
                            String entryFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_food_id")));
                            String entryRecipeIdSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_recipe_id")));
                            String entryNameSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_name")));
                            String entryManufacturerNameSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_manufacturer_name")));
                            String entryServingSizeSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_serving_size")));
                            String entryServingSizeMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_serving_size_measurement")));
                            String entryEnergyPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_energy_per_entry")));
                            String entryFatPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_fat_per_entry")));
                            String entryCarbPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_carb_per_entry")));
                            String entryProteinPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_protein_per_entry")));
                            String entryTextSQL = db.quoteSmart(new OutputString().outputHTML(entriesObject.getString("entry_text")));

                            q = "SELECT _id FROM meal_plans_entries WHERE entry_id=" + entryIdSQL;
                            checkCursor = db.rawQuery(q);
                            if (checkCursor.getCount() == 0) {


                                // Insert
                                q = "INSERT INTO meal_plans_entries (_id, entry_id, entry_meal_plan_id, " +
                                        "entry_day_number, entry_meal_number, entry_weight, " +
                                        "entry_food_id, entry_recipe_id, entry_name, " +
                                        "entry_manufacturer_name, entry_serving_size, entry_serving_size_measurement, " +
                                        "entry_energy_per_entry, entry_fat_per_entry, entry_carb_per_entry, " +
                                        "entry_protein_per_entry, entry_text) " +
                                        "VALUES (" +
                                        "NULL, " + entryIdSQL + ", " + entryMealPlanIdSQL + ", " +
                                        entryDayNumberSQL + ", " + entryMealNumberSQL + ", " + entryWeightSQL + ", " +
                                        entryFoodIdSQL + ", " + entryRecipeIdSQL + ", " + entryNameSQL + ", " +
                                        entryManufacturerNameSQL + ", " + entryServingSizeSQL + ", " + entryServingSizeMeasurementSQL + ", " +
                                        entryEnergyPerEntrySQL + ", " + entryFatPerEntrySQL + ", " + entryCarbPerEntrySQL + ", " +
                                        entryProteinPerEntrySQL + ", " + entryTextSQL +
                                        ")";
                                db.rawQuery(q);

                            } else {
                                // Update
                                q = "UPDATE meal_plans_entries SET " +
                                        "entry_meal_plan_id=" + entryMealPlanIdSQL + ", " +
                                        "entry_day_number=" + entryDayNumberSQL + ", " +
                                        "entry_meal_number=" + entryMealNumberSQL + ", " +
                                        "entry_weight=" + entryWeightSQL + ", " +
                                        "entry_food_id=" + entryFoodIdSQL + ", " +
                                        "entry_recipe_id=" + entryRecipeIdSQL + ", " +
                                        "entry_name=" + entryNameSQL + ", " +
                                        "entry_manufacturer_name=" + entryManufacturerNameSQL + ", " +
                                        "entry_serving_size=" + entryServingSizeSQL + ", " +
                                        "entry_serving_size_measurement=" + entryServingSizeMeasurementSQL + ", " +
                                        "entry_energy_per_entry=" + entryEnergyPerEntrySQL + ", " +
                                        "entry_fat_per_entry=" + entryFatPerEntrySQL + ", " +
                                        "entry_carb_per_entry=" + entryCarbPerEntrySQL + ", " +
                                        "entry_protein_per_entry=" + entryProteinPerEntrySQL + ", " +
                                        "entry_text=" + entryTextSQL + " " +
                                        "WHERE entry_id=" + entryIdSQL;
                                db.rawQuery(q);
                            } // update
                        } // for
                    } catch (JSONException je) {
                        // Server error 500
                        WriteToErrorLog log = new WriteToErrorLog(context);
                        log.writeToErrorLog("SynchronizeHMealPlans", "answerMealPlans meal_plans_entries", "error", je.getMessage());
                    } // food_index_stores


                    // index_tags
                    try {
                        JSONArray mealsArray = object.getJSONArray("meals");
                        for (int y = 0; y < mealsArray.length(); y++) {
                            JSONObject mealsObject = mealsArray.getJSONObject(y);

                            String mealIdSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_id")));
                            // String mealPlanIdSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_meal_plan_id")));
                            String mealDayNumberSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_day_number")));
                            String mealNumberSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_number")));
                            String mealEnergyQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_energy")));
                            String mealFatSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_fat")));
                            String mealCarbSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_carb")));
                            String mealProteinSQL = db.quoteSmart(new OutputString().outputHTML(mealsObject.getString("meal_protein")));

                            q = "SELECT _id FROM meal_plans_meals WHERE meal_id=" + mealIdSQL;
                            checkCursor = db.rawQuery(q);
                            if (checkCursor.getCount() == 0) {



                                // Insert
                                q = "INSERT INTO meal_plans_meals (_id, meal_id, meal_meal_plan_id, " +
                                        "meal_day_number, meal_number, meal_energy, " +
                                        "meal_fat, meal_carb, meal_protein) " +
                                        "VALUES (" +
                                        "NULL, " + mealIdSQL + ", " + mealPlanIdSQL  + ", " +
                                        mealDayNumberSQL + ", " + mealNumberSQL + ", " + mealEnergyQL + ", " +
                                        mealFatSQL + ", " + mealCarbSQL + ", " + mealProteinSQL +
                                        ")";
                                db.rawQuery(q);

                            } else {
                                // Update
                                q = "UPDATE meal_plans_meals SET " +
                                        "meal_day_number=" + mealDayNumberSQL + ", " +
                                        "meal_number=" + mealNumberSQL + ", " +
                                        "meal_energy=" + mealEnergyQL + ", " +
                                        "meal_fat=" + mealFatSQL + ", " +
                                        "meal_carb=" + mealCarbSQL + ", " +
                                        "meal_protein=" + mealProteinSQL + " " +
                                        "WHERE meal_meal_plan_id=" + mealIdSQL;
                                db.rawQuery(q);
                            } // update
                        } // for
                    } catch (JSONException je) {
                        // Server error 500
                        WriteToErrorLog log = new WriteToErrorLog(context);
                        log.writeToErrorLog("SynchronizeHMealPlans", "answerMealPlans meal_plans_meals", "error", je.getMessage());
                    } // meal_plans_meals



                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeHMealPlans", "answerMealPlans", "error", e.getMessage());
            } //
        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("answerMealPlans", "answerMealPlans", "error", "Answer blank string");
        }

        // Go to next
        askMealPlans();
    } // answerFoodIndex
}
