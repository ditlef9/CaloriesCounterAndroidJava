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

public class SynchronizeIFoodDiaryGoals {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";
    String apiURL = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword = "w7Vdwenb";

    /* Synchronize variables */
    private final Context context;
    int currentUserId = 0;
    int currentLastIdLocal = 0;
    int currentLastIdOnServer = 0;

    public SynchronizeIFoodDiaryGoals(Context context) {
        this.context = context;
    }

    /*- Update last synchronized date ------------------------------------------------------------ */
    public void updateLastSynchronizedDate() {
        /* Database */
        DBAdapter db = new DBAdapter(context);
        db.open();



        // Fetch data
        String q = "SELECT _id, name, last_on_local, synchronized_week FROM synchronize WHERE name='food_diary_goals'";
        Cursor cursorWorkoutPlansWeekly = db.rawQuery(q);
        int size = cursorWorkoutPlansWeekly.getCount();
        if (size == 0) {
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'food_diary_goals')";
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
                "WHERE name='food_diary_goals'";
        try {
            db.rawQuery(q);
        } catch (SQLException e) {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeIFoodDiaryGoals", "updateLastSynchronizedDate", "error", e.getMessage());
        }
        db.close();

        // Get my user id
        getMyUserId();

        // Get number of exercises equipments on server
        askFoodDiaryGoals();
    } // updateLastSynchronizedDate

    private void getMyUserId() {
        DBAdapter db = new DBAdapter(context);
        db.open();

        String q = "SELECT _id, user_id FROM users WHERE _id='1'";
        Cursor userCursor = db.rawQuery(q);
        currentUserId = userCursor.getInt(1);

        db.close();

    }

    /*- Get last workout plans weekly id from server --------------------------------------- */
    private void askFoodDiaryGoals() {

        // Call HTTP request
        String url = apiURL + "/get_food_diary_goals.php?user_id=" + currentUserId;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(context, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerFoodDiaryGoals();
            }
        });
        task.execute();

    } // askFoodCategories
    public void answerFoodDiaryGoals(){

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








                    String goalIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_id")));
                    String goalUserIdSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_user_id")));
                    String goalCurrentWeightSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_weight")));
                    String goalCurrentFatPercentageSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_fat_percentage")));
                    String goalTargetWeightSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_weight")));
                    String goalTargetFatPercentageSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_fat_percentage")));
                    String goalIWantToSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_i_want_to")));
                    String goalWeeklyGoalSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_weekly_goal")));
                    String goalDateSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_date")));
                    String goalActivityLevelSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_activity_level")));
                    String goalCurrentBmiSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_bmi")));
                    String goalTargetBmiSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_bmi")));
                    String goalCurrentBmrCaloriesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_bmr_calories")));
                    String goalCurrentBmrFatSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_bmr_fat")));
                    String goalCurrentBmrCarbsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_bmr_carbs")));
                    String goalCurrentBmrProteinsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_bmr_proteins")));
                    String goalCurrentSedentaryCaloriesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_sedentary_calories")));
                    String goalCurrentSedentaryFatSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_sedentary_fat")));
                    String goalCurrentSedentaryCarbsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_sedentary_carbs")));
                    String goalCurrentSedentaryProteinsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_sedentary_proteins")));
                    String goalCurrentWithActivityCaloriesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_with_activity_calories")));
                    String goalCurrentWithActivityFatSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_with_activity_fat")));
                    String goalCurrentWithActivityCarbsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_with_activity_carbs")));
                    String goalCurrentWithActivityProteinsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_current_with_activity_proteins")));
                    String goalTargetBmrCaloriesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_bmr_calories")));
                    String goalTargetBmrFatSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_bmr_fat")));
                    String goalTargetBmrCarbsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_bmr_carbs")));
                    String goalTargetBmrProteinsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_bmr_proteins")));
                    String goalTargetSedentaryCaloriesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_sedentary_calories")));
                    String goalTargetSedentaryFatSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_sedentary_fat")));
                    String goalTargetSedentaryCarbsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_sedentary_carbs")));
                    String goalTargetSedentaryProteinsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_sedentary_proteins")));
                    String goalTargetWithActivityCaloriesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_with_activity_calories")));
                    String goalTargetWithActivityFatSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_with_activity_fat")));
                    String goalTargetWithActivityCarbsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_with_activity_carbs")));
                    String goalTargetWithActivityProteinsSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_target_with_activity_proteins")));
                    String goalUpdatedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_updated")));
                    String goalSynchronizedSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_synchronized")));
                    String goalNotesSQL = db.quoteSmart(new OutputString().outputHTML(object.getString("goal_notes")));



                    // Category
                    String q = "SELECT _id FROM food_diary_goals WHERE goal_id=" + goalIdSQL;
                    Cursor checkCursor = db.rawQuery(q);
                    if (checkCursor.getCount() == 0) {
                        // Insert
                        q = "INSERT INTO food_diary_goals (_id, goal_id, goal_user_id, " +
                                "goal_current_weight, goal_current_fat_percentage, goal_target_weight, " +
                                "goal_target_fat_percentage, goal_i_want_to, goal_weekly_goal, " +
                                "goal_date, goal_activity_level, goal_current_bmi, " +
                                "goal_target_bmi, goal_current_bmr_calories, goal_current_bmr_fat, " +
                                "goal_current_bmr_carbs, goal_current_bmr_proteins, goal_current_sedentary_calories, " +
                                "goal_current_sedentary_fat, goal_current_sedentary_carbs, goal_current_sedentary_proteins, " +
                                "goal_current_with_activity_calories, goal_current_with_activity_fat, goal_current_with_activity_carbs, " +
                                "goal_current_with_activity_proteins, goal_target_bmr_calories, goal_target_bmr_fat, " +
                                "goal_target_bmr_carbs, goal_target_bmr_proteins, goal_target_sedentary_calories, " +
                                "goal_target_sedentary_fat, goal_target_sedentary_carbs, goal_target_sedentary_proteins, " +
                                "goal_target_with_activity_calories, goal_target_with_activity_fat, goal_target_with_activity_carbs, " +
                                "goal_target_with_activity_proteins, goal_updated, goal_synchronized, " +
                                "goal_notes) " +
                                "VALUES ("
                                + "NULL, "
                                + goalIdSQL + ", "
                                + goalUserIdSQL + ", "
                                + goalCurrentWeightSQL + ", "
                                + goalCurrentFatPercentageSQL + ", "
                                + goalTargetWeightSQL + ", "
                                + goalTargetFatPercentageSQL + ", "
                                + goalIWantToSQL + ", "
                                + goalWeeklyGoalSQL + ", "
                                + goalDateSQL + ", "
                                + goalActivityLevelSQL + ", "
                                + goalCurrentBmiSQL + ", "
                                + goalTargetBmiSQL + ", "
                                + goalCurrentBmrCaloriesSQL + ", "
                                + goalCurrentBmrFatSQL + ", "
                                + goalCurrentBmrCarbsSQL + ", "
                                + goalCurrentBmrProteinsSQL + ", "
                                + goalCurrentSedentaryCaloriesSQL + ", "
                                + goalCurrentSedentaryFatSQL + ", "
                                + goalCurrentSedentaryCarbsSQL + ", "
                                + goalCurrentSedentaryProteinsSQL + ", "
                                + goalCurrentWithActivityCaloriesSQL + ", " +
                                goalCurrentWithActivityFatSQL + ", " +
                                goalCurrentWithActivityCarbsSQL + ", " +
                                goalCurrentWithActivityProteinsSQL + ", " +
                                goalTargetBmrCaloriesSQL + ", " +
                                goalTargetBmrFatSQL + ", " +
                                goalTargetBmrCarbsSQL + ", " +
                                goalTargetBmrProteinsSQL + ", " +
                                goalTargetSedentaryCaloriesSQL + ", " +
                                goalTargetSedentaryFatSQL + ", " +
                                goalTargetSedentaryCarbsSQL + ", " +
                                goalTargetSedentaryProteinsSQL + ", " +
                                goalTargetWithActivityCaloriesSQL + ", " +
                                goalTargetWithActivityFatSQL + ", " +
                                goalTargetWithActivityCarbsSQL + ", " +
                                goalTargetWithActivityProteinsSQL + ", " +
                                goalUpdatedSQL + ", " +
                                goalSynchronizedSQL + ", " +
                                goalNotesSQL +
                                ")";
                        db.rawQuery(q);

                    } else {
                        // Update
                        q = "UPDATE food_diary_goals SET " +
                                "goal_user_id=" + goalUserIdSQL + ", " +
                                "goal_current_weight=" + goalCurrentWeightSQL + ", " +
                                "goal_current_fat_percentage=" + goalCurrentFatPercentageSQL + ", " +
                                "goal_target_weight=" + goalTargetWeightSQL + ", " +
                                "goal_target_fat_percentage=" + goalTargetFatPercentageSQL + ", " +
                                "goal_i_want_to=" + goalIWantToSQL + ", " +
                                "goal_weekly_goal=" + goalWeeklyGoalSQL + ", " +
                                "goal_date=" + goalDateSQL + ", " +
                                "goal_activity_level=" + goalActivityLevelSQL + ", " +
                                "goal_current_bmi=" + goalCurrentBmiSQL + ", " +
                                "goal_target_bmi=" + goalTargetBmiSQL + ", " +
                                "goal_current_bmr_calories=" + goalCurrentBmrCaloriesSQL + ", " +
                                "goal_current_bmr_fat=" + goalCurrentBmrFatSQL + ", " +
                                "goal_current_bmr_carbs=" + goalCurrentBmrCarbsSQL + ", " +
                                "goal_current_bmr_proteins=" + goalCurrentBmrProteinsSQL + ", " +
                                "goal_current_sedentary_calories=" + goalCurrentSedentaryCaloriesSQL + ", " +
                                "goal_current_sedentary_fat=" + goalCurrentSedentaryFatSQL + ", " +
                                "goal_current_sedentary_carbs=" + goalCurrentSedentaryCarbsSQL + ", " +
                                "goal_current_sedentary_proteins=" + goalCurrentSedentaryProteinsSQL + ", " +
                                "goal_current_with_activity_calories=" + goalCurrentWithActivityCaloriesSQL + ", " +
                                "goal_current_with_activity_fat=" + goalCurrentWithActivityFatSQL + ", " +
                                "goal_current_with_activity_carbs=" + goalCurrentWithActivityCarbsSQL + ", " +
                                "goal_current_with_activity_proteins=" + goalCurrentWithActivityProteinsSQL + ", " +
                                "goal_target_bmr_calories=" + goalTargetBmrCaloriesSQL + ", " +
                                "goal_target_bmr_fat=" + goalTargetBmrFatSQL + ", " +
                                "goal_target_bmr_carbs=" + goalTargetBmrCarbsSQL + ", " +
                                "goal_target_bmr_proteins=" + goalTargetBmrProteinsSQL + ", " +
                                "goal_target_sedentary_calories=" + goalTargetSedentaryCaloriesSQL + ", " +
                                "goal_target_sedentary_fat=" + goalTargetSedentaryFatSQL + ", " +
                                "goal_target_sedentary_carbs=" + goalTargetSedentaryCarbsSQL + ", " +
                                "goal_target_sedentary_proteins=" + goalTargetSedentaryProteinsSQL + ", " +
                                "goal_target_with_activity_calories=" + goalTargetWithActivityCaloriesSQL + ", " +
                                "goal_target_with_activity_fat=" + goalTargetWithActivityFatSQL + ", " +
                                "goal_target_with_activity_carbs=" + goalTargetWithActivityCarbsSQL + ", " +
                                "goal_target_with_activity_proteins=" + goalTargetWithActivityProteinsSQL + ", " +
                                "goal_updated=" + goalUpdatedSQL + ", " +
                                "goal_synchronized=" + goalSynchronizedSQL + ", " +
                                "goal_notes=" + goalNotesSQL + " " +
                                "WHERE goal_id=" + goalIdSQL;

                        db.rawQuery(q);

                    } // update



                } // for json data response
            } catch (JSONException e) {
                // Server error 500
                WriteToErrorLog log = new WriteToErrorLog(context);
                log.writeToErrorLog("SynchronizeIFoodDiaryGoals", "answerFoodDiaryGoals", "error", e.getMessage());
            } //

            // Database
            db.close();

        } else {
            WriteToErrorLog log = new WriteToErrorLog(context);
            log.writeToErrorLog("SynchronizeIFoodDiaryGoals", "answerFoodDiaryGoals", "error", "answerFoodDiaryGoals() Answer blank string");
        }

        // Finished
        SynchronizeJFoodDiaryLastUsed sync = new SynchronizeJFoodDiaryLastUsed(context);
        sync.updateLastSynchronizedDate();

    } // answerFoodDiaryGoals

}
