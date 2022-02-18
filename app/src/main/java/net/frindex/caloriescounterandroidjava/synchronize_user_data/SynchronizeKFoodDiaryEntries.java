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

public class SynchronizeKFoodDiaryEntries {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    int currentUserId = 0;

    public SynchronizeKFoodDiaryEntries(Context context) {
        this.context = context;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();

        // Test
        db.truncate("json_temp_data");

        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_diary_entires'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_diary_entires')";
            db.rawQuery(q);
        }
        else{
            //  currentLastIdLocal = cursorWorkoutPlansWeekly.getInt(2);
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
                "last_on_local='0', " +
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
                "WHERE name='food_diary_entires'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeKFoodDiaryEntries", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();

        // Get my user id
        getMyUserId();

        // Get number of exercises equipments on server
        askTodaysFoodDiaryEntries();
    } // updateLastSynchronizedDate

    private void getMyUserId() {
        DBAdapter db = new DBAdapter(context);
        db.open();

        String q = "SELECT _id, user_id FROM users WHERE _id='1'";
        Cursor userCursor = db.rawQuery(q);
        currentUserId = userCursor.getInt(1);

        db.close();

    }

    public void askTodaysFoodDiaryEntries(){
        // Call HTTP request
        String url = apiURL + "/get_todays_food_diary_entires.php?user_id=" + currentUserId;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerTodaysFoodDiaryEntries();
            }
        });
        task.execute();
    } // askTodaysEntries

    public void answerTodaysFoodDiaryEntries(){

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



                    String entryIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_id")));
                    String entryUserIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_user_id")));
                    String entryDateSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_date")));
                    String entryMealIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_meal_id")));
                    String entryFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_food_id")));
                    String entryRecipeIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_recipe_id")));
                    String entryNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_name")));
                    String entryManufacturerNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_manufacturer_name")));
                    String entryServingSizeSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_serving_size")));
                    String entryServingSizeMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_serving_size_measurement")));
                    String entryEnergyPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_energy_per_entry")));
                    String entryFatPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_fat_per_entry")));
                    String entryCarbPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_carb_per_entry")));
                    String entryProteinPerEntrySQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_protein_per_entry")));
                    String entryTextSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_text")));
                    String entryDeletedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_deleted")));
                    String entryUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_updated")));
                    String entrySynchronizedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("entry_synchronized")));



                    // Category
                    String q = "SELECT _id FROM food_diary_entires WHERE entry_id=" + entryIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_diary_entires (_id, entry_id, entry_user_id, " +
                                "entry_date, entry_meal_id, entry_food_id, " +
                                "entry_recipe_id, entry_name, entry_manufacturer_name, " +
                                "entry_serving_size, entry_serving_size_measurement, entry_energy_per_entry, " +
                                "entry_fat_per_entry, entry_carb_per_entry, entry_protein_per_entry, " +
                                "entry_text, entry_deleted, entry_updated, " +
                                "entry_synchronized) " +
                                "VALUES (" +
                                "NULL, " +
                                entryIdSQL + ", " +
                                entryUserIdSQL + ", " +
                                entryDateSQL + ", " +
                                entryMealIdSQL + ", " +
                                entryFoodIdSQL + ", " +
                                entryRecipeIdSQL + ", " +
                                entryNameSQL + ", " +
                                entryManufacturerNameSQL + ", " +
                                entryServingSizeSQL + ", " +
                                entryServingSizeMeasurementSQL + ", " +
                                entryEnergyPerEntrySQL + ", " +
                                entryFatPerEntrySQL + ", " +
                                entryCarbPerEntrySQL + ", " +
                                entryProteinPerEntrySQL + ", " +
                                entryTextSQL + ", " +
                                entryDeletedSQL + ", " +
                                entryUpdatedSQL + ", " +
                                entrySynchronizedSQL +
                                ")";
                        db.rawQuery(q);


                    } else {
                        // Update
                        q = "UPDATE food_diary_entires SET " +
                                "entry_user_id=" + entryUserIdSQL + ", " +
                                "entry_date=" + entryDateSQL + ", " +
                                "entry_meal_id=" + entryMealIdSQL + ", " +
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
                                "entry_text=" + entryTextSQL + ", " +
                                "entry_deleted=" + entryDeletedSQL + ", " +
                                "entry_updated=" + entryUpdatedSQL + ", " +
                                "entry_synchronized=" + entrySynchronizedSQL + " " +
                                "WHERE entry_id=" + entryIdSQL;


                        db.rawQuery(q);

                    } // update



                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeKFoodDiaryEntries", "answerTodaysFoodDiaryEntries", "error",  e.toString());
            } //

            // Database
            db.close();

        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeKFoodDiaryEntries", "answerTodaysFoodDiaryEntries", "error",  "Answer blank string");
        }


        // Finish
        WriteToErrorLog log = new WriteToErrorLog(context);
        log.writeToErrorLog("SynchronizeKFoodDiaryEntries", "answerTodaysFoodDiaryEntries", "info", "Finished with answerTodaysFoodDiaryEntries");
    } // answerTodaysFoodDiaryEntries
}
