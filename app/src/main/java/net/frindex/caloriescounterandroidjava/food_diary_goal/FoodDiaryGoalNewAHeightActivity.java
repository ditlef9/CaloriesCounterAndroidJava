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
public class FoodDiaryGoalNewAHeightActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_food_diary_goal_new_aheight);

        // Fetch my profile
        fetchMyProfile();

        // Measurement and measurement button
        initializeMeasurementAndMeasurementButton();

        // Setup listeners
        initializeNextButtonListeners();
    }



    /*- Select action to start with ------------------------------------------------------------- */
    private void fetchMyProfile() {
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

        // Check height
        if (myUserHeight != null) {
            if (!(myUserHeight.equals("")) && !(myUserHeight.equals("0"))) {
                Intent i = new Intent(FoodDiaryGoalNewAHeightActivity.this, FoodDiaryGoalNewBGenderActivity.class);
                startActivity(i);
                finish();
            }
        }

    } // fetchMyProfile

    private void initializeMeasurementAndMeasurementButton(){

        // Hide CM/Feet/Inches
        Button buttonSwitchMeasurement = findViewById(R.id.buttonSwitchMeasurement);

        EditText editTextHeightFeet = findViewById(R.id.editTextHeightFeet);
        EditText editTextHeightInches = findViewById(R.id.editTextHeightInches);
        EditText editTextHeightCm = findViewById(R.id.editTextHeightCm);

        TextView textViewHeightFeet = findViewById(R.id.textViewHeightFeet);
        TextView textViewHeightInches = findViewById(R.id.textViewHeightInches);
        TextView textViewHeightCm = findViewById(R.id.textViewHeightCm);
        if(myUserMeasurement.equals("metric")){
            buttonSwitchMeasurement.setText(R.string.switch_to_feet_and_inches);

            editTextHeightFeet.setVisibility(View.GONE);
            editTextHeightInches.setVisibility(View.GONE);
            textViewHeightFeet.setVisibility(View.GONE);
            textViewHeightInches.setVisibility(View.GONE);

            editTextHeightCm.setVisibility(View.VISIBLE);
            textViewHeightCm.setVisibility(View.VISIBLE);
        }
        else if(myUserMeasurement.equals("imperial")){
            buttonSwitchMeasurement.setText(R.string.switch_to_cm);

            editTextHeightFeet.setVisibility(View.VISIBLE);
            editTextHeightInches.setVisibility(View.VISIBLE);
            textViewHeightFeet.setVisibility(View.VISIBLE);
            textViewHeightInches.setVisibility(View.VISIBLE);

            editTextHeightCm.setVisibility(View.GONE);
            textViewHeightCm.setVisibility(View.GONE);
        }
        else{
            // Unknown measurement
            DBAdapter db = new DBAdapter(this);
            db.open();
            String q = "UPDATE users SET user_measurement='imperial' WHERE _id='1'";
            db.rawQuery(q);
            db.close();

            // Fields
            buttonSwitchMeasurement.setText(R.string.switch_to_cm);
            editTextHeightCm.setVisibility(View.GONE);
            textViewHeightCm.setVisibility(View.GONE);

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

        // Fields
        Button buttonSwitchMeasurement = findViewById(R.id.buttonSwitchMeasurement);

        EditText editTextHeightFeet = findViewById(R.id.editTextHeightFeet);
        EditText editTextHeightInches = findViewById(R.id.editTextHeightInches);
        EditText editTextHeightCm = findViewById(R.id.editTextHeightCm);

        TextView textViewHeightFeet = findViewById(R.id.textViewHeightFeet);
        TextView textViewHeightInches = findViewById(R.id.textViewHeightInches);
        TextView textViewHeightCm = findViewById(R.id.textViewHeightCm);


        // Update my table
        DBAdapter db = new DBAdapter(this);
        db.open();
        if(myUserMeasurement.equals("metric")) {
            // Toast
            Toast.makeText(this, "Switching to feet/inches", Toast.LENGTH_SHORT).show();

            // Db
            String q = "UPDATE users SET user_measurement='imperial' WHERE _id='1'";
            db.rawQuery(q);

            // Var
            myUserMeasurement = "imperial";

            // Fields
            buttonSwitchMeasurement.setText(R.string.switch_to_cm);

            editTextHeightFeet.setVisibility(View.VISIBLE);
            editTextHeightInches.setVisibility(View.VISIBLE);
            textViewHeightFeet.setVisibility(View.VISIBLE);
            textViewHeightInches.setVisibility(View.VISIBLE);

            editTextHeightCm.setVisibility(View.GONE);
            textViewHeightCm.setVisibility(View.GONE);

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
            Toast.makeText(this, "Switching to cm", Toast.LENGTH_SHORT).show();

            // Db
            String q = "UPDATE users SET user_measurement='metric' WHERE _id='1'";
            db.rawQuery(q);

            // Var
            myUserMeasurement = "metric";

            // Fields
            editTextHeightFeet.setVisibility(View.GONE);
            editTextHeightInches.setVisibility(View.GONE);
            textViewHeightFeet.setVisibility(View.GONE);
            textViewHeightInches.setVisibility(View.GONE);

            editTextHeightCm.setVisibility(View.VISIBLE);
            textViewHeightCm.setVisibility(View.VISIBLE);

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
        // Fields
        EditText editTextHeightFeet = findViewById(R.id.editTextHeightFeet);
        EditText editTextHeightInches = findViewById(R.id.editTextHeightInches);
        EditText editTextHeightCm = findViewById(R.id.editTextHeightCm);


        // Results
        String stringHeightCm = "0";

        if(myUserMeasurement.equals("metric")){
            stringHeightCm = editTextHeightCm.getText().toString();
            int intHeightCm = 0;
            try {
                intHeightCm = Integer.parseInt(stringHeightCm);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "That is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
            }

        } // metric
        else{
            String stringHeightFeet = editTextHeightFeet.getText().toString();
            double doubleHeightFeet = 0;
            try {
                doubleHeightFeet = Double.parseDouble(stringHeightFeet);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Feet is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
            }

            String stringHeightInches = editTextHeightInches.getText().toString();
            double doubleHeightInches = 0;
            try {
                doubleHeightInches = Double.parseDouble(stringHeightInches);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(this, "Inches is not a number (" + nfe.getMessage() + ")", Toast.LENGTH_LONG).show();
            }

            double doubleHeightFeetCm = doubleHeightFeet*30.48;
            double doubleHeightInchesCm = doubleHeightInches*2.54;

            double doubleHeightCm = doubleHeightFeetCm+doubleHeightInchesCm;

            int intHeightCm = (int)doubleHeightCm;

            stringHeightCm = String.valueOf(intHeightCm);

        }

        // Update Db
        DBAdapter db = new DBAdapter(this);
        db.open();
        String stringHeightCmSQL = db.quoteSmart(stringHeightCm);
        String q = "UPDATE users SET user_height=" + stringHeightCmSQL + " WHERE _id='1'";
        db.rawQuery(q);


        // Send to PHP
        String url    = apiFooDiaryURL + "/post_user_height.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_user_height", stringHeightCm);
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                buttonNextClickedAnswer();
            }
        });
        task.execute();



        // Move to next
        Intent i = new Intent(FoodDiaryGoalNewAHeightActivity.this, FoodDiaryGoalNewBGenderActivity.class);
        startActivity(i);
        finish();


    } // buttonNextClicked


    public void buttonNextClickedAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);
        db.close();

        Toast.makeText(this, dataResult, Toast.LENGTH_SHORT).show();
    }
}