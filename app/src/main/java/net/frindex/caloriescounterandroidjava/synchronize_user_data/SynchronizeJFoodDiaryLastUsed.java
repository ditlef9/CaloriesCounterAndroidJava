package net.frindex.caloriescounterandroidjava.synchronize_user_data;

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

public class SynchronizeJFoodDiaryLastUsed {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    int currentUserId = 0;
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;

    public SynchronizeJFoodDiaryLastUsed(Context context) {
        this.context = context;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();

        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_diary_last_used'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_diary_last_used')";
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
                "WHERE name='food_diary_last_used'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeJFoodDiaryLastUsed", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();

        // Get my user id
        getMyUserId();

        // Get number of exercises equipments on server
        askNumberOfLastUsed();
    } // updateLastSynchronizedDate

    private void getMyUserId() {
        DBAdapter db = new DBAdapter(context);
        db.open();

        String q = "SELECT _id, user_id FROM users WHERE _id='1'";
        Cursor userCursor = db.rawQuery(q);
        currentUserId = userCursor.getInt(1);

        db.close();

    }

    public void askNumberOfLastUsed(){

        // Call HTTP request
        String url = apiURL + "/get_last_food_diary_last_used_id.php?user_id=" + currentUserId;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerNumberOfLastUsed();
            }
        });
        task.execute();
    }

    public void answerNumberOfLastUsed(){
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
            String q = "UPDATE synchronize SET last_on_server=" + lastIdOnServerSQL + " WHERE name='food_diary_last_used'";
            db.rawQuery(q);
        }
        catch(NumberFormatException nfe) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeJFoodDiaryLastUsed", "answerNumberOfLastUsed", "error", nfe.getMessage());
        }

        // Close
        db.close();


        /* Start synchronization */
        askFoodDiaryLastUsed();
    } // answerNumberOfLastUsed

    public void askFoodDiaryLastUsed(){

        // Set start and stop
        int limitStart = currentLastIdLocal;
        int limitStop = 10;
        int stop  = limitStart+limitStop;


        // Update synchronize last on local to stop
        DBAdapter db = new DBAdapter(context);
        db.open();
        if(stop < currentLastIdOnServer) {
            String q = "UPDATE synchronize SET last_on_local='" + stop + "' WHERE name='food_diary_last_used'";
            db.rawQuery(q);
        } else {
            String q = "UPDATE synchronize SET last_on_local='" + currentLastIdOnServer + "' WHERE name='food_diary_last_used'";
            db.rawQuery(q);
        }
        db.close();


        if(currentLastIdLocal < currentLastIdOnServer) {

            // Pass new variable to currentLastIdLocal
            currentLastIdLocal = stop;

            // HTTP Request
            String url = apiURL + "/get_many_food_diary_last_used.php?start=" + limitStart +
                    "&stop=" + limitStop + "&user_id=" + currentUserId;
            String stringMethod = "get";
            String stringSend = "";


            HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    answerFoodDiaryLastUsed();
                }
            });
            task.execute();


        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeJFoodDiaryLastUsed", "askFoodDiaryLastUsed", "info", "Finished with askFoodDiaryLastUsed");

            SynchronizeKFoodDiaryEntries synchronizeKFoodDiaryEntries = new SynchronizeKFoodDiaryEntries(context);
            synchronizeKFoodDiaryEntries.updateLastSynchronizedDate();

        }

    } // askFoodDiaryLastUsed

    public void answerFoodDiaryLastUsed(){

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


                    String lastUsedIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_id")));
                    String lastUsedUserIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_user_id")));
                    String lastUsedDayOfWeekSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_day_of_week")));
                    String lastUsedMealIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_meal_id")));
                    String lastUsedFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_food_id")));
                    String lastUsedRecipeIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_recipe_id")));
                    String lastServingSizeSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_serving_size")));
                    String lastUsedTimesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_times")));
                    String lastUsedDateSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_date")));
                    String lastUsedUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_updated")));
                    String lastUsedSynchronizedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_synchronized")));

                    String lastUsedNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_name")));
                    String lastUsedManufacturerSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_manufacturer")));
                    String lastUsedImagePathSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_image_path")));
                    String lastUsedImageThumbSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_image_thumb")));
                    String lastUsedNetContentSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_net_content")));
                    String lastUsedNetContentMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_net_content_measurement")));
                    String lastUsedServingSizeGramSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_serving_size_gram")));
                    String lastUsedServingSizeGramMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_serving_size_gram_measurement")));
                    String lastUsedServingSizePcsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_serving_size_pcs")));
                    String lastUsedServingSizePcsMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_serving_size_pcs_measurement")));
                    String lastUsedCaloriesPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_calories_per_hundred")));
                    String lastUsedFatPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_fat_per_hundred")));
                    String lastUsedSaturatedFattyAcidsPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_saturated_fatty_acids_per_hundred")));
                    String lastUsedCarbsPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_carbs_per_hundred")));
                    String lastUsedSugarPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_sugar_per_hundred")));
                    String lastUsedProteinsPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_proteins_per_hundred")));
                    String lastUsedSaltPerHundredSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_salt_per_hundred")));
                    String lastUsedCaloriesPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_calories_per_serving")));
                    String lastUsedFatPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_fat_per_serving")));
                    String lastUsedSaturatedFattyAcidsPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_saturated_fatty_acids_per_serving")));
                    String lastUsedCarbsPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_carbs_per_serving")));
                    String lastUsedSugarPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_sugar_per_serving")));
                    String lastUsedProteinsPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_proteins_per_serving")));
                    String lastUsedSaltPerServingSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("last_used_salt_per_serving")));



                    // Category
                    String q = "SELECT _id FROM food_diary_last_used WHERE last_used_id=" + lastUsedIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_diary_last_used (_id, last_used_id, last_used_user_id, " +
                                "last_used_day_of_week, last_used_meal_id, last_used_food_id, " +
                                "last_used_recipe_id, last_used_serving_size, last_used_times, " +
                                "last_used_date, last_used_updated, last_used_synchronized, last_used_name," +
                                "last_used_manufacturer, last_used_image_path, last_used_image_thumb," +
                                "last_used_net_content, last_used_net_content_measurement, last_used_serving_size_gram," +
                                "last_used_serving_size_gram_measurement, last_used_serving_size_pcs, last_used_serving_size_pcs_measurement," +
                                "last_used_calories_per_hundred, last_used_fat_per_hundred, last_used_saturated_fatty_acids_per_hundred," +
                                "last_used_carbs_per_hundred, last_used_sugar_per_hundred, last_used_proteins_per_hundred," +
                                "last_used_salt_per_hundred, last_used_calories_per_serving, last_used_fat_per_serving," +
                                "last_used_saturated_fatty_acids_per_serving, last_used_carbs_per_serving, last_used_sugar_per_serving," +
                                "last_used_proteins_per_serving, last_used_salt_per_serving) " +
                                "VALUES (" +
                                "NULL, " +
                                lastUsedIdSQL + ", " +
                                lastUsedUserIdSQL + ", " +
                                lastUsedDayOfWeekSQL + ", " +
                                lastUsedMealIdSQL + ", " +
                                lastUsedFoodIdSQL + ", " +
                                lastUsedRecipeIdSQL + ", " +
                                lastServingSizeSQL + ", " +
                                lastUsedTimesSQL + ", " +
                                lastUsedDateSQL + ", " +
                                lastUsedUpdatedSQL + ", " +
                                lastUsedSynchronizedSQL + ", " +
                                lastUsedNameSQL + ", " +
                                lastUsedManufacturerSQL + ", " +
                                lastUsedImagePathSQL + ", " +
                                lastUsedImageThumbSQL + ", " +
                                lastUsedNetContentSQL + ", " +
                                lastUsedNetContentMeasurementSQL + ", " +
                                lastUsedServingSizeGramSQL + ", " +
                                lastUsedServingSizeGramMeasurementSQL + ", " +
                                lastUsedServingSizePcsSQL + ", " +
                                lastUsedServingSizePcsMeasurementSQL + ", " +
                                lastUsedCaloriesPerHundredSQL + ", " +
                                lastUsedFatPerHundredSQL + ", " +
                                lastUsedSaturatedFattyAcidsPerHundredSQL + ", " +
                                lastUsedCarbsPerHundredSQL + ", " +
                                lastUsedSugarPerHundredSQL + ", " +
                                lastUsedProteinsPerHundredSQL + ", " +
                                lastUsedSaltPerHundredSQL + ", " +
                                lastUsedCaloriesPerServingSQL + ", " +
                                lastUsedFatPerServingSQL + ", " +
                                lastUsedSaturatedFattyAcidsPerServingSQL + ", " +
                                lastUsedCarbsPerServingSQL + ", " +
                                lastUsedSugarPerServingSQL + ", " +
                                lastUsedProteinsPerServingSQL + ", " +
                                lastUsedSaltPerServingSQL +
                                ")";
                        db.rawQuery(q);


                    } else {
                        // Update
                        q = "UPDATE food_diary_last_used SET " +
                                "last_used_id=" + lastUsedIdSQL + ", " +
                                "last_used_user_id=" + lastUsedUserIdSQL + ", " +
                                "last_used_day_of_week=" + lastUsedDayOfWeekSQL + ", " +
                                "last_used_meal_id=" + lastUsedMealIdSQL + ", " +
                                "last_used_food_id=" + lastUsedFoodIdSQL + ", " +
                                "last_used_recipe_id=" + lastUsedRecipeIdSQL + ", " +
                                "last_used_serving_size=" + lastServingSizeSQL + ", " +
                                "last_used_times=" + lastUsedTimesSQL + ", " +
                                "last_used_date=" + lastUsedDateSQL + ", " +
                                "last_used_updated=" + lastUsedUpdatedSQL + ", " +
                                "last_used_synchronized=" + lastUsedSynchronizedSQL + ", " +
                                "last_used_name=" + lastUsedNameSQL + ", " +
                                "last_used_manufacturer=" + lastUsedManufacturerSQL + ", " +
                                "last_used_image_path=" + lastUsedImagePathSQL + ", " +
                                "last_used_image_thumb=" + lastUsedImageThumbSQL + ", " +
                                "last_used_net_content=" + lastUsedNetContentSQL + ", " +
                                "last_used_net_content_measurement=" + lastUsedNetContentMeasurementSQL + ", " +
                                "last_used_serving_size_gram=" + lastUsedServingSizeGramSQL + ", " +
                                "last_used_serving_size_gram_measurement="+ lastUsedServingSizeGramMeasurementSQL + ", " +
                                "last_used_serving_size_pcs=" +lastUsedServingSizePcsSQL + ", " +
                                "last_used_serving_size_pcs_measurement=" + lastUsedServingSizePcsMeasurementSQL + ", " +
                                "last_used_calories_per_hundred=" + lastUsedCaloriesPerHundredSQL + ", " +
                                "last_used_fat_per_hundred=" + lastUsedFatPerHundredSQL + ", " +
                                "last_used_saturated_fatty_acids_per_hundred=" + lastUsedSaturatedFattyAcidsPerHundredSQL + ", " +
                                "last_used_carbs_per_hundred=" + lastUsedCarbsPerHundredSQL + ", " +
                                "last_used_sugar_per_hundred=" + lastUsedSugarPerHundredSQL + ", " +
                                "last_used_proteins_per_hundred=" + lastUsedProteinsPerHundredSQL + ", " +
                                "last_used_salt_per_hundred=" + lastUsedSaltPerHundredSQL + ", " +
                                "last_used_calories_per_serving=" + lastUsedCaloriesPerServingSQL + ", " +
                                "last_used_fat_per_serving=" + lastUsedFatPerServingSQL + ", " +
                                "last_used_saturated_fatty_acids_per_serving=" +lastUsedSaturatedFattyAcidsPerServingSQL + ", " +
                                "last_used_carbs_per_serving=" + lastUsedCarbsPerServingSQL + ", " +
                                "last_used_sugar_per_serving=" + lastUsedSugarPerServingSQL + ", " +
                                "last_used_proteins_per_serving=" + lastUsedProteinsPerServingSQL + ", " +
                                "last_used_salt_per_serving=" + lastUsedSaltPerServingSQL + " " +
                                "WHERE last_used_id=" + lastUsedIdSQL;


                        db.rawQuery(q);

                    } // update



                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeJFoodDiaryLastUsed", "answerFoodDiaryLastUsed", "error",  e.toString());
            } //

            // Database
            db.close();

        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeJFoodDiaryLastUsed", "answerFoodDiaryLastUsed", "error",  "Answer blank string");
        }

        // Ask again
        askFoodDiaryLastUsed();
    } // answerFoodDiaryLastUsed
}
