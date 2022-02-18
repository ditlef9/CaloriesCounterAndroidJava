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

public class SynchronizeEStores {
    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/food/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    String currentLanguage = "";
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;


    public SynchronizeEStores(Context context, String currentLanguage) {
        this.context = context;
        this.currentLanguage = currentLanguage;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();



        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_stores'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_stores')";
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
                "WHERE name='food_stores'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeEStores", "updateLastSynchronizedDate", "error", e.toString());
        }
        db.close();


        // Get number of exercises equipments on server
        askLastStoreId();
    }



    /*- 1. askLastPricesCurrencyId ------------------------------------------------------- */
    public void askLastStoreId(){


        // Call HTTP request
        String url = apiURL + "/get_last_store_id.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerLastStoreId();
            }
        });
        task.execute();
    } // askLastPricesCurrencyId

    public void answerLastStoreId(){
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
            String q = "UPDATE synchronize SET last_on_server=" + lastIdOnServerSQL + " WHERE name='food_stores'";
            db.rawQuery(q);
        }
        catch(NumberFormatException nfe) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeEStores", "answerLastStoreId", "error", nfe.toString());
        }

        // Close
        db.close();


        /* Start synchronization */
        askStores();
    } // answerLastExerciseIndexOnServer



    /*- 2. askStores --------------------------------------------------------- */
    public void askStores(){

        // Set start and stop
        int start = currentLastIdLocal;
        int stop  = start+10;


        // Update synchronize last on local to stop
        DBAdapter db = new DBAdapter(context);
        db.open();
        if(stop < currentLastIdOnServer) {
            String q = "UPDATE synchronize SET last_on_local='" + stop + "' WHERE name='food_stores'";
            db.rawQuery(q);
        } else {
            String q = "UPDATE synchronize SET last_on_local='" + currentLastIdOnServer + "' WHERE name='food_stores'";
            db.rawQuery(q);
        }
        db.close();


        if(currentLastIdLocal < currentLastIdOnServer) {

            // Pass new variable to currentLastIdLocal
            currentLastIdLocal = stop;

            // HTTP Request
            String url = apiURL + "/get_many_stores_from_server.php?start=" + start +
                    "&stop=" + stop + "&l=" + currentLanguage;
            String stringMethod = "get";
            String stringSend = "";


            HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    answerStores();
                }
            });
            task.execute();


        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeEStores", "answerLastStoreId", "info", "Synchronization finished");

            // Start recipes categories
            SynchronizeFRecipesCategories sync = new SynchronizeFRecipesCategories(context, currentLanguage);
            sync.updateLastSynchronizedDate();

        }
    } // answerPricesCurrencies

    public void answerStores(){

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


                    String storeIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_id")));
                    String storeUserIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_user_id")));
                    String storeNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_name")));
                    String storeCountrySQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_country")));
                    String storeLanguageSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_language")));
                    String storeWebsiteSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_website")));
                    String storeLogoSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_logo")));
                    String storeAddedDatetimeSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_added_datetime")));
                    String storeAddedDatetimePrintSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_added_datetime_print")));
                    String storeUpdatetDatetimeSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_updatet_datetime")));
                    String storeUpdatetDatetimePrintSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_updatet_datetime_print")));
                    String storeReportedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_reported")));
                    String storeReportedCheckedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("store_reported_checked")));

                    String q = "SELECT _id FROM food_stores WHERE store_id=" + storeIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_stores (_id, store_id, store_user_id, " +
                                "store_name, store_country, store_language, " +
                                "store_website, store_logo, store_added_datetime, " +
                                "store_added_datetime_print, store_updatet_datetime, " +
                                "store_updatet_datetime_print, store_reported, store_reported_checked) " +
                                "VALUES ("
                                + "NULL, "
                                + storeIdSQL + ", "
                                + storeUserIdSQL + ", "
                                + storeNameSQL + ", "
                                + storeCountrySQL + ", "
                                + storeLanguageSQL + ", "
                                + storeWebsiteSQL + ", "
                                + storeLogoSQL + ", "
                                + storeAddedDatetimeSQL + ", "
                                + storeAddedDatetimePrintSQL + ", "
                                + storeUpdatetDatetimeSQL + ", "
                                + storeUpdatetDatetimePrintSQL + ", "
                                + storeReportedSQL + ", "
                                + storeReportedCheckedSQL
                                + ")";
                        db.rawQuery(q);
                    } else {
                        // Update
                        q = "UPDATE food_stores SET " +
                                "store_user_id=" + storeUserIdSQL + ", " +
                                "store_name=" + storeNameSQL+ ", " +
                                "store_country=" + storeCountrySQL + ", " +
                                "store_language=" + storeLanguageSQL + ", " +
                                "store_website=" + storeWebsiteSQL + ", " +
                                "store_logo=" + storeLogoSQL + ", " +
                                "store_added_datetime=" + storeAddedDatetimeSQL + ", " +
                                "store_added_datetime_print=" + storeAddedDatetimePrintSQL + ", " +
                                "store_updatet_datetime=" + storeUpdatetDatetimeSQL + ", " +
                                "store_updatet_datetime_print=" + storeUpdatetDatetimePrintSQL + ", " +
                                "store_reported=" + storeReportedSQL + ", " +
                                "store_reported_checked=" + storeReportedCheckedSQL + " " +
                                "WHERE store_id=" + storeIdSQL;
                        db.rawQuery(q);
                    } // update

                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeEStores", "answerStores", "error", e.toString());

            } //
        } else {
            // Toast.makeText(this, "Answer blank string", Toast.LENGTH_LONG).show();
        }

        // Go to next
        askStores();
    } // answerStores
}
