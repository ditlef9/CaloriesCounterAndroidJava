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

public class SynchronizeFRecipesCategories {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/recipes/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    String currentLanguage = "";
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;


    public SynchronizeFRecipesCategories(Context context, String currentLanguage) {
        this.context = context;
        this.currentLanguage = currentLanguage;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();


        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='recipes_categories'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'recipes_categories')";
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
                "WHERE name='recipes_categories'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeFRecipesCategories", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();

        // Get number of exercises equipments on server
        askGetCategoriesFromServer();
    } // updateLastSynchronizedDate

    /*- Ask: Get categories from server ---------------------------------------------------------- */
    public void askGetCategoriesFromServer(){
        // Database
        DBAdapter db = new DBAdapter(context);
        db.open();

        // Get categories

        // Call HTTP request
        String url = apiURL + "/categories_get.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";

        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerGetCategoriesFromServer();
            }
        });
        task.execute();



    } //askGetCategoriesFromServer

    public void answerGetCategoriesFromServer(){

        // Dynamic Text
        DBAdapter db = new DBAdapter(context);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        db.close();

        if(cursorData.getCount() > 0) {
            String dataResult = cursorData.getString(1);

            if(!(dataResult.equals("")) && !(dataResult.equals("[]"))) {
                // Database
                db.open();

                try {
                    JSONArray array = new JSONArray(dataResult);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        String categoryId = db.quoteSmart(new OutputString().outputHTML(object.getString("category_id")));
                        String categoryName = db.quoteSmart(new OutputString().outputHTML(object.getString("category_name")));
                        String categoryAgeRestriction = db.quoteSmart(new OutputString().outputHTML(object.getString("category_age_restriction")));

                        String categoryTranslationId = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_id")));
                        String categoryTranslationLanguage = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_language")));
                        String categoryTranslationValue = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_value")));
                        String categoryTranslationNoRecipes = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_no_recipes")));
                        String categoryTranslationLastUpdated = db.quoteSmart(new OutputString().outputHTML(object.getString("category_translation_last_updated")));



                        String q = "SELECT _id FROM recipes_categories WHERE category_id=" + categoryId;
                        Cursor checkCursor = db.rawQuery(q);
                        if (checkCursor.getCount() == 0) {
                            // Insert
                            q = "INSERT INTO recipes_categories (_id, category_id, category_name, category_age_restriction) " +
                                    "VALUES ("
                                    + "NULL, "
                                    + categoryId + ", "
                                    + categoryName + ", "
                                    + categoryAgeRestriction
                                    + ")";
                            db.rawQuery(q);
                        }

                        q = "SELECT _id FROM recipes_categories_translations WHERE category_translation_id=" + categoryTranslationId;
                        checkCursor = db.rawQuery(q);
                        if (checkCursor.getCount() == 0) {
                            // Translation
                            q = "INSERT INTO recipes_categories_translations (_id, category_translation_id, category_id, category_translation_language, " +
                                    "category_translation_value, category_translation_no_recipes, category_translation_last_updated) " +
                                    "VALUES ("
                                    + "NULL, "
                                    + categoryTranslationId + ", "
                                    + categoryId + ", "
                                    + categoryTranslationLanguage + ", "
                                    + categoryTranslationValue + ", "
                                    + categoryTranslationNoRecipes + ", "
                                    + categoryTranslationLastUpdated
                                    + ")";
                            db.rawQuery(q);
                        }

                    }

                } catch (JSONException e) {
                    // Server error 500
                    WriteToErrorLog log = new WriteToErrorLog(context);
                    log.writeToErrorLog("SynchronizeFRecipesCategories", "answerGetCategoriesFromServer", "error", e.getMessage());
                } //
                // Db close
                db.close();
            } // if(!(stringDynamicText.equals("")) && !(stringDynamicText.equals("[]"))) {


            // Move
            SynchronizeGRecipes sync = new SynchronizeGRecipes(context, currentLanguage);
            sync.updateLastSynchronizedDate();

        } else {
            // Toast
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeFRecipesCategories", "answerGetCategoriesFromServer", "error", "Error:" + cursorData.getCount());

        }

    } // answerGetCategoriesFromServer


}
