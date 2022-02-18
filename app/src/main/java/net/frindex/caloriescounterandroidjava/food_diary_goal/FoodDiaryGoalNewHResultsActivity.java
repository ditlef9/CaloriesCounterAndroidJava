package net.frindex.caloriescounterandroidjava.food_diary_goal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * File: FoodAMainCategoriesActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class FoodDiaryGoalNewHResultsActivity extends AppCompatActivity {

    /* Api variables */
    String apiUsersURL      = "https://summerslim.codecourses.eu/users/api"; // Without ending slash
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

    // Step machine
    String currentStep = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_goal_new_hresults);


        // In case we want to edit a existing goal, then we fetch it from here:
        try {
            Bundle b = getIntent().getExtras();
            myGoalId = b.getInt("myGoalId");
        } catch (Exception e){

        }

        // Fetch profile and goal
        fetchProfileAndGoal();

        /* Step Machine */
        stepMachineController("");

        /* Initialize listeners */
        listeners();

        /* Hide edit text and buttons */
        hideEditTextAndButtons();
    }

    private void fetchProfileAndGoal() {

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // My Profile
        String q = "SELECT user_id, user_alias, user_email, user_password, user_language, user_gender, user_height, user_measurement, user_dob FROM users WHERE _id=1";
        Cursor cursorUser = db.rawQuery(q);

        myUserId = cursorUser.getInt(0);
        myUserAlias = cursorUser.getString(1);
        myUserEmail = cursorUser.getString(2);
        myUserPassword = cursorUser.getString(3);
        myUserLanguage = cursorUser.getString(4);
        myUserGender = cursorUser.getString(5);
        myUserHeight = cursorUser.getInt(6);
        myUserMeasurement = cursorUser.getString(7);
        myUserDob = cursorUser.getString(8);

        // My Goal
        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String dateyyyyMMdd = dfyyyymmdd.format(Calendar.getInstance().getTime());

        String query = "SELECT _id, goal_id, goal_user_id, goal_current_weight, goal_current_fat_percentage, goal_target_weight, goal_target_fat_percentage, goal_i_want_to, goal_weekly_goal, goal_date, goal_activity_level, goal_current_bmi, goal_target_bmi, goal_current_bmr_calories, goal_current_bmr_fat, goal_current_bmr_carbs, goal_current_bmr_proteins, goal_current_sedentary_calories, goal_current_sedentary_fat, goal_current_sedentary_carbs, goal_current_sedentary_proteins, goal_current_with_activity_calories, goal_current_with_activity_fat, goal_current_with_activity_carbs, goal_current_with_activity_proteins, goal_target_bmr_calories, goal_target_bmr_fat, goal_target_bmr_carbs, goal_target_bmr_proteins, goal_target_sedentary_calories, goal_target_sedentary_fat, goal_target_sedentary_carbs, goal_target_sedentary_proteins, goal_target_with_activity_calories, goal_target_with_activity_fat, goal_target_with_activity_carbs, goal_target_with_activity_proteins, goal_updated, goal_synchronized, goal_notes " +
                "FROM food_diary_goals WHERE goal_user_id='" + myUserId + " ";

        if(myGoalId == 0) {
            query = query  +"' AND goal_date='" + dateyyyyMMdd + "'";
        }
        else{
            query = query  +"' AND goal_id='" + myGoalId + "'";
        }
        Cursor goalCursor = db.rawQuery(query);

        int goalCursorCount = goalCursor.getCount();
        if(goalCursorCount == 0) {
            Toast.makeText(this, "Please set your goal! :)", Toast.LENGTH_LONG).show();
            Intent i = new Intent(FoodDiaryGoalNewHResultsActivity.this, FoodDiaryGoalNewAHeightActivity.class);
            startActivity(i);
            finish();
        }
        else{
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
        }

        // DB Close
        db.close();

    } // fetchProfileAndGoal


    private void listeners() {

        /* Next listener */
        Button buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepMachineController("go_to_previous_state");
            }
        });

        /* Next listener */
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepMachineController("go_to_next_state");
            }
        });

        /* Finish listener */
        Button buttonFinish = findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFinishClicked();
            }
        });

        /* Text A listener */
        TextView textViewSummaryAText = findViewById(R.id.textViewSummaryAText);
        textViewSummaryAText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSummaryXTextClicked("a");
            }
        });
        /* Text B listener */
        TextView textViewSummaryBText = findViewById(R.id.textViewSummaryBText);
        textViewSummaryBText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSummaryXTextClicked("b");
            }
        });
        /* Text C listener */
        TextView textViewSummaryCText = findViewById(R.id.textViewSummaryCText);
        textViewSummaryCText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSummaryXTextClicked("c");
            }
        });
        /* Text D listener */
        TextView textViewSummaryDText = findViewById(R.id.textViewSummaryDText);
        textViewSummaryDText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSummaryXTextClicked("d");
            }
        });

        /* Button A listener */
        Button buttonSummaryAText = findViewById(R.id.buttonSummaryAText);
        buttonSummaryAText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSummaryXTextClicked("a");
            }
        });

        /* Button B listener */
        Button buttonSummaryBText = findViewById(R.id.buttonSummaryBText);
        buttonSummaryBText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSummaryXTextClicked("b");
            }
        });

        /* Button D listener */
        Button buttonSummaryCText = findViewById(R.id.buttonSummaryCText);
        buttonSummaryCText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSummaryXTextClicked("c");
            }
        });

        /* Button D listener */
        Button buttonSummaryDText = findViewById(R.id.buttonSummaryDText);
        buttonSummaryDText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSummaryXTextClicked("d");
            }
        });

    }

    /*- Step Machine Controller ----------------------------------------------------------------- */
    public void stepMachineController(String whatToDo){
        if(currentStep.equals("") || currentStep.equals("a")){
            if(whatToDo.equals("go_to_previous_state")) {
                // We want to set activity level
                Intent i = new Intent(FoodDiaryGoalNewHResultsActivity.this, FoodDiaryGoalNewGActivityLevelActivity.class);
                startActivity(i);
                finish();
            }
            else if(whatToDo.equals("go_to_next_state")) {
                currentStep = "b";
                stepBCurrentBmr();
            }
            else {
                currentStep = "a";
                stepACurrentWeightTargetWeight();
            }
        }

        else if(currentStep.equals("b")){
            if(whatToDo.equals("go_to_previous_state")) {
                currentStep = "a";
                stepACurrentWeightTargetWeight();
            }
            else if(whatToDo.equals("go_to_next_state")) {
                currentStep = "c";
                stepCTargetBmr();
            }
        }

        else if(currentStep.equals("c")){
            if(whatToDo.equals("go_to_previous_state")) {
                currentStep = "b";
                stepBCurrentBmr();
            }
            else if(whatToDo.equals("go_to_next_state")) {
                currentStep = "stepD";
                buttonFinishClicked();
            }
        }

    } // stepMachineController

    public void buttonFinishClicked(){
        Intent i = new Intent(FoodDiaryGoalNewHResultsActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    } // buttonFinishClicked


    public void stepACurrentWeightTargetWeight(){
        // Title
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(getResources().getString(R.string.summary));

        // Current weight
        TextView textViewSummaryAText = findViewById(R.id.textViewSummaryAText);
        if(myUserMeasurement.equals("metric")) {
            textViewSummaryAText.setText(getResources().getString(R.string.current_weight) + " " + myGoalCurrentWeight +
                    " " +  getResources().getString(R.string.kg_lowercase));
        } else {
            double currentWeightLbs = myGoalCurrentWeight /0.45359237;

            textViewSummaryAText.setText(getResources().getString(R.string.current_weight) + " " + currentWeightLbs +
                    " " +  getResources().getString(R.string.lbs_lowercase));
        }

        // Current BMI
        TextView textViewSummaryBText = findViewById(R.id.textViewSummaryBText);
        textViewSummaryBText.setText(getResources().getString(R.string.current_bmi) + " " + myGoalCurrentBmi);

        // Target weight
        TextView textViewSummaryCText = findViewById(R.id.textViewSummaryCText);
        if(myUserMeasurement.equals("metric")) {
            textViewSummaryCText.setText(getResources().getString(R.string.target_weight) + " " + myGoalTargetWeight +
                    " " +  getResources().getString(R.string.kg_lowercase));
        } else {
            double targetWeightLbs = myGoalTargetWeight /0.45359237;

            textViewSummaryCText.setText(getResources().getString(R.string.target_weight) + " " + targetWeightLbs +
                    " " +  getResources().getString(R.string.lbs_lowercase));
        }

        // Target BMI
        TextView textViewSummaryDText = findViewById(R.id.textViewSummaryDText);
        textViewSummaryDText.setText(getResources().getString(R.string.target_bmi) + " " + myGoalTargetBmi);



    } // stepACurrentWeightTargetWeight

    public void stepBCurrentBmr(){
        // Title
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(getResources().getString(R.string.this_is_what_you_can_eat_with_out_gaining_or_loosing_weight));

        // Current BMR
        TextView textViewSummaryAText = findViewById(R.id.textViewSummaryAText);
        textViewSummaryAText.setText(getResources().getString(R.string.bmr) + " " + myGoalCurrentBmrCalories + " " + getResources().getString(R.string.calories_lowercase));

        // Current Sedentary
        TextView textViewSummaryBText = findViewById(R.id.textViewSummaryBText);
        textViewSummaryBText.setText(getResources().getString(R.string.sedentary) + " " + myGoalCurrentSedentaryCalories + " " + getResources().getString(R.string.calories_lowercase));

        // Current Active
        TextView textViewSummaryCText = findViewById(R.id.textViewSummaryCText);
        textViewSummaryCText.setText(getResources().getString(R.string.active) + " " + myGoalCurrentWithActivityCalories + " " + getResources().getString(R.string.calories_lowercase));

        // Text D
        TextView textViewSummaryDText = findViewById(R.id.textViewSummaryDText);
        textViewSummaryDText.setText(getResources().getString(R.string.you_can_click_on_a_number_to_change_it));


    } // stepBCurrentBmr

    public void stepCTargetBmr(){

        // Title
        int weightDiff = myGoalCurrentWeight-myGoalTargetWeight;
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        if(weightDiff > 0){
            textViewHeadline.setText(getResources().getString(R.string.this_is_what_you_can_eat_to_loose_weight));
        }
        else if(weightDiff < 0){
            textViewHeadline.setText(getResources().getString(R.string.this_is_what_you_can_eat_to_gain_weight));
        }
        else{
            textViewHeadline.setText(getResources().getString(R.string.this_is_what_you_can_eat_with_out_gaining_or_loosing_weight));
        }


        // Target BMR
        TextView textViewSummaryAText = findViewById(R.id.textViewSummaryAText);
        textViewSummaryAText.setText(getResources().getString(R.string.bmr) + " " + myGoalTargetBmrCalories + " " + getResources().getString(R.string.calories_lowercase));

        // Target Sedentary
        TextView textViewSummaryBText = findViewById(R.id.textViewSummaryBText);
        textViewSummaryBText.setText(getResources().getString(R.string.sedentary) + " " + myGoalTargetSedentaryCalories + " " + getResources().getString(R.string.calories_lowercase));

        // Target Active
        TextView textViewSummaryCText = findViewById(R.id.textViewSummaryCText);
        textViewSummaryCText.setText(getResources().getString(R.string.active) + " " + myGoalTargetWithActivityCalories + " " + getResources().getString(R.string.calories_lowercase));

        // Text D
        TextView textViewSummaryDText = findViewById(R.id.textViewSummaryDText);
        textViewSummaryDText.setText(getResources().getString(R.string.you_can_click_on_a_number_to_change_it));


    } // stepCTargetBmr

    /*- Hide edit text and buttons -------------------------------------------------------------- */
    public void hideEditTextAndButtons(){

        EditText editTextSummaryAText = findViewById(R.id.editTextSummaryAText);
        editTextSummaryAText.setVisibility(View.GONE);

        EditText editTextSummaryBText = findViewById(R.id.editTextSummaryBText);
        editTextSummaryBText.setVisibility(View.GONE);

        EditText editTextSummaryCText = findViewById(R.id.editTextSummaryCText);
        editTextSummaryCText.setVisibility(View.GONE);

        EditText editTextSummaryDText = findViewById(R.id.editTextSummaryDText);
        editTextSummaryDText.setVisibility(View.GONE);


        Button buttonSummaryAText = findViewById(R.id.buttonSummaryAText);
        buttonSummaryAText.setVisibility(View.GONE);

        Button buttonSummaryBText = findViewById(R.id.buttonSummaryBText);
        buttonSummaryBText.setVisibility(View.GONE);

        Button buttonSummaryCText = findViewById(R.id.buttonSummaryCText);
        buttonSummaryCText.setVisibility(View.GONE);

        Button buttonSummaryDText = findViewById(R.id.buttonSummaryDText);
        buttonSummaryDText.setVisibility(View.GONE);
    }

    /*- Text View X text Clicked ---------------------------------------------------------------- */
    /* We want to change a number */
    public void textViewSummaryXTextClicked(String letterClicked){
        // View Edit text and button
        TextView textViewSummaryAText = findViewById(R.id.textViewSummaryAText);
        EditText editTextSummaryAText = findViewById(R.id.editTextSummaryAText);
        Button buttonSummaryAText = findViewById(R.id.buttonSummaryAText);

        TextView textViewSummaryBText = findViewById(R.id.textViewSummaryBText);
        EditText editTextSummaryBText = findViewById(R.id.editTextSummaryBText);
        Button buttonSummaryBText = findViewById(R.id.buttonSummaryBText);

        TextView textViewSummaryCText = findViewById(R.id.textViewSummaryCText);
        EditText editTextSummaryCText = findViewById(R.id.editTextSummaryCText);
        Button buttonSummaryCText = findViewById(R.id.buttonSummaryCText);

        TextView textViewSummaryDText = findViewById(R.id.textViewSummaryDText);
        EditText editTextSummaryDText = findViewById(R.id.editTextSummaryDText);
        Button buttonSummaryDText = findViewById(R.id.buttonSummaryDText);

        if(letterClicked.equals("a")){
            textViewSummaryAText.setVisibility(View.GONE);
            editTextSummaryAText.setVisibility(View.VISIBLE);
            buttonSummaryAText.setVisibility(View.VISIBLE);
        }
        else if(letterClicked.equals("b")){
            if(currentStep.equals("") || currentStep.equals("a")) {
                Toast.makeText(this, "Sorry, you cannot change the current BMI manually", Toast.LENGTH_LONG).show();
            }
            else {
                textViewSummaryBText.setVisibility(View.GONE);
                editTextSummaryBText.setVisibility(View.VISIBLE);
                buttonSummaryBText.setVisibility(View.VISIBLE);
            }
        }
        else if(letterClicked.equals("c")){
            textViewSummaryCText.setVisibility(View.GONE);
            editTextSummaryCText.setVisibility(View.VISIBLE);
            buttonSummaryCText.setVisibility(View.VISIBLE);
        }
        else if(letterClicked.equals("d")){
            if(currentStep.equals("") || currentStep.equals("a")) {
                Toast.makeText(this, "Sorry, you cannot change the target BMI manually", Toast.LENGTH_LONG).show();
            }
            else if(currentStep.equals("b")) {
                Toast.makeText(this, "Hey, that tickles ;)", Toast.LENGTH_LONG).show();
            }
            else if(currentStep.equals("c")) {
                Toast.makeText(this, "Lets do this!", Toast.LENGTH_LONG).show();
            }
            else {
                textViewSummaryDText.setVisibility(View.GONE);
                editTextSummaryDText.setVisibility(View.VISIBLE);
                buttonSummaryDText.setVisibility(View.VISIBLE);
            }
        }

        // Content
        if(currentStep.equals("") || currentStep.equals("a")){
            // Current weight
            editTextSummaryAText.setText(myGoalCurrentWeight + "");

            // Target weight
            editTextSummaryCText.setText(myGoalTargetWeight + "");
        } // step a
        else if(currentStep.equals("b")){
            // Current BMR Calories
            editTextSummaryAText.setText(myGoalCurrentBmrCalories + "");

            // Current Sedentary Calories
            editTextSummaryBText.setText(myGoalCurrentSedentaryCalories + "");

            // Current Active Calories
            editTextSummaryCText.setText(myGoalCurrentWithActivityCalories + "");

        } // step b
        else if(currentStep.equals("c")){
            // Target BMR Calories
            editTextSummaryAText.setText(myGoalTargetBmrCalories + "");

            // Target Sedentary Calories
            editTextSummaryBText.setText(myGoalTargetSedentaryCalories + "");

            // Target Active Calories
            editTextSummaryCText.setText(myGoalTargetWithActivityCalories + "");

        } // step c
    } // textViewSummaryXTextClicked


    /*- Button Summary X Text Clicked ---------------------------------------------------------- */
    public void buttonSummaryXTextClicked(String letterClicked){

        // View Edit text and button
        TextView textViewSummaryAText = findViewById(R.id.textViewSummaryAText);
        EditText editTextSummaryAText = findViewById(R.id.editTextSummaryAText);
        Button buttonSummaryAText = findViewById(R.id.buttonSummaryAText);
        String stringSummaryAText = "";
        double doubleSummaryAText = 0.0;
        int intSummaryAText = 0;

        TextView textViewSummaryBText = findViewById(R.id.textViewSummaryBText);
        EditText editTextSummaryBText = findViewById(R.id.editTextSummaryBText);
        Button buttonSummaryBText = findViewById(R.id.buttonSummaryBText);
        String stringSummaryBText = "";
        double doubleSummaryBText = 0.0;
        int intSummaryBText = 0;

        TextView textViewSummaryCText = findViewById(R.id.textViewSummaryCText);
        EditText editTextSummaryCText = findViewById(R.id.editTextSummaryCText);
        Button buttonSummaryCText = findViewById(R.id.buttonSummaryCText);
        String stringSummaryCText = "";
        double doubleSummaryCText = 0.0;
        int intSummaryCText = 0;

        TextView textViewSummaryDText = findViewById(R.id.textViewSummaryDText);
        EditText editTextSummaryDText = findViewById(R.id.editTextSummaryDText);
        Button buttonSummaryDText = findViewById(R.id.buttonSummaryDText);
        String stringSummaryDText = "";
        double doubleSummaryDText = 0.0;
        int intSummaryDText = 0;


        if(letterClicked.equals("a")){
            textViewSummaryAText.setVisibility(View.VISIBLE);
            editTextSummaryAText.setVisibility(View.GONE);
            buttonSummaryAText.setVisibility(View.GONE);

            stringSummaryAText = editTextSummaryAText.getText().toString();

            try {
                doubleSummaryAText = Double.parseDouble(stringSummaryAText);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Text A: Could not parse string to double " + nfe, Toast.LENGTH_SHORT).show();
            }


            try {
                intSummaryAText = Integer.parseInt(stringSummaryAText);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Text A: Could not parse string to int " + nfe, Toast.LENGTH_SHORT).show();
            }


        } // letter a
        else if(letterClicked.equals("b")){
            textViewSummaryBText.setVisibility(View.VISIBLE);
            editTextSummaryBText.setVisibility(View.GONE);
            buttonSummaryBText.setVisibility(View.GONE);

            stringSummaryBText = editTextSummaryBText.getText().toString();

            try {
                doubleSummaryBText = Double.parseDouble(stringSummaryBText);
            } catch (NumberFormatException nfe) {
                Toast.makeText(this, "Text B: Could not parse string to double " + nfe, Toast.LENGTH_SHORT).show();
            }


            try {
                intSummaryBText = Integer.parseInt(stringSummaryBText);
            } catch (NumberFormatException nfe) {
                Toast.makeText(this, "Text B: Could not parse string to int " + nfe, Toast.LENGTH_SHORT).show();
            }

        } // letter b
        else if(letterClicked.equals("c")){
            textViewSummaryCText.setVisibility(View.VISIBLE);
            editTextSummaryCText.setVisibility(View.GONE);
            buttonSummaryCText.setVisibility(View.GONE);

            stringSummaryCText = editTextSummaryCText.getText().toString();

            try {
                doubleSummaryCText = Double.parseDouble(stringSummaryCText);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Text C: Could not parse string to double " + nfe, Toast.LENGTH_SHORT).show();
            }

            try {
                intSummaryCText = Integer.parseInt(stringSummaryCText);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Text C: Could not parse string to int " + nfe, Toast.LENGTH_SHORT).show();
            }
        } // letter c
        else if(letterClicked.equals("d")){
            textViewSummaryDText.setVisibility(View.VISIBLE);
            editTextSummaryDText.setVisibility(View.GONE);
            buttonSummaryDText.setVisibility(View.GONE);

            stringSummaryDText = editTextSummaryDText.getText().toString();

            try {
                doubleSummaryDText = Double.parseDouble(stringSummaryDText);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Text D: Could not parse string to double " + nfe, Toast.LENGTH_SHORT).show();
            }

            try {
                intSummaryDText = Integer.parseInt(stringSummaryDText);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Text D: Could not parse string to int " + nfe, Toast.LENGTH_SHORT).show();
            }
        } // letter d

        // Update SQL
        DBAdapter db = new DBAdapter(this);
        db.open();
        if(currentStep.equals("") || currentStep.equals("a")){
            if(letterClicked.equals("a")){
                // Current weight
                if(myUserMeasurement.equals("imperial")){
                    doubleSummaryAText = doubleSummaryAText*0.45359237;
                    intSummaryAText = (int)doubleSummaryAText;
                }

                // Calculate current BMI
                // BMI = m/h^2
                double doubleHeightCm = 0;
                try {
                    doubleHeightCm = Double.valueOf(myUserHeight);
                }
                catch(NumberFormatException nfe) {
                    Toast.makeText(this, "My user height is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
                }


                double heightMeter = doubleHeightCm/100;
                double heightSquared = heightMeter*heightMeter;
                double doubleBmi = 0;
                if(heightSquared != 0){
                    doubleBmi = doubleSummaryAText/heightSquared;
                }
                String stringBmi = doubleBmi + "";
                String stringBmiSQL = db.quoteSmart(stringBmi);

                // SQL
                String q = "UPDATE food_diary_goals SET goal_current_weight=" +  intSummaryAText + ", goal_current_bmi=" + stringBmiSQL + " WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated current weight", Toast.LENGTH_SHORT).show();
            } // step a - letter a
            else if(letterClicked.equals("c")){
                // Target weight
                if(myUserMeasurement.equals("imperial")){
                    doubleSummaryCText = doubleSummaryCText*0.45359237;
                    intSummaryCText = (int)doubleSummaryCText;
                }

                // Calculate current BMI
                // BMI = m/h^2
                double doubleHeightCm = 0;
                try {
                    doubleHeightCm = Double.valueOf(myUserHeight);
                }
                catch(NumberFormatException nfe) {
                    Toast.makeText(this, "My user height is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
                }


                double heightMeter = doubleHeightCm/100;
                double heightSquared = heightMeter*heightMeter;
                double doubleTargetBmi = 0;
                if(heightSquared != 0){
                    doubleTargetBmi = doubleSummaryCText/heightSquared;
                }
                String stringTargetBmi = doubleTargetBmi + "";
                String stringTargetBmiQL = db.quoteSmart(stringTargetBmi);

                // SQL
                String q = "UPDATE food_diary_goals SET goal_target_weight=" +  intSummaryCText + ", goal_target_bmi=" + stringTargetBmiQL + " WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated target weight", Toast.LENGTH_SHORT).show();
            } // step a - letter c

            // Reload step A
            fetchProfileAndGoal();
            stepACurrentWeightTargetWeight();
        } // step a
        else if(currentStep.equals("b")){
            if(letterClicked.equals("a")){
                // Current BMR

                double doubleBmrFat = (doubleSummaryAText*13)/100;
                int intBmrFat = (int)doubleBmrFat;

                double doubleBmrCarbs = (doubleSummaryAText*44)/100;
                int intBmrCarbs = (int)doubleBmrCarbs;

                double doubleBmrProtein = (doubleSummaryAText*43)/100;
                int intBmrProtein = (int)doubleBmrProtein;

                // SQL
                String q = "UPDATE food_diary_goals SET " +
                        "goal_current_bmr_calories=" + intSummaryAText + ", " +
                        "goal_current_bmr_fat=" + intBmrFat + ", " +
                        "goal_current_bmr_carbs=" + intBmrCarbs + ", " +
                        "goal_current_bmr_proteins=" + intBmrProtein + " " +
                        "WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated current BMR", Toast.LENGTH_SHORT).show();
            } // step b - letter a

            else if(letterClicked.equals("b")){
                // Sedentary BMR


                double doubleSedentaryFat = (doubleSummaryBText*13)/100;
                int intSedentaryFat = (int)doubleSedentaryFat;

                double doubleSedentaryCarbs = (doubleSummaryBText*44)/100;
                int intSedentaryCarbs = (int)doubleSedentaryCarbs;

                double doubleSedentaryProtein = (doubleSummaryBText*43)/100;
                int intSedentaryProtein = (int)doubleSedentaryProtein;

                // SQL
                String q = "UPDATE food_diary_goals SET " +
                        "goal_current_sedentary_calories=" + intSummaryBText + ", " +
                        "goal_current_sedentary_fat=" + intSedentaryFat + ", " +
                        "goal_current_sedentary_carbs=" + intSedentaryCarbs + ", " +
                        "goal_current_sedentary_proteins=" + intSedentaryProtein + " " +
                        "WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated current sedentary", Toast.LENGTH_SHORT).show();
            } // step b - letter b
            else if(letterClicked.equals("c")){
                // Current Active

                double doubleWithActivityFat = (doubleSummaryCText*13)/100;
                int intWithActivityFat = (int)doubleWithActivityFat;

                double doubleWithActivityCarbs = (doubleSummaryCText*44)/100;
                int intWithActivityCarbs = (int)doubleWithActivityCarbs;

                double doubleWithActivityProtein = (doubleSummaryCText*43)/100;
                int intWithActivityProtein = (int)doubleWithActivityProtein;

                // SQL
                String q = "UPDATE food_diary_goals SET " +
                        "goal_current_with_activity_calories=" + intSummaryCText + ", " +
                        "goal_current_with_activity_fat=" + intWithActivityFat + ", " +
                        "goal_current_with_activity_carbs=" + intWithActivityCarbs + ", " +
                        "goal_current_with_activity_proteins=" + intWithActivityProtein + " " +
                        "WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated current active", Toast.LENGTH_SHORT).show();
            } // step b - letter c

            // Reload step B
            fetchProfileAndGoal();
            stepBCurrentBmr();
        }
        else if(currentStep.equals("c")){
            if(letterClicked.equals("a")){
                // Target BMR

                double doubleBmrFat = (doubleSummaryAText*13)/100;
                int intBmrFat = (int)doubleBmrFat;

                double doubleBmrCarbs = (doubleSummaryAText*44)/100;
                int intBmrCarbs = (int)doubleBmrCarbs;

                double doubleBmrProtein = (doubleSummaryAText*43)/100;
                int intBmrProtein = (int)doubleBmrProtein;

                // SQL
                String q = "UPDATE food_diary_goals SET " +
                        "goal_target_bmr_calories=" + intSummaryAText + ", " +
                        "goal_target_bmr_fat=" + intBmrFat + ", " +
                        "goal_target_bmr_carbs=" + intBmrCarbs + ", " +
                        "goal_target_bmr_proteins=" + intBmrProtein + " " +
                        "WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated target BMR", Toast.LENGTH_SHORT).show();
            } // step b - letter a

            else if(letterClicked.equals("b")){
                // Target BMR


                double doubleSedentaryFat = (doubleSummaryBText*13)/100;
                int intSedentaryFat = (int)doubleSedentaryFat;

                double doubleSedentaryCarbs = (doubleSummaryBText*44)/100;
                int intSedentaryCarbs = (int)doubleSedentaryCarbs;

                double doubleSedentaryProtein = (doubleSummaryBText*43)/100;
                int intSedentaryProtein = (int)doubleSedentaryProtein;

                // SQL
                String q = "UPDATE food_diary_goals SET " +
                        "goal_target_sedentary_calories=" + intSummaryBText + ", " +
                        "goal_target_sedentary_fat=" + intSedentaryFat + ", " +
                        "goal_target_sedentary_carbs=" + intSedentaryCarbs + ", " +
                        "goal_target_sedentary_proteins=" + intSedentaryProtein + " " +
                        "WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated target sedentary", Toast.LENGTH_SHORT).show();
            } // step b - letter b
            else if(letterClicked.equals("c")){
                // target Active

                double doubleWithActivityFat = (doubleSummaryCText*13)/100;
                int intWithActivityFat = (int)doubleWithActivityFat;

                double doubleWithActivityCarbs = (doubleSummaryCText*44)/100;
                int intWithActivityCarbs = (int)doubleWithActivityCarbs;

                double doubleWithActivityProtein = (doubleSummaryCText*43)/100;
                int intWithActivityProtein = (int)doubleWithActivityProtein;

                // SQL
                String q = "UPDATE food_diary_goals SET " +
                        "goal_target_with_activity_calories=" + intSummaryCText + ", " +
                        "goal_target_with_activity_fat=" + intWithActivityFat + ", " +
                        "goal_target_with_activity_carbs=" + intWithActivityCarbs + ", " +
                        "goal_target_with_activity_proteins=" + intWithActivityProtein + " " +
                        "WHERE goal_id=" + myGoalId;
                db.rawQuery(q);

                Toast.makeText(this, "Updated target active", Toast.LENGTH_SHORT).show();
            } // step b - letter c

            // Reload step C
            fetchProfileAndGoal();
            stepCTargetBmr();
        }
        // Close db
        db.close();


        // Send to server
        sendUpdatesToServer();
    } // buttonSummaryXTextClicked


    /*- Send update to server ------------------------------------------------------------------- */
    /* This updates everything */
    private void sendUpdatesToServer() {
        String url    = apiFooDiaryURL + "/post_user_update_food_diary_results.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_goal_id", String.valueOf(myGoalId));

        data.put("inp_goal_current_weight", String.valueOf(myGoalCurrentWeight));
        data.put("inp_goal_current_fat_percentage", String.valueOf(myGoalCurrentFatPercentage));

        data.put("inp_goal_target_weight", String.valueOf(myGoalTargetWeight));
        data.put("inp_goal_target_fat_percentage", String.valueOf(myGoalTargetFatPercentage));


        data.put("inp_goal_i_want_to", String.valueOf(myGoalIWantTo));
        data.put("inp_goal_weekly_goal", String.valueOf(myGoalWeeklyGoal));
        data.put("inp_goal_date", String.valueOf(myGoalDate));
        data.put("inp_goal_activity_level", String.valueOf(myGoalActivityLevel));
        data.put("inp_goal_current_bmi", String.valueOf(myGoalCurrentBmi));
        data.put("inp_goal_target_bmi", String.valueOf(myGoalTargetBmi));

        data.put("inp_goal_current_bmr_calories", String.valueOf(myGoalCurrentBmrCalories));
        data.put("inp_goal_current_bmr_fat", String.valueOf(myGoalCurrentBmrFat));
        data.put("inp_goal_current_bmr_carbs", String.valueOf(myGoalCurrentBmrCarbs));
        data.put("inp_goal_current_bmr_proteins", String.valueOf(myGoalCurrentBmrProteins));

        data.put("inp_goal_current_sedentary_calories", String.valueOf(myGoalCurrentSedentaryCalories));
        data.put("inp_goal_current_sedentary_fat", String.valueOf(myGoalCurrentSedentaryFat));
        data.put("inp_goal_current_sedentary_carbs", String.valueOf(myGoalCurrentSedentaryCarbs));
        data.put("inp_goal_current_sedentary_proteins", String.valueOf(myGoalCurrentSedentaryProteins));

        data.put("inp_goal_current_with_activity_calories", String.valueOf(myGoalCurrentWithActivityCalories));
        data.put("inp_goal_current_with_activity_fat", String.valueOf(myGoalCurrentWithActivityFat));
        data.put("inp_goal_current_with_activity_carbs", String.valueOf(myGoalCurrentWithActivityCarbs));
        data.put("inp_goal_current_with_activity_proteins", String.valueOf(myGoalCurrentWithActivityProteins));

        data.put("inp_goal_target_bmr_calories", String.valueOf(myGoalTargetBmrCalories));
        data.put("inp_goal_target_bmr_fat", String.valueOf(myGoalTargetBmrFat));
        data.put("inp_goal_target_bmr_carbs", String.valueOf(myGoalTargetBmrCarbs));
        data.put("inp_goal_target_bmr_proteins", String.valueOf(myGoalTargetBmrProteins));

        data.put("inp_goal_target_sedentary_calories", String.valueOf(myGoalTargetSedentaryCalories));
        data.put("inp_goal_target_sedentary_fat", String.valueOf(myGoalTargetSedentaryFat));
        data.put("inp_goal_target_sedentary_carbs", String.valueOf(myGoalTargetSedentaryCarbs));
        data.put("inp_goal_target_sedentary_proteins", String.valueOf(myGoalTargetSedentaryProteins));

        data.put("inp_goal_target_with_activity_calories", String.valueOf(myGoalTargetWithActivityCalories));
        data.put("inp_goal_target_with_activity_fat", String.valueOf(myGoalTargetWithActivityFat));
        data.put("inp_goal_target_with_activity_carbs", String.valueOf(myGoalTargetWithActivityCarbs));
        data.put("inp_goal_target_with_activity_proteins", String.valueOf(myGoalTargetWithActivityProteins));
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                sendUpdatesToServerAnswer();
            }
        });
        task.execute();
    }


    public void sendUpdatesToServerAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        Toast.makeText(this, dataResult, Toast.LENGTH_SHORT).show();

        // Close db
        db.close();

    } // buttonNextClickedAnswer
}