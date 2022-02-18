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

public class SynchronizeBFoodIndex {

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

    public SynchronizeBFoodIndex(Context context, String currentLanguage, String synchronizationMode) {
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
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_index'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_index')";
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
                "WHERE name='food_index'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            // Error
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeBFoodIndex", "updateLastSynchronizedDate", "error", e.getMessage());

        }
        db.close();


        // Get number of exercises equipments on server
        askLastFoodIndexIdOnServer();
    }



    /*- 1. askLastFoodIndexIdOnServer ------------------------------------------------------- */
    public void askLastFoodIndexIdOnServer(){


        // Call HTTP request
        String url = apiURL + "/get_last_food_index_id_on_server.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerLastFoodIndexIdOnServer();
            }
        });
        task.execute();
    } // askLastExerciseIndexOnServer

    public void answerLastFoodIndexIdOnServer(){
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
            String q = "UPDATE synchronize SET last_on_server=" + lastIdOnServerSQL + " WHERE name='food_index'";
            db.rawQuery(q);
        }
        catch(NumberFormatException nfe) {
            // Error
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeBFoodIndex", "answerLastFoodIndexIdOnServer", "error", nfe.getMessage());
        }

        // Close
        db.close();


        /* Start synchronization */
        askFoodIndex();
    } // answerLastFoodIndexIdOnServer



    /*- 2. askFoodIndex --------------------------------------------------------- */
    public void askFoodIndex(){

        // Set start and stop
        int start = currentLastIdLocal;
        int stop  = start+10;


        // Update synchronize last on local to stop
        DBAdapter db = new DBAdapter(context);
        db.open();
        if(stop < currentLastIdOnServer) {
            String q = "UPDATE synchronize SET last_on_local='" + stop + "' WHERE name='food_index'";
            db.rawQuery(q);
        } else {
            String q = "UPDATE synchronize SET last_on_local='" + currentLastIdOnServer + "' WHERE name='food_index'";
            db.rawQuery(q);
        }
        db.close();


        if(currentLastIdLocal < currentLastIdOnServer) {

            // Pass new variable to currentLastIdLocal
            currentLastIdLocal = stop;

            // HTTP Request
            String url = apiURL + "/get_many_food_index_from_server.php?start=" + start +
                    "&stop=" + stop + "&l=" + currentLanguage;
            String stringMethod = "get";
            String stringSend = "";


            HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    answerFoodIndex();
                }
            });
            task.execute();


        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeBFoodIndex", "askFoodIndex", "info", "Finished with food index");

            SynchronizeCFoodMeasurements synchronizeCFoodMeasurements = new SynchronizeCFoodMeasurements(context, currentLanguage);
            synchronizeCFoodMeasurements.updateLastSynchronizedDate();
        }
    } // askFoodIndex

    public void answerFoodIndex(){

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


                    // Food index
                    JSONObject indexObject = object.getJSONObject("index");
                    String foodIdSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_id")));
                    String foodUserIdSQL = new OutputString().outputHTML(indexObject.getString("food_user_id"));
                    String foodNameSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_name")));
                    String foodCleanNameSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_clean_name")));
                    String foodManufacturerNameSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_manufacturer_name")));
                    String foodManufacturerNameAndFoodNameSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_manufacturer_name_and_food_name")));
                    String foodDescriptionSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_description")));
                    String foodNetContentSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_net_content")));
                    String foodNetContentMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_net_content_measurement")));
                    String foodServingSizeGramSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_serving_size_gram")));
                    String foodServingSizeGramMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_serving_size_gram_measurement")));
                    String foodServingSizePcsSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_serving_size_pcs")));
                    String foodServingSizePcsMeasurementSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_serving_size_pcs_measurement")));
                    String foodEnergySQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_energy")));
                    String foodFatSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_fat")));
                    String foodFatOfWhichSaturatedFattyAcidsSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_fat_of_which_saturated_fatty_acids")));
                    String foodCarbohydratesSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_carbohydrates")));
                    String foodCarbohydratesOfWhichSugarsSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_carbohydrates_of_which_sugars")));
                    String foodProteinsSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_proteins")));
                    String foodSaltSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_salt")));
                    String foodScoreSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_score")));
                    String foodEnergyCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_energy_calculated")));
                    String foodFatCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_fat_calculated")));
                    String foodFatOfWhichSaturatedFattyAcidsCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_fat_of_which_saturated_fatty_acids_calculated")));
                    String foodCarbohydratesCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_carbohydrates_calculated")));
                    String foodCarbohydratesOfWhichSugarsCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_carbohydrates_of_which_sugars_calculated")));
                    String foodProteinsCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_proteins_calculated")));
                    String foodSaltCalculatedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_salt_calculated")));
                    String foodBarcodeSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_barcode")));
                    String foodCategoryIdSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_category_id")));
                    String foodImagePathSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_image_path")));
                    String foodThumbSmallSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_thumb_small")));
                    String foodThumbMediumSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_thumb_medium")));
                    String foodThumbLargeSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_thumb_large")));
                    String foodImageASQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_image_a")));
                    String foodImageBSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_image_b")));
                    String foodImageCSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_image_c")));
                    String foodImageDSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_image_d")));
                    String foodImageESQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_image_e")));
                    String foodLastUsedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_last_used")));
                    String foodLanguageSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_language")));
                    String foodSynchronizedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_synchronized")));
                    String foodAcceptedAsMasterSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_accepted_as_master")));
                    String foodNotesSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_notes")));
                    String foodUniqueHitsSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_unique_hits")));
                    String foodCommentsSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_comments")));
                    String foodLikesSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_likes")));
                    String foodDislikesSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_dislikes")));
                    String foodDateSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_date")));
                    String foodTimeSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_time")));
                    String foodLastViewedSQL = db.quoteSmart(new OutputString().outputHTML(indexObject.getString("food_last_viewed")));

                    String q = "SELECT _id FROM food_index WHERE food_id=" + foodIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {


                        // Insert
                        q = "INSERT INTO food_index (_id, food_id, food_user_id, " +
                                "food_name, food_clean_name, food_manufacturer_name, " +
                                "food_manufacturer_name_and_food_name, food_description, food_net_content, " +
                                "food_net_content_measurement, food_serving_size_gram, food_serving_size_gram_measurement, " +
                                "food_serving_size_pcs, food_serving_size_pcs_measurement, food_energy, " +
                                "food_fat, food_fat_of_which_saturated_fatty_acids, food_carbohydrates, " +
                                "food_carbohydrates_of_which_sugars, food_proteins, food_salt, " +
                                "food_score, food_energy_calculated, food_fat_calculated, " +
                                "food_fat_of_which_saturated_fatty_acids_calculated, food_carbohydrates_calculated, food_carbohydrates_of_which_sugars_calculated, " +
                                "food_proteins_calculated, food_salt_calculated, food_barcode, " +
                                "food_category_id, food_image_path, food_thumb_small, " +
                                "food_thumb_medium, food_thumb_large, " +
                                "food_image_a, food_image_b, food_image_c, " +
                                "food_image_d, food_image_e, food_last_used, " +
                                "food_language, food_synchronized, food_accepted_as_master, " +
                                "food_notes, food_unique_hits, food_comments, " +
                                "food_likes, food_dislikes, food_date, " +
                                "food_time, food_last_viewed) " +
                                "VALUES (" +
                                "NULL, " + foodIdSQL + ", " + foodUserIdSQL + ", " +
                                foodNameSQL + ", " + foodCleanNameSQL + ", " + foodManufacturerNameSQL + ", " +
                                foodManufacturerNameAndFoodNameSQL + ", " + foodDescriptionSQL + ", " + foodNetContentSQL + ", " +
                                foodNetContentMeasurementSQL + ", " + foodServingSizeGramSQL + ", " + foodServingSizeGramMeasurementSQL + ", " +
                                foodServingSizePcsSQL + ", " + foodServingSizePcsMeasurementSQL + ", " + foodEnergySQL + ", " +
                                foodFatSQL + ", " + foodFatOfWhichSaturatedFattyAcidsSQL + ", " + foodCarbohydratesSQL + ", " +
                                foodCarbohydratesOfWhichSugarsSQL + ", " + foodProteinsSQL + ", " + foodSaltSQL + ", " +
                                foodScoreSQL + ", " + foodEnergyCalculatedSQL + ", " + foodFatCalculatedSQL + ", " +
                                foodFatOfWhichSaturatedFattyAcidsCalculatedSQL + ", " + foodCarbohydratesCalculatedSQL + ", " + foodCarbohydratesOfWhichSugarsCalculatedSQL + ", " +
                                foodProteinsCalculatedSQL + ", " + foodSaltCalculatedSQL + ", " + foodBarcodeSQL + ", " +
                                foodCategoryIdSQL + ", " + foodImagePathSQL + ", "  + foodThumbSmallSQL + ", " +
                                foodThumbMediumSQL + ", " + foodThumbLargeSQL + ", " +
                                foodImageASQL + ", "  + foodImageBSQL + ", " + foodImageCSQL + ", " +
                                foodImageDSQL + ", "  + foodImageESQL + ", " + foodLastUsedSQL + ", " +
                                foodLanguageSQL + ", " + foodSynchronizedSQL + ", " + foodAcceptedAsMasterSQL + ", " +
                                foodNotesSQL + ", " + foodUniqueHitsSQL + ", "  + foodCommentsSQL + ", " +
                                foodLikesSQL + ", "  + foodDislikesSQL + ", " + foodDateSQL + ", " +
                                foodTimeSQL + ", " + foodLastViewedSQL
                                + ")";

                        db.rawQuery(q);
                    } else {

                        // Update
                        q = "UPDATE food_index SET " +
                                "food_user_id=" + foodUserIdSQL + ", " +
                                "food_name=" + foodNameSQL + ", " +
                                "food_clean_name=" + foodCleanNameSQL + ", " +
                                "food_manufacturer_name=" + foodManufacturerNameSQL + ", " +
                                "food_manufacturer_name_and_food_name=" + foodManufacturerNameAndFoodNameSQL + ", " +
                                "food_description=" + foodDescriptionSQL + ", " +
                                "food_net_content=" + foodNetContentSQL + ", " +
                                "food_net_content_measurement=" + foodNetContentMeasurementSQL + ", " +
                                "food_serving_size_gram=" + foodServingSizeGramSQL + ", " +
                                "food_serving_size_gram_measurement=" + foodServingSizeGramMeasurementSQL + ", " +
                                "food_serving_size_pcs=" + foodServingSizePcsSQL + ", " +
                                "food_serving_size_pcs_measurement=" + foodServingSizePcsMeasurementSQL + ", " +
                                "food_energy=" + foodEnergySQL + ", " +
                                "food_fat=" + foodFatSQL + ", " +
                                "food_fat_of_which_saturated_fatty_acids=" + foodFatOfWhichSaturatedFattyAcidsSQL + ", " +
                                "food_carbohydrates=" + foodCarbohydratesSQL + ", " +
                                "food_carbohydrates_of_which_sugars=" + foodCarbohydratesOfWhichSugarsSQL + ", " +
                                "food_proteins=" + foodProteinsSQL + ", " +
                                "food_salt=" + foodSaltSQL + ", " +
                                "food_score=" + foodScoreSQL + ", " +
                                "food_energy_calculated=" + foodEnergyCalculatedSQL + ", " +
                                "food_fat_calculated=" + foodFatCalculatedSQL + ", " +
                                "food_fat_of_which_saturated_fatty_acids_calculated=" + foodFatOfWhichSaturatedFattyAcidsCalculatedSQL + ", " +
                                "food_carbohydrates_calculated=" + foodCarbohydratesCalculatedSQL + ", " +
                                "food_carbohydrates_of_which_sugars_calculated=" + foodCarbohydratesOfWhichSugarsCalculatedSQL + ", " +
                                "food_proteins_calculated=" + foodProteinsCalculatedSQL + ", " +
                                "food_salt_calculated=" + foodSaltCalculatedSQL + ", " +
                                "food_barcode=" + foodBarcodeSQL + ", " +
                                "food_category_id=" + foodCategoryIdSQL + ", " +
                                "food_image_path=" + foodImagePathSQL + ", " +
                                "food_thumb_small=" + foodThumbSmallSQL + ", " +
                                "food_thumb_medium=" + foodThumbMediumSQL + ", " +
                                "food_thumb_large=" + foodThumbLargeSQL + ", " +
                                "food_image_a=" + foodImageASQL + ", " +
                                "food_image_b=" + foodImageBSQL + ", " +
                                "food_image_c=" + foodImageCSQL + ", " +
                                "food_image_d=" + foodImageDSQL + ", " +
                                "food_image_e=" + foodImageESQL + ", " +
                                "food_last_used=" + foodLastUsedSQL + ", " +
                                "food_language=" + foodLanguageSQL + ", " +
                                "food_synchronized=" + foodSynchronizedSQL + ", " +
                                "food_accepted_as_master=" + foodAcceptedAsMasterSQL + ", " +
                                "food_notes=" + foodNotesSQL + ", " +
                                "food_unique_hits=" + foodUniqueHitsSQL + ", " +
                                "food_comments=" + foodCommentsSQL + ", " +
                                "food_likes=" + foodLikesSQL + ", " +
                                "food_dislikes=" + foodDislikesSQL + ", " +
                                "food_date=" + foodDateSQL + ", " +
                                "food_time=" + foodTimeSQL + ", " +
                                "food_last_viewed=" + foodLastViewedSQL + " " +
                                "WHERE food_id=" + foodIdSQL;

                        db.rawQuery(q);
                    }



                    // food_index_ads
                    /*
                    JSONObject adsObject = object.getJSONObject("index_ads");
                    String adIdSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_id")));
                    String adFoodLanguageSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_language")));
                    String adFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_id")));
                    String adTextSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_text")));
                    String adUrlSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_url")));
                    String adFoodCreatedDatetimeSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_created_datetime")));
                    String adFoodCreatedByUserIdSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_created_by_user_id")));
                    String adFoodUpdatedDatetimeSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_updated_datetime")));
                    String adFoodUpdatedByUserIdSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_updated_by_user_id")));
                    String adFoodUniqueClicksSQL = db.quoteSmart(new OutputString().outputHTML(adsObject.getString("ad_food_unique_clicks")));

                    q = "SELECT _id FROM food_index_ads WHERE ad_id=" + adIdSQL;
                    checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_index_ads (_id, ad_id, ad_food_language, " +
                                "ad_food_id, ad_text, ad_url, " +
                                "ad_food_created_datetime, ad_food_created_by_user_id, ad_food_updated_datetime, " +
                                "ad_food_updated_by_user_id, ad_food_unique_clicks) " +
                                "VALUES ("
                                + "NULL, "
                                + adIdSQL + ", "
                                + adFoodLanguageSQL + ", "
                                + adFoodIdSQL + ", "
                                + adTextSQL + ", "
                                + adUrlSQL + ", "
                                + adFoodCreatedDatetimeSQL + ", "
                                + adFoodCreatedByUserIdSQL + ", "
                                + adFoodUpdatedDatetimeSQL + ", "
                                + adFoodUpdatedByUserIdSQL + ", "
                                + adFoodUniqueClicksSQL
                                + ")";
                        db.rawQuery(q);

                    } else {

                        // Update
                        q = "UPDATE food_index_ads SET " +
                                "ad_food_language=" + adFoodLanguageSQL + ", " +
                                "ad_food_id=" + adFoodIdSQL + ", " +
                                "ad_text=" + adTextSQL + ", " +
                                "ad_url=" + adUrlSQL+  ", " +
                                "ad_food_created_datetime=" + adFoodCreatedDatetimeSQL + ", " +
                                "ad_food_created_by_user_id=" + adFoodCreatedByUserIdSQL + ", " +
                                "ad_food_updated_datetime=" + adFoodUpdatedDatetimeSQL + ", " +
                                "ad_food_updated_by_user_id=" + adFoodUpdatedByUserIdSQL+  ", " +
                                "ad_food_unique_clicks=" + adFoodUniqueClicksSQL + " " +
                                "WHERE ad_id=" + adIdSQL;
                        db.rawQuery(q);
                    } // update
                    */

                    // index_prices
                    try {
                        JSONArray pricesArray = object.getJSONArray("index_prices");
                        for (int y = 0; y < pricesArray.length(); y++) {
                            JSONObject pricesObject = pricesArray.getJSONObject(y);


                            String foodPriceIdQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_id")));
                            String foodPriceFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_food_id")));
                            String foodPriceStoreIdSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_store_id")));
                            String foodPriceStoreNameSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_store_name")));
                            String foodPricePriceSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_price")));
                            String foodPriceCurrencySQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_currency")));
                            String foodPriceOfferSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_offer")));
                            String foodPriceOfferValidFromSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_offer_valid_from")));
                            String foodPriceOfferValidToSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_offer_valid_to")));
                            String foodPriceUserIdSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_user_id")));
                            String foodPriceUserIpSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_user_ip")));
                            String foodPriceAddedDatetimeSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_added_datetime")));
                            String foodPriceAddedDatetimePrintSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_added_datetime_print")));
                            String foodPriceUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_updated")));
                            String foodPriceUpdatedPrintSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_updated_print")));
                            String foodPriceReportedSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_reported")));
                            String foodPriceReportedCheckedSQL = db.quoteSmart(new OutputString().outputHTML(pricesObject.getString("food_price_reported_checked")));

                            q = "SELECT _id FROM food_index_prices WHERE food_price_id=" + foodPriceIdQL;
                            checkCursor = db.rawQuery(q);
                            if (checkCursor.getCount() == 0) {


                                // Insert
                                q = "INSERT INTO food_index_prices (_id, food_price_id, food_price_food_id, " +
                                        "food_price_store_id, food_price_store_name, food_price_price, " +
                                        "food_price_currency, food_price_offer, food_price_offer_valid_from, " +
                                        "food_price_offer_valid_to, food_price_user_id, food_price_user_ip, " +
                                        "food_price_added_datetime, food_price_added_datetime_print, food_price_updated, " +
                                        "food_price_updated_print, food_price_reported, food_price_reported_checked) " +
                                        "VALUES ("
                                        + "NULL, "
                                        + foodPriceIdQL + ", "
                                        + foodPriceFoodIdSQL + ", "
                                        + foodPriceStoreIdSQL + ", "
                                        + foodPriceStoreNameSQL + ", "
                                        + foodPricePriceSQL + ", "
                                        + foodPriceCurrencySQL + ", "
                                        + foodPriceOfferSQL + ", "
                                        + foodPriceOfferValidFromSQL + ", "
                                        + foodPriceOfferValidToSQL + ", "
                                        + foodPriceUserIdSQL + ", "
                                        + foodPriceUserIpSQL + ", "
                                        + foodPriceAddedDatetimeSQL + ", "
                                        + foodPriceAddedDatetimePrintSQL + ", "
                                        + foodPriceUpdatedSQL + ", "
                                        + foodPriceUpdatedPrintSQL + ", "
                                        + foodPriceReportedSQL + ", "
                                        + foodPriceReportedCheckedSQL
                                        + ")";


                                db.rawQuery(q);



                            } else {

                                // Update
                                q = "UPDATE food_index_prices SET " +
                                        "food_price_food_id=" + foodPriceFoodIdSQL + ", " +
                                        "food_price_store_id=" + foodPriceStoreIdSQL + ", "  +
                                        "food_price_store_name=" + foodPriceStoreNameSQL + ", " +
                                        "food_price_price=" + foodPricePriceSQL + ", " +
                                        "food_price_currency=" + foodPriceCurrencySQL + ", " +
                                        "food_price_offer=" + foodPriceOfferSQL + ", " +
                                        "food_price_offer_valid_from=" + foodPriceOfferValidFromSQL + " , " +
                                        "food_price_offer_valid_to=" + foodPriceOfferValidToSQL+ ", " +
                                        "food_price_user_id=" + foodPriceUserIdSQL + ", " +
                                        "food_price_user_ip=" + foodPriceUserIpSQL + ", " +
                                        "food_price_added_datetime=" + foodPriceAddedDatetimeSQL + ", " +
                                        "food_price_added_datetime_print=" + foodPriceAddedDatetimePrintSQL + ", " +
                                        "food_price_updated=" + foodPriceUpdatedSQL + ", " +
                                        "food_price_updated_print=" + foodPriceUpdatedPrintSQL + ", " +
                                        "food_price_reported=" + foodPriceReportedSQL + ", " +
                                        "food_price_reported_checked=" + foodPriceReportedCheckedSQL + " " +
                                        "WHERE food_price_id=" + foodPriceIdQL;

                                db.rawQuery(q);
                            } // update
                        } // for
                    } catch (JSONException je) {
                        // Server error 500
                        WriteToErrorLog log = new WriteToErrorLog(context);
                        log.writeToErrorLog("SynchronizeBFoodIndex", "answerFoodIndex", "error", je.getMessage());
                    } // food_index_prices


                    // index_stores
                    try {
                        JSONArray storesArray = object.getJSONArray("index_stores");
                        for (int y = 0; y < storesArray.length(); y++) {
                            JSONObject storesObject = storesArray.getJSONObject(y);


                            String foodStoreIdQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_id")));
                            String foodStoreFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_food_id")));
                            String foodStoreStoreIdSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_store_id")));
                            String foodStoreStoreNameSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_store_name")));
                            String foodStoreStoreLogoSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_store_logo")));

                            String foodStoreStorePriceSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_store_price")));
                            String foodStoreStoreCurrencySQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_store_currency")));
                            String foodStoreUserIdSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_user_id")));
                            String foodStoreUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(storesObject.getString("food_store_updated")));

                            q = "SELECT _id FROM food_index_stores WHERE food_store_id=" + foodStoreIdQL;
                            checkCursor = db.rawQuery(q);
                            if (checkCursor.getCount() == 0) {


                                // Insert
                                q = "INSERT INTO food_index_stores (_id, food_store_id, food_store_food_id, " +
                                        "food_store_store_id, food_store_store_name, food_store_store_logo, " +
                                        "food_store_store_price, food_store_store_currency, food_store_user_id, " +
                                        "food_store_updated) " +
                                        "VALUES (" +
                                        "NULL, " +
                                        foodStoreIdQL + ", " +
                                        foodStoreFoodIdSQL + ", " +
                                        foodStoreStoreIdSQL + ", " +
                                        foodStoreStoreNameSQL + ", " +
                                        foodStoreStoreLogoSQL + ", " +
                                        foodStoreStorePriceSQL + ", "+
                                        foodStoreStoreCurrencySQL + ", " +
                                        foodStoreUserIdSQL + ", " +
                                        foodStoreUpdatedSQL +
                                        ")";
                                db.rawQuery(q);

                            } else {
                                // Update
                                q = "UPDATE food_index_stores SET " +
                                        "food_store_food_id=" + foodStoreFoodIdSQL + ", " +
                                        "food_store_store_id=" + foodStoreStoreIdSQL + ", " +
                                        "food_store_store_name=" + foodStoreStoreNameSQL + ", " +
                                        "food_store_store_logo=" + foodStoreStoreLogoSQL + ", " +
                                        "food_store_store_price=" + foodStoreStorePriceSQL + ", " +
                                        "food_store_store_currency=" + foodStoreStoreCurrencySQL + ", " +
                                        "food_store_user_id=" + foodStoreUserIdSQL + ", " +
                                        "food_store_updated=" + foodStoreUpdatedSQL + " " +
                                        "WHERE food_store_id=" + foodStoreIdQL;
                                db.rawQuery(q);
                            } // update
                        } // for
                    } catch (JSONException je) {
                        // Server error 500
                        WriteToErrorLog log = new WriteToErrorLog(context);
                        log.writeToErrorLog("SynchronizeBFoodIndex", "answerFoodIndex food_index_stores", "error", je.getMessage());
                    } // food_index_stores


                    // index_tags
                    try {
                        JSONArray tagsArray = object.getJSONArray("index_tags");
                        for (int y = 0; y < tagsArray.length(); y++) {
                            JSONObject tagsObject = tagsArray.getJSONObject(y);

                            String tagIdQL = db.quoteSmart(new OutputString().outputHTML(tagsObject.getString("tag_id")));
                            String tagLanguageSQL = db.quoteSmart(new OutputString().outputHTML(tagsObject.getString("tag_language")));
                            String tagFoodIdSQL = db.quoteSmart(new OutputString().outputHTML(tagsObject.getString("tag_food_id")));
                            String tagTitleSQL = db.quoteSmart(new OutputString().outputHTML(tagsObject.getString("tag_title")));
                            String tagTitleCleanSQL = db.quoteSmart(new OutputString().outputHTML(tagsObject.getString("tag_title_clean")));
                            String tagUserIdSQL = db.quoteSmart(new OutputString().outputHTML(tagsObject.getString("tag_user_id")));

                            q = "SELECT _id FROM food_index_tags WHERE tag_id=" + tagIdQL;
                            checkCursor = db.rawQuery(q);
                            if (checkCursor.getCount() == 0) {



                                // Insert
                                q = "INSERT INTO food_index_tags (_id, tag_id, tag_language, " +
                                        "tag_food_id, tag_title, tag_title_clean, " +
                                        "tag_user_id) " +
                                        "VALUES (" +
                                        "NULL, " +
                                        tagIdQL + ", " +
                                        tagLanguageSQL + ", " +
                                        tagFoodIdSQL + ", " +
                                        tagTitleSQL + ", " +
                                        tagTitleCleanSQL + ", " +
                                        tagUserIdSQL +
                                        ")";
                                db.rawQuery(q);

                            } else {
                                // Update
                                q = "UPDATE food_index_tags SET " +
                                        "tag_language=" + tagLanguageSQL + ", " +
                                        "tag_food_id=" + tagFoodIdSQL + ", " +
                                        "tag_title=" + tagTitleSQL + ", " +
                                        "tag_title_clean=" + tagTitleCleanSQL + ", " +
                                        "tag_user_id=" + tagUserIdSQL + " " +
                                        "WHERE tag_id=" + tagIdQL;
                                db.rawQuery(q);
                            } // update
                        } // for
                    } catch (JSONException je) {
                        // Server error 500
                        WriteToErrorLog log = new WriteToErrorLog(context);
                        log.writeToErrorLog("SynchronizeBFoodIndex", "answerFoodIndex food_index_tags", "error", je.getMessage());
                    } // food_index_tags



                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeBFoodIndex", "answerFoodIndex", "error", e.getMessage());
            } //
        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeBFoodIndex", "answerFoodIndex", "error", "Answer blank string");
        }

        // Go to next
        askFoodIndex();
    } // answerFoodIndex


}
