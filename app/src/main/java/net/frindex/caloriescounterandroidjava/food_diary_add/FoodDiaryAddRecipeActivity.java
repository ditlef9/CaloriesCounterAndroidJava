package net.frindex.caloriescounterandroidjava.food_diary_add;
/**
 *
 * File: FoodDiaryAddRecipeActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestImageDownloadToCache;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FoodDiaryAddRecipeActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";


    // Class variables
    String currentEntryDate;
    String currentEntryMealName;
    int currentEntryMealId;

    // Cursor
    Cursor cursorRecipesMainCategories;
    Cursor cursorRecipes;

    int currentRecipeMainCategoryId;
    String currentRecipeMainCategoryTranslationValue;


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

    // Recipe
    int currentRecipe_id;
    int currentRecipeId;
    int currentRecipeUserId;
    String currentRecipeTitle;
    int currentRecipeCategoryId;
    String currentRecipeLanguage;
    String currentRecipeIntroduction;
    String currentRecipeDirections;
    String currentRecipeImagePath;
    String currentRecipeImage;
    String currentRecipeThumb;
    String currentRecipeVideo;
    String currentRecipeDate;
    String currentRecipeTime;
    int currentRecipeCusineId;
    int currentRecipeSeasonId;
    int currentRecipeOccasionId;
    String currentRecipeMarkedAsSpam;
    int currentRecipeUniqueHits;
    String currentRecipeUniqueHitsIpBlock;
    int currentRecipeComments;
    String currentRecipeUserIp;
    String currentRecipeNotes;
    String currentRecipePassword;
    String currentRecipeLastViewed;

    int currentNumberHundredCalories;
    int currentNumberHundredProteins;
    int currentNumberHundredFat;
    int currentNumberHundredCarbs;
    int currentNumberServingCalories;
    int currentNumberServingProteins;
    int currentNumberServingFat;
    int currentNumberServingCarbs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_add_recipe);

        // Add food data
        Bundle b = getIntent().getExtras();
        currentEntryDate = b.getString("currentEntryDate");
        currentEntryMealName = b.getString("currentEntryMealName");
        currentEntryMealId = b.getInt("currentEntryMealId");

        // Toolbar
        initializeToolbar();

        // Listeners
        listeners();

        // My user
        fetchMyProfile();

        // Recipe categories
        populateARecipeMainCategories();
    }


    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        if (currentEntryMealName.equals("breakfast")) {
            actionBar.setTitle(getResources().getString(R.string.breakfast));
        } else if (currentEntryMealName.equals("lunch")) {
            actionBar.setTitle(getResources().getString(R.string.lunch));
        } else if (currentEntryMealName.equals("before_training")) {
            actionBar.setTitle(getResources().getString(R.string.before_training));
        } else if (currentEntryMealName.equals("after_training")) {
            actionBar.setTitle(getResources().getString(R.string.after_training));
        } else if (currentEntryMealName.equals("dinner")) {
            actionBar.setTitle(getResources().getString(R.string.dinner));
        } else if (currentEntryMealName.equals("snacks")) {
            actionBar.setTitle(getResources().getString(R.string.snacks));
        } else if (currentEntryMealName.equals("supper")) {
            actionBar.setTitle(getResources().getString(R.string.supper));
        }

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
        getMenuInflater().inflate(R.menu.menu_food_diary_add, menu);
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
        else if (id == R.id.navigationMyRecipes) {
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
        else if (id == R.id.navigationSynchronize) {
            Toast.makeText(this, "Starting Synchronization..", Toast.LENGTH_SHORT).show();

            // Delete synchronization status (user want to synchronize everything)
            DBAdapter db = new DBAdapter(this);
            db.open();
            String q = "UPDATE synchronize SET last_on_local='0'";
            db.rawQuery(q);
            db.close();

            /*
            SynchronizeAFoodCategories synchronizeAFoodCategories = new SynchronizeAFoodCategories(this, myUserLanguage);
            synchronizeAFoodCategories.updateLastSynchronizedDate();

            SynchronizeIFoodDiaryGoals synchronizeIFoodDiaryGoals = new SynchronizeIFoodDiaryGoals(this);
            synchronizeIFoodDiaryGoals.updateLastSynchronizedDate();
             */
        }
        else {
            // Up button
            Intent i = new Intent(FoodDiaryAddRecipeActivity.this, MainActivity.class);
            i.putExtra("currentEntryDate", currentEntryDate);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /*- Listeners ----------------------------------------------------------------------------- */
    private void listeners() {
        // Navigation
        TextView textViewNavigationRecent = findViewById(R.id.textViewNavigationRecent);
        textViewNavigationRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewNavigationRecentClicked();
            }
        });
        TextView textViewNavigationFood = findViewById(R.id.textViewNavigationFood);
        textViewNavigationFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewNavigationFoodClicked();
            }
        });
        TextView textViewNavigationRecipes = findViewById(R.id.textViewNavigationRecipes);
        textViewNavigationRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewNavigationRecipesClicked();
            }
        });
        textViewNavigationRecipes.setPaintFlags(textViewNavigationFood.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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

        // Barcode
        ImageView imageViewScan = findViewById(R.id.imageViewScan);
        imageViewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewScanClicked();
            }
        });
        imageViewScan.setVisibility(View.GONE);


        // Previous
        TextView textViewMainPrevious = findViewById(R.id.textViewMainPrevious);
        textViewMainPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMainPreviousClicked();
            }
        });


        // Add food
        TextView textViewGram = findViewById(R.id.textViewGram);
        textViewGram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewGramOrPortionClicked("gram");
            }
        });

        TextView textViewPortion = findViewById(R.id.textViewPortion);
        textViewPortion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewGramOrPortionClicked("portion");
            }
        });

    }

    public void textViewNavigationRecentClicked() {
        Intent i = new Intent(FoodDiaryAddRecipeActivity.this, FoodDiaryAddRecentActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        startActivity(i);
    }

    public void textViewNavigationFoodClicked() {
        Intent i = new Intent(FoodDiaryAddRecipeActivity.this, FoodDiaryAddFoodActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        startActivity(i);
    }

    public void textViewNavigationRecipesClicked() {
        Intent i = new Intent(FoodDiaryAddRecipeActivity.this, FoodDiaryAddRecipeActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        startActivity(i);
    }

    public void imageViewScanClicked() {
        Intent i = new Intent(FoodDiaryAddRecipeActivity.this, FoodDiaryAddBarcodeActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        i.putExtra("currentFromClass", "FoodDiaryAddRecentActivity");
        startActivity(i);
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
        populateARecipeMainCategories();
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
    private void populateARecipeMainCategories() {

        // Hide previous
        TextView textViewMainPrevious = findViewById(R.id.textViewMainPrevious);
        textViewMainPrevious.setVisibility(View.GONE);

        View viewMainPrevious = findViewById(R.id.viewMainPrevious);
        viewMainPrevious.setVisibility(View.GONE);

        // Hide view recipe
        LinearLayout linearLayoutViewRecipe = findViewById(R.id.linearLayoutViewRecipe);
        linearLayoutViewRecipe.setVisibility(View.GONE);

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT recipes_categories._id, recipes_categories.category_id, recipes_categories_translations.category_translation_value FROM recipes_categories " +
                "JOIN recipes_categories_translations ON recipes_categories.category_id=recipes_categories_translations.category_id " +
                " WHERE category_translation_language='" + myUserLanguage + "' ORDER BY category_translation_value ASC";

        cursorRecipesMainCategories = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);


        // Setup cursor adapter using cursor from last step
        FoodDiaryAddRecipeMainCategoriesAdapter categoriesAdapter = new FoodDiaryAddRecipeMainCategoriesAdapter(this, cursorRecipesMainCategories);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter


        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateARecipeMainCategoriesClicked(arg2);
            }
        });


        db.close();

    } // populateARecipeMainCategories

    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateARecipeMainCategoriesClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorRecipesMainCategories.moveToPosition(listItemIDClicked);

        // Get Main Category
        int foodMainCategory_Id = cursorRecipesMainCategories.getInt(0);
        currentRecipeMainCategoryId = cursorRecipesMainCategories.getInt(1);
        currentRecipeMainCategoryTranslationValue = cursorRecipesMainCategories.getString(2);

        // Load sub categories
        populateBRecipes();
    }

    private void populateBRecipes() {
        // Show previous
        TextView textViewMainPrevious = findViewById(R.id.textViewMainPrevious);
        textViewMainPrevious.setVisibility(View.VISIBLE);
        textViewMainPrevious.setText(currentRecipeMainCategoryTranslationValue);

        View viewMainPrevious = findViewById(R.id.viewMainPrevious);
        viewMainPrevious.setVisibility(View.VISIBLE);


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

    } // populateBRecipes


    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateBRecipesClicked(int listItemIDClicked) {

        // Show view recipe
        LinearLayout linearLayoutViewRecipe = findViewById(R.id.linearLayoutViewRecipe);
        linearLayoutViewRecipe.setVisibility(View.VISIBLE);

        // Move cursor to ID clicked
        cursorRecipes.moveToPosition(listItemIDClicked);

        // Get Recipe
        currentRecipe_id = cursorRecipes.getInt(0);
        currentRecipeId = cursorRecipes.getInt(1);
        currentRecipeUserId = cursorRecipes.getInt(2);

        currentRecipeTitle = cursorRecipes.getString(3);
        currentRecipeCategoryId = cursorRecipes.getInt(4);
        currentRecipeLanguage = cursorRecipes.getString(5);

        currentRecipeIntroduction = cursorRecipes.getString(6);
        currentRecipeDirections = cursorRecipes.getString(7);
        currentRecipeImagePath = cursorRecipes.getString(8);
        currentRecipeImage = cursorRecipes.getString(9);
        currentRecipeThumb = cursorRecipes.getString(10);
        currentRecipeVideo = cursorRecipes.getString(11);
        currentRecipeDate = cursorRecipes.getString(12);
        currentRecipeTime = cursorRecipes.getString(13);
        currentRecipeCusineId = cursorRecipes.getInt(14);
        currentRecipeSeasonId = cursorRecipes.getInt(15);
        currentRecipeOccasionId = cursorRecipes.getInt(16);
        currentRecipeMarkedAsSpam = cursorRecipes.getString(17);
        currentRecipeUniqueHits = cursorRecipes.getInt(18);
        currentRecipeUniqueHitsIpBlock = cursorRecipes.getString(19);
        currentRecipeComments = cursorRecipes.getInt(20);
        currentRecipeUserIp = cursorRecipes.getString(21);
        currentRecipePassword = cursorRecipes.getString(22);
        currentRecipeLastViewed = cursorRecipes.getString(23);

        currentNumberHundredCalories = cursorRecipes.getInt(24);
        currentNumberHundredProteins = cursorRecipes.getInt(25);
        currentNumberHundredFat = cursorRecipes.getInt(26);
        currentNumberHundredCarbs = cursorRecipes.getInt(27);
        currentNumberServingCalories = cursorRecipes.getInt(28);
        currentNumberServingProteins = cursorRecipes.getInt(29);
        currentNumberServingFat = cursorRecipes.getInt(30);
        currentNumberServingCarbs = cursorRecipes.getInt(31);


        // Title
        TextView textViewRecipeTitle = findViewById(R.id.textViewRecipeTitle);
        textViewRecipeTitle.setText(currentRecipeTitle);

        // Introduction
        TextView textViewRecipeIntroduction = findViewById(R.id.textViewRecipeIntroduction);
        textViewRecipeIntroduction.setText(currentRecipeIntroduction);

        // Serving size
        EditText editTextRecipeServingSize = findViewById(R.id.editTextRecipeServingSize);
        editTextRecipeServingSize.setText("1");


        // Per hundred
        TextView textViewHundredCalories = findViewById(R.id.textViewHundredCalories);
        textViewHundredCalories.setText(String.valueOf(currentNumberHundredCalories));

        TextView textViewHundredFat = findViewById(R.id.textViewHundredFat);
        textViewHundredFat.setText(String.valueOf(currentNumberHundredFat));

        TextView textViewHundredCarb = findViewById(R.id.textViewHundredCarb);
        textViewHundredCarb.setText(String.valueOf(currentNumberHundredCarbs));

        TextView textViewHundredProtein = findViewById(R.id.textViewHundredProtein);
        textViewHundredProtein.setText(String.valueOf(currentNumberHundredProteins));

        // Per piece
        TextView textViewPieceCalories = findViewById(R.id.textViewPieceCalories);
        textViewPieceCalories.setText(String.valueOf(currentNumberServingCalories));

        TextView textViewPieceFat = findViewById(R.id.textViewPieceFat);
        textViewPieceFat.setText(String.valueOf(currentNumberServingFat));

        TextView textViewPieceCarb = findViewById(R.id.textViewPieceCarb);
        textViewPieceCarb.setText(String.valueOf(currentNumberServingCarbs));

        TextView textViewPieceProtein = findViewById(R.id.textViewPieceProtein);
        textViewPieceProtein.setText(String.valueOf(currentNumberServingProteins));



        // Image
        ImageView imageViewRecipeImageA = findViewById(R.id.imageViewRecipeImageA);
        if(currentRecipeImage != null) {
            if (!(currentRecipeImage.equals("")) && !(currentRecipeImage.equals(""))) {
                imageViewRecipeImageA.setVisibility(View.VISIBLE);

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentRecipeImage);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewRecipeImageA.setImageBitmap(myBitmap);
                } else {
                    // Exist in res?
                    String androidImageStyle = currentRecipeImage.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if (id != 0) {
                        imageViewRecipeImageA.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentRecipeImagePath + "/" + currentRecipeImage;
                        String imageName = currentRecipeImage;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewRecipeImageA).execute();


                    }
                }
            }
            else {
                imageViewRecipeImageA.setVisibility(View.GONE);
            }
        }
        else {
            imageViewRecipeImageA.setVisibility(View.GONE);
        }


    } // populateBRecipesClicked

    public void textViewGramOrPortionClicked(String gramOrPortion){


        // Db
        DBAdapter db = new DBAdapter(this);
        db.open();

        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inpUpdated = dfhhmmss.format(Calendar.getInstance().getTime());




        // Serving size String
        EditText editTextServingSize = findViewById(R.id.editTextRecipeServingSize);
        String stringInpEntryServingSize = editTextServingSize.getText().toString();
        stringInpEntryServingSize = stringInpEntryServingSize.replace(",", ".");
        if(stringInpEntryServingSize.equals("")) {
            Toast.makeText(this, "Please enter a serving size", Toast.LENGTH_LONG).show();
        }
        String stringInpEntryServingSizeSQL = db.quoteSmart(stringInpEntryServingSize);

        // Serving size double
        double doubleInpEntryServingSize = 0.0;
        try {
            doubleInpEntryServingSize = Double.parseDouble(stringInpEntryServingSize);
        }
        catch(NumberFormatException nfe) {
            Toast.makeText(this, "Serving size is not a number (" + nfe + ")", Toast.LENGTH_LONG).show();
        }

        // Name
        String inpEntryName = currentRecipeTitle;
        int len = inpEntryName.length();
        if(len > 63){
            inpEntryName = inpEntryName.substring(0, 60);
            inpEntryName = inpEntryName + "...";
        }
        String inpEntryNameSQL = db.quoteSmart(inpEntryName);


        // Gram or pcs
        String stringInpEntryServingSizeMeasurement = "";
        String stringInpEntryServingSizeMeasurementSQL = "";

        double doubleInpEntryEnergyPerEntry = 0;
        int intInpEntryEnergyPerEntry = 0;

        double doubleInpEntryFatPerEntry = 0;
        int intInpEntryFatPerEntry = 0;

        double doubleInpEntryCarbPerEntry = 0;
        int intInpEntryCarbPerEntry = 0;

        double doubleInpEntryProteinPerEntry = 0;
        int intInpEntryProteinPerEntry = 0;


        if (gramOrPortion.equals("gram")) {
            // Gram
            stringInpEntryServingSizeMeasurement = "gram";
            stringInpEntryServingSizeMeasurementSQL = db.quoteSmart(stringInpEntryServingSizeMeasurement);



            doubleInpEntryEnergyPerEntry = (doubleInpEntryServingSize*currentNumberHundredCalories)/100;
            intInpEntryEnergyPerEntry = (int)doubleInpEntryEnergyPerEntry;

            doubleInpEntryFatPerEntry = (doubleInpEntryServingSize*currentNumberHundredFat)/100;
            intInpEntryFatPerEntry = (int)doubleInpEntryFatPerEntry;

            doubleInpEntryCarbPerEntry = (doubleInpEntryServingSize*currentNumberHundredCarbs)/100;
            intInpEntryCarbPerEntry = (int)doubleInpEntryCarbPerEntry;

            doubleInpEntryProteinPerEntry = (doubleInpEntryServingSize*currentNumberHundredProteins)/100;
            intInpEntryProteinPerEntry = (int)doubleInpEntryProteinPerEntry;


        } // gramOrPcs = gram
        else{
            stringInpEntryServingSizeMeasurement = "portion";
            stringInpEntryServingSizeMeasurementSQL = db.quoteSmart(stringInpEntryServingSizeMeasurement);



            doubleInpEntryEnergyPerEntry = doubleInpEntryServingSize*currentNumberServingCalories;
            intInpEntryEnergyPerEntry = (int)doubleInpEntryEnergyPerEntry;

            doubleInpEntryFatPerEntry = doubleInpEntryServingSize*currentNumberServingFat;
            intInpEntryFatPerEntry = (int)doubleInpEntryFatPerEntry;

            doubleInpEntryCarbPerEntry = doubleInpEntryServingSize*currentNumberHundredCarbs;
            intInpEntryCarbPerEntry = (int)doubleInpEntryCarbPerEntry;

            doubleInpEntryProteinPerEntry = doubleInpEntryServingSize*currentNumberServingProteins;
            intInpEntryProteinPerEntry = (int)doubleInpEntryProteinPerEntry;

        } // gramOrPcs = pcs


        // Insert
        String q = "INSERT INTO food_diary_entires (_id, entry_id, entry_user_id, entry_date, " +
                "entry_meal_id, entry_food_id, entry_recipe_id, " +
                "entry_name, entry_manufacturer_name, entry_serving_size, " +
                "entry_serving_size_measurement, entry_energy_per_entry, entry_fat_per_entry, " +
                "entry_carb_per_entry, entry_protein_per_entry, entry_updated, " +
                "entry_synchronized) " +
                "VALUES (" +
                "NULL, 0, " + myUserId + ", " +
                "'" + currentEntryDate + "', " +
                currentEntryMealId + ", " +
                "0, " +
                currentRecipeId + ", " +
                inpEntryNameSQL + ", " +
                "''" + ", " +
                stringInpEntryServingSizeSQL + ", " +
                stringInpEntryServingSizeMeasurementSQL + ", " +
                intInpEntryEnergyPerEntry + ", " +
                intInpEntryFatPerEntry + ", " +
                intInpEntryCarbPerEntry + ", " +
                intInpEntryProteinPerEntry + ", " +
                "'" + inpUpdated + "', " +
                0 +
                ")";
        db.rawQuery(q);



        // food_diary_totals_meals :: Calculate :: Get all meals for that day, and update numbers
        String query = "SELECT _id, entry_id, entry_energy_per_entry, entry_fat_per_entry, entry_carb_per_entry, entry_protein_per_entry " +
                "FROM food_diary_entires WHERE entry_user_id=" + myUserId + " " +
                "AND entry_date='" + currentEntryDate + "' " +
                "AND entry_meal_id=" + currentEntryMealId;
        Cursor cursorDiaryEntries = db.rawQuery(query);

        int intInpTotalMealEnergy = 0;
        int intInpTotalMealFat = 0;
        int intInpTotalMealCarb = 0;
        int intInpTotalMealProtein = 0;

        for (int x = 0; x < cursorDiaryEntries.getCount(); x++) {

            int getEntry_Id = cursorDiaryEntries.getInt(0);
            int getEntryId = cursorDiaryEntries.getInt(1);
            int getEntryEnergyPerEntry = cursorDiaryEntries.getInt(2);
            int getEntryFatPerEntry = cursorDiaryEntries.getInt(3);
            int getEntryCarbPerEntry = cursorDiaryEntries.getInt(4);
            int getEntryProteinPerEntry = cursorDiaryEntries.getInt(5);

            intInpTotalMealEnergy = intInpTotalMealEnergy + getEntryEnergyPerEntry;
            intInpTotalMealFat = intInpTotalMealFat + getEntryFatPerEntry;
            intInpTotalMealCarb = intInpTotalMealCarb + getEntryCarbPerEntry;
            intInpTotalMealProtein = intInpTotalMealProtein + getEntryProteinPerEntry;

            cursorDiaryEntries.moveToNext();
        }

        // food_diary_totals_meals :: --> Check if food diary total meals exists
        q = "SELECT _id FROM food_diary_totals_meals WHERE total_meal_user_id=" + myUserId + " " +
                "AND total_meal_date='" + currentEntryDate + "' " +
                "AND total_meal_meal_id=" + currentEntryMealId;
        Cursor cursorCheck = db.rawQuery(q);
        int size = cursorCheck.getCount();
        if(size == 0) {
            // Insert
            // Toast.makeText(this, "First meal of the day is " + intInpEntryFoodEnergyPerEntry + " cal", Toast.LENGTH_SHORT).show();
            q = "INSERT INTO food_diary_totals_meals (_id, total_meal_id, total_meal_user_id, " +
                    "total_meal_date, total_meal_meal_id, total_meal_energy, " +
                    "total_meal_fat, total_meal_carb, total_meal_protein, " +
                    "total_meal_updated, total_meal_synchronized) " +
                    "VALUES (" +
                    "NULL, " + '0' + ", " + myUserId + ", " +
                    "'" + currentEntryDate + "', " +
                    currentEntryMealId + ", " +
                    intInpTotalMealEnergy + ", " +
                    intInpTotalMealFat + ", " +
                    intInpTotalMealCarb + ", " +
                    intInpTotalMealProtein + ", " +
                    "'" + inpUpdated + "', " +
                    0 +
                    ")";
            db.rawQuery(q);
        }
        else{
            String qUpdate = "UPDATE food_diary_totals_meals SET " +
                    "total_meal_energy='" + intInpTotalMealEnergy + "', " +
                    "total_meal_fat='" + intInpTotalMealFat + "', " +
                    "total_meal_carb='" + intInpTotalMealCarb + "', " +
                    "total_meal_protein='" + intInpTotalMealProtein + "', " +
                    "total_meal_updated='" + inpUpdated + "', " +
                    "total_meal_synchronized='0' " +
                    "WHERE total_meal_user_id=" + myUserId + " " +
                    "AND total_meal_date='" + currentEntryDate + "' " +
                    "AND total_meal_meal_id=" + currentEntryMealId;
            db.rawQuery(qUpdate);
        } // diary total meals exists



        // food_diary_totals_days
        q = "SELECT _id, total_day_id, total_day_user_id, total_day_date, " +
                "total_day_consumed_energy, total_day_consumed_fat, total_day_consumed_carb, " +
                "total_day_consumed_protein, total_day_target_sedentary_energy, total_day_target_sedentary_fat, " +
                "total_day_target_sedentary_carb, total_day_target_sedentary_protein, total_day_target_with_activity_energy, " +
                "total_day_target_with_activity_fat, total_day_target_with_activity_carb, total_day_target_with_activity_protein, " +
                "total_day_diff_sedentary_energy, total_day_diff_sedentary_fat, total_day_diff_sedentary_carb, " +
                "total_day_diff_sedentary_protein, total_day_diff_with_activity_energy, total_day_diff_with_activity_fat, " +
                "total_day_diff_with_activity_carb, total_day_diff_with_activity_protein " +
                "FROM food_diary_totals_days WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + currentEntryDate + "'";
        Cursor cursorTotalDays = db.rawQuery(q);
        size = cursorTotalDays.getCount();

        int totalDay_id = cursorTotalDays.getInt(0);
        int totalDayId = cursorTotalDays.getInt(1);
        int totalDayUserId = cursorTotalDays.getInt(2);
        String totalDayDate = cursorTotalDays.getString(3);
        int totalDayConsumedEnergy = cursorTotalDays.getInt(4);
        int totalDayConsumedFat = cursorTotalDays.getInt(5);
        int totalDayConsumedCarb = cursorTotalDays.getInt(6);
        int totalDayConsumedProtein = cursorTotalDays.getInt(7);
        int totalDayTargetSedentaryEnergy = cursorTotalDays.getInt(8);
        int totalDayTargetSedentaryFat = cursorTotalDays.getInt(9);
        int totalDayTargetSedentaryCarb = cursorTotalDays.getInt(10);
        int totalDayTargetSedentaryProtein = cursorTotalDays.getInt(11);
        int totalDayTargetWithActivityEnergy = cursorTotalDays.getInt(12);
        int totalDayTargetWithActivityFat = cursorTotalDays.getInt(13);
        int totalDayTargetWithActivityCarb = cursorTotalDays.getInt(14);
        int totalDayTargetWithActivityProtein = cursorTotalDays.getInt(15);
        int totalDayDiffSedentaryEnergy = cursorTotalDays.getInt(16);
        int totalDayDiffSedentaryFat = cursorTotalDays.getInt(17);
        int totalDayDiffSedentaryCarb = cursorTotalDays.getInt(18);
        int totalDayDiffSedentaryProtein = cursorTotalDays.getInt(19);
        int totalDayDiffWithActivityEnergy = cursorTotalDays.getInt(20);
        int totalDayDiffWithActivityFat = cursorTotalDays.getInt(21);
        int totalDayDiffWithActivityCarb = cursorTotalDays.getInt(22);
        int totalDayDiffWithActivityProtein = cursorTotalDays.getInt(23);


        int inpTotalDayConsumedEnergy = 0;
        int inpTotalDayConsumedFat = 0;
        int inpTotalDayConsumedCarb = 0;
        int inpTotalDayConsumedProtein = 0;

        q = "SELECT _id, total_meal_id, total_meal_energy, total_meal_fat, total_meal_carb, total_meal_protein FROM food_diary_totals_meals " +
                "WHERE total_meal_user_id=" + myUserId + " AND total_meal_date='" + currentEntryDate + "'";
        Cursor cursorTotalMeals = db.rawQuery(q);
        for(int x=0;x<cursorTotalMeals.getCount();x++){


            int totalMeal_id = cursorTotalMeals.getInt(0);
            int totalMealId = cursorTotalMeals.getInt(1);
            int totalMealEnergy = cursorTotalMeals.getInt(2);
            int totalMealFat = cursorTotalMeals.getInt(3);
            int totalMealCarb = cursorTotalMeals.getInt(4);
            int totalMealProtein = cursorTotalMeals.getInt(5);


            inpTotalDayConsumedEnergy = inpTotalDayConsumedEnergy + totalMealEnergy;
            inpTotalDayConsumedFat = inpTotalDayConsumedFat + totalMealFat;
            inpTotalDayConsumedCarb = inpTotalDayConsumedCarb + totalMealCarb;
            inpTotalDayConsumedProtein = inpTotalDayConsumedProtein + totalMealProtein;

            cursorTotalMeals.moveToNext();
        }

        int inpTotalDayDiffSedentaryEnergy = totalDayTargetSedentaryEnergy-inpTotalDayConsumedEnergy;
        int inpTotalDayDiffSedentaryFat = totalDayTargetSedentaryFat-inpTotalDayConsumedFat;
        int inpTotalDayDiffSedentaryCarb = totalDayTargetSedentaryCarb-inpTotalDayConsumedCarb;
        int inpTotalDayDiffSedentaryProtein = totalDayTargetSedentaryProtein-inpTotalDayConsumedProtein;


        int inpTotalDayDiffWithActivityEnergy = totalDayTargetWithActivityEnergy-inpTotalDayConsumedEnergy;
        int inpTotalDayDiffWithActivityFat = totalDayTargetWithActivityFat-inpTotalDayConsumedFat;
        int inpTotalDayDiffWithActivityCarb = totalDayTargetWithActivityCarb-inpTotalDayConsumedCarb;
        int inpTotalDayDiffWithActivityProtein = totalDayTargetWithActivityProtein-inpTotalDayConsumedProtein;

        String update = "UPDATE food_diary_totals_days SET " +
                "total_day_consumed_energy='" + inpTotalDayConsumedEnergy + "', " +
                "total_day_consumed_fat='" + inpTotalDayConsumedFat + "', " +
                "total_day_consumed_carb='" + inpTotalDayConsumedCarb + "', " +
                "total_day_consumed_protein=" + inpTotalDayConsumedProtein + ", " +

                "total_day_diff_sedentary_energy='" + inpTotalDayDiffSedentaryEnergy + "', " +
                "total_day_diff_sedentary_fat='" + inpTotalDayDiffSedentaryFat + "', " +
                "total_day_diff_sedentary_carb='" + inpTotalDayDiffSedentaryCarb + "', " +
                "total_day_diff_sedentary_protein='" + inpTotalDayDiffSedentaryProtein + "', " +

                "total_day_diff_with_activity_energy='" + inpTotalDayDiffWithActivityEnergy + "', " +
                "total_day_diff_with_activity_fat='" + inpTotalDayDiffWithActivityFat + "', " +
                "total_day_diff_with_activity_carb='" + inpTotalDayDiffWithActivityCarb + "', " +
                "total_day_diff_with_activity_protein='" + inpTotalDayDiffWithActivityProtein + "', " +
                "total_day_updated='" + inpUpdated + "', " +
                "total_day_synchronized='0' " +
                "WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + currentEntryDate + "'";
        db.rawQuery(update);


        // Insert into last used food
        DateFormat dfF = new SimpleDateFormat("F");
        String dayOfTheWeek = dfF.format(Calendar.getInstance().getTime());

        query = "SELECT last_used_id, last_used_times FROM food_diary_last_used WHERE last_used_user_id=" + myUserId + " " +
                "AND last_used_day_of_week='" + dayOfTheWeek + "' AND last_used_meal_id=" + currentEntryMealId + " " +
                "AND last_used_recipe_id=" + currentRecipeId + "";
        Cursor cursorLastUsed = db.rawQuery(query);
        if(cursorLastUsed.getCount() == 0){

            // First time used this food
            String insert = "INSERT INTO food_diary_last_used (_id, last_used_id, last_used_user_id, " +
                    "last_used_day_of_week, last_used_meal_id, last_used_food_id, " +
                    "last_used_recipe_id, last_used_serving_size, last_used_times, " +
                    "last_used_date, last_used_updated, last_used_synchronized, " +
                    "last_used_name, last_used_manufacturer, last_used_image_path, " +
                    "last_used_image_thumb, last_used_net_content, last_used_net_content_measurement, " +
                    "last_used_serving_size_gram, last_used_serving_size_gram_measurement, last_used_serving_size_pcs, " +
                    "last_used_serving_size_pcs_measurement, last_used_calories_per_hundred, last_used_fat_per_hundred, " +
                    "last_used_saturated_fatty_acids_per_hundred, last_used_carbs_per_hundred, last_used_sugar_per_hundred, " +
                    "last_used_proteins_per_hundred, last_used_salt_per_hundred, last_used_calories_per_serving, " +
                    "last_used_fat_per_serving, last_used_saturated_fatty_acids_per_serving, last_used_carbs_per_serving, " +
                    "last_used_sugar_per_serving, last_used_proteins_per_serving, last_used_salt_per_serving)" +
                    "VALUES" +
                    "(NULL, " +
                    "'0', " +
                    myUserId + ", " +
                    "'" + dayOfTheWeek + "', " +
                    "'" + currentEntryMealId + "', " +
                    "'0', " +
                    "'" + currentRecipeId + "', " +
                    stringInpEntryServingSizeSQL + ", " +
                    "'1', " +
                    "'" + currentEntryDate + "', " +
                    "'" + inpUpdated + "', " +
                    "0, " +

                    inpEntryNameSQL + ", " +
                    "''" + ", " +
                    "'" + currentRecipeImagePath + "', " +
                    "'" + currentRecipeThumb + "', " +
                    "'0', " +
                    "'0', " +

                    "'" + "100" + "', " +
                    "'" + "g" + "', " +
                    "'" + "1" + "', " +
                    "'" + "portion" + "', " +
                    currentNumberHundredCalories + ", " +
                    currentNumberHundredFat + ", " +
                    "'0', " +
                    currentNumberHundredCarbs + ", " +
                    "'0', " +
                    currentNumberHundredProteins + ", " +
                    "'0', " +

                    currentNumberServingCalories + ", " +
                    currentNumberServingFat + ", " +
                    "'0', " +
                    currentNumberServingCarbs + ", " +
                    "'0', " +
                    currentNumberServingProteins + ", " +
                    "'0'" +
                    ")";
            db.rawQuery(insert);

        }
        else{
            // Update counter and date
            int lastUsedId = cursorLastUsed.getInt(0);
            int lastUsedTimes = cursorLastUsed.getInt(1);

            int inpLastUsedTimes = lastUsedTimes + 1;

            update = "UPDATE food_diary_last_used SET " +
                    "last_used_times='" + inpLastUsedTimes + "', " +
                    "last_used_serving_size=" + stringInpEntryServingSizeSQL + ", " +
                    "last_used_date='" + currentEntryDate + "', " +
                    "last_used_updated='" + inpUpdated + "', " +
                    "last_used_synchronized='0' " +
                    " WHERE last_used_user_id=" + myUserId + " " +
                    "AND last_used_day_of_week='" + dayOfTheWeek + "' AND last_used_meal_id=" + currentEntryMealId + " " +
                    "AND last_used_recipe_id=" + currentRecipeId + "";
            db.rawQuery(update);

        }


        // Send to server
        String url    = apiFooDiaryURL + "/post_new_food_diary_add_food.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);


        data.put("inp_entry_date", currentEntryDate);
        data.put("inp_entry_meal_id", String.valueOf(currentEntryMealId));
        data.put("inp_entry_food_id",  "0");
        data.put("inp_entry_recipe_id", String.valueOf(currentRecipeId));
        data.put("inp_entry_name", inpEntryName);
        data.put("inp_entry_manufacturer_name", "");
        data.put("inp_entry_serving_size", stringInpEntryServingSize);
        data.put("inp_entry_serving_size_measurement", stringInpEntryServingSizeMeasurement);
        data.put("inp_entry_energy_per_entry", String.valueOf(intInpEntryEnergyPerEntry));
        data.put("inp_entry_fat_per_entry", String.valueOf(intInpEntryFatPerEntry));
        data.put("inp_entry_carb_per_entry", String.valueOf(intInpEntryCarbPerEntry));
        data.put("inp_entry_protein_per_entry", String.valueOf(intInpEntryProteinPerEntry));
        data.put("inp_entry_updated", inpUpdated);

        data.put("inp_total_meal_energy", String.valueOf(intInpTotalMealEnergy));
        data.put("inp_total_meal_fat", String.valueOf(intInpTotalMealFat));
        data.put("inp_total_meal_carb", String.valueOf(intInpTotalMealCarb));
        data.put("inp_total_meal_protein", String.valueOf(intInpTotalMealProtein));

        data.put("inp_total_day_consumed_energy", String.valueOf(inpTotalDayConsumedEnergy));
        data.put("inp_total_day_consumed_fat", String.valueOf(inpTotalDayConsumedFat));
        data.put("inp_total_day_consumed_carb", String.valueOf(inpTotalDayConsumedCarb));
        data.put("inp_total_day_consumed_protein", String.valueOf(inpTotalDayConsumedProtein));

        data.put("inp_total_day_target_sedentary_energy", String.valueOf(totalDayTargetSedentaryEnergy));
        data.put("inp_total_day_target_sedentary_fat", String.valueOf(totalDayTargetSedentaryFat));
        data.put("inp_total_day_target_sedentary_carb", String.valueOf(totalDayTargetSedentaryCarb));
        data.put("inp_total_day_target_sedentary_protein", String.valueOf(totalDayTargetSedentaryProtein));

        data.put("inp_total_day_target_with_activity_energy", String.valueOf(totalDayTargetWithActivityEnergy));
        data.put("inp_total_day_target_with_activity_fat", String.valueOf(totalDayTargetWithActivityFat));
        data.put("inp_total_day_target_with_activity_carb", String.valueOf(totalDayTargetWithActivityCarb));
        data.put("inp_total_day_target_with_activity_protein", String.valueOf(totalDayTargetWithActivityProtein));

        data.put("inp_total_day_diff_sedentary_energy", String.valueOf(inpTotalDayDiffSedentaryEnergy));
        data.put("inp_total_day_diff_sedentary_fat", String.valueOf(inpTotalDayDiffSedentaryFat));
        data.put("inp_total_day_diff_sedentary_carb", String.valueOf(inpTotalDayDiffSedentaryCarb));
        data.put("inp_total_day_diff_sedentary_protein", String.valueOf(inpTotalDayDiffSedentaryProtein));

        data.put("inp_total_day_diff_with_activity_energy", String.valueOf(inpTotalDayDiffWithActivityEnergy));
        data.put("inp_total_day_diff_with_activity_fat", String.valueOf(inpTotalDayDiffWithActivityFat));
        data.put("inp_total_day_diff_with_activity_carb", String.valueOf(inpTotalDayDiffWithActivityCarb));
        data.put("inp_total_day_diff_with_activity_protein", String.valueOf(inpTotalDayDiffWithActivityProtein));
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                textViewGramOrPcsClickedSendUpdatesToServerAnswer();
            }
        });
        task.execute();

        // Go to main
        Intent i = new Intent(FoodDiaryAddRecipeActivity.this, MainActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        startActivity(i);
        finish();
    } // textViewGramOrPcsClicked

    public void textViewGramOrPcsClickedSendUpdatesToServerAnswer(){
        // Date


        DateFormat dfF = new SimpleDateFormat("F");
        String inpDayOfTheWeek = dfF.format(Calendar.getInstance().getTime());


        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        // Format: entry_id:X | total_meal_id:X | total_day_id:X
        String[] resultArray = dataResult.split("-");
        String entryId = resultArray[0];
        String entryIdSQL = db.quoteSmart(entryId);

        String totalMealId = resultArray[1];
        String totalMealIdSQL = db.quoteSmart(totalMealId);

        String totalDayId = resultArray[2];
        String totalDayIdSQL = db.quoteSmart(totalDayId);

        String lastUsedIdSQL = db.quoteSmart("9999");
        try {
            String lastUsedId = resultArray[3];
            lastUsedIdSQL = db.quoteSmart(lastUsedId);

        }
        catch (Exception e){
            Toast.makeText(this, "textViewGramOrPcsClickedSendUpdatesToServerAnswer: " + e.getMessage(), Toast.LENGTH_LONG).show();


        }
        String update = "UPDATE food_diary_entires SET entry_id=" + entryIdSQL + " WHERE entry_id=0";
        db.rawQuery(update);

        update = "UPDATE food_diary_totals_meals SET total_meal_id=" + totalMealIdSQL + " WHERE total_meal_user_id=" + myUserId + " " +
                "AND total_meal_date='" + currentEntryDate + "' " +
                "AND total_meal_meal_id=" + currentEntryMealId;
        db.rawQuery(update);

        update = "UPDATE food_diary_totals_days SET total_day_id=" + totalDayIdSQL + " WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + currentEntryDate + "'";
        db.rawQuery(update);




        update = "UPDATE food_diary_last_used SET last_used_id=" + lastUsedIdSQL + " WHERE last_used_user_id=" + myUserId + " " +
                "AND last_used_day_of_week='" + inpDayOfTheWeek + "' AND last_used_meal_id=" + currentEntryMealId + " " +
                "AND last_used_recipe_id=" + currentRecipeId + "";
        db.rawQuery(update);


        // Close db
        db.close();

        // Return
        Toast.makeText(this, "Saved to diary", Toast.LENGTH_SHORT).show();


    } // textViewGramOrPcsClickedSendUpdatesToServerAnswer
}