package net.frindex.caloriescounterandroidjava.meal_plans;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 *
 * File: FoodBSubCategoriesActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class MealPlansActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL       = "https://summerslim.codecourses.eu";
    String apiMealPlansURL  = "https://summerslim.codecourses.eu/meal_plans/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // Cursors
    Cursor cursorMealPlans;

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
        setContentView(R.layout.activity_meal_plans);

        // Toolbar
        initializeToolbar();

        // Listeners
        listeners();

        // My user
        fetchMyProfile();

        // Populate food
        populateMealPlans();

        // Check if updates are needed
        checkIfUpdatesAreNeeded();
    }

    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.meal_plans));

        // Back icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Show
        actionBar.show();
    } // initializeToolbar

    /*- On Create Options Menu ----------------------------------------------------------- */
    /* @About The menu on the toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meal_plans, menu);
        return true;
    }


    /* One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.navigationMyMealPlans) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/meal_plans/my_meal_plans.php?l=" + myUserLanguage));
            startActivity(intent);
        }
        else if (id == R.id.navigationNewMealPlan) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/meal_plans/new_meal_plan.php?l=" + myUserLanguage));
            startActivity(intent);
        }
        else {
            // Up button
            Intent i = new Intent(MealPlansActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /*- Listeners ----------------------------------------------------------------------------- */
    private void listeners() {
        // Search
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextSearchOnTextChanged();
                }
            }
        });

    } // listeners


    public void editTextSearchOnTextChanged(){
        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        EditText editTextSearch = findViewById(R.id.editTextSearch);
        String stringSearchQuery = editTextSearch.getText().toString();
        stringSearchQuery = "%" + stringSearchQuery + "%";
        String stringSearchQuerySQL = db.quoteSmart(stringSearchQuery);


        // Cursor
        String query = "SELECT _id, meal_plan_id, meal_plan_user_id, " +
                "meal_plan_language, meal_plan_title, meal_plan_title_clean, " +
                "meal_plan_number_of_days, meal_plan_introduction, meal_plan_total_energy_without_training, " +
                "meal_plan_total_fat_without_training, meal_plan_total_carb_without_training, meal_plan_total_protein_without_training, " +
                "meal_plan_total_energy_with_training, meal_plan_total_fat_with_training, meal_plan_total_carb_with_training, meal_plan_total_protein_with_training, " +
                "meal_plan_average_kcal_without_training, meal_plan_average_fat_without_training, meal_plan_average_carb_without_training, " +
                "meal_plan_average_protein_without_training, meal_plan_average_kcal_with_training, meal_plan_average_fat_with_training, " +
                "meal_plan_average_carb_with_training, meal_plan_average_protein_with_training, meal_plan_created, " +
                "meal_plan_updated, meal_plan_user_ip, meal_plan_image_path, meal_plan_image_thumb, meal_plan_image_file, " +
                "meal_plan_views, meal_plan_views_ip_block, meal_plan_likes, " +
                "meal_plan_dislikes, meal_plan_rating, meal_plan_rating_ip_block, " +
                "meal_plan_comments FROM meal_plans WHERE meal_plan_language='" + myUserLanguage + "' " +
                " AND meal_plan_title LIKE " + stringSearchQuerySQL + "";

        cursorMealPlans = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);

        // Setup cursor adapter using cursor from last step
        MealPlansAdapter categoriesAdapter = new MealPlansAdapter(this, cursorMealPlans);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter



        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateMealPlansClicked(arg2);
            }
        });


        db.close();

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


    /*- Populate B Food Sub Categories -------------------------------------------------------- */
    public void populateMealPlans(){

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT _id, meal_plan_id, meal_plan_user_id, " +
                "meal_plan_language, meal_plan_title, meal_plan_title_clean, " +
                "meal_plan_number_of_days, meal_plan_introduction, meal_plan_total_energy_without_training, " +
                "meal_plan_total_fat_without_training, meal_plan_total_carb_without_training, meal_plan_total_protein_without_training, " +
                "meal_plan_total_energy_with_training, meal_plan_total_fat_with_training, meal_plan_total_carb_with_training, meal_plan_total_protein_with_training, " +
                "meal_plan_average_kcal_without_training, meal_plan_average_fat_without_training, meal_plan_average_carb_without_training, " +
                "meal_plan_average_protein_without_training, meal_plan_average_kcal_with_training, meal_plan_average_fat_with_training, " +
                "meal_plan_average_carb_with_training, meal_plan_average_protein_with_training, meal_plan_created, " +
                "meal_plan_updated, meal_plan_user_ip, meal_plan_image_path, meal_plan_image_thumb, meal_plan_image_file, " +
                "meal_plan_views, meal_plan_views_ip_block, meal_plan_likes, " +
                "meal_plan_dislikes, meal_plan_rating, meal_plan_rating_ip_block, " +
                "meal_plan_comments FROM meal_plans WHERE meal_plan_language='" + myUserLanguage + "' " +
                " ORDER BY meal_plan_id DESC";

        cursorMealPlans = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);

        // Setup cursor adapter using cursor from last step
        MealPlansAdapter categoriesAdapter = new MealPlansAdapter(this, cursorMealPlans);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter



        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateMealPlansClicked(arg2);
            }
        });



        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateMealPlansClicked(arg2);
            }
        });


        db.close();

    } // populateAFoodMainCategories


    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateMealPlansClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorMealPlans.moveToPosition(listItemIDClicked);

        // Get Food
        int currentMealPlan_Id = cursorMealPlans.getInt(0);
        int currentMealPlanId = cursorMealPlans.getInt(1);

        // Load Food
        Intent i = new Intent(MealPlansActivity.this, MealPlanViewActivity.class);
        i.putExtra("currentMealPlanId", currentMealPlanId);
        startActivity(i);
    }

    public void checkIfUpdatesAreNeeded(){
        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();


        // Fetch data
        String q = "SELECT _id, synchronized_week, synchronized_second FROM synchronize WHERE name='meal_plans'";
        Cursor syncCursor = db.rawQuery(q);
        int size = syncCursor.getCount();
        String synchWeek = "0";
        String synchSecond = "0";
        if(size != 0) {
            synchWeek = syncCursor.getString(1);
            synchSecond = syncCursor.getString(2);
        }
        else{
            q = "INSERT INTO synchronize (_id, name) VALUES (NULL, 'meal_plans')";
            db.rawQuery(q);
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
            /*SynchronizeHMealPlans synchronizeHMealPlans = new SynchronizeHMealPlans(this, myUserLanguage);
            synchronizeHMealPlans.updateLastSynchronizedDate();*/

            // Reload after 3 seconds
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    populateMealPlans();
                }
            }, 3000);
        }
    }
}