package net.frindex.caloriescounterandroidjava.food_diary_add;
/**
 *
 * File: FoodDiaryAddFoodActivity.java
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

public class FoodDiaryAddFoodActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // Class variables
    String currentEntryDate;
    String currentEntryMealName;
    int currentEntryMealId;

    Cursor cursorFoodMainCategories;
    Cursor cursorFoodSubCategories;
    Cursor cursorFood;

    int currentFoodMainCategoryId;
    String currentFoodMainCategoryTranslationValue;
    int currentFoodSubCategoryId;
    String currentFoodSubCategoryTranslationValue;

    // Food selected
    int currentFood_Id;
    int currentFoodId;
    int currentFoodUserId;
    String currentFoodName;
    String currentFoodManufacturerName;
    String currentFoodDescription;
    String stringCurrentFoodServingSizeGram;
    double doubleCurrentFoodServingSizeGram;
    String currentFoodServingSizeGramMeasurement;
    String stringCurrentFoodServingSizePcs;
    double doubleCurrentFoodServingSizePcs;
    String currentFoodServingSizePcsMeasurement;
    int currentFoodEnergy;
    int currentFoodProteins;
    int currentFoodCarbohydrates;
    int currentFoodFat;
    int currentFoodEnergyCalculated;
    int currentFoodProteinsCalculated;
    int currentFoodCarbohydratesCalculated;
    int currentFoodFatCalculated;
    String currentFoodBarcode;
    int currentFoodCategoryId;
    String currentFoodImagePath;
    String currentFoodThumb;
    String currentFoodImageA;
    String currentFoodImageB;
    String currentFoodImageC;
    String currentFoodImageD;
    String currentFoodImageE;
    int currentFoodUniqueHits;
    int currentFoodLikes;
    int currentFoodDislikes;



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
        setContentView(R.layout.activity_food_diary_add_food);


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

        // Populate food
        populateAFoodMainCategories();
    }



    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        if(currentEntryMealName.equals("breakfast")) {
            actionBar.setTitle(getResources().getString(R.string.breakfast));
        }
        else if(currentEntryMealName.equals("lunch")) {
            actionBar.setTitle(getResources().getString(R.string.lunch));
        }
        else if(currentEntryMealName.equals("before_training")) {
            actionBar.setTitle(getResources().getString(R.string.before_training));
        }
        else if(currentEntryMealName.equals("after_training")) {
            actionBar.setTitle(getResources().getString(R.string.after_training));
        }
        else if(currentEntryMealName.equals("dinner")) {
            actionBar.setTitle(getResources().getString(R.string.dinner));
        }
        else if(currentEntryMealName.equals("snacks")) {
            actionBar.setTitle(getResources().getString(R.string.snacks));
        }
        else if(currentEntryMealName.equals("supper")) {
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
            Intent i = new Intent(FoodDiaryAddFoodActivity.this, MainActivity.class);
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
        textViewNavigationFood.setPaintFlags(textViewNavigationFood.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView textViewNavigationRecipes = findViewById(R.id.textViewNavigationRecipes);
        textViewNavigationRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewNavigationRecipesClicked();
            }
        });

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
        TextView textViewSubPrevious = findViewById(R.id.textViewSubPrevious);
        textViewSubPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSubPreviousClicked();
            }
        });

        // Add food
        TextView textViewGram = findViewById(R.id.textViewGram);
        textViewGram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewGramOrPcsClicked("gram");
            }
        });

        TextView textViewPcs = findViewById(R.id.textViewPcs);
        textViewPcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewGramOrPcsClicked("pcs");
            }
        });

    }
    public void textViewNavigationRecentClicked(){
        Intent i = new Intent(FoodDiaryAddFoodActivity.this, FoodDiaryAddRecentActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        startActivity(i);
    }
    public void textViewNavigationFoodClicked(){
        Intent i = new Intent(FoodDiaryAddFoodActivity.this, FoodDiaryAddFoodActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        startActivity(i);
    }
    public void textViewNavigationRecipesClicked(){
        Intent i = new Intent(FoodDiaryAddFoodActivity.this, FoodDiaryAddRecipeActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        startActivity(i);
    }
    public void imageViewScanClicked(){
        Intent i = new Intent(FoodDiaryAddFoodActivity.this, FoodDiaryAddBarcodeActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        i.putExtra("currentEntryMealName", currentEntryMealName);
        i.putExtra("currentEntryMealId", currentEntryMealId);
        i.putExtra("currentFromClass", "FoodDiaryAddRecentActivity");
        startActivity(i);
    }
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
    public void textViewMainPreviousClicked(){
        populateAFoodMainCategories();

    } // textViewMainPreviousClicked
    public void textViewSubPreviousClicked(){
        populateBFoodSubCategories();
    } // textViewSubPreviousClicked

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

        // Hide View food
        LinearLayout linearLayoutViewFood = findViewById(R.id.linearLayoutViewFood);
        linearLayoutViewFood.setVisibility(View.GONE);

        // Hide previous
        TextView textViewMainPrevious = findViewById(R.id.textViewMainPrevious);
        textViewMainPrevious.setVisibility(View.GONE);

        View viewMainPrevious = findViewById(R.id.viewMainPrevious);
        viewMainPrevious.setVisibility(View.GONE);

        TextView textViewSubPrevious = findViewById(R.id.textViewSubPrevious);
        textViewSubPrevious.setVisibility(View.GONE);

        View viewSubPrevious = findViewById(R.id.viewSubPrevious);
        viewSubPrevious.setVisibility(View.GONE);

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
        currentFoodMainCategoryId = cursorFoodMainCategories.getInt(1);
        currentFoodMainCategoryTranslationValue = cursorFoodMainCategories.getString(4);

        // Load sub categories
        populateBFoodSubCategories();
    }

    /*- Populate B Food Sub Categories ---------------------------------------------------------- */
    public void populateBFoodSubCategories(){
        // Hide View food
        LinearLayout linearLayoutViewFood = findViewById(R.id.linearLayoutViewFood);
        linearLayoutViewFood.setVisibility(View.GONE);

        // Show previous
        TextView textViewMainPrevious = findViewById(R.id.textViewMainPrevious);
        textViewMainPrevious.setVisibility(View.VISIBLE);
        textViewMainPrevious.setText(currentFoodMainCategoryTranslationValue);

        View viewMainPrevious = findViewById(R.id.viewMainPrevious);
        viewMainPrevious.setVisibility(View.VISIBLE);

        TextView textViewSubPrevious = findViewById(R.id.textViewSubPrevious);
        textViewSubPrevious.setVisibility(View.GONE);

        View viewSubPrevious = findViewById(R.id.viewSubPrevious);
        viewSubPrevious.setVisibility(View.GONE);


        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT food_categories._id, food_categories.category_id, food_categories.category_name, food_categories.category_parent_id, " +
                "food_categories_translations.category_translation_value FROM food_categories " +
                "JOIN food_categories_translations ON food_categories.category_id=food_categories_translations.category_id " +
                " WHERE food_categories.category_user_id='0' " +
                "AND food_categories.category_parent_id='" + currentFoodMainCategoryId + "' " +
                "AND food_categories_translations.category_translation_language='" +  myUserLanguage + "' ORDER BY category_translation_value ASC";

        cursorFoodSubCategories = db.rawQuery(query);

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);


        // Setup cursor adapter using cursor from last step
        FoodDiaryAddFoodSubCategoriesAdapter categoriesAdapter = new FoodDiaryAddFoodSubCategoriesAdapter(this, cursorFoodSubCategories);

        // Attach cursor adapter to the ListView
        lvItems.setAdapter(categoriesAdapter); // uses categoriesAdapter



        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                populateBFoodSubCategoriesClicked(arg2);
            }
        });


        db.close();

    } // populateAFoodMainCategories
    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateBFoodSubCategoriesClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorFoodSubCategories.moveToPosition(listItemIDClicked);

        // Get Sub category
        int foodSubCategory_Id = cursorFoodSubCategories.getInt(0);
        currentFoodSubCategoryId = cursorFoodSubCategories.getInt(1);
        currentFoodSubCategoryTranslationValue = cursorFoodSubCategories.getString(4);

        // Load Food
        populateCFood();
    }

    private void populateCFood(){
        // Hide View food
        LinearLayout linearLayoutViewFood = findViewById(R.id.linearLayoutViewFood);
        linearLayoutViewFood.setVisibility(View.GONE);

        // Show previous
        TextView textViewSubPrevious = findViewById(R.id.textViewSubPrevious);
        textViewSubPrevious.setVisibility(View.VISIBLE);
        textViewSubPrevious.setText(currentFoodSubCategoryTranslationValue);

        View viewSubPrevious = findViewById(R.id.viewSubPrevious);
        viewSubPrevious.setVisibility(View.VISIBLE);


        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

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
                " AND food_category_id=" + currentFoodSubCategoryId + " " +
                "ORDER BY food_manufacturer_name, food_name ASC";

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
    /*- Category Item Clicked ------------------------------------------------------ */
    public void populateCFoodClicked(int listItemIDClicked) {

        // Move cursor to ID clicked
        cursorFood.moveToPosition(listItemIDClicked);

        // Get Food
        int currentFood_Id = cursorFood.getInt(0);
        currentFoodId = cursorFood.getInt(1);
        int currentFoodUserId = cursorFood.getInt(2);
        currentFoodName = cursorFood.getString(3);
        currentFoodManufacturerName = cursorFood.getString(4);
        currentFoodDescription = cursorFood.getString(5);
        stringCurrentFoodServingSizeGram = cursorFood.getString(6);
        doubleCurrentFoodServingSizeGram = cursorFood.getDouble(6);
        currentFoodServingSizeGramMeasurement = cursorFood.getString(7);
        stringCurrentFoodServingSizePcs = cursorFood.getString(8);
        doubleCurrentFoodServingSizePcs = cursorFood.getDouble(8);
        currentFoodServingSizePcsMeasurement = cursorFood.getString(9);
        currentFoodEnergy = cursorFood.getInt(10);
        currentFoodProteins = cursorFood.getInt(11);
        currentFoodCarbohydrates = cursorFood.getInt(12);
        currentFoodFat = cursorFood.getInt(13);
        currentFoodEnergyCalculated = cursorFood.getInt(14);
        currentFoodProteinsCalculated = cursorFood.getInt(15);
        currentFoodCarbohydratesCalculated = cursorFood.getInt(16);
        currentFoodFatCalculated = cursorFood.getInt(17);
        currentFoodBarcode = cursorFood.getString(18);
        currentFoodCategoryId = cursorFood.getInt(19);
        currentFoodImagePath = cursorFood.getString(20);
        currentFoodThumb = cursorFood.getString(21);
        currentFoodImageA = cursorFood.getString(22);
        currentFoodImageB = cursorFood.getString(23);
        currentFoodImageC = cursorFood.getString(24);
        currentFoodImageD = cursorFood.getString(25);
        currentFoodImageE = cursorFood.getString(26);
        currentFoodUniqueHits = cursorFood.getInt(27);
        currentFoodLikes = cursorFood.getInt(28);
        currentFoodDislikes = cursorFood.getInt(29);

        // Load Food
        loadDFood(currentFoodId);
    }

    /*- Food clicked ---------------------------------------------------------------------------- */
    public void loadDFood(int foodId){

        // Show View food
        LinearLayout linearLayoutViewFood = findViewById(R.id.linearLayoutViewFood);
        linearLayoutViewFood.setVisibility(View.VISIBLE);


        // Name
        TextView textViewFoodName = findViewById(R.id.textViewFoodName);
        textViewFoodName.setText(currentFoodManufacturerName + " " + currentFoodName);

        // Description
        TextView textViewFoodDescription = findViewById(R.id.textViewFoodDescription);
        textViewFoodDescription.setText(currentFoodDescription);


        // Image A
        ImageView imageViewFoodImageA = findViewById(R.id.imageViewFoodImageA);
        if(currentFoodImageA != null) {
            if (!(currentFoodImageA.equals("")) && !(currentFoodImageA.equals(""))) {
                imageViewFoodImageA.setVisibility(View.VISIBLE);

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentFoodImageA);
                if (file.exists ()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewFoodImageA.setImageBitmap(myBitmap);
                }
                else{
                    // Exist in res?
                    String androidImageStyle = currentFoodImageA.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if(id != 0){
                        imageViewFoodImageA.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentFoodImagePath + "/" + currentFoodImageA;
                        String imageName = currentFoodImageA;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewFoodImageA).execute();


                    }
                }
            }
            else {
                imageViewFoodImageA.setVisibility(View.GONE);
            }
        }
        else {
            imageViewFoodImageA.setVisibility(View.GONE);
        }


        // Image B
        ImageView imageViewFoodImageB = findViewById(R.id.imageViewFoodImageB);
        if(currentFoodImageB != null) {
            if (!(currentFoodImageB.equals("")) && !(currentFoodImageB.equals(""))) {
                imageViewFoodImageB.setVisibility(View.VISIBLE);

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentFoodImageB);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewFoodImageB.setImageBitmap(myBitmap);
                } else {
                    // Exist in res?
                    String androidImageStyle = currentFoodImageB.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if (id != 0) {
                        imageViewFoodImageB.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentFoodImagePath + "/" + currentFoodImageB;
                        String imageName = currentFoodImageB;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewFoodImageB).execute();


                    }
                }
            }
            else {
                imageViewFoodImageB.setVisibility(View.GONE);
            }
        }
        else {
            imageViewFoodImageB.setVisibility(View.GONE);
        }
        // Image c
        ImageView imageViewFoodImageC = findViewById(R.id.imageViewFoodImageC);
        if(currentFoodImageC != null) {
            if (!(currentFoodImageC.equals("")) && !(currentFoodImageC.equals(""))) {
                imageViewFoodImageC.setVisibility(View.VISIBLE);

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentFoodImageC);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewFoodImageB.setImageBitmap(myBitmap);
                } else {
                    // Exist in res?
                    String androidImageStyle = currentFoodImageC.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if (id != 0) {
                        imageViewFoodImageC.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentFoodImagePath + "/" + currentFoodImageC;
                        String imageName = currentFoodImageC;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewFoodImageC).execute();


                    }
                }

            }
            else {
                imageViewFoodImageC.setVisibility(View.GONE);
            }
        }
        else {
            imageViewFoodImageC.setVisibility(View.GONE);
        }
        // Image D
        ImageView imageViewFoodImageD = findViewById(R.id.imageViewFoodImageD);
        if(currentFoodImageD != null) {
            if (!(currentFoodImageD.equals("")) && !(currentFoodImageD.equals(""))) {
                imageViewFoodImageD.setVisibility(View.VISIBLE);

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentFoodImageD);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewFoodImageB.setImageBitmap(myBitmap);
                } else {
                    // Exist in res?
                    String androidImageStyle = currentFoodImageD.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if (id != 0) {
                        imageViewFoodImageD.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentFoodImagePath + "/" + currentFoodImageD;
                        String imageName = currentFoodImageD;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewFoodImageD).execute();


                    }
                }
            }
            else {
                imageViewFoodImageD.setVisibility(View.GONE);
            }
        }
        else {
            imageViewFoodImageD.setVisibility(View.GONE);
        }

        // Image E
        ImageView imageViewFoodImageE = findViewById(R.id.imageViewFoodImageE);
        if(currentFoodImageE != null) {
            if (!(currentFoodImageE.equals("")) && !(currentFoodImageE.equals(""))) {
                imageViewFoodImageE.setVisibility(View.VISIBLE);

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentFoodImageE);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewFoodImageB.setImageBitmap(myBitmap);
                } else {
                    // Exist in res?
                    String androidImageStyle = currentFoodImageE.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if (id != 0) {
                        imageViewFoodImageD.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentFoodImagePath + "/" + currentFoodImageE;
                        String imageName = currentFoodImageE;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewFoodImageE).execute();


                    }
                }
            }
            else {
                imageViewFoodImageE.setVisibility(View.GONE);
            }
        }
        else {
            imageViewFoodImageE.setVisibility(View.GONE);
        }

        // Per hundred
        TextView textViewHundred = findViewById(R.id.textViewHundred);
        textViewHundred.setText("100 " + currentFoodServingSizeGramMeasurement);

        TextView textViewHundredCalories = findViewById(R.id.textViewHundredCalories);
        textViewHundredCalories.setText(String.valueOf(currentFoodEnergy));

        TextView textViewHundredFat = findViewById(R.id.textViewHundredFat);
        textViewHundredFat.setText(String.valueOf(currentFoodFat));

        TextView textViewHundredCarb = findViewById(R.id.textViewHundredCarb);
        textViewHundredCarb.setText(String.valueOf(currentFoodCarbohydrates));

        TextView textViewHundredProtein = findViewById(R.id.textViewHundredProtein);
        textViewHundredProtein.setText(String.valueOf(currentFoodProteins));

        // Per piece
        TextView textViewPiece = findViewById(R.id.textViewPiece);
        textViewPiece.setText(stringCurrentFoodServingSizePcs + " " + currentFoodServingSizePcsMeasurement);

        TextView textViewPieceCalories = findViewById(R.id.textViewPieceCalories);
        textViewPieceCalories.setText(String.valueOf(currentFoodEnergyCalculated));

        TextView textViewPieceFat = findViewById(R.id.textViewPieceFat);
        textViewPieceFat.setText(String.valueOf(currentFoodFatCalculated));

        TextView textViewPieceCarb = findViewById(R.id.textViewPieceCarb);
        textViewPieceCarb.setText(String.valueOf(currentFoodCarbohydratesCalculated));

        TextView textViewPieceProtein = findViewById(R.id.textViewPieceProtein);
        textViewPieceProtein.setText(String.valueOf(currentFoodProteinsCalculated));


        // Amount
        EditText editTextFoodServingSize = findViewById(R.id.editTextFoodServingSize);
        TextView textViewGram = findViewById(R.id.textViewGram);
        TextView textViewPcs = findViewById(R.id.textViewPcs);
        if(currentFoodServingSizePcsMeasurement.equals("g")) {
            editTextFoodServingSize.setText(stringCurrentFoodServingSizeGram);

            textViewGram.setText(currentFoodServingSizeGramMeasurement);
            textViewPcs.setVisibility(View.GONE);
        }
        else{
            editTextFoodServingSize.setText(stringCurrentFoodServingSizePcs);

            textViewGram.setText(currentFoodServingSizeGramMeasurement);
            textViewPcs.setVisibility(View.VISIBLE);
            textViewPcs.setText(currentFoodServingSizePcsMeasurement);
        }


    } // loadDFood

    public void textViewGramOrPcsClicked(String gramOrPcs){
        // Db
        DBAdapter db = new DBAdapter(this);
        db.open();

        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inpUpdated = dfhhmmss.format(Calendar.getInstance().getTime());

        // currentEntryMealId

        // currentFoodId

        // Food serving size String
        EditText editTextFoodServingSize = findViewById(R.id.editTextFoodServingSize);
        String stringInpEntryFoodServingSize = editTextFoodServingSize.getText().toString();
        stringInpEntryFoodServingSize = stringInpEntryFoodServingSize.replace(",", ".");
        if(stringInpEntryFoodServingSize.equals("")) {
            Toast.makeText(this, "Please enter a serving size", Toast.LENGTH_LONG).show();
        }
        String stringInpEntryFoodServingSizeSQL = db.quoteSmart(stringInpEntryFoodServingSize);

        // Food serving size double
        double doubleInpEntryFoodServingSize = 0.0;
        try {
            doubleInpEntryFoodServingSize = Double.parseDouble(stringInpEntryFoodServingSize);
        }
        catch(NumberFormatException nfe) {
            Toast.makeText(this, "Serving size is not a number (" + nfe + ")", Toast.LENGTH_LONG).show();
        }

        // Food name
        String inpEntryFoodName = currentFoodName;
        int len = inpEntryFoodName.length();
        if(len > 63){
            inpEntryFoodName = inpEntryFoodName.substring(0, 60);
            inpEntryFoodName = inpEntryFoodName + "...";
        }
        String inpEntryFoodNameSQL = db.quoteSmart(inpEntryFoodName);


        String inpEntryFoodManufacturerName = currentFoodManufacturerName;
        String inpEntryFoodManufacturerNameSQL = db.quoteSmart(inpEntryFoodManufacturerName);

        // Gram or pcs
        String stringInpEntryFoodServingSizeMeasurement = "";
        String stringInpEntryFoodServingSizeMeasurementSQL = "";

        double doubleInpEntryFoodEnergyPerEntry = 0;
        int intInpEntryFoodEnergyPerEntry = 0;

        double doubleInpEntryFoodFatPerEntry = 0;
        int intInpEntryFoodFatPerEntry = 0;

        double doubleInpEntryFoodCarbPerEntry = 0;
        int intInpEntryFoodCarbPerEntry = 0;

        double doubleInpEntryFoodProteinPerEntry = 0;
        int intInpEntryFoodProteinPerEntry = 0;


        if (gramOrPcs.equals("gram")) {
            // Gram
            stringInpEntryFoodServingSizeMeasurement = currentFoodServingSizeGramMeasurement;
            stringInpEntryFoodServingSizeMeasurementSQL = db.quoteSmart(stringInpEntryFoodServingSizeMeasurement);



            doubleInpEntryFoodEnergyPerEntry = (doubleInpEntryFoodServingSize*currentFoodEnergy)/100;
            intInpEntryFoodEnergyPerEntry = (int)doubleInpEntryFoodEnergyPerEntry;

            doubleInpEntryFoodFatPerEntry = (doubleInpEntryFoodServingSize*currentFoodFat)/100;
            intInpEntryFoodFatPerEntry = (int)doubleInpEntryFoodFatPerEntry;

            doubleInpEntryFoodCarbPerEntry = (doubleInpEntryFoodServingSize*currentFoodCarbohydrates)/100;
            intInpEntryFoodCarbPerEntry = (int)doubleInpEntryFoodCarbPerEntry;

            doubleInpEntryFoodProteinPerEntry = (doubleInpEntryFoodServingSize*currentFoodProteins)/100;
            intInpEntryFoodProteinPerEntry = (int)doubleInpEntryFoodProteinPerEntry;


        } // gramOrPcs = gram
        else{
            stringInpEntryFoodServingSizeMeasurement = currentFoodServingSizePcsMeasurement;
            stringInpEntryFoodServingSizeMeasurementSQL = db.quoteSmart(stringInpEntryFoodServingSizeMeasurement);

            doubleInpEntryFoodEnergyPerEntry = (doubleInpEntryFoodServingSize*currentFoodEnergyCalculated)/doubleCurrentFoodServingSizePcs;
            intInpEntryFoodEnergyPerEntry = (int)doubleInpEntryFoodEnergyPerEntry;

            doubleInpEntryFoodFatPerEntry = (doubleInpEntryFoodServingSize*currentFoodFatCalculated)/doubleCurrentFoodServingSizePcs;
            intInpEntryFoodFatPerEntry = (int)doubleInpEntryFoodFatPerEntry;

            doubleInpEntryFoodCarbPerEntry = (doubleInpEntryFoodServingSize*currentFoodCarbohydratesCalculated)/doubleCurrentFoodServingSizePcs;
            intInpEntryFoodCarbPerEntry = (int)doubleInpEntryFoodCarbPerEntry;

            doubleInpEntryFoodProteinPerEntry = (doubleInpEntryFoodServingSize*currentFoodProteinsCalculated)/doubleCurrentFoodServingSizePcs;
            intInpEntryFoodProteinPerEntry = (int)doubleInpEntryFoodProteinPerEntry;

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
                currentFoodId + ", " +
                "0," +
                inpEntryFoodNameSQL + ", " +
                inpEntryFoodManufacturerNameSQL + ", " +
                stringInpEntryFoodServingSizeSQL + ", " +
                stringInpEntryFoodServingSizeMeasurementSQL + ", " +
                intInpEntryFoodEnergyPerEntry + ", " +
                intInpEntryFoodFatPerEntry + ", " +
                intInpEntryFoodCarbPerEntry + ", " +
                intInpEntryFoodProteinPerEntry + ", " +
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
                "AND last_used_food_id=" + currentFoodId + "";
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
                    "'" + currentFoodId + "', " +
                    "'0', " +
                    stringInpEntryFoodServingSizeSQL + ", " +
                    "'1', " +
                    "'" + currentEntryDate + "', " +
                    "'" + inpUpdated + "', " +
                    "0, " +

                    inpEntryFoodNameSQL + ", " +
                    inpEntryFoodManufacturerNameSQL + ", " +
                    "'" + currentFoodImagePath + "', " +
                    "'" + currentFoodThumb + "', " +
                    "'0', " +
                    "'0', " +

                    "'" + stringCurrentFoodServingSizeGram + "', " +
                    "'" + currentFoodServingSizeGramMeasurement + "', " +
                    "'" + stringCurrentFoodServingSizePcs + "', " +
                    "'" + currentFoodServingSizePcsMeasurement + "', " +
                    currentFoodEnergy + ", " +
                    currentFoodFat + ", " +
                    "'0', " +
                    currentFoodCarbohydrates + ", " +
                    "'0', " +
                    currentFoodProteins + ", " +
                    "'0', " +

                    currentFoodEnergyCalculated + ", " +
                    currentFoodFatCalculated + ", " +
                    "'0', " +
                    currentFoodCarbohydratesCalculated + ", " +
                    "'0', " +
                    currentFoodProteinsCalculated + ", " +
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
                    "last_used_serving_size=" + stringInpEntryFoodServingSizeSQL + ", " +
                    "last_used_date='" + currentEntryDate + "', " +
                    "last_used_updated='" + inpUpdated + "', " +
                    "last_used_synchronized='0' " +
                    " WHERE last_used_user_id=" + myUserId + " " +
                    "AND last_used_day_of_week='" + dayOfTheWeek + "' AND last_used_meal_id=" + currentEntryMealId + " " +
                    "AND last_used_food_id=" + currentFoodId + "";
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
        data.put("inp_entry_food_id",  String.valueOf(currentFoodId));
        data.put("inp_entry_recipe_id", "0");
        data.put("inp_entry_name", inpEntryFoodName);
        data.put("inp_entry_manufacturer_name", inpEntryFoodManufacturerName);
        data.put("inp_entry_serving_size", stringInpEntryFoodServingSize);
        data.put("inp_entry_serving_size_measurement", stringInpEntryFoodServingSizeMeasurement);
        data.put("inp_entry_energy_per_entry", String.valueOf(intInpEntryFoodEnergyPerEntry));
        data.put("inp_entry_fat_per_entry", String.valueOf(intInpEntryFoodFatPerEntry));
        data.put("inp_entry_carb_per_entry", String.valueOf(intInpEntryFoodCarbPerEntry));
        data.put("inp_entry_protein_per_entry", String.valueOf(intInpEntryFoodProteinPerEntry));
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
        Intent i = new Intent(FoodDiaryAddFoodActivity.this, MainActivity.class);
        i.putExtra("currentEntryDate", currentEntryDate);
        startActivity(i);
        finish();
    } // textViewGramOrPcsClicked

    public void textViewGramOrPcsClickedSendUpdatesToServerAnswer(){

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

        String entryIdSQL = db.quoteSmart("99991");
        try {
            String entryId = resultArray[0];
            entryIdSQL = db.quoteSmart(entryId);
        }
        catch (Exception e){
            Toast.makeText(this, "textViewGramOrPcsClickedSendUpdatesToServerAnswer: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        String totalMealIdSQL = db.quoteSmart("99992");
        try {
            String totalMealId = resultArray[1];
            totalMealIdSQL = db.quoteSmart(totalMealId);
        }
        catch (Exception e){
            Toast.makeText(this, "textViewGramOrPcsClickedSendUpdatesToServerAnswer: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        String totalDayIdSQL = db.quoteSmart("99993");
        try {
            String totalDayId = resultArray[2];
            totalDayIdSQL = db.quoteSmart(totalDayId);
        }
        catch (Exception e){
            Toast.makeText(this, "textViewGramOrPcsClickedSendUpdatesToServerAnswer: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        String lastUsedIdSQL = db.quoteSmart("99994");
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
                "AND last_used_food_id=" + currentFoodId + "";
        db.rawQuery(update);


        // Close db
        db.close();

        // Return
        // Toast.makeText(this, "Saved to diary", Toast.LENGTH_SHORT).show();


    } // textViewGramOrPcsClickedSendUpdatesToServerAnswer
}