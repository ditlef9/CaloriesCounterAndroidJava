package net.frindex.caloriescounterandroidjava.recipes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.food_diary_add.FoodDiaryAddRecipeAdapter;

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
public class RecipesBShowRecipesInMainCategoryActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // class variables
    int currentRecipeMainCategoryId;
    String currentRecipeMainCategoryTranslationValue;

    // Cursor
    Cursor cursorRecipes;

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
        setContentView(R.layout.activity_recipes_bshow_recipes_in_main_category);

        Bundle b = getIntent().getExtras();
        currentRecipeMainCategoryId = b.getInt("currentRecipeMainCategoryId");
        currentRecipeMainCategoryTranslationValue = b.getString("currentRecipeMainCategoryTranslationValue");


        // Toolbar
        initializeToolbar();

        // Listeners
        listeners();

        // My user
        fetchMyProfile();

        // Recipe categories
        populateBRecipes();

    }


    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.recipes) + " > " + currentRecipeMainCategoryTranslationValue);

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
        getMenuInflater().inflate(R.menu.menu_recipes_a_main_categories, menu);
        return true;
    }

    /* One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.navigationMyRecipes) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/recipes/my_recipes.php?l=" + myUserLanguage));
            startActivity(intent);
        }
        else if (id == R.id.navigationAddRecipe) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/recipes/submit_recipe.php?l=" + myUserLanguage));
            startActivity(intent);
        }
        else {
            // Up button
            Intent i = new Intent(RecipesBShowRecipesInMainCategoryActivity.this, RecipesAMainCategoriesActivity.class);
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


        // Previous
        TextView textViewMainPrevious = findViewById(R.id.textViewMainPrevious);
        textViewMainPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMainPreviousClicked();
            }
        });
        textViewMainPrevious.setText(currentRecipeMainCategoryTranslationValue);
    }
    public void editTextSearchOnTextChanged() {
        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        EditText editTextSearch = findViewById(R.id.editTextSearch);
        String stringSearchQuery = editTextSearch.getText().toString();
        stringSearchQuery = "%" + stringSearchQuery + "%";
        String stringSearchQuerySQL = db.quoteSmart(stringSearchQuery);


        // Cursor
        String query = "SELECT recipes._id, recipes.recipe_id, recipes.recipe_user_id, " +
                "recipes.recipe_title, recipes.recipe_category_id, recipes.recipe_language, " +
                "recipes.recipe_introduction, recipes.recipe_directions, recipes.recipe_image_path, " +
                "recipes.recipe_image, recipes.recipe_thumb, recipes.recipe_video, " +
                "recipes.recipe_date, recipes.recipe_time, recipes.recipe_cusine_id, recipes.recipe_season_id, " +
                "recipes.recipe_occasion_id, recipes.recipe_marked_as_spam, recipes.recipe_unique_hits, " +
                "recipes.recipe_unique_hits_ip_block, recipes.recipe_comments, recipes.recipe_user_ip, " +
                "recipes.recipe_password, recipes.recipe_last_viewed, recipes_numbers.number_hundred_calories,  " +
                "recipes_numbers.number_hundred_proteins, recipes_numbers.number_hundred_fat, recipes_numbers.number_hundred_carbs, recipes_numbers.number_serving_calories, " +
                "recipes_numbers.number_serving_proteins, recipes_numbers.number_serving_fat, recipes_numbers.number_serving_carbs " +
                "FROM recipes " +
                "JOIN recipes_numbers ON recipes.recipe_id=recipes_numbers.number_recipe_id " +
                " WHERE recipes.recipe_title LIKE " + stringSearchQuerySQL + " " +
                "AND recipes.recipe_language='" + myUserLanguage + "'";
        cursorRecipes = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);


        // Setup cursor adapter using cursor from last step
        FoodDiaryAddRecipeAdapter categoriesAdapter = new FoodDiaryAddRecipeAdapter(this, cursorRecipes);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter


        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateBRecipesClicked(arg2);
            }
        });
        db.close();
    }
    public void textViewMainPreviousClicked(){
        Intent i = new Intent(RecipesBShowRecipesInMainCategoryActivity.this, RecipesAMainCategoriesActivity.class);
        startActivity(i);
        finish();
    } // textViewMainPreviousClicked


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


    /*- Load recipe main categories ------------------------------------------------------------- */
    private void populateBRecipes() {

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT recipes._id, recipes.recipe_id, recipes.recipe_user_id, " +
                "recipes.recipe_title, recipes.recipe_category_id, recipes.recipe_language, " +
                "recipes.recipe_introduction, recipes.recipe_directions, recipes.recipe_image_path, " +
                "recipes.recipe_image, recipes.recipe_thumb, recipes.recipe_video, " +
                "recipes.recipe_date, recipes.recipe_time, recipes.recipe_cusine_id, recipes.recipe_season_id, " +
                "recipes.recipe_occasion_id, recipes.recipe_marked_as_spam, recipes.recipe_unique_hits, " +
                "recipes.recipe_unique_hits_ip_block, recipes.recipe_comments, recipes.recipe_user_ip, " +
                "recipes.recipe_password, recipes.recipe_last_viewed, recipes_numbers.number_hundred_calories,  " +
                "recipes_numbers.number_hundred_proteins, recipes_numbers.number_hundred_fat, recipes_numbers.number_hundred_carbs, recipes_numbers.number_serving_calories, " +
                "recipes_numbers.number_serving_proteins, recipes_numbers.number_serving_fat, recipes_numbers.number_serving_carbs " +
                "FROM recipes " +
                "JOIN recipes_numbers ON recipes.recipe_id=recipes_numbers.number_recipe_id " +
                " WHERE recipes.recipe_category_id=" + currentRecipeMainCategoryId + " " +
                "AND recipes.recipe_language='" + myUserLanguage + "' ORDER BY recipes.recipe_title ASC";
        cursorRecipes = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);


        // Setup cursor adapter using cursor from last step
        FoodDiaryAddRecipeAdapter categoriesAdapter = new FoodDiaryAddRecipeAdapter(this, cursorRecipes);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter


        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateBRecipesClicked(arg2);
            }
        });


        db.close();

    } // populateARecipeMainCategories

    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateBRecipesClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorRecipes.moveToPosition(listItemIDClicked);

        // Get Main Category
        int currentRecipe_id = cursorRecipes.getInt(0);
        int currentRecipeId = cursorRecipes.getInt(1);

        // Load sub categories
        Intent i = new Intent(RecipesBShowRecipesInMainCategoryActivity.this, RecipesCViewRecipeActivity.class);
        i.putExtra("currentRecipeId", currentRecipeId);
        startActivity(i);
    }

}