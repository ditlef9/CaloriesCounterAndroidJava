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

public class SynchronizeCFoodMeasurements {

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

    public SynchronizeCFoodMeasurements(Context context, String currentLanguage) {
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
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_measurements'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_measurements')";
            db.rawQuery(q);
        } else {
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
                "WHERE name='food_measurements'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeCFoodMeasurements", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();

        // Get number of exercises equipments on server
        askFoodMeasurements();
    }

    /*- Get last workout plans weekly id from server --------------------------------------- */
    private void askFoodMeasurements() {

        // Call HTTP request
        String url = apiURL + "/get_measurements_from_server.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerFoodMeasurements();
            }
        });
        task.execute();

    } // askFoodMeasurements

    public void answerFoodMeasurements() {

        // Dynamic Text
        DBAdapter db = new DBAdapter(context);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);
        db.close();


        if (!(dataResult.equals("")) && !(dataResult.equals("[]"))) {


            // Database
            db.open();


            try {
                JSONArray array = new JSONArray(dataResult);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);

                    String measurementIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_id")));
                    String measurementNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_name")));
                    String measurementLastUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_last_updated")));

                    String measurementTranslationIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_translation_id")));
                    String measurementTranslationLanguageSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_translation_language")));
                    String measurementTranslationValueSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_translation_value")));
                    String measurementTranslationLastUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("measurement_translation_last_updated")));


                    // Category
                    String q = "SELECT _id FROM food_measurements WHERE measurement_id=" + measurementIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_measurements (_id, measurement_id, measurement_name, " +
                                "measurement_last_updated) " +
                                "VALUES ("
                                + "NULL, "
                                + measurementIdSQL + ", "
                                + measurementNameSQL + ", "
                                + measurementLastUpdatedSQL
                                + ")";
                        db.rawQuery(q);

                    } else {
                        // Update
                        q = "UPDATE food_measurements SET " +
                                "Measurement_name=" + measurementNameSQL + ", " +
                                "measurement_last_updated=" + measurementLastUpdatedSQL + " " +
                                "WHERE measurement_id=" + measurementIdSQL;
                        db.rawQuery(q);

                    } // update


                    // Translation
                    q = "SELECT _id FROM food_measurements_translations WHERE measurement_translation_id=" + measurementTranslationIdSQL;
                    checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_measurements_translations (_id, measurement_translation_id, measurement_id, " +
                                "measurement_translation_language, measurement_translation_value, measurement_translation_last_updated) " +
                                "VALUES (" +
                                "NULL, " +
                                measurementTranslationIdSQL + ", " +
                                measurementIdSQL + ", " +
                                measurementTranslationLanguageSQL + ", " +
                                measurementTranslationValueSQL + ", " +
                                measurementTranslationLastUpdatedSQL +
                                ")";
                        db.rawQuery(q);


                    } else {
                        // Update
                        q = "UPDATE food_measurements_translations SET " +
                                "measurement_id=" + measurementIdSQL + ", " +
                                "measurement_translation_language=" + measurementTranslationLanguageSQL + ", " +
                                "measurement_translation_value=" + measurementTranslationValueSQL+ ", " +
                                "measurement_translation_last_updated=" + measurementTranslationLastUpdatedSQL + " " +
                                "WHERE measurement_translation_id=" + measurementTranslationIdSQL;
                        db.rawQuery(q);


                    } // update


                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeCFoodMeasurements", "answerFoodMeasurements", "error", e.getMessage());
            } //

            // Database
            db.close();

        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeCFoodMeasurements", "answerFoodMeasurements", "error", "answerFoodMeasurements() Answer blank string");
        }




        SynchronizeDPricesCurrencies synchronizeDPricesCurrencies = new SynchronizeDPricesCurrencies(context, currentLanguage);
        synchronizeDPricesCurrencies.updateLastSynchronizedDate();

    } // answerFoodMeasurements
}
