package net.frindex.caloriescounterandroidjava;
/**
 *
 * File: MainActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.food_diary_add.FoodDiaryAddRecentActivity;
import net.frindex.caloriescounterandroidjava.food_diary_edit_delete.FoodDiaryEntriesEditDeleteActivity;
import net.frindex.caloriescounterandroidjava.food_diary_goal.FoodDiaryGoalNewAHeightActivity;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;
import net.frindex.caloriescounterandroidjava.menu.MenuActivity;
import net.frindex.caloriescounterandroidjava.setup.SetupASelectLanguageActivity;
import net.frindex.caloriescounterandroidjava.synchronize_app.SynchronizeAFoodCategories;
import net.frindex.caloriescounterandroidjava.synchronize_user_data.SynchronizeIFoodDiaryGoals;
import net.frindex.caloriescounterandroidjava.user.UserLoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    /* Api variables */
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // My user
    int myUserId;
    String myUserAlias;
    String myUserEmail;
    String myUserPassword;
    String myUserLanguage;
    String myUserGender;
    int myUserHeight;
    String myUserMeasurement;
    String myUserDob;

    // Todays date
    String stringTodaysDate = "";
    LocalDate localDateTodaysDate;

    // My Goal
    int myGoal_Id;
    int myGoalId = 0;
    int myGoalUserId;
    int myGoalCurrentWeight;
    double myGoalCurrentFatPercentage;
    int myGoalTargetWeight;
    double myGoalTargetFatPercentage;
    String myGoalIWantTo;
    double myGoalWeeklyGoal;
    String myGoalDate;
    double myGoalActivityLevel;
    double myGoalCurrentBmi;
    double myGoalTargetBmi;
    int myGoalCurrentBmrCalories;
    int myGoalCurrentBmrFat;
    int myGoalCurrentBmrCarbs;
    int myGoalCurrentBmrProteins;
    int myGoalCurrentSedentaryCalories;
    int myGoalCurrentSedentaryFat;
    int myGoalCurrentSedentaryCarbs;
    int myGoalCurrentSedentaryProteins;
    int myGoalCurrentWithActivityCalories;
    int myGoalCurrentWithActivityFat;
    int myGoalCurrentWithActivityCarbs;
    int myGoalCurrentWithActivityProteins;
    int myGoalTargetBmrCalories;
    int myGoalTargetBmrFat;
    int myGoalTargetBmrCarbs;
    int myGoalTargetBmrProteins;
    int myGoalTargetSedentaryCalories;
    int myGoalTargetSedentaryFat;
    int myGoalTargetSedentaryCarbs;
    int myGoalTargetSedentaryProteins;
    int myGoalTargetWithActivityCalories;
    int myGoalTargetWithActivityFat;
    int myGoalTargetWithActivityCarbs;
    int myGoalTargetWithActivityProteins;
    String myGoalUpdated;
    String myGoalSynchronized;
    String myGoalNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If we come from another class then we may have the date we want to show in food diary
        Bundle b = getIntent().getExtras();
        if(b != null) {
            try {
                stringTodaysDate = b.getString("currentEntryDate");
            } catch (Exception e){

            }
        }

        /* Stetho */
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        // Setup run?
        checkIfLoggedIn();

        setContentView(R.layout.activity_main);


        // Synchronize
        if(myUserId != 0) {
            checkIfSynchronizationIsNeeded();
        }

        // Set todays date
        setTodaysDate();

        // Initialize home screen
        // Did I set goal?
        if(myUserId != 0) {
            initializeHome();
        }


        // Listeners
        listeners();

    }


    /*- On Create Options Menu ----------------------------------------------------------- */
    // The menu on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /*- On Options Item Selected --------------------------------------------------------- */
    // One of the toolbar icons was clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.navigationMenu) {
            Intent i = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    /* Check if Logged in ---------------------------------------------------------------- */
    public void checkIfLoggedIn() {
        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Database: Count rows in user table
        String q = "SELECT user_id, user_alias, user_email, user_password, user_language, user_gender, user_height, user_measurement, user_dob FROM users WHERE _id=1";
        Cursor cursorUser = db.rawQuery(q);
        int numberUsersRows = cursorUser.getCount();


        if (numberUsersRows  == 0) {
            // Not registered
            // Need to select language
            // Toast.makeText(this, "Setup", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, SetupASelectLanguageActivity.class);
            startActivity(i);
            finish();
        }
        else {

            myUserId = cursorUser.getInt(0);
            myUserAlias = cursorUser.getString(1);
            myUserEmail = cursorUser.getString(2);
            myUserPassword = cursorUser.getString(3);
            myUserLanguage = cursorUser.getString(4);
            myUserGender = cursorUser.getString(5);
            myUserHeight = cursorUser.getInt(6);
            myUserMeasurement = cursorUser.getString(7);
            myUserDob = cursorUser.getString(8);

            if (myUserAlias == null) {
                // I am not finished with my registration...
                Toast.makeText(this, "Please log in", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(i);
                finish();
            } else {
                if (myUserAlias.equals("")) {
                    Toast.makeText(this, "Please log in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MainActivity.this, UserLoginActivity.class);
                    startActivity(i);
                    finish();
                }

                // Toast
                // Toast.makeText(this, "Welcome " + myUserId, Toast.LENGTH_LONG).show();
                setLocale(myUserLanguage);
            }
        }

        // Database: close
        db.close();


    } // checkIfLoggedIn

    /*- Set locale ------------------------------------------------------------ */
    // Changes screen to specified language
    public void setLocale(String lang) {


        Locale locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        String locales = conf.getLocales().toString();
        conf.setLocale(locale);
        res.updateConfiguration(conf, dm);

        // Refresh
        // Intent refresh = new Intent(this, SignUp.class);
        //startActivity(refresh);

    }


    /*- Check synchronize -------------------------------------------------------- */
    public void checkIfSynchronizationIsNeeded(){
        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();


        // Fetch data
        String q = "SELECT _id, synchronized_week, synchronized_second FROM synchronize WHERE name='food_categories'";
        Cursor syncCursor = db.rawQuery(q);
        int size = syncCursor.getCount();
        String synchWeek = "0";
        String synchSecond = "0";
        if(size != 0) {
            synchWeek = syncCursor.getString(1);
            synchSecond = syncCursor.getString(2);
        }
        else{
            Toast.makeText(this, "Never synchronized before!", Toast.LENGTH_SHORT).show();
        }

        DateFormat dfw = new SimpleDateFormat("w");
        String datew = dfw.format(Calendar.getInstance().getTime());

        DateFormat dfss = new SimpleDateFormat("ss");
        String datess = dfss.format(Calendar.getInstance().getTime());


        if(!(datew.equals(synchWeek))){
            // Weekly synchronization needed
            // Truncate cache
            db.truncate("json_temp_data");

            Toast.makeText(this, "Starting weekly synchronization\nCurrent week: " + datew + ". Last synced week: " + synchWeek, Toast.LENGTH_SHORT).show();
            SynchronizeAFoodCategories synchronizeAFoodCategories = new SynchronizeAFoodCategories(this, myUserLanguage);
            synchronizeAFoodCategories.updateLastSynchronizedDate();

            SynchronizeIFoodDiaryGoals synchronizeIFoodDiaryGoals = new SynchronizeIFoodDiaryGoals(this);
            synchronizeIFoodDiaryGoals.updateLastSynchronizedDate();
        }

    } // checkIfSynchronizationIsNeeded

    public void setTodaysDate(){
        // Today
        DateFormat dfyyyy = new SimpleDateFormat("yyyy");
        DateFormat dfmm = new SimpleDateFormat("MM");
        DateFormat dfdd = new SimpleDateFormat("d");
        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String stringYear = dfyyyy.format(Calendar.getInstance().getTime());
        String stringMonth = dfmm.format(Calendar.getInstance().getTime());
        String stringDay = dfdd.format(Calendar.getInstance().getTime());
        String getDateNowOrFromVariable = "now";
        if(stringTodaysDate == null) {
            getDateNowOrFromVariable = "now";
        }
        else{
            if(stringTodaysDate.equals("")){
                getDateNowOrFromVariable = "now";
            }
            else {
                getDateNowOrFromVariable = "variable";
            }
        }

        if(getDateNowOrFromVariable.equals("now")){
            stringTodaysDate = dfyyyymmdd.format(Calendar.getInstance().getTime());
            localDateTodaysDate = LocalDate.now();
        }
        else{
            localDateTodaysDate = LocalDate.parse(stringTodaysDate);

            DateTimeFormatter dtfyyyy = DateTimeFormatter.ofPattern("yyyy");
            DateTimeFormatter dtfmm = DateTimeFormatter.ofPattern("MM");
            DateTimeFormatter dtfd = DateTimeFormatter.ofPattern("d");
            stringYear = localDateTodaysDate.format(dtfyyyy);
            stringMonth = localDateTodaysDate.format(dtfmm);
            stringDay = localDateTodaysDate.format(dtfd);
        }



        if(stringMonth.equals("01")){
            stringMonth = getString(R.string.january);
        }
        else if(stringMonth.equals("02")){
            stringMonth = getString(R.string.february);
        }
        else if(stringMonth.equals("03")){
            stringMonth = getString(R.string.march);
        }
        else if(stringMonth.equals("04")){
            stringMonth = getString(R.string.april);
        }
        else if(stringMonth.equals("05")){
            stringMonth = getString(R.string.may);
        }
        else if(stringMonth.equals("06")){
            stringMonth = getString(R.string.june);
        }
        else if(stringMonth.equals("07")){
            stringMonth = getString(R.string.july);
        }
        else if(stringMonth.equals("08")){
            stringMonth = getString(R.string.august);
        }
        else if(stringMonth.equals("09")){
            stringMonth = getString(R.string.september);
        }
        else if(stringMonth.equals("10")){
            stringMonth = getString(R.string.october);
        }
        else if(stringMonth.equals("11")){
            stringMonth = getString(R.string.november);
        }
        else if(stringMonth.equals("12")){
            stringMonth = getString(R.string.december);
        }
        TextView textViewDatePicked = findViewById(R.id.textViewDatePicked);
        textViewDatePicked.setText(stringDay + " " + stringMonth + " " + stringYear);

    } // setTodaysDate

    public void initializeHome(){

        DBAdapter db = new DBAdapter(this);
        db.open();

        // For testing
        // String testQ = "UPDATE users SET user_dob='' WHERE _id='1'";
        //  db.rawQuery(testQ);
        // db.truncate("food_diary_goals");


        String q = "SELECT _id, goal_id, goal_user_id, " +
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
                "goal_notes " +
                "FROM food_diary_goals WHERE goal_user_id=" + myUserId + " " +
                "ORDER BY _id DESC";
        Cursor goalCursor = db.rawQuery(q);

        int goalSize = goalCursor.getCount();
        if(goalSize == 0){
            // Toast.makeText(this, "Please set your goal", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, FoodDiaryGoalNewAHeightActivity.class);
            startActivity(i);
            finish();
        }
        else {
            myGoal_Id = goalCursor.getInt(0);
            myGoalId = goalCursor.getInt(1);
            myGoalUserId = goalCursor.getInt(2);
            myGoalCurrentWeight = goalCursor.getInt(3);
            myGoalCurrentFatPercentage = goalCursor.getInt(4);
            myGoalTargetWeight = goalCursor.getInt(5);
            myGoalTargetFatPercentage = goalCursor.getInt(6);
            myGoalIWantTo = goalCursor.getString(7);
            myGoalWeeklyGoal = goalCursor.getInt(8);
            myGoalDate = goalCursor.getString(9);
            myGoalActivityLevel = goalCursor.getInt(10);
            myGoalCurrentBmi = goalCursor.getInt(11);
            myGoalTargetBmi = goalCursor.getInt(12);
            myGoalCurrentBmrCalories = goalCursor.getInt(13);
            myGoalCurrentBmrFat = goalCursor.getInt(14);
            myGoalCurrentBmrCarbs = goalCursor.getInt(15);
            myGoalCurrentBmrProteins = goalCursor.getInt(16);
            myGoalCurrentSedentaryCalories = goalCursor.getInt(17);
            myGoalCurrentSedentaryFat = goalCursor.getInt(18);
            myGoalCurrentSedentaryCarbs = goalCursor.getInt(19);
            myGoalCurrentSedentaryProteins = goalCursor.getInt(20);
            myGoalCurrentWithActivityCalories = goalCursor.getInt(21);
            myGoalCurrentWithActivityFat = goalCursor.getInt(22);
            myGoalCurrentWithActivityCarbs = goalCursor.getInt(23);
            myGoalCurrentWithActivityProteins = goalCursor.getInt(24);
            myGoalTargetBmrCalories = goalCursor.getInt(25);
            myGoalTargetBmrFat = goalCursor.getInt(26);
            myGoalTargetBmrCarbs = goalCursor.getInt(27);
            myGoalTargetBmrProteins = goalCursor.getInt(28);
            myGoalTargetSedentaryCalories = goalCursor.getInt(29);
            myGoalTargetSedentaryFat = goalCursor.getInt(30);
            myGoalTargetSedentaryCarbs = goalCursor.getInt(31);
            myGoalTargetSedentaryProteins = goalCursor.getInt(32);
            myGoalTargetWithActivityCalories = goalCursor.getInt(33);
            myGoalTargetWithActivityFat = goalCursor.getInt(34);
            myGoalTargetWithActivityCarbs = goalCursor.getInt(35);
            myGoalTargetWithActivityProteins = goalCursor.getInt(36);
            myGoalUpdated = goalCursor.getString(37);
            myGoalSynchronized = goalCursor.getString(38);
            myGoalNotes = goalCursor.getString(39);



        } // goal found

        // Today
        String query = "SELECT _id, total_day_id, total_day_user_id, " +
                "total_day_date, total_day_consumed_energy, total_day_consumed_fat, " +
                "total_day_consumed_carb, total_day_consumed_protein, total_day_target_sedentary_energy, " +
                "total_day_target_sedentary_fat, total_day_target_sedentary_carb, total_day_target_sedentary_protein, " +
                "total_day_target_with_activity_energy, total_day_target_with_activity_fat, total_day_target_with_activity_carb, " +
                "total_day_target_with_activity_protein, total_day_diff_sedentary_energy, total_day_diff_sedentary_fat, " +
                "total_day_diff_sedentary_carb, total_day_diff_sedentary_protein, total_day_diff_with_activity_energy, " +
                "total_day_diff_with_activity_fat, total_day_diff_with_activity_carb, total_day_diff_with_activity_protein, " +
                "total_day_updated " +
                "FROM food_diary_totals_days WHERE total_day_date='" + stringTodaysDate + "'";
        Cursor cursorTotalsDay = db.rawQuery(query);
        int countTotalsDay = cursorTotalsDay.getCount();
        if(countTotalsDay == 0){
            // Insert
            DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateyyyyMMdddfhhmmss = dfhhmmss.format(Calendar.getInstance().getTime());

            String insert = "INSERT INTO food_diary_totals_days (_id, total_day_id, total_day_user_id, " +
                    "total_day_date, total_day_consumed_energy, total_day_consumed_fat, " +
                    "total_day_consumed_carb, total_day_consumed_protein, total_day_target_sedentary_energy, " +
                    "total_day_target_sedentary_fat, total_day_target_sedentary_carb, total_day_target_sedentary_protein, " +
                    "total_day_target_with_activity_energy, total_day_target_with_activity_fat, total_day_target_with_activity_carb, " +
                    "total_day_target_with_activity_protein, total_day_diff_sedentary_energy, total_day_diff_sedentary_fat, " +
                    "total_day_diff_sedentary_carb, total_day_diff_sedentary_protein, total_day_diff_with_activity_energy, " +
                    "total_day_diff_with_activity_fat, total_day_diff_with_activity_carb, total_day_diff_with_activity_protein, " +
                    "total_day_updated)  " +
                    "VALUES  " +
                    "(NULL, " +
                    "0, " +
                    myUserId + ", " +
                    "'" + stringTodaysDate + "', '0', '0', "  +
                    "'0', '0'," + myGoalTargetSedentaryCalories + ", " +
                    myGoalTargetSedentaryFat + ", " + myGoalTargetSedentaryCarbs + ", " + myGoalTargetSedentaryProteins + ", " +
                    myGoalTargetWithActivityCalories + ", " + myGoalTargetWithActivityFat + ", " + myGoalTargetWithActivityCarbs + ", " +
                    myGoalTargetWithActivityProteins + ", " + myGoalTargetSedentaryCalories + ", " + myGoalTargetSedentaryFat + ", " +
                    myGoalTargetSedentaryCarbs + ", " + myGoalTargetSedentaryProteins + ", " + myGoalTargetWithActivityCalories + ", " +
                    myGoalTargetWithActivityFat + ", " + myGoalTargetWithActivityCarbs + ", " + myGoalTargetWithActivityProteins + ", " +
                    "'" + dateyyyyMMdddfhhmmss + "'" +
                    ")";
            db.rawQuery(insert);
            cursorTotalsDay = db.rawQuery(query);

            sendNewFoodDiaryTotalsDaysToServer();
        } // today found

        int totalDay_id = cursorTotalsDay.getInt(0);
        int totalDayId = cursorTotalsDay.getInt(1);
        int totalDayUser_id = cursorTotalsDay.getInt(2);
        int totalDayDate = cursorTotalsDay.getInt(3);
        int totalDayConsumedEnergy = cursorTotalsDay.getInt(4);
        int totalDayConsumedFat = cursorTotalsDay.getInt(5);
        int totalDayConsumedCarb = cursorTotalsDay.getInt(6);
        int totalDayConsumedProtein = cursorTotalsDay.getInt(7);
        int totalDayTargetSedentaryEnergy = cursorTotalsDay.getInt(8);
        int totalDayTargetSedentaryFat = cursorTotalsDay.getInt(9);
        int totalDayTargetSedentaryCarb = cursorTotalsDay.getInt(10);
        int totalDayTargetSedentaryProtein = cursorTotalsDay.getInt(11);
        int totalDayTargetWithActivityEnergy = cursorTotalsDay.getInt(12);
        int totalDayTargetWithActivityFat = cursorTotalsDay.getInt(13);
        int totalDayTargetWithActivityCarb = cursorTotalsDay.getInt(14);
        int totalDayTargetWithActivityProtein = cursorTotalsDay.getInt(15);
        int totalDayDiffSedentaryEnergy = cursorTotalsDay.getInt(16);
        int totalDayDiffSedentaryFat = cursorTotalsDay.getInt(17);
        int totalDayDiffSedentaryCarb = cursorTotalsDay.getInt(18);
        int totalDayDiffSedentaryProtein = cursorTotalsDay.getInt(19);
        int totalDayDiffWithActivityEnergy = cursorTotalsDay.getInt(20);
        int totalDayDiffWithActivityFat = cursorTotalsDay.getInt(21);
        int totalDayDiffWithActivityCarb = cursorTotalsDay.getInt(22);
        int totalDayDiffWithActivityProtein = cursorTotalsDay.getInt(23);
        int totalDayUpdated = cursorTotalsDay.getInt(24);


        // Active goal
        TextView textViewActiveGoal = findViewById(R.id.textViewActiveGoal);
        textViewActiveGoal.setText(totalDayTargetWithActivityEnergy + "");

        // Active food (same as Sedentary food)
        TextView textVievFood = findViewById(R.id.textViewFood);
        textVievFood.setText(totalDayConsumedEnergy + "");

        // Active remaining
        TextView textViewActiveRemaining = findViewById(R.id.textViewActiveRemaining);
        if(totalDayDiffWithActivityEnergy == 0){
            textViewActiveRemaining.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        }
        else if(totalDayDiffWithActivityEnergy < 0){
            textViewActiveRemaining.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        }
        else if(totalDayDiffWithActivityEnergy > 0){
            textViewActiveRemaining.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        textViewActiveRemaining.setText(totalDayDiffWithActivityEnergy + "");

        // Sedentary goal
        TextView textViewSedentaryGoal = findViewById(R.id.textViewSedentaryGoal);
        textViewSedentaryGoal.setText(totalDayTargetSedentaryEnergy + "");

        // Sedentary remaining
        TextView textViewSedentaryRemaining = findViewById(R.id.textViewSedentaryRemaining);
        if(totalDayDiffSedentaryEnergy == 0){
            textViewSedentaryRemaining.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        }
        else if(totalDayDiffSedentaryEnergy < 0){
            textViewSedentaryRemaining.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        }
        else if(totalDayDiffSedentaryEnergy > 0){
            textViewSedentaryRemaining.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        textViewSedentaryRemaining.setText(totalDayDiffSedentaryEnergy + "");


        // Meals
        TextView textViewEnergyBreakfast = findViewById(R.id.textViewEnergyBreakfast);
        TextView textViewEnergyLunch = findViewById(R.id.textViewEnergyLunch);
        TextView textViewEnergyBeforeTraining = findViewById(R.id.textViewEnergyBeforeTraining);
        TextView textViewEnergyAfterTraining = findViewById(R.id.textViewEnergyAfterTraining);
        TextView textViewEnergyDinner = findViewById(R.id.textViewEnergyDinner);
        TextView textViewEnergySnacks = findViewById(R.id.textViewEnergySnacks);
        TextView textViewEnergySupper = findViewById(R.id.textViewEnergySupper);

        for(int x=0;x<7;x++) {
            // Find out how many calories I have eaten for this meal
            query = "SELECT _id, total_meal_id, total_meal_user_id, total_meal_date, " +
                    "total_meal_meal_id, total_meal_energy, total_meal_fat, " +
                    "total_meal_carb, total_meal_protein FROM food_diary_totals_meals " +
                    "WHERE total_meal_user_id=" + myUserId + " AND total_meal_date='" + stringTodaysDate + "' AND total_meal_meal_id='" + x + "'";
            Cursor cursorTotalMeal = db.rawQuery(query);
            // Toast.makeText(this, "query=" + query, Toast.LENGTH_LONG).show();
            if (cursorTotalMeal.getCount() != 0) {
                int totalMeal_Id = cursorTotalMeal.getInt(0);
                int totalMealId = cursorTotalMeal.getInt(1);
                int totalMealUserId = cursorTotalMeal.getInt(2);
                String totalMealDate = cursorTotalMeal.getString(3);
                int totalMealMealId = cursorTotalMeal.getInt(4);
                int totalMealEnergy = cursorTotalMeal.getInt(5);
                int totalMealFat = cursorTotalMeal.getInt(6);
                int totalMealCarb = cursorTotalMeal.getInt(7);
                int totalMealProtein = cursorTotalMeal.getInt(8);

                if (x == 0) {
                    textViewEnergyBreakfast.setText(String.valueOf(totalMealEnergy));
                } else if (x == 1) {
                    textViewEnergyLunch.setText(String.valueOf(totalMealEnergy));
                } else if (x == 2) {
                    textViewEnergyBeforeTraining.setText(String.valueOf(totalMealEnergy));
                } else if (x == 3) {
                    textViewEnergyAfterTraining.setText(String.valueOf(totalMealEnergy));
                } else if (x == 4) {
                    textViewEnergyDinner.setText(String.valueOf(totalMealEnergy));
                } else if (x == 5) {
                    textViewEnergySnacks.setText(String.valueOf(totalMealEnergy));
                } else if (x == 6) {
                    textViewEnergySupper.setText(String.valueOf(totalMealEnergy));
                }
            } // cursorTotalMeal.getCount() != 0)


            // Find entries for that meals
            query = "SELECT _id, entry_id, entry_user_id, " +
                    "entry_date, entry_meal_id, entry_food_id, " +
                    "entry_recipe_id, entry_name, entry_manufacturer_name, " +
                    "entry_serving_size, entry_serving_size_measurement, entry_energy_per_entry, " +
                    "entry_fat_per_entry, entry_carb_per_entry, entry_protein_per_entry, " +
                    "entry_text FROM food_diary_entires WHERE entry_user_id=" + myUserId + " " +
                    "AND entry_date='" + stringTodaysDate + "' " +
                    "AND entry_meal_id='" + x + "'";
            Cursor cursorEntries = db.rawQuery(query);
            for(int y=0;y<cursorEntries.getCount();y++) {
                int entry_Id = cursorEntries.getInt(0);
                int entryId = cursorEntries.getInt(1);
                int entryUserId = cursorEntries.getInt(2);
                String entryDate = cursorEntries.getString(3);
                int entryMealId = cursorEntries.getInt(4);
                int entryFoodId = cursorEntries.getInt(5);
                int entryRecipeId = cursorEntries.getInt(6);
                String entryName = cursorEntries.getString(7);
                String entryManufacturerName = cursorEntries.getString(8);
                String entryServingSize = cursorEntries.getString(9);
                String entryServingSizeMeasurement = cursorEntries.getString(10);
                int entryEnergyPerEntry = cursorEntries.getInt(11);
                int entryFatPerEntry = cursorEntries.getInt(12);
                int entryCarbPerEntry = cursorEntries.getInt(13);
                int entryProteinPerEntry = cursorEntries.getInt(14);
                String entryText = cursorEntries.getString(15);


                // Add table rows
                TableLayout tl = null;
                if (x == 0) {
                    tl = (TableLayout) findViewById(R.id.tableLayoutBreakfastItems); /* Find Tablelayout defined in main.xml */
                } else if (x == 1) {
                    tl = (TableLayout) findViewById(R.id.tableLayoutLunchItems); /* Find Tablelayout defined in main.xml */
                } else if (x == 2) {
                    tl = (TableLayout) findViewById(R.id.tableLayoutBeforeTrainingItems); /* Find Tablelayout defined in main.xml */
                } else if (x == 3) {
                    tl = (TableLayout) findViewById(R.id.tableLayoutAfterTrainingItems); /* Find Tablelayout defined in main.xml */
                } else if (x == 4) {
                    tl = (TableLayout) findViewById(R.id.tableLayoutDinnerItems); /* Find Tablelayout defined in main.xml */
                } else if (x == 5) {
                    tl = (TableLayout) findViewById(R.id.tableLayoutSnacksItems); /* Find Tablelayout defined in main.xml */
                } else {
                    tl = (TableLayout) findViewById(R.id.tableLayoutSupperItems); /* Find Tablelayout defined in main.xml */
                }
                TableRow tr1 = new TableRow(this); /* Create a new row to be added. */
                tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TableRow tr2 = new TableRow(this); /* Create a new row to be added. */
                tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


                // Table row: TextView Name
                TextView textViewName = new TextView(this); // Add textview
                textViewName.setText(entryName);
                textViewName.setTextSize(18);
                textViewName.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textViewName.setGravity(Gravity.CENTER_VERTICAL);
                TableRow.LayoutParams paramsName = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                paramsName.setMargins(4, 8, 0, 0); // Left, top, right, bottom
                tr1.addView(textViewName, paramsName);

                // Table row 1 layout params
                TableRow.LayoutParams paramsTableRow1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                paramsTableRow1.setMargins(0, 8, 10, 0); // Left, top, right, bottom


                // Table row 1: TextView Calories
                TextView textViewEnergy = new TextView(this); // Add textview
                textViewEnergy.setText(String.valueOf(entryEnergyPerEntry));
                textViewEnergy.setTextSize(18);
                textViewEnergy.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textViewEnergy.setGravity(Gravity.CENTER_VERTICAL);
                tr1.addView(textViewEnergy, paramsTableRow1);


                // Table row: TextView subLine
                TableRow.LayoutParams paramsSubLine = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                paramsSubLine.setMargins(4, 0, 0, 12); // Left, top, right, bottom

                TextView textViewSubLine = new TextView(this); // Add textview
                if (entryManufacturerName.equals("")){
                    textViewSubLine.setText(entryServingSize + " " + entryServingSizeMeasurement);
                }
                else{
                    textViewSubLine.setText(entryManufacturerName + ", " +
                            entryServingSize + " " + entryServingSizeMeasurement);
                }
                textViewSubLine.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
                tr2.addView(textViewSubLine, paramsSubLine);

                // Add row to table
                tl.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)); /* Add row to TableLayout. */
                tl.addView(tr2, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)); /* Add row to TableLayout. */


                // Add Listener
                final int finalEntry_Id = entry_Id;
                final int finalEntryId = entryId;
                tr1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        v.setBackgroundColor(getResources().getColor(android.R.color.background_light));

                        // Get the row text
                        TableRow row = (TableRow)v;
                        TextView tv = (TextView)row.getChildAt(0);

                        // Send it to edit
                        String tvText ="" + tv.getText(); // Lot VALUE Selected
                        rowOnClickEditDeleteEntriesLine(finalEntry_Id, finalEntryId);
                    }
                });

                // Add Listener
                tr2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        v.setBackgroundColor(getResources().getColor(android.R.color.background_light));

                        // Get the row text
                        TableRow row = (TableRow)v;
                        TextView tv = (TextView)row.getChildAt(0);

                        // Send it to edit
                        String tvText ="" + tv.getText(); // Lot VALUE Selected
                        rowOnClickEditDeleteEntriesLine(finalEntry_Id, finalEntryId);
                    }
                });


                cursorEntries.moveToNext();
            } // entries for meal


        } // for meals


        // Update details
        TextView textViewDetailsCaloriesText = findViewById(R.id.textViewDetailsCaloriesText);
        textViewDetailsCaloriesText.setText(String.valueOf(totalDayConsumedEnergy));

        TextView textViewDetailsFatText = findViewById(R.id.textViewDetailsFatText);
        textViewDetailsFatText.setText(String.valueOf(totalDayConsumedFat));

        TextView textViewDetailsCarbsText = findViewById(R.id.textViewDetailsCarbsText);
        textViewDetailsCarbsText.setText(String.valueOf(totalDayConsumedCarb));

        TextView textViewDetailsProteinText = findViewById(R.id.textViewDetailsProteinText);
        textViewDetailsProteinText.setText(String.valueOf(totalDayConsumedProtein));

        // Percentage
        double totalFatCarbProteins = totalDayConsumedFat+totalDayConsumedCarb+totalDayConsumedProtein;
        if(totalFatCarbProteins != 0){
            double doubleTotalConsumedFatPercentage = 0;
            double doubleTotalConsumedCarbsPercentage = 0;
            double doubleTotalConsumedProteinPercentage = 0;
            if(totalDayConsumedFat != 0){
                doubleTotalConsumedFatPercentage = (totalDayConsumedFat/totalFatCarbProteins)*100;


                TextView textViewDetailFatRight = findViewById(R.id.textViewDetailFatRight);
                textViewDetailFatRight.setText("(" + (int)doubleTotalConsumedFatPercentage + " %)");
            }
            if(totalDayConsumedCarb != 0){
                doubleTotalConsumedCarbsPercentage = (totalDayConsumedCarb/totalFatCarbProteins)*100;

                TextView textViewDetailCarbsRight = findViewById(R.id.textViewDetailCarbsRight);
                textViewDetailCarbsRight.setText("(" + (int)doubleTotalConsumedCarbsPercentage + " %)");
            }
            if(totalDayConsumedProtein != 0){
                doubleTotalConsumedProteinPercentage = (totalDayConsumedProtein/totalFatCarbProteins)*100;

                TextView textViewDetailProteinRight = findViewById(R.id.textViewDetailProteinRight);
                textViewDetailProteinRight.setText("(" + (int)doubleTotalConsumedProteinPercentage + " %)");
            }



            int w = 236, h = 236;
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
            int[] colors ={Color.parseColor("#c0504c"), Color.parseColor("#4f81bc"), Color.parseColor("#99bc58")};
            int slices[]={(int)doubleTotalConsumedFatPercentage,(int)doubleTotalConsumedCarbsPercentage,(int)doubleTotalConsumedProteinPercentage};
            drawPieChart(bmp, colors, slices);

            ImageView imageViewPie = findViewById(R.id.imageViewPie);
            imageViewPie.setImageBitmap(bmp);

        }



        db.close();

    } // initializeHome


    public static void drawPieChart(Bitmap bmp, int[] colors, int[] slices){
        //canvas to draw on it
        Canvas canvas = new Canvas(bmp);
        RectF box = new RectF(2, 2,bmp.getWidth()-2 , bmp.getHeight()-2);

        //get value for 100%
        int sum = 0;
        for (int slice : slices) {
            sum += slice;
        }
        //initalize painter
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float start = 0;
        //draw slices
        for(int i =0; i < slices.length; i++){
            paint.setColor(colors[i]);
            float angle;
            angle = ((360.0f / sum) * slices[i]);
            canvas.drawArc(box, start, angle, true, paint);
            start += angle;
        }
    } // drawPieChart

    /*- Row on click edit / delete entries line ------------------------------------------------- */
    private void rowOnClickEditDeleteEntriesLine(int inpEntry_Id, int inpEntryId) {
        // We want to edit / delete this line
        Intent i = new Intent(MainActivity.this, FoodDiaryEntriesEditDeleteActivity.class);
        i.putExtra("currentEntry_Id", inpEntry_Id);
        i.putExtra("currentEntryId", inpEntryId);
        startActivity(i);

    } // rowOnClickEditDeleteEntriesLine


    /*- Send new food diary total days to server ------------------------------------------------ */
    public void sendNewFoodDiaryTotalsDaysToServer(){

        // Send to PHP
        String url    = apiFooDiaryURL + "/post_new_food_diary_totals_days.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_total_day_date", stringTodaysDate);

        data.put("inp_total_day_target_sedentary_energy", String.valueOf(myGoalTargetSedentaryCalories));
        data.put("inp_total_day_target_sedentary_fat", String.valueOf(myGoalTargetSedentaryFat));
        data.put("inp_total_day_target_sedentary_carb", String.valueOf(myGoalTargetSedentaryCarbs));
        data.put("inp_total_day_target_sedentary_protein", String.valueOf(myGoalTargetSedentaryProteins));
        data.put("inp_total_day_target_with_activity_energy", String.valueOf(myGoalTargetWithActivityCalories));
        data.put("inp_total_day_target_with_activity_fat", String.valueOf(myGoalTargetWithActivityFat));
        data.put("inp_total_day_target_with_activity_carb", String.valueOf(myGoalTargetWithActivityCarbs));
        data.put("inp_total_day_target_with_activity_protein", String.valueOf(myGoalTargetWithActivityProteins));

        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                sendNewFoodDiaryTotalsDaysToServerAnswer();
            }
        });
        task.execute();
    } // sendNewFoodDiaryTotalsDaysToServer

    public void sendNewFoodDiaryTotalsDaysToServerAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        String idSQL = db.quoteSmart(dataResult);

        String q = "UPDATE food_diary_totals_days SET total_day_id=" + idSQL + " WHERE total_day_date='" + stringTodaysDate + "'";
        db.rawQuery(q);

        db.close();


        // Toast.makeText(this, "New day!" + dataResult, Toast.LENGTH_SHORT).show();
    } // sendNewFoodDiaryTotalsDaysToServerAnswer


    /*- Listeners ----------------------------------------------------------------------------- */
    private void listeners() {
        // Next and previous date
        ImageView imageViewPreviousDate = (ImageView)findViewById(R.id.imageViewPreviousDate);
        imageViewPreviousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewNextPreviousDateClicked(-1);
            }
        });
        ImageView imageViewNextDate = (ImageView)findViewById(R.id.imageViewNextDate);
        imageViewNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewNextPreviousDateClicked(1);
            }
        });

        // 0 == Breakfast
        ImageView imageViewAddBreakfast = (ImageView)findViewById(R.id.imageViewAddBreakfast);
        imageViewAddBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("breakfast", 0);
            }
        });
        TextView textViewHeadlineBreakfast = findViewById(R.id.textViewHeadlineBreakfast);
        textViewHeadlineBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("breakfast", 0);
            }
        });

        // 1 == Lunch
        ImageView imageViewAddLunch = (ImageView)findViewById(R.id.imageViewAddLunch);
        imageViewAddLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("lunch", 1);
            }
        });
        TextView textViewHeadlineLunch = findViewById(R.id.textViewHeadlineLunch);
        textViewHeadlineLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("lunch", 1);
            }
        });

        // 2 == Before training
        ImageView imageViewAddBeforeTraining = (ImageView)findViewById(R.id.imageViewAddBeforeTraining);
        imageViewAddBeforeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("before_training", 2);
            }
        });
        TextView textViewHeadlineBeforeTraining = findViewById(R.id.textViewHeadlineBeforeTraining);
        textViewHeadlineBeforeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("before_training", 2);
            }
        });

        // 3 == After training
        ImageView imageViewAddAfterTraining = (ImageView)findViewById(R.id.imageViewAddAfterTraining);
        imageViewAddAfterTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("after_training", 3);
            }
        });
        TextView textViewHeadlineAfterTraining = findViewById(R.id.textViewHeadlineAfterTraining);
        textViewHeadlineAfterTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("after_training", 3);
            }
        });

        // 4 == Dinner
        ImageView imageViewAddDinner = (ImageView)findViewById(R.id.imageViewAddDinner);
        imageViewAddDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("dinner", 4);
            }
        });
        TextView textViewHeadlineDinner = findViewById(R.id.textViewHeadlineDinner);
        textViewHeadlineDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("dinner", 4);
            }
        });

        // 5 = Snacks
        ImageView imageViewAddSnacks = (ImageView)findViewById(R.id.imageViewAddSnacks);
        imageViewAddSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("snacks", 5);
            }
        });
        TextView textViewHeadlineSnacks = findViewById(R.id.textViewHeadlineSnacks);
        textViewHeadlineSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("snacks", 5);
            }
        });

        // 6 = Supper
        ImageView imageViewAddSupper = (ImageView)findViewById(R.id.imageViewAddSupper);
        imageViewAddSupper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("supper", 6);
            }
        });
        TextView textViewHeadlineSupper = findViewById(R.id.textViewHeadlineSupper);
        textViewHeadlineSupper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFoodClicked("supper", 6);
            }
        });
    }

    public void imageViewAddFoodClicked(String mealName, int mealId){


        Intent i = new Intent(MainActivity.this, FoodDiaryAddRecentActivity.class);
        i.putExtra("currentEntryDate", stringTodaysDate);
        i.putExtra("currentEntryMealName", mealName);
        i.putExtra("currentEntryMealId", mealId);
        startActivity(i);

    } // imageViewAddFoodClicked

    public void imageViewNextPreviousDateClicked(int dateDiff){
        // Calculate new date
        if(dateDiff == 1) {
            localDateTodaysDate = localDateTodaysDate.plusDays(dateDiff);
        }
        else{
            localDateTodaysDate = localDateTodaysDate.minusDays(1);

        }
        DateTimeFormatter dfyyyy = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter dfmm = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter dfd = DateTimeFormatter.ofPattern("d");
        DateTimeFormatter dfyyyymmdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String stringYear = localDateTodaysDate.format(dfyyyy);
        String stringMonth = localDateTodaysDate.format(dfmm);
        String stringDay = localDateTodaysDate.format(dfd);
        stringTodaysDate = localDateTodaysDate.format(dfyyyymmdd);

        // Print date
        if(stringMonth.equals("01")){
            stringMonth = getString(R.string.january);
        }
        else if(stringMonth.equals("02")){
            stringMonth = getString(R.string.february);
        }
        else if(stringMonth.equals("03")){
            stringMonth = getString(R.string.march);
        }
        else if(stringMonth.equals("04")){
            stringMonth = getString(R.string.april);
        }
        else if(stringMonth.equals("05")){
            stringMonth = getString(R.string.may);
        }
        else if(stringMonth.equals("06")){
            stringMonth = getString(R.string.june);
        }
        else if(stringMonth.equals("07")){
            stringMonth = getString(R.string.july);
        }
        else if(stringMonth.equals("08")){
            stringMonth = getString(R.string.august);
        }
        else if(stringMonth.equals("09")){
            stringMonth = getString(R.string.september);
        }
        else if(stringMonth.equals("10")){
            stringMonth = getString(R.string.october);
        }
        else if(stringMonth.equals("11")){
            stringMonth = getString(R.string.november);
        }
        else if(stringMonth.equals("12")){
            stringMonth = getString(R.string.december);
        }
        TextView textViewDatePicked = findViewById(R.id.textViewDatePicked);
        textViewDatePicked.setText(stringDay + " " + stringMonth + " " + stringYear);

        // Remove tables
        TableLayout tableLayoutBreakfastItems = (TableLayout) findViewById(R.id.tableLayoutBreakfastItems);
        tableLayoutBreakfastItems.removeAllViews();

        TableLayout tableLayoutLunchItems = (TableLayout) findViewById(R.id.tableLayoutLunchItems);
        tableLayoutLunchItems.removeAllViews();

        TableLayout tableLayoutBeforeTrainingItems = (TableLayout) findViewById(R.id.tableLayoutBeforeTrainingItems);
        tableLayoutBeforeTrainingItems.removeAllViews();

        TableLayout tableLayoutAfterTrainingItems = (TableLayout) findViewById(R.id.tableLayoutAfterTrainingItems);
        tableLayoutAfterTrainingItems.removeAllViews();

        TableLayout tableLayoutDinnerItems = (TableLayout) findViewById(R.id.tableLayoutDinnerItems);
        tableLayoutDinnerItems.removeAllViews();

        TableLayout tableLayoutSnacksItems = (TableLayout) findViewById(R.id.tableLayoutSnacksItems);
        tableLayoutSnacksItems.removeAllViews();

        TableLayout tableLayoutSupperItems = (TableLayout) findViewById(R.id.tableLayoutSupperItems);
        tableLayoutSupperItems.removeAllViews();

        // Set 0 to calories
        TextView textViewEnergyBreakfast = findViewById(R.id.textViewEnergyBreakfast);
        textViewEnergyBreakfast.setText("0");

        TextView textViewEnergyLunch = findViewById(R.id.textViewEnergyLunch);
        textViewEnergyLunch.setText("0");

        TextView textViewEnergyBeforeTraining = findViewById(R.id.textViewEnergyBeforeTraining);
        textViewEnergyBeforeTraining.setText("0");

        TextView textViewEnergyAfterTraining = findViewById(R.id.textViewEnergyAfterTraining);
        textViewEnergyAfterTraining.setText("0");

        TextView textViewEnergyDinner = findViewById(R.id.textViewEnergyDinner);
        textViewEnergyDinner.setText("0");

        TextView textViewEnergySnacks = findViewById(R.id.textViewEnergySnacks);
        textViewEnergySnacks.setText("0");

        TextView textViewEnergySupper = findViewById(R.id.textViewEnergySupper);
        textViewEnergySupper.setText("0");

        // Redraw home
        initializeHome();
    }
}