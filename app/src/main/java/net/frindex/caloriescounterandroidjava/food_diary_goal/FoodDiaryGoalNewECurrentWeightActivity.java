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
public class FoodDiaryGoalNewECurrentWeightActivity extends AppCompatActivity {

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
    String myUserHeight;
    String myUserMeasurement;
    String myUserDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_goal_new_ecurrent_weight);

        // Fetch my profile and goal
        fetchMyProfileAndGoal();

        // Measurement and measurement button
        initializeMeasurementAndMeasurementButton();

        // Fetch my current weight
        // Todo: fetchMyCurrentWeight();

        // Next button listeners
        initializeNextButtonListeners();
    }

    /*- Select action to start with ------------------------------------------------------------- */
    private void fetchMyProfileAndGoal() {

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        String q = "SELECT user_id, user_alias, user_email, user_password, user_language, user_gender, user_height, user_measurement, user_dob FROM users WHERE _id=1";
        Cursor cursorUser = db.rawQuery(q);

        myUserId = cursorUser.getInt(0);
        myUserAlias = cursorUser.getString(1);
        myUserEmail = cursorUser.getString(2);
        myUserPassword = cursorUser.getString(3);
        myUserLanguage = cursorUser.getString(4);
        myUserGender = cursorUser.getString(5);
        myUserHeight = cursorUser.getString(6);
        myUserMeasurement = cursorUser.getString(7);
        myUserDob = cursorUser.getString(8);

        db.close();


    } // fetchMyProfileAndGoal




    private void initializeMeasurementAndMeasurementButton() {

        // Buttons and text view
        Button buttonSwitchMeasurement = findViewById(R.id.buttonSwitchMeasurement);
        TextView textViewWeight = findViewById(R.id.textViewWeight);

        if(myUserMeasurement.equals("metric")){
            buttonSwitchMeasurement.setText(R.string.switch_to_lbs);
            textViewWeight.setText(R.string.kg_lowercase);
        }
        else if(myUserMeasurement.equals("imperial")){
            buttonSwitchMeasurement.setText(R.string.switch_to_kg);
            textViewWeight.setText(R.string.lbs_lowercase);
        }
        else{
            // Unknown measurement
            DBAdapter db = new DBAdapter(this);
            db.open();
            String q = "UPDATE users SET user_measurement='imperial' WHERE _id='1'";
            db.rawQuery(q);
            db.close();

            // Fields
            buttonSwitchMeasurement.setText(R.string.switch_to_kg);
            textViewWeight.setText(R.string.lbs_lowercase);

            // Variable
            myUserMeasurement = "imperial";
        }

        // Listener
        buttonSwitchMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSwitchMeasurementClicked();
            }
        });
    } // initializeMeasurementAndMeasurementButton

    public void buttonSwitchMeasurementClicked(){

        // Buttons and text view
        Button buttonSwitchMeasurement = findViewById(R.id.buttonSwitchMeasurement);
        TextView textViewWeight = findViewById(R.id.textViewWeight);

        // Update my table
        DBAdapter db = new DBAdapter(this);
        db.open();
        if(myUserMeasurement.equals("metric")) {
            // Toast
            Toast.makeText(this, "Switching to lbs", Toast.LENGTH_SHORT).show();

            // Db
            String q = "UPDATE users SET user_measurement='imperial' WHERE _id='1'";
            db.rawQuery(q);

            // Var
            myUserMeasurement = "imperial";

            // Fields
            buttonSwitchMeasurement.setText(R.string.switch_to_kg);
            textViewWeight.setText(R.string.lbs_lowercase);

            // Send to PHP
            String url    = apiUsersURL + "/post_update_user_language_measurement.php";
            String stringMethod = "post";

            Map<String, String> data = new HashMap<String, String>();
            data.put("inp_api_password", apiPassword);
            data.put("inp_user_email", myUserEmail);
            data.put("inp_user_password", myUserPassword);
            data.put("inp_user_measurement", "imperial");
            data.put("inp_user_language", myUserLanguage);
            HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    buttonSwitchMeasurementClickedAnswer();
                }
            });
            task.execute();

        } // metric
        else{
            // Toast
            Toast.makeText(this, "Switching to kg", Toast.LENGTH_SHORT).show();

            // Db
            String q = "UPDATE users SET user_measurement='metric' WHERE _id='1'";
            db.rawQuery(q);

            // Var
            myUserMeasurement = "metric";

            // Fields
            buttonSwitchMeasurement.setText(R.string.switch_to_lbs);
            textViewWeight.setText(R.string.kg_lowercase);

            // Send to PHP
            String url    = apiUsersURL + "/post_update_user_language_measurement.php";
            String stringMethod = "post";

            Map<String, String> data = new HashMap<String, String>();
            data.put("inp_api_password", apiPassword);
            data.put("inp_user_email", myUserEmail);
            data.put("inp_user_password", myUserPassword);
            data.put("inp_user_measurement", "metric");
            data.put("inp_user_language", myUserLanguage);
            HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    buttonSwitchMeasurementClickedAnswer();
                }
            });
            task.execute();
        } // imperial

        db.close();


    } // buttonSwitchMeasurementClicked
    public void buttonSwitchMeasurementClickedAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);
        db.close();


        // Toast.makeText(this, "Changed measurement", Toast.LENGTH_SHORT).show();
    }

    /*- Initialize Listeners -------------------------------------------------------------------- */
    private void initializeNextButtonListeners() {
        /* Next listener */
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { buttonNextClicked();
            }
        });

    }

    /*- Button next clicked ----------------------------------------------------------------------- */
    private void buttonNextClicked() {

        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();


        // Fields
        EditText editTextWeight = findViewById(R.id.editTextWeight);

        // Weight
        String stringWeightKg = "0";

        if(myUserMeasurement.equals("metric")){
            stringWeightKg = editTextWeight.getText().toString();
            int intWeightKg = 0;
            try {
                intWeightKg = Integer.parseInt(stringWeightKg);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "That is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
            }

        } // metric
        else{
            String stringWeightLbs = editTextWeight.getText().toString();
            double doubleWeightLbs = 0;
            try {
                doubleWeightLbs = Double.parseDouble(stringWeightLbs);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Feet is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
            }

            double doubleWeightKg = doubleWeightLbs*0.45359237;

            int intWeightKg = (int)doubleWeightKg;

            stringWeightKg = String.valueOf(intWeightKg);

        }
        String stringWeightKgSQL = db.quoteSmart(stringWeightKg);

        // Calculate BMI
        // BMI = m/h^2
        double doubleHeightCm = 0;
        try {
            doubleHeightCm = Double.parseDouble(myUserHeight);
        }
        catch(NumberFormatException nfe) {
            Toast.makeText(this, "My user height is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
        }

        double doubleWeight = 0;
        try {
            doubleWeight = Double.parseDouble(stringWeightKg);
        }
        catch(NumberFormatException nfe) {
            Toast.makeText(this, "My user weight is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
        }


        double heightMeter = doubleHeightCm/100;
        double heightSquared = heightMeter*heightMeter;
        double doubleBmi = 0;
        if(heightSquared != 0){
            doubleBmi = doubleWeight/heightSquared;
        }
        String stringBmi = doubleBmi + "";
        String stringBmiSQL = db.quoteSmart(stringBmi);



        // Date
        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String dateyyyyMMdd = dfyyyymmdd.format(Calendar.getInstance().getTime());

        // Fields

        // Insert or update?
        String query = "SELECT _id, goal_id FROM food_diary_goals WHERE goal_user_id='" + myUserId + "' AND goal_date='" + dateyyyyMMdd + "'";
        Cursor goalCursor = db.rawQuery(query);
        int goalCursorCount = goalCursor.getCount();
        int goal_Id = 0;
        int goalId = 0;
        if(goalCursorCount == 0){
            // Insert
            String qInsert = "INSERT INTO food_diary_goals (_id, goal_id, goal_user_id, " +
                    "goal_current_weight, goal_date, goal_current_bmi) " +
                    "VALUES ("
                    + "NULL, "
                    + '0' + ", "
                    + myUserId + ", "
                    + stringWeightKgSQL + ", "
                    + "'" + dateyyyyMMdd + "', "
                    + stringBmiSQL
                    + ")";
            db.rawQuery(qInsert);

            // Find the new _id
            goalCursor = db.rawQuery(query);
            goal_Id = goalCursor.getInt(0);
            goalId = goalCursor.getInt(1);


        }
        else {
            goal_Id = goalCursor.getInt(0);
            goalId = goalCursor.getInt(1);
            String q = "UPDATE food_diary_goals SET goal_current_weight=" + stringWeightKgSQL + ", " +
                    "goal_current_bmi=" + stringBmiSQL + " WHERE _id=" + goal_Id + "";
            db.rawQuery(q);
        }


        // Send to PHP
        // We will get the ID of the food diary goal back
        String url    = apiFooDiaryURL + "/post_user_new_food_diary_goal.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_current_weight", stringWeightKg);
        data.put("inp_current_bmi", stringBmi);
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                buttonNextClickedAnswer();
            }
        });
        task.execute();

        // Close db
        db.close();


        // Loading
        Toast.makeText(this, "Saving current weight.. Please wait..", Toast.LENGTH_SHORT).show();

    } // buttonNextClicked


    public void buttonNextClickedAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        // Toast.makeText(this, dataResult, Toast.LENGTH_SHORT).show();

        if(!(dataResult.equals(""))){

            // Date
            DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
            String dateyyyyMMdd = dfyyyymmdd.format(Calendar.getInstance().getTime());

            // Goal id
            String goalIdSQL = db.quoteSmart(dataResult);

            // Update
            String q = "UPDATE food_diary_goals SET goal_id=" + goalIdSQL + " " +
                    " WHERE goal_date='" + dateyyyyMMdd + "'";
            db.rawQuery(q);
        }

        // Close db
        db.close();


        // Move to next
        Intent i = new Intent(FoodDiaryGoalNewECurrentWeightActivity.this, FoodDiaryGoalNewFTargetWeightActivity.class);
        startActivity(i);
        finish();


    } // buttonNextClickedAnswer

}