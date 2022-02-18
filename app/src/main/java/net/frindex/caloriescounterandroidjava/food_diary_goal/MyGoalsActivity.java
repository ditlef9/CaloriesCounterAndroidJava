package net.frindex.caloriescounterandroidjava.food_diary_goal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
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
public class MyGoalsActivity extends AppCompatActivity {

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

    // Cursor
    Cursor cursorMyGoals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goals);



        // Toolbar
        initializeToolbar();

        // Fetch my profile
        fetchMyProfile();

        // Setup listeners
        initializeListeners();


        // Populate goals
        populateMyGoals();
    }


    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.my_goals));

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
        Intent i = new Intent(MyGoalsActivity.this, MainActivity.class);
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



    } // fetchMyProfile


    /*- Initialize Listeners -------------------------------------------------------------------- */
    private void initializeListeners() {
        /* Next listener */
        Button buttonNewGoal = findViewById(R.id.buttonNewGoal);
        buttonNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNewGoalClicked();
            }
        });
    }
    private void buttonNewGoalClicked() {
        // Move to next
        Intent i = new Intent(MyGoalsActivity.this, FoodDiaryGoalNewAHeightActivity.class);
        startActivity(i);
        finish();
    }


    /*- Populate my goals ----------------------------------------------------------------------- */
    public void populateMyGoals(){
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Count rows
        String query = "SELECT _id,goal_id, goal_user_id, goal_current_weight, goal_current_fat_percentage, goal_target_weight, goal_target_fat_percentage, goal_i_want_to, goal_weekly_goal, goal_date, goal_activity_level, goal_current_bmi, goal_target_bmi, goal_current_bmr_calories, goal_current_bmr_fat, goal_current_bmr_carbs, goal_current_bmr_proteins, goal_current_sedentary_calories, goal_current_sedentary_fat, goal_current_sedentary_carbs, goal_current_sedentary_proteins, goal_current_with_activity_calories, goal_current_with_activity_fat, goal_current_with_activity_carbs, goal_current_with_activity_proteins, goal_target_bmr_calories, goal_target_bmr_fat, goal_target_bmr_carbs, goal_target_bmr_proteins, goal_target_sedentary_calories, goal_target_sedentary_fat, goal_target_sedentary_carbs, goal_target_sedentary_proteins, goal_target_with_activity_calories, goal_target_with_activity_fat, goal_target_with_activity_carbs, goal_target_with_activity_proteins, goal_updated, goal_synchronized, goal_notes " +
                "FROM food_diary_goals WHERE goal_user_id=" + myUserId + " " +
                "ORDER BY goal_id DESC";
        cursorMyGoals = db.rawQuery(query);


        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);

        // Setup cursor adapter using cursor from last step
        MyGoalsCursorAdapter cursorAdapter = new MyGoalsCursorAdapter(this, cursorMyGoals);

        // Attach cursor adapter to the ListView
        try{
            lvItems.setAdapter(cursorAdapter); // uses ContinensCursorAdapter
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        // Close db
        db.close();


        // OnClick/
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                cursorMyGoalsClicked(arg2);
            }
        });


        db.close();
    } // populateMyGoals


    private void cursorMyGoalsClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorMyGoals.moveToPosition(listItemIDClicked);

        // Get ID and name from cursor
        int goal_Id = cursorMyGoals.getInt(0);
        int goalId = cursorMyGoals.getInt(1);

        // Move
        Intent i = new Intent(MyGoalsActivity.this, FoodDiaryGoalNewHResultsActivity.class);
        i.putExtra("currentGoalId", goalId);
        startActivity(i);

    } // cursorDiaryPlansClicked


}