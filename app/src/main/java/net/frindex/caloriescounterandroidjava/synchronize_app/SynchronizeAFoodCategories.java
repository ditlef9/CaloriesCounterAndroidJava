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

public class SynchronizeAFoodCategories {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/food/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    String currentLanguage = "";
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;


    String synchronizationMode = "full"; // full, before login or after login

    public SynchronizeAFoodCategories(Context context, String currentLanguage) {
        this.context = context;
        this.currentLanguage = currentLanguage;
        this.synchronizationMode = synchronizationMode;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();


        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_categories'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_categories')";
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
                "WHERE name='food_categories'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeAFoodCategories", "updateLastSynchronizedDate", "error", e.getMessage());
            // Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        db.close();

        // Get number of exercises equipments on server
        askFoodCategories();
    }

    /*- Get last workout plans weekly id from server --------------------------------------- */
    private void askFoodCategories() {

        // Call HTTP request
        String url = apiURL + "/get_categories_from_server.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerFoodCategories();
            }
        });
        task.execute();

    } // askFoodCategories
    public void answerFoodCategories(){

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







                    String categoryIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_id")));
                    String categoryUserIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_user_id")));
                    String categoryNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_name")));
                    String categoryAgeRestrictionSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_age_restriction")));
                    String categoryParentIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_parent_id")));
                    String categoryIconSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_icon")));
                    String categoryLastUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_last_updated")));
                    String categoryNoteSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_note")));

                    String categoryTranslationIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_id")));
                    String categoryTranslationLanguageSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_language")));
                    String categoryTranslationValueSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_value")));
                    String categoryTranslationNoFoodSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_no_food")));
                    String categoryTranslationlastUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_last_updated")));
                    String categoryCaloriesMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_calories_min")));
                    String categoryCaloriesMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_calories_med")));
                    String categoryCaloriesMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_calories_max")));
                    String categoryFatMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_fat_min")));
                    String categoryFatMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_fat_med")));
                    String categoryFatMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_fat_max")));
                    String categoryFatOfWhichSaturatedFattyAcidsMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_fat_of_which_saturated_fatty_acids_min")));
                    String categoryFatOfWhichSaturatedFattyAcidsMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_fat_of_which_saturated_fatty_acids_med")));
                    String categoryFatOfWhichSaturatedFattyAcidsMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_fat_of_which_saturated_fatty_acids_max")));
                    String categoryCarbMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_carb_min")));
                    String categoryCarbMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_carb_med")));
                    String categoryCarbMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_carb_max")));
                    String categoryCarbOfWhichSugarsMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_carb_of_which_sugars_min")));
                    String categoryCarbOfWhichSugarsMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_carb_of_which_sugars_med")));
                    String categoryCarbOfWhichSugarsMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_carb_of_which_sugars_max")));
                    String categoryProteinsMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_proteins_min")));
                    String categoryProteinsMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_proteins_med")));
                    String categoryProteinsMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_proteins_max")));
                    String categorySaltMinSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_salt_min")));
                    String categorySaltMedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_salt_med")));
                    String categorySaltMaxSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("category_salt_max")));



                    // Category
                    String q = "SELECT _id FROM food_categories WHERE category_id=" + categoryIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_categories (_id, category_id, category_user_id, " +
                                "category_name, category_age_restriction, category_parent_id, " +
                                "category_icon, category_last_updated, category_note) " +
                                "VALUES ("
                                + "NULL, "
                                + categoryIdSQL + ", "
                                + categoryUserIdSQL + ", "
                                + categoryNameSQL + ", "
                                + categoryAgeRestrictionSQL + ", "
                                + categoryParentIdSQL + ", "
                                + categoryIconSQL + ", "
                                + categoryLastUpdatedSQL + ", "
                                + categoryNoteSQL
                                + ")";
                        db.rawQuery(q);

                    } else {
                        // Update
                        q = "UPDATE food_categories SET " +
                                "category_user_id=" + categoryUserIdSQL + ", " +
                                "category_name=" + categoryNameSQL + ", " +
                                "category_age_restriction=" + categoryAgeRestrictionSQL + ", " +
                                "category_parent_id=" + categoryParentIdSQL + ", " +
                                "category_icon=" + categoryIconSQL + ", " +
                                "category_last_updated=" + categoryLastUpdatedSQL + ", " +
                                "category_note=" + categoryNoteSQL + " " +
                                "WHERE category_id=" + categoryIdSQL;
                        db.rawQuery(q);

                    } // update


                    // Translation
                    q = "SELECT _id FROM food_categories_translations WHERE category_translation_id=" + categoryTranslationIdSQL;
                    checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_categories_translations (_id, category_translation_id, category_id, " +
                                "category_translation_language, category_translation_value, category_translation_no_food, " +
                                "category_translation_last_updated, category_calories_min, category_calories_med, " +
                                "category_calories_max, category_fat_min, category_fat_med, " +
                                "category_fat_max, category_fat_of_which_saturated_fatty_acids_min, category_fat_of_which_saturated_fatty_acids_med, " +
                                "category_fat_of_which_saturated_fatty_acids_max, category_carb_min, category_carb_med, " +
                                "category_carb_max, category_carb_of_which_sugars_min, category_carb_of_which_sugars_med, " +
                                "category_carb_of_which_sugars_max, category_proteins_min, category_proteins_med, " +
                                "category_proteins_max, category_salt_min, category_salt_med, " +
                                "category_salt_max) " +
                                "VALUES ("
                                + "NULL, "
                                + categoryTranslationIdSQL + ", "
                                + categoryIdSQL + ", "
                                + categoryTranslationLanguageSQL + ", "
                                + categoryTranslationValueSQL + ", "
                                + categoryTranslationNoFoodSQL + ", "
                                + categoryTranslationlastUpdatedSQL + ", "
                                + categoryCaloriesMinSQL + ", "
                                + categoryCaloriesMedSQL + ", "
                                + categoryCaloriesMaxSQL + ", "
                                + categoryFatMinSQL + ", "
                                + categoryFatMedSQL + ", "
                                + categoryFatMaxSQL + ", "
                                + categoryFatOfWhichSaturatedFattyAcidsMinSQL + ", "
                                + categoryFatOfWhichSaturatedFattyAcidsMedSQL + ", "
                                + categoryFatOfWhichSaturatedFattyAcidsMaxSQL + ", "
                                + categoryCarbMinSQL + ", "
                                + categoryCarbMedSQL + ", "
                                + categoryCarbMaxSQL + ", "
                                + categoryCarbOfWhichSugarsMinSQL + ", "
                                + categoryCarbOfWhichSugarsMedSQL + ", "
                                + categoryCarbOfWhichSugarsMaxSQL + ", "
                                + categoryProteinsMinSQL + ", "
                                + categoryProteinsMedSQL + ", "
                                + categoryProteinsMaxSQL + ", "
                                + categorySaltMinSQL + ", "
                                + categorySaltMedSQL + ", "
                                + categorySaltMaxSQL
                                + ")";
                        db.rawQuery(q);


                    } else {
                        // Update
                        q = "UPDATE food_categories_translations SET " +
                                "category_id=" + categoryIdSQL + ", " +
                                "category_translation_language=" + categoryTranslationLanguageSQL + ", " +
                                "category_translation_value=" + categoryTranslationValueSQL + ", " +
                                "category_translation_no_food=" + categoryTranslationNoFoodSQL + ", " +
                                "category_translation_last_updated=" + categoryTranslationlastUpdatedSQL + ", " +
                                "category_calories_min=" + categoryCaloriesMinSQL + ", " +
                                "category_calories_med=" + categoryCaloriesMedSQL + ", " +
                                "category_calories_max=" + categoryCaloriesMaxSQL + ", " +
                                "category_fat_min=" + categoryFatMinSQL + ", " +
                                "category_fat_med=" + categoryFatMedSQL + ", " +
                                "category_fat_max=" + categoryFatMaxSQL + ", " +
                                "category_fat_of_which_saturated_fatty_acids_min=" + categoryFatOfWhichSaturatedFattyAcidsMinSQL + ", " +
                                "category_fat_of_which_saturated_fatty_acids_med=" + categoryFatOfWhichSaturatedFattyAcidsMedSQL + ", " +
                                "category_fat_of_which_saturated_fatty_acids_max=" + categoryFatOfWhichSaturatedFattyAcidsMaxSQL + ", " +
                                "category_carb_min=" + categoryCarbMinSQL + ", " +
                                "category_carb_med=" + categoryCarbMedSQL + ", " +
                                "category_carb_max=" + categoryCarbMaxSQL+ ", " +
                                "category_carb_of_which_sugars_min=" + categoryCarbOfWhichSugarsMinSQL + ", " +
                                "category_carb_of_which_sugars_med=" + categoryCarbOfWhichSugarsMedSQL + ", " +
                                "category_carb_of_which_sugars_max=" + categoryCarbOfWhichSugarsMaxSQL + ", " +
                                "category_proteins_min=" + categoryProteinsMinSQL + ", " +
                                "category_proteins_med=" + categoryProteinsMedSQL + ", " +
                                "category_proteins_max=" + categoryProteinsMaxSQL + ", " +
                                "category_salt_min=" + categorySaltMinSQL + ", " +
                                "category_salt_med=" + categorySaltMedSQL + ", " +
                                "category_salt_max=" + categorySaltMaxSQL + " " +
                                "WHERE category_translation_id=" + categoryTranslationIdSQL;
                        db.rawQuery(q);


                    } // update


                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeAFoodCategories", "answerFoodCategories", "error", e.getMessage());

            } //

            // Database
            db.close();

        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeAFoodCategories", "answerFoodCategories", "error", "Answer blank string");
        }


        // Synchronize food
        SynchronizeBFoodIndex synchronizeBFoodIndex = new SynchronizeBFoodIndex(context, currentLanguage, synchronizationMode);
        synchronizeBFoodIndex.updateLastSynchronizedDate();


    } // answerFoodCategories





}
