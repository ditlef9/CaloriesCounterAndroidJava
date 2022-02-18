package net.frindex.caloriescounterandroidjava.food_diary_goal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
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
public class MyProfileGenderActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_gender);

        // Toolbar
        initializeToolbar();


        // Fetch my profile
        fetchMyProfile();

        // Setup listeners
        initializeListeners();

    }

    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.my_profile));

        // Back icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Show
        actionBar.show();


    } // initializeToolbar

    /* One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Up button
        Intent i = new Intent(MyProfileGenderActivity.this, MyProfileActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
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


        // Select correct gender
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupInpGroup);
        if(myUserGender.equals("male")) {
            radioGroupGender.check(R.id.radioButtonInpA);
        }
        else{
            radioGroupGender.check(R.id.radioButtonInpB);
        }


    } // fetchMyProfile


    /*- Initialize Listeners -------------------------------------------------------------------- */
    private void initializeListeners() {

        /* Next listener */
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSaveClicked();
            }
        });



    }
    private void buttonSaveClicked() {

        // Gender
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupInpGroup);
        int radioButtonID = radioGroupGender.getCheckedRadioButtonId(); // get selected radio button from radioGroup
        View radioButtonGender = radioGroupGender.findViewById(radioButtonID);
        int position = radioGroupGender.indexOfChild(radioButtonGender); // If you want position of Radiobutton

        String stringGender = "";
        if(position == 0){
            stringGender = "male";
        }
        else{
            stringGender = "female";
        }

        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Put date of birth togheter
        String stringGenderSQL = db.quoteSmart(stringGender);
        String q = "UPDATE users SET user_gender=" + stringGenderSQL + " WHERE _id='1'";
        db.rawQuery(q);

        // Close db connection
        db.close();

        // Send to PHP


        // Send to PHP
        String url    = apiFooDiaryURL + "/post_user_gender.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_user_gender", stringGender);
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                buttonNextClickedAnswer();
            }
        });
        task.execute();




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


        // Move to next
        Intent i = new Intent(MyProfileGenderActivity.this, MyProfileActivity.class);
        startActivity(i);
        finish();
    }
}