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

public class SynchronizeDPricesCurrencies {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/food/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    String currentLanguage = "";
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;


    public SynchronizeDPricesCurrencies(Context context, String currentLanguage) {
        this.context = context;
        this.currentLanguage = currentLanguage;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();



        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_prices_currencies'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_prices_currencies')";
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
                "WHERE name='food_prices_currencies'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeDPricesCurrencies", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();


        // Get number of exercises equipments on server
        askLastPricesCurrencyId();
    }



    /*- 1. askLastPricesCurrencyId ------------------------------------------------------- */
    public void askLastPricesCurrencyId(){


        // Call HTTP request
        String url = apiURL + "/get_last_prices_currency_id.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerLastPricesCurrencyId();
            }
        });
        task.execute();
    } // askLastPricesCurrencyId

    public void answerLastPricesCurrencyId(){
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
            String q = "UPDATE synchronize SET last_on_server=" + lastIdOnServerSQL + " WHERE name='food_prices_currencies'";
            db.rawQuery(q);
        }
        catch(NumberFormatException nfe) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeDPricesCurrencies", "answerLastPricesCurrencyId", "error", nfe.getMessage());
        }

        // Close
        db.close();


        /* Start synchronization */
        askPricesCurrencies();
    } // answerLastExerciseIndexOnServer



    /*- 2. askFoodIndex --------------------------------------------------------- */
    public void askPricesCurrencies(){

        // Set start and stop
        int start = currentLastIdLocal;
        int stop  = start+10;


        // Update synchronize last on local to stop
        DBAdapter db = new DBAdapter(context);
        db.open();
        if(stop < currentLastIdOnServer) {
            String q = "UPDATE synchronize SET last_on_local='" + stop + "' WHERE name='food_prices_currencies'";
            db.rawQuery(q);
        } else {
            String q = "UPDATE synchronize SET last_on_local='" + currentLastIdOnServer + "' WHERE name='food_prices_currencies'";
            db.rawQuery(q);
        }
        db.close();


        if(currentLastIdLocal < currentLastIdOnServer) {

            // Pass new variable to currentLastIdLocal
            currentLastIdLocal = stop;

            // HTTP Request
            String url = apiURL + "/get_many_prices_currencies_from_server.php?start=" + start +
                    "&stop=" + stop + "&l=" + currentLanguage;
            String stringMethod = "get";
            String stringSend = "";


            HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    answerPricesCurrencies();
                }
            });
            task.execute();


        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeDPricesCurrencies", "answerPricesCurrencies", "info", "Finished with Prices Currencies");

            SynchronizeEStores synchronizeEStores = new SynchronizeEStores(context, currentLanguage);
            synchronizeEStores.updateLastSynchronizedDate();
        }
    } // answerPricesCurrencies

    public void answerPricesCurrencies(){

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



                    String currencyIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_id")));
                    String currencyNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_name")));
                    String currencyCodeSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_code")));
                    String currencySymbolSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_symbol")));
                    String currencyCountryIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_country_id")));
                    String currencyCountryNameSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_country_name")));
                    String currencyLastUsedLanguageSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("currency_last_used_language")));

                    String q = "SELECT _id FROM food_prices_currencies WHERE currency_id=" + currencyIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_prices_currencies (_id, currency_id, currency_name, " +
                                "currency_code, currency_symbol, currency_country_id, " +
                                "currency_country_name, currency_last_used_language) " +
                                "VALUES ("
                                + "NULL, "
                                + currencyIdSQL + ", "
                                + currencyNameSQL + ", "
                                + currencyCodeSQL + ", "
                                + currencySymbolSQL + ", "
                                + currencyCountryIdSQL + ", "
                                + currencyCountryNameSQL + ", "
                                + currencyLastUsedLanguageSQL
                                + ")";
                        db.rawQuery(q);
                    } else {
                        // Update
                        q = "UPDATE food_prices_currencies SET " +
                                "currency_name=" +currencyNameSQL+ ", " +
                                "currency_code=" + currencyCodeSQL+ ", " +
                                "currency_symbol=" + currencySymbolSQL + ", " +
                                "currency_country_id=" + currencyCountryIdSQL + ", " +
                                "currency_country_name=" + currencyCountryNameSQL + ", " +
                                "currency_last_used_language=" + currencyLastUsedLanguageSQL + " " +
                                "WHERE currency_id=" + currencyIdSQL;
                        db.rawQuery(q);




                    } // update

                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeDPricesCurrencies", "answerPricesCurrencies", "error", e.toString());
            } //
        } else {
            // Toast.makeText(this, "Answer blank string", Toast.LENGTH_LONG).show();
        }

        // Go to next
        askPricesCurrencies();
    } // answerPricesCurrencies

}
