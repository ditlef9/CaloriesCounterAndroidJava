package net.frindex.caloriescounterandroidjava.synchronize_app;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

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

public class SynchronizeGRecipes {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/recipes/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    String currentLanguage = "";
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;


    public SynchronizeGRecipes(Context context, String currentLanguage) {
        this.context = context;
        this.currentLanguage = currentLanguage;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();


        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='recipes'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'recipes')";
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
                "WHERE name='recipes'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeGRecipes", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();

        // Get number of exercises equipments on server
        askLastRecipeIdOnServer();
    } // updateLastSynchronizedDate

    /*- Ask for number of recipes on server ------------------------------------------- */
    public void askLastRecipeIdOnServer(){



        // Call HTTP request
        String url = apiURL + "/get_last_recipe_id_on_server.php?l=" + currentLanguage;
        String stringMethod = "get";
        String stringSend   = "";

        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerLastRecipeIdOnServer();
            }
        });
        task.execute();

    }
    public void answerLastRecipeIdOnServer(){
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
            String q = "UPDATE synchronize SET last_on_server=" + lastIdOnServerSQL + " WHERE name='recipes'";
            db.rawQuery(q);
        }
        catch(NumberFormatException nfe) {
            // Error
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeGRecipes", "answerLastRecipeIdOnServer", "error", nfe.getMessage());
        }

        // Close
        db.close();


        /* Start synchronization */
        askGetRecipesFromServer();
    } // answerLastRecipeIdOnServer


    /*- Ask: Get recipes from server ---------------------------------------------------------- */
    public void askGetRecipesFromServer(){


        // Set start and stop
        int start = currentLastIdLocal;
        int stop  = start+10;


        // Update synchronize last on local to stop
        DBAdapter db = new DBAdapter(context);
        db.open();
        if(stop < currentLastIdOnServer) {
            String q = "UPDATE synchronize SET last_on_local='" + stop + "' WHERE name='recipes'";
            db.rawQuery(q);
        } else {
            String q = "UPDATE synchronize SET last_on_local='" + currentLastIdOnServer + "' WHERE name='recipes'";
            db.rawQuery(q);
        }
        db.close();


        if(currentLastIdLocal < currentLastIdOnServer) {

            // Pass new variable to currentLastIdLocal
            currentLastIdLocal = stop;

            // HTTP Request
            String url = apiURL + "/get_recipes_small_from_server.php?from=" + start + "&to="  + stop;
            String stringMethod = "get";
            String stringSend = "";


            HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    answerGetRecipeFromServer();
                }
            });
            task.execute();


        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeGRecipes", "askGetRecipesFromServer", "info", "Finished with recipes");

        }


    } // askGetRecipesFromServer

    /*- Answer: Get recipe from server ------------------------------------------------- */
    public void answerGetRecipeFromServer(){

        // Dynamic Text
        DBAdapter db = new DBAdapter(context);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);
        db.close();


        if(!(dataResult.equals("")) && !(dataResult.equals("[]"))) {



            try {
                JSONArray array = new JSONArray(dataResult);
                for (int i = 0; i < array.length(); i++) {


                    JSONObject recipeObject = array.getJSONObject(i);

                    String recipeId = new OutputString().outputHTML(recipeObject.getString("recipe_id"));
                    String recipeIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_id")));
                    String recipeUserIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_user_id")));
                    String recipeTitleSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_title")));
                    String recipeCategoryIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_category_id")));
                    String recipeLanguage = new OutputString().outputHTML(recipeObject.getString("recipe_language"));
                    String recipeLanguageSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_language")));
                    String recipeIntroductionSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_introduction")));
                    String recipeImagePathSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_image_path")));
                    String recipeImage = new OutputString().outputHTML(recipeObject.getString("recipe_image"));
                    String recipeImageSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_image")));
                    String recipeThumbSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_thumb")));
                    String recipeMarkedAsSpamSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_marked_as_spam")));
                    String recipePasswordSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_password")));


                    String numberHundredCalories = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_hundred_calories")));
                    String numberHundredProteins = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_hundred_proteins")));
                    String numberHundredFat = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_hundred_fat")));
                    String numberHundredCarbs = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_hundred_carbs")));
                    String numberServingCalories = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_serving_calories")));
                    String numberServingProteins = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_serving_proteins")));
                    String numberServingFat = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_serving_fat")));
                    String numberServingCarbs = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_serving_carbs")));
                    String numberTotalWeight = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_total_weight")));
                    String numberTotalCalories = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_total_calories")));
                    String numberTotalProteins = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_total_proteins")));
                    String numberTotalFat = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_total_fat")));
                    String numberTotalCarbs = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_total_carbs")));
                    String numberServings = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("number_servings")));

                    String tagAId = new OutputString().outputHTML(recipeObject.getString("tag_a_id"));
                    String tagAIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_id")));
                    String tagATitleSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_title")));
                    String tagATitleCleanSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_title_clean")));

                    String tagBId = new OutputString().outputHTML(recipeObject.getString("tag_a_id"));
                    String tagBIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_id")));
                    String tagBTitleSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_title")));
                    String tagBTitleCleanSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_title_clean")));

                    String tagCId = new OutputString().outputHTML(recipeObject.getString("tag_a_id"));
                    String tagCIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_id")));
                    String tagCTitleSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_title")));
                    String tagCTitleCleanSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("tag_a_title_clean")));

                    if(recipeLanguage.equals(currentLanguage) && !(recipeImage.equals(""))) {

                        // Database
                        db.open();

                        // Do we have the recipe saved from before_

                        String q = "SELECT _id FROM recipes WHERE recipe_id=" + recipeIdSQL;
                        Cursor recipeCursor = db.rawQuery(q);
                        int cursorLength = recipeCursor.getCount();
                        if (cursorLength < 1) {
                            // Toast.makeText(this, "New recipe " + recipeObject.getString("recipe_title"), Toast.LENGTH_SHORT).show();

                            // We don't have it, insert it
                            q = "INSERT INTO recipes(_id, recipe_id, recipe_user_id, recipe_title, " +
                                    "recipe_category_id, recipe_language, recipe_introduction, " +
                                    "recipe_image_path, recipe_image, recipe_thumb, " +
                                    "recipe_marked_as_spam, recipe_password) " +
                                    " VALUES (" +
                                    "NULL, " +
                                    recipeIdSQL + ", " +
                                    recipeUserIdSQL + ", " +
                                    recipeTitleSQL + ", " +
                                    recipeCategoryIdSQL + ", " +
                                    recipeLanguageSQL + ", " +
                                    recipeIntroductionSQL + ", " +
                                    recipeImagePathSQL + ", " +
                                    recipeImageSQL + ", " +
                                    recipeThumbSQL + ", " +
                                    recipeMarkedAsSpamSQL + ", " +
                                    recipePasswordSQL +
                                    ")";
                            try {
                                db.rawQuery(q);
                            } catch (SQLException sqle) {
                                Toast.makeText(context, "Could not insert into recipes: " + sqle.toString(), Toast.LENGTH_LONG).show();
                            }


                        } else {
                            // update it
                            // Toast.makeText(this, "Update recipe " + recipeObject.getString("recipe_title"), Toast.LENGTH_SHORT).show();
                            q = "UPDATE recipes SET " +
                                    "recipe_user_id=" + recipeUserIdSQL + "," +
                                    "recipe_title=" + recipeTitleSQL + "," +
                                    "recipe_category_id=" + recipeCategoryIdSQL + "," +
                                    "recipe_language=" + recipeLanguageSQL + "," +
                                    "recipe_introduction=" + recipeIntroductionSQL + "," +
                                    "recipe_image_path=" + recipeImagePathSQL + "," +
                                    "recipe_image=" + recipeImageSQL + "," +
                                    "recipe_thumb=" + recipeThumbSQL + "," +
                                    "recipe_marked_as_spam=" + recipeMarkedAsSpamSQL + "," +
                                    "recipe_password=" + recipePasswordSQL +
                                    " WHERE recipe_id=" + recipeIdSQL;
                            try {
                                db.rawQuery(q);
                            } catch (SQLException sqle) {
                                Toast.makeText(context, "Could not update into recipes: " + sqle.toString(), Toast.LENGTH_LONG).show();
                            }

                        }

                        // Numbers
                        q = "SELECT _id FROM recipes_numbers WHERE number_recipe_id=" + recipeIdSQL;
                        recipeCursor = db.rawQuery(q);
                        cursorLength = recipeCursor.getCount();
                        if (cursorLength == 0) {


                            query = "INSERT INTO recipes_numbers (_id, number_recipe_id, number_hundred_calories, number_hundred_proteins, number_hundred_fat, number_hundred_carbs, number_serving_calories, number_serving_proteins, number_serving_fat, number_serving_carbs, number_total_weight, number_total_calories, number_total_proteins, number_total_fat, number_total_carbs, number_servings) " +
                                    "VALUES (" +
                                    "null, " +
                                    recipeIdSQL + ", " +
                                    numberHundredCalories + ", " +
                                    numberHundredProteins + ", " +
                                    numberHundredFat + ", " +
                                    numberHundredCarbs + ", " +
                                    numberServingCalories + ", " +
                                    numberServingProteins + ", " +
                                    numberServingFat + ", " +
                                    numberServingCarbs + ", " +
                                    numberTotalWeight + ", " +
                                    numberTotalCalories + ", " +
                                    numberTotalProteins + ", " +
                                    numberTotalFat + ", " +
                                    numberTotalCarbs + ", " +
                                    numberServings +
                                    ")";
                            try {
                                db.rawQuery(query);
                            } catch (SQLException e) {
                                Toast.makeText(context, "recipes_numbers: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }


                        // Tag A
                        if(!(tagAId.equals(""))) {
                            // Check if we have Tag A


                            q = "SELECT _id FROM recipes_tags WHERE tag_id=" + tagAIdSQL;
                            Cursor tagCursor = db.rawQuery(q);
                            int tagCursorLength = tagCursor.getCount();
                            if (tagCursorLength < 1) {
                                // Insert it
                                q = "INSERT INTO recipes_tags(_id, tag_id, tag_language, tag_recipe_id, " +
                                        "tag_title, tag_title_clean, tag_user_id) " +
                                        " VALUES (" +
                                        "NULL, " +
                                        tagAIdSQL + ", " +
                                        recipeLanguageSQL + ", " +
                                        recipeIdSQL + ", " +
                                        tagATitleSQL + ", " +
                                        tagATitleCleanSQL + ", " +
                                        recipeUserIdSQL +
                                        ")";
                                try {
                                    db.rawQuery(q);
                                } catch (SQLException sqle) {
                                    Toast.makeText(context, "Could not insert into tags A: " + sqle.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        } // Tag A

                        // Tag B
                        if(!(tagBId.equals(""))) {
                            // Check if we have Tag B
                            q = "SELECT _id FROM recipes_tags WHERE tag_id=" + tagBIdSQL;
                            Cursor tagCursor = db.rawQuery(q);
                            int tagCursorLength = tagCursor.getCount();
                            if (tagCursorLength < 1) {
                                // Insert it
                                q = "INSERT INTO recipes_tags(_id, tag_id, tag_language, tag_recipe_id, " +
                                        "tag_title, tag_title_clean, tag_user_id) " +
                                        " VALUES (" +
                                        "NULL, " +
                                        tagBIdSQL + ", " +
                                        recipeLanguageSQL + ", " +
                                        recipeIdSQL + ", " +
                                        tagBTitleSQL + ", " +
                                        tagBTitleCleanSQL + ", " +
                                        recipeUserIdSQL +
                                        ")";
                                try {
                                    db.rawQuery(q);
                                } catch (SQLException sqle) {
                                    Toast.makeText(context, "Could not insert into tags B: " + sqle.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        } // Tag B

                        // Tag C
                        if(!(tagCId.equals(""))) {
                            // Check if we have Tag B
                            q = "SELECT _id FROM recipes_tags WHERE tag_id=" + tagCIdSQL;
                            Cursor tagCursor = db.rawQuery(q);
                            int tagCursorLength = tagCursor.getCount();
                            if (tagCursorLength < 1) {
                                // Insert it
                                q = "INSERT INTO recipes_tags(_id, tag_id, tag_language, tag_recipe_id, " +
                                        "tag_title, tag_title_clean, tag_user_id) " +
                                        " VALUES (" +
                                        "NULL, " +
                                        tagCIdSQL + ", " +
                                        recipeLanguageSQL + ", " +
                                        recipeIdSQL + ", " +
                                        tagCTitleSQL + ", " +
                                        tagCTitleCleanSQL + ", " +
                                        recipeUserIdSQL +
                                        ")";
                                try {
                                    db.rawQuery(q);
                                } catch (SQLException sqle) {
                                    Toast.makeText(context, "Could not insert into tags C: " + sqle.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        } // Tag B



                        db.close();

                    } // recipeLanguage.equals(currentLanguage)) {


                } // for

            } catch (JSONException jsonEx) {
                // Server error 500
                Toast.makeText(context, "JSONException: " + jsonEx.toString(), Toast.LENGTH_LONG).show();
            } //
        } // if(!(dataResult.equals("")) && !(dataResult.equals("[]"))) {





        askGetRecipesFromServer();
    } // answerGetRecipeFromServer

}
