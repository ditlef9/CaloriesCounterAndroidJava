package net.frindex.caloriescounterandroidjava.food;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.food_diary_add.FoodDiaryAddFoodAdapter;
import net.frindex.caloriescounterandroidjava.food_diary_add.FoodDiaryAddFoodMainCategoriesAdapter;

public class FoodAMainCategoriesActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // Cursors
    Cursor cursorFoodMainCategories;
    Cursor cursorFood;

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
        setContentView(R.layout.activity_food_amain_categories);


        // Toolbar
        initializeToolbar();

        // Listeners
        listeners();

        // My user
        fetchMyProfile();

        // Populate food
        populateAFoodMainCategories();
    }



    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.food));

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
        getMenuInflater().inflate(R.menu.menu_food_a_main_categories, menu);
        return true;
    }
    /* One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.navigationMyFood) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/food/my_food.php?l=" + myUserLanguage));
            startActivity(intent);
        }
        else if (id == R.id.navigationAddFood) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/food/new_food.php?l=" + myUserLanguage));
            startActivity(intent);
        }
        else {
            // Up button
            Intent i = new Intent(FoodAMainCategoriesActivity.this, MainActivity.class);
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
        String query = "SELECT _id, food_id, food_user_id, " +
                "food_name, food_manufacturer_name, food_description, " +
                "food_serving_size_gram, food_serving_size_gram_measurement, food_serving_size_pcs, " +
                "food_serving_size_pcs_measurement, food_energy, food_proteins, " +
                "food_carbohydrates, food_fat, food_energy_calculated, " +
                "food_proteins_calculated, food_carbohydrates_calculated, food_fat_calculated, " +
                "food_barcode, food_category_id, food_image_path, food_thumb_small, " +
                "food_image_a, food_image_b, food_image_c, food_image_d, food_image_e, food_unique_hits, food_likes, " +
                "food_dislikes FROM food_index WHERE food_language='" + myUserLanguage + "' " +
                " AND (food_name LIKE " + stringSearchQuerySQL + " OR " +
                "food_manufacturer_name  LIKE " + stringSearchQuerySQL + ")";

        cursorFood = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);

        // Setup cursor adapter using cursor from last step
        FoodDiaryAddFoodAdapter categoriesAdapter = new FoodDiaryAddFoodAdapter(this, cursorFood);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter



        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateCFoodClicked(arg2);
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


    /*- Populate A Food Main Categories -------------------------------------------------------- */
    public void populateAFoodMainCategories(){
        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT food_categories._id, food_categories.category_id, food_categories.category_name, food_categories.category_parent_id, " +
                "food_categories_translations.category_translation_value FROM food_categories " +
                "JOIN food_categories_translations ON food_categories.category_id=food_categories_translations.category_id " +
                " WHERE food_categories.category_user_id='0' " +
                "AND food_categories.category_parent_id='0' " +
                "AND food_categories_translations.category_translation_language='" +  myUserLanguage + "' ORDER BY category_translation_value ASC";

        cursorFoodMainCategories = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);


        // Setup cursor adapter using cursor from last step
        FoodDiaryAddFoodMainCategoriesAdapter categoriesAdapter = new FoodDiaryAddFoodMainCategoriesAdapter(this, cursorFoodMainCategories);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter



        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateAFoodMainCategoriesClicked(arg2);
            }
        });


        db.close();

    } // populateAFoodMainCategories


    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateAFoodMainCategoriesClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorFoodMainCategories.moveToPosition(listItemIDClicked);

        // Get Main Category
        int foodMainCategory_Id = cursorFoodMainCategories.getInt(0);
        int currentFoodMainCategoryId = cursorFoodMainCategories.getInt(1);
        String currentFoodMainCategoryTranslationValue = cursorFoodMainCategories.getString(4);

        // Load sub categories
        Intent i = new Intent(FoodAMainCategoriesActivity.this, FoodBSubCategoriesActivity.class);
        i.putExtra("currentFoodMainCategoryId", currentFoodMainCategoryId);
        i.putExtra("currentFoodMainCategoryTranslationValue", currentFoodMainCategoryTranslationValue);
        startActivity(i);
    }

    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateCFoodClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorFood.moveToPosition(listItemIDClicked);

        // Get Food
        int currentFood_Id = cursorFood.getInt(0);
        int currentFoodId = cursorFood.getInt(1);

        // Load Food
        Intent i = new Intent(FoodAMainCategoriesActivity.this, FoodDViewFoodActivity.class);
        i.putExtra("currentFoodId", currentFoodId);
        startActivity(i);
    }
}