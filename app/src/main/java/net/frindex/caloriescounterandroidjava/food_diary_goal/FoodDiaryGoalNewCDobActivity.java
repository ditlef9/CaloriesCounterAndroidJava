package net.frindex.caloriescounterandroidjava.food_diary_goal;

import static android.widget.Toast.makeText;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

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
public class FoodDiaryGoalNewCDobActivity extends AppCompatActivity {

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
    String myUserHeight;
    String myUserMeasurement;
    String myUserDob;

    /* Variables */
    private String[] arraySpinnerDOBDay = new String[31];
    private String[] arraySpinnerDOBMonth = new String[12];
    private String[] arraySpinnerDOBYear = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_goal_new_cdob);

        // Fetch my profile
        fetchMyProfile();

        // Gender
        actionSetDob();

        // Setup listeners
        buttonNextListeners();
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


        if (!(myUserDob.equals("")) && !(myUserDob.equals("0000-00-00"))) {
            // Move to next
            Intent i = new Intent(FoodDiaryGoalNewCDobActivity.this, FoodDiaryGoalNewECurrentWeightActivity.class);
            startActivity(i);
            finish();

        }


    } // fetchMyProfile


    /*- Action Set Dob -------------------------------------------------------------------------- */
    private void actionSetDob() {

        // My DOB
        String stringUserDobYear = "";
        String stringUserDobMonth = "";
        String stringUserDobYDay = "";
        try {
            String[] items1 = myUserDob.split("-");
            stringUserDobYear = items1[0];
            stringUserDobMonth = items1[1];
            stringUserDobYDay = items1[2];
        }
        catch(Exception e){
        }


        // Day
        int spinnerDOBDaySelectedIndex = 0;
        int human_counter = 0;
        for(int x=0;x<31;x++){
            human_counter=x+1;
            arraySpinnerDOBDay[x] = "" + human_counter;
            if(stringUserDobYDay.equals("0" + human_counter) || stringUserDobYDay.equals(""+human_counter)){
                spinnerDOBDaySelectedIndex = x;
                // Toast.makeText(this, "Day: " + stringUserDobYDay + " Index: " + spinnerDOBDaySelectedIndex, Toast.LENGTH_LONG).show();
            }
        }
        Spinner spinnerDOBDay = findViewById(R.id.spinnerDOBDay);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBDay);
        spinnerDOBDay.setAdapter(adapter);
        spinnerDOBDay.setSelection(spinnerDOBDaySelectedIndex); // Select index


        // Month
        arraySpinnerDOBMonth[0] = getResources().getString(R.string.january);
        arraySpinnerDOBMonth[1] = getResources().getString(R.string.february);
        arraySpinnerDOBMonth[2] = getResources().getString(R.string.march);
        arraySpinnerDOBMonth[3] = getResources().getString(R.string.april);
        arraySpinnerDOBMonth[4] = getResources().getString(R.string.may);
        arraySpinnerDOBMonth[5] = getResources().getString(R.string.june);
        arraySpinnerDOBMonth[6] = getResources().getString(R.string.july);
        arraySpinnerDOBMonth[7] = getResources().getString(R.string.august);
        arraySpinnerDOBMonth[8] = getResources().getString(R.string.september);
        arraySpinnerDOBMonth[9] = getResources().getString(R.string.november);
        arraySpinnerDOBMonth[10] = getResources().getString(R.string.october);
        arraySpinnerDOBMonth[11] = getResources().getString(R.string.december);


        int intUserDobMonth = 0;
        stringUserDobYDay.replace("0", "");
        try {
            intUserDobMonth = Integer.parseInt(stringUserDobMonth);
            intUserDobMonth = intUserDobMonth-1;
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        Spinner spinnerDOBMonth = findViewById(R.id.spinnerDOBMonth);

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBMonth);
        spinnerDOBMonth.setAdapter(adapterMonth);
        spinnerDOBMonth.setSelection(intUserDobMonth); // Select index

        // Year
        int spinnerDOBYearSelectedIndex = 0;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int end = year-100;
        int index = 0;
        for(int x=year;x>end;x--){
            this.arraySpinnerDOBYear[index] = "" + x;
            // Toast.makeText(this, "x = " + x, Toast.LENGTH_SHORT).show();

            try {
                if (stringUserDobYear.equals("" + x)) {
                    spinnerDOBYearSelectedIndex = index;
                    //Toast.makeText(getActivity(), "Year: " + x + " Index: " + spinnerDOBYearSelectedIndex, Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e){

            }
            index++;
        }

        Spinner spinnerDOBYear = findViewById(R.id.spinnerDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBYear);
        spinnerDOBYear.setAdapter(adapterYear);
        spinnerDOBYear.setSelection(spinnerDOBYearSelectedIndex); // Select index


    } // actionSetDob

    /*- Initialize Listeners -------------------------------------------------------------------- */
    private void buttonNextListeners() {

        /* Next listener */
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNextClicked();
            }
        });


    }
    public void buttonNextClicked(){

        // Error
        int error = 0;


        // Date of Birth Day
        Spinner spinnerDOBDay = findViewById(R.id.spinnerDOBDay);
        String stringDOBDay = spinnerDOBDay.getSelectedItem().toString();
        int intDOBDay = 0;
        try {
            intDOBDay = Integer.parseInt(stringDOBDay);

            if(intDOBDay < 10){
                stringDOBDay = "0" + stringDOBDay;
            }

        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            makeText(this, "Please select a day for your birthday.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Date of Birth Month
        String stringDOBMonth = "";
        if(error == 0) {
            Spinner spinnerDOBMonth = findViewById(R.id.spinnerDOBMonth);
            stringDOBMonth = spinnerDOBMonth.getSelectedItem().toString();
            int positionDOBMonth = spinnerDOBMonth.getSelectedItemPosition();
            int month = positionDOBMonth + 1;
            if (month < 10) {
                stringDOBMonth = "0" + month;
            } else {
                stringDOBMonth = "" + month;
            }
        }

        // Date of Birth Year
        int intDOBYear = 0;
        if(error == 0) {
            Spinner spinnerDOBYear = findViewById(R.id.spinnerDOBYear);
            String stringDOBYear = spinnerDOBYear.getSelectedItem().toString();
            try {
                intDOBYear = Integer.parseInt(stringDOBYear);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
                makeText(this, "Please select a year for your birthday.", Toast.LENGTH_SHORT).show();
            }
        }


        // Insert to SQL
        if(error == 0){
            // Open db connection
            DBAdapter db = new DBAdapter(this);
            db.open();

            // Put date of birth together
            String dateOfBirth = intDOBYear + "-" + stringDOBMonth + "-" + stringDOBDay;
            String dateOfBirthSQL = db.quoteSmart(dateOfBirth);

            String q = "UPDATE users SET user_dob=" + dateOfBirthSQL + " WHERE _id='1'";
            db.rawQuery(q);
            // Close db connection
            db.close();

            // Send to PHP
            String url    = apiFooDiaryURL + "/post_user_dob.php";
            String stringMethod = "post";

            Map<String, String> data = new HashMap<String, String>();
            data.put("inp_api_password", apiPassword);
            data.put("inp_user_id", String.valueOf(myUserId));
            data.put("inp_user_password", myUserPassword);
            data.put("inp_user_dob", dateOfBirth);
            HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    buttonNextClickedAnswer();
                }
            });
            task.execute();
        }



        // Move to next
        Intent i = new Intent(FoodDiaryGoalNewCDobActivity.this, FoodDiaryGoalNewECurrentWeightActivity.class);
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