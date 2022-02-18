package net.frindex.caloriescounterandroidjava.recipes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.common.NonScrollListView;
import net.frindex.caloriescounterandroidjava.common.OutputString;
import net.frindex.caloriescounterandroidjava.common.WriteToErrorLog;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestImageDownloadToCache;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
public class RecipesCViewRecipeActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiRecipesURL       = "https://summerslim.codecourses.eu/recipes/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // Class variables
    int currentRecipeMainCategoryId;
    String currentRecipeMainCategoryTranslationValue;
    Cursor cursorComments;

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
    int currentNumberSumberServings;

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
        setContentView(R.layout.activity_recipes_cview_recipe);

        Bundle b = getIntent().getExtras();
        currentRecipeId = b.getInt("currentRecipeId");



        // My user
        fetchMyProfile();

        // Recipe
        showRecipe();


        // Listeners
        listeners();

        // Toolbar
        initializeToolbar();


        // fill spinner
        fillSpinnerMeals();


        // Populate comments list
        populateComments();

        // Fetch recipe from server
        // First it gets recipe updates, then comments, then author
        fetchRecipeUpdatesFromServer();
    }

    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.recipes) + " > " + currentRecipeTitle);

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


        Intent i = new Intent(RecipesCViewRecipeActivity.this, RecipesBShowRecipesInMainCategoryActivity.class);
        i.putExtra("currentRecipeMainCategoryId", currentRecipeMainCategoryId);
        i.putExtra("currentRecipeMainCategoryTranslationValue", currentRecipeMainCategoryTranslationValue);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }


    /*- Listeners ----------------------------------------------------------------------------- */
    private void listeners() {

        // Add recipe to diary
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

        Button buttonAddComment = findViewById(R.id.buttonAddComment);
        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddCommentOnClick();
            }
        });


    }
    public void buttonAddCommentOnClick(){

        Intent i = new Intent(RecipesCViewRecipeActivity.this, CommentWriteNewToRecipeActivity.class);
        i.putExtra("currentRecipeId", currentRecipeId);
        startActivity(i);


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
    public void showRecipe(){
        // DB
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
                "recipes_numbers.number_serving_proteins, recipes_numbers.number_serving_fat, recipes_numbers.number_serving_carbs, " +
                "number_servings  " +
                "FROM recipes " +
                "JOIN recipes_numbers ON recipes.recipe_id=recipes_numbers.number_recipe_id " +
                " WHERE recipes.recipe_id=" + currentRecipeId + "";
        Cursor cursorRecipes = db.rawQuery(query);


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
        currentNumberSumberServings = cursorRecipes.getInt(32);


        // Category
        query = "SELECT recipes_categories._id, recipes_categories.category_id, recipes_categories_translations.category_translation_value FROM recipes_categories " +
                "JOIN recipes_categories_translations ON recipes_categories.category_id=recipes_categories_translations.category_id " +
                " WHERE recipes_categories.category_id=" + currentRecipeCategoryId + " AND category_translation_language='" + myUserLanguage + "'";

        Cursor cursorCategory = db.rawQuery(query);
        int currentRecipeMainCategory_Id = cursorCategory.getInt(0);
        currentRecipeMainCategoryId = cursorCategory.getInt(1);
        currentRecipeMainCategoryTranslationValue = cursorCategory.getString(2);


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
                Toast.makeText(this, "No picture", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            imageViewRecipeImageA.setVisibility(View.GONE);
            Toast.makeText(this, "Picture is null", Toast.LENGTH_SHORT).show();
        }

        // Servings

        TextView textViewServings = findViewById(R.id.textViewServings);
        textViewServings.setText(String.valueOf(currentNumberSumberServings));

        // Directions
        currentRecipeDirections = currentRecipeDirections.replace("\\r", "");
        currentRecipeDirections = currentRecipeDirections.replace("\\n", "\n");
        currentRecipeDirections = currentRecipeDirections.replace("\\", "");
        WebView webViewRecipeDirections = findViewById(R.id.webViewRecipeDirections);
        webViewRecipeDirections.setBackgroundColor(Color.TRANSPARENT);
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webViewRecipeDirections.loadData(currentRecipeDirections, mimeType, encoding);

        webViewRecipeDirections.setWebViewClient(new WebViewClient(){

            @Override

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                final String url = uri.toString();
                loadUrl(url);
                return true;
            }

        });



        // Find view
        LinearLayout linearLayoutIngredients = findViewById(R.id.linearLayoutIngredients);
        linearLayoutIngredients.removeAllViews(); // Clearing i

        try {
            query = "SELECT _id, group_id, group_title FROM recipes_groups WHERE group_recipe_id=" + currentRecipeId;
            Cursor cursorGroups = db.rawQuery(query);
            for(int x=0;x<cursorGroups.getCount();x++){

                // Add Group: Strings
                int groupId = cursorGroups.getInt(1);
                String groupTitle = new OutputString().outputHTML(cursorGroups.getString(2));

                TextView TextViewGroupTitle = new TextView(this); // Add TextView Group title
                TextViewGroupTitle.setText(groupTitle);
                TextViewGroupTitle.setTextSize(18);
                TextViewGroupTitle.setTextColor(Color.BLACK);
                linearLayoutIngredients.addView(TextViewGroupTitle);

                // Items
                query = "SELECT _id, item_id, item_amount, item_measurement, item_grocery, item_calories_per_hundred, item_calories_calculated FROM recipes_items " +
                        "WHERE item_group_id=" + groupId;
                Cursor cursorItems = db.rawQuery(query);
                for(int y=0;y<cursorItems.getCount();y++) {

                    // Items: Strings
                    String amount = cursorItems.getString(2);
                    String measurement = cursorItems.getString(3);
                    String grocery = new OutputString().outputHTML(cursorItems.getString(4));
                    int caloriesPerHundred = cursorItems.getInt(5);
                    int caloriesCalculated = cursorItems.getInt(6);

                    // Create array for TableLayout groups
                    TableLayout tableLayoutItems = new TableLayout(this);

                    // Table row for item
                    TableRow tableRowItems = new TableRow(this); // Add row
                    tableRowItems.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // Text view amount
                    TextView TextViewItemAmount = new TextView(this); // Add textview
                    TextViewItemAmount.setText(amount);
                    TextViewItemAmount.setTextSize(16);
                    TextViewItemAmount.setTextColor(Color.BLACK);
                    TextViewItemAmount.setPadding(0, 0, 10, 0);
                    tableRowItems.addView(TextViewItemAmount);

                    // Text view measurement
                    TextView TextViewItemMeasurement = new TextView(this); // Add textview
                    TextViewItemMeasurement.setText(measurement);
                    TextViewItemMeasurement.setTextSize(16);
                    TextViewItemMeasurement.setTextColor(Color.BLACK);
                    TextViewItemMeasurement.setPadding(0, 0, 10, 0);
                    tableRowItems.addView(TextViewItemMeasurement);

                    // Text view grocery
                    TextView TextViewItemGrocery = new TextView(this); // Add textview
                    grocery = new OutputString().outputHTML(grocery);
                    TextViewItemGrocery.setText(grocery);
                    TextViewItemGrocery.setTextSize(16);
                    TextViewItemGrocery.setTextColor(Color.BLACK);
                    TextViewItemGrocery.setPadding(0, 0, 10, 0);
                    tableRowItems.addView(TextViewItemGrocery);
                    String kcalTranslation=getString (R.string.kcal_lowercase);

                    if(caloriesPerHundred != 0){
                        // Text view calories per hundred
                        TextView TextViewItemCaloriesPerHundred = new TextView(this); // Add textview
                        TextViewItemCaloriesPerHundred.setText("(" + caloriesPerHundred + " " + kcalTranslation + " " + getResources().getString(R.string.per_hundred_lowercase) + ")");
                        TextViewItemCaloriesPerHundred.setTextSize(16);
                        TextViewItemCaloriesPerHundred.setTextColor(Color.GRAY);
                        tableRowItems.addView(TextViewItemCaloriesPerHundred);

                    }
                    else{
                        if(caloriesCalculated != 0){
                            // Text view calories calculated
                            TextView TextViewItemCaloriesCalculated = new TextView(this); // Add textview
                            TextViewItemCaloriesCalculated.setText("(" + caloriesCalculated + " " + kcalTranslation+ ")" );
                            TextViewItemCaloriesCalculated.setTextSize(16);
                            TextViewItemCaloriesCalculated.setTextColor(Color.GRAY);
                            tableRowItems.addView(TextViewItemCaloriesCalculated);

                        }
                    }

                    tableLayoutItems.addView(tableRowItems, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)); /* Add row to TableLayout. */
                    linearLayoutIngredients.addView(tableLayoutItems);




                    cursorItems.moveToNext();
                }


                // Next group
                cursorGroups.moveToNext();

            }


        }
        catch (SQLException sqle){
            Toast.makeText(this, sqle.toString(), Toast.LENGTH_LONG).show();
        }


        db.close();
    } // showRecipe

    private void loadUrl(String url) {
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();

        if(url != null) {
            Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browse);
        }
    } // loadUrl


    private void fillSpinnerMeals() {

        String[] arraySpinnerMeal = new String[] { getResources().getString(R.string.breakfast),
                getResources().getString(R.string.lunch),
                getResources().getString(R.string.before_training),
                getResources().getString(R.string.after_training),
                getResources().getString(R.string.dinner),
                getResources().getString(R.string.snacks),
                getResources().getString(R.string.supper) };

        Spinner spinnerMeals = findViewById(R.id.spinnerMeals);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinnerMeal);
        spinnerMeals.setAdapter(adapter);


    }


    /*- Populate comments ----------------------------------------------------------- */
    public void populateComments(){

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();


        // Get all comments
        try {
            String query = "SELECT _id, comment_id, comment_date_print, comment_user_id, " +
                    "comment_user_alias, comment_user_image_path, comment_user_image_file, comment_title, comment_text, comment_rating" +
                    " FROM recipes_comments WHERE comment_recipe_id=" + currentRecipeId +
                    " ORDER BY comment_id ASC";

            cursorComments = db.rawQuery(query);
        }
        catch (SQLException sqle){
            Toast.makeText(this, "populateComments SQL: " + sqle.toString(), Toast.LENGTH_LONG).show();
        }

        // Find ListView to populate
        NonScrollListView lvItems = (NonScrollListView) findViewById(R.id.listViewComments);


        // Setup cursor adapter using cursor from last step
        RecipesViewCommentsCursorAdapter commentsAdapter = new RecipesViewCommentsCursorAdapter(this, cursorComments);

        // Attach cursor adapter to the ListView
        try{
            lvItems.setAdapter(commentsAdapter); // uses ContinensCursorAdapter
        }
        catch (Exception e){
            Toast.makeText(this, "populateComments adapter error:", Toast.LENGTH_LONG).show();
        }
        // Close db
        db.close();



        // OnClick
        /*
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                recipesListItemClicked(arg2);
            }
        });*/

    }



    public void textViewGramOrPortionClicked(String gramOrPortion){


        // Db
        DBAdapter db = new DBAdapter(this);
        db.open();

        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inpUpdated = dfhhmmss.format(Calendar.getInstance().getTime());

        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String inpEntryDate = dfyyyymmdd.format(Calendar.getInstance().getTime());

        // currentEntryMealId
        Spinner spinnerMeals = findViewById(R.id.spinnerMeals);
        int currentEntryMealId = spinnerMeals.getSelectedItemPosition();




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

            doubleInpEntryFatPerEntry = doubleInpEntryServingSize*currentNumberServingCalories;
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
                "'" + inpEntryDate + "', " +
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
                "AND entry_date='" + inpEntryDate + "' " +
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
                "AND total_meal_date='" + inpEntryDate + "' " +
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
                    "'" + inpEntryDate + "', " +
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
                    "AND total_meal_date='" + inpEntryDate + "' " +
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
                "FROM food_diary_totals_days WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + inpEntryDate + "'";
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
                "WHERE total_meal_user_id=" + myUserId + " AND total_meal_date='" + inpEntryDate + "'";
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
                "WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + inpEntryDate + "'";
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
                    "'" + inpEntryDate + "', " +
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
                    "last_used_date='" + inpEntryDate + "', " +
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


        data.put("inp_entry_date", inpEntryDate);
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
        Intent i = new Intent(RecipesCViewRecipeActivity.this, MainActivity.class);
        i.putExtra("currentEntryDate", inpEntryDate);
        startActivity(i);
        finish();
    } // textViewGramOrPcsClicked

    public void textViewGramOrPcsClickedSendUpdatesToServerAnswer(){
        // Date


        DateFormat dfF = new SimpleDateFormat("F");
        String inpDayOfTheWeek = dfF.format(Calendar.getInstance().getTime());

        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String inpEntryDate = dfyyyymmdd.format(Calendar.getInstance().getTime());

        Spinner spinnerMeals = findViewById(R.id.spinnerMeals);
        int currentEntryMealId = spinnerMeals.getSelectedItemPosition();



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
                "AND total_meal_date='" + inpEntryDate + "' " +
                "AND total_meal_meal_id=" + currentEntryMealId;
        db.rawQuery(update);

        update = "UPDATE food_diary_totals_days SET total_day_id=" + totalDayIdSQL + " WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + inpEntryDate + "'";
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





    /*- Fetch recipe from server ----------------------------------------- */
    public void fetchRecipeUpdatesFromServer(){
        // Gets full recipe from server
        DBAdapter db = new DBAdapter(this);
        db.open();
        db.truncate("json_temp_data");
        db.close();

        String url = apiRecipesURL + "/get_recipe_full_from_server.php?recipe_id=" + currentRecipeId;
        String stringMethod = "get";
        String stringSend   = "";

        //Toast.makeText(this, "URL: " + url, Toast.LENGTH_LONG).show();
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, stringSend, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerRecipeUpdatesFromServer();
            }
        });
        task.execute();
    }
    public void answerRecipeUpdatesFromServer(){

        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);
        db.close();




        if(!(dataResult.equals("")) && !(dataResult.equals("[]"))) {



            try {
                // Database
                db.open();


                JSONObject object = new JSONObject(dataResult);


                // 1. Recipe
                JSONObject recipeObject = object.getJSONObject("recipe");
                String recipeIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_id")));
                String recipeUserIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_user_id")));
                String recipeTitleSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_title")));
                String recipeCategoryIdSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_category_id")));
                String recipeLanguage = new OutputString().outputHTML(recipeObject.getString("recipe_language"));
                String recipeLanguageSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_language")));
                String recipeIntroductionSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_introduction")));
                String recipeDirectionsSQL = db.quoteSmart(recipeObject.getString("recipe_directions"));
                String recipeImagePathSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_image_path")));
                String recipeImageSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_image")));
                String recipeThumbSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_thumb")));
                String recipeMarkedAsSpamSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_marked_as_spam")));
                String recipePasswordSQL = db.quoteSmart(new OutputString().outputHTML(recipeObject.getString("recipe_password")));


                DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateyyyyMMdddfhhmmss = dfhhmmss.format(Calendar.getInstance().getTime());

                // Do we have the recipe saved from before_
                String q = "UPDATE recipes SET " +
                        "recipe_user_id=" + recipeUserIdSQL + "," +
                        "recipe_title=" + recipeTitleSQL + "," +
                        "recipe_category_id=" + recipeCategoryIdSQL + "," +
                        "recipe_language=" + recipeLanguageSQL + "," +
                        "recipe_introduction=" + recipeIntroductionSQL + "," +
                        "recipe_directions=" + recipeDirectionsSQL + "," +
                        "recipe_image_path=" + recipeImagePathSQL + "," +
                        "recipe_image=" + recipeImageSQL + "," +
                        "recipe_thumb=" + recipeThumbSQL + "," +
                        "recipe_marked_as_spam=" + recipeMarkedAsSpamSQL + "," +
                        "recipe_password=" + recipePasswordSQL + "," +
                        "recipe_last_viewed='" + dateyyyyMMdddfhhmmss + "' " +
                        " WHERE recipe_id=" + recipeIdSQL;
                try {
                    db.rawQuery(q);
                } catch (SQLException e) {
                    Toast.makeText(this, "Error recipe: " + e.toString(), Toast.LENGTH_SHORT).show();
                }


                // 2. Groups
                try {
                    JSONArray groupArray = object.getJSONArray("groups");
                    for (int i = 0; i < groupArray.length(); i++) {
                        JSONObject groupObject = groupArray.getJSONObject(i);

                        String groupIdSQL = db.quoteSmart(new OutputString().outputHTML(groupObject.getString("group_id")));
                        String groupTitleSQL = db.quoteSmart(new OutputString().outputHTML(groupObject.getString("group_title")));


                        // Do we have the group saved from before
                        q = "SELECT _id FROM recipes_groups WHERE group_id=" + groupIdSQL;
                        Cursor groupCursor = db.rawQuery(q);
                        int cursorSize = groupCursor.getCount();

                        if (cursorSize == 0) {
                            q = "INSERT INTO recipes_groups (_id, group_id, group_recipe_id, group_title) " +
                                    "VALUES ("
                                    + "NULL, "
                                    + groupIdSQL + ", "
                                    + currentRecipeId + ", "
                                    + groupTitleSQL
                                    + ")";
                            db.rawQuery(q);
                        }
                    }

                } catch (JSONException e) {
                    // Server error 500
                    Toast.makeText(this, "Error groups: " + e.toString(), Toast.LENGTH_SHORT).show();
                } // groups


                // 3. Items
                try {
                    JSONArray itemsArray = object.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObject = itemsArray.getJSONObject(i);

                        String itemId = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_id")));
                        String itemRecipeId = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_recipe_id")));
                        String itemGroupId = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_group_id")));
                        String itemAmount = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_amount")));
                        String itemMeasurement = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_measurement")));
                        String itemGrocery = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_grocery")));
                        String itemCaloriesPerHundred = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_calories_per_hundred")));
                        String itemCaloriesCalculated = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("item_calories_calculated")));


                        // Do we have the items saved from before
                        q = "SELECT _id FROM recipes_items WHERE item_id=" + itemId;
                        Cursor groupItems = db.rawQuery(q);
                        int cursorItemSize = groupItems.getCount();

                        if (cursorItemSize < 1) {
                            // Insert
                            String itemsFields = "_id, item_id, item_recipe_id, item_group_id, item_amount, item_measurement, item_grocery, item_calories_per_hundred, item_calories_calculated";
                            String itemsValues = "NULL, " +
                                    itemId + ", " +
                                    itemRecipeId + ", " +
                                    itemGroupId + ", " +
                                    itemAmount + ", " +
                                    itemMeasurement + ", " +
                                    itemGrocery + ", " +
                                    itemCaloriesPerHundred + ", " +
                                    itemCaloriesCalculated;
                            db.insert("recipes_items", itemsFields, itemsValues);
                        }
                    }

                } catch (JSONException e) {
                    // Server error 500
                    //(this, "Error items: " + e.toString(), Toast.LENGTH_SHORT).show();
                }


                // 4. Rating
                JSONObject ratingObject = object.getJSONObject("rating");

                String rating1 = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_1")));
                String rating2 = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_2")));
                String rating3 = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_3")));
                String rating4 = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_4")));
                String rating5 = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_5")));
                String ratingTotalVotes = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_total_votes")));
                String ratingAverage = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_average")));
                String ratingPopularity = db.quoteSmart(new OutputString().outputHTML(ratingObject.getString("rating_popularity")));

                // Do we have the rating saved from before
                q = "SELECT _id FROM recipes_rating WHERE rating_recipe_id=" + recipeIdSQL;
                Cursor cursorRating = db.rawQuery(q);
                int cursorRatingSize = cursorRating.getCount();

                if (cursorRatingSize < 1) {
                    q = "INSERT INTO recipes_rating (_id, rating_recipe_id, rating_1, rating_2, rating_3, rating_4, rating_5, rating_total_votes, rating_average, rating_popularity) " +
                            "VALUES (" +
                            "null, " +
                            recipeIdSQL + ", " +
                            rating1 + ", " +
                            rating2 + ", " +
                            rating3 + ", " +
                            rating4 + ", " +
                            rating5 + ", " +
                            ratingTotalVotes + ", " +
                            ratingAverage + ", " +
                            ratingPopularity +
                            ")";
                    try {
                        db.rawQuery(q);
                    } catch (SQLException e) {
                        //Toast.makeText(this, "recipes_numbers: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                // 5. Numbers
                JSONObject numbersObject = object.getJSONObject("numbers");

                String numberHundredCalories = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_hundred_calories"))));
                String numberHundredProteins = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_hundred_proteins"))));
                String numberHundredFat = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_hundred_fat"))));
                String numberHundredCarbs = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_hundred_carbs"))));
                String numberServingCalories = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_serving_calories"))));
                String numberServingProteins = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_serving_proteins"))));
                String numberServingFat = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_serving_fat"))));
                String numberServingCarbs = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_serving_carbs"))));
                String numberTotalWeight = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_total_weight"))));
                String numberTotalCalories = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_total_calories"))));
                String numberTotalProteins = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_total_proteins"))));
                String numberTotalFat = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_total_fat"))));
                String numberTotalCarbs = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_total_carbs"))));
                String numberServings = db.quoteSmart(new OutputString().outputHTML((numbersObject.getString("number_servings"))));

                // Do we have the numbers saved from before
                q = "SELECT _id FROM recipes_numbers WHERE number_recipe_id=" + recipeIdSQL;
                Cursor cursorNumbers = db.rawQuery(q);
                int cursorNumberSize = cursorNumbers.getCount();

                if (cursorNumberSize < 1) {
                    query = "INSERT INTO recipes_numbers (_id, number_recipe_id, number_hundred_calories, number_hundred_proteins, number_hundred_fat, number_hundred_carbs, number_serving_calories, number_serving_proteins, number_serving_fat, number_serving_carbs, number_total_weight, number_total_calories, number_total_proteins, number_total_fat, number_total_carbs, number_servings) " +
                            "VALUES (" +
                            "null, " +
                            recipeIdSQL + ", " +
                            numberHundredCalories + ", " +
                            numberHundredProteins + ", " +
                            numberHundredFat + ", " +
                            numberHundredCarbs + ", " +
                            numberServingCalories + ", " +
                            numberServingProteins + ", " +
                            numberServingFat + ", " +
                            numberServingCarbs + ", " +
                            numberTotalWeight + ", " +
                            numberTotalCalories + ", " +
                            numberTotalProteins + ", " +
                            numberTotalFat + ", " +
                            numberTotalCarbs + ", " +
                            numberServings +
                            ")";
                    try {
                        db.rawQuery(query);
                    } catch (SQLException e) {
                        //Toast.makeText(this, "recipes_numbers: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }


                // 6. Comments
                try {
                    JSONArray itemsArray = object.getJSONArray("comments");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObject = itemsArray.getJSONObject(i);

                        String commentIdSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_id")));
                        String commentRecipeIdSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_recipe_id")));
                        String commentLanguageSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_language")));
                        String commentApprovedSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_approved")));
                        String commentDatetimeSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_datetime")));
                        String commentTimeSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_time")));
                        String commentDatePrintSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_date_print")));
                        String commentUserIdSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_id")));
                        String commentUserAliasSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_alias")));
                        String commentUserImagePathSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_image_path")));
                        String commentUserImageFileSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_image_file")));
                        String commentUserIpSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_ip")));
                        String commentUserHostnameSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_hostname")));
                        String commentUserAgentSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_user_agent")));
                        String commentTitleSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_title")));
                        String commentTextSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_text")));
                        String commentRatingSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_rating")));
                        String commentHelpfulClicksSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_helpful_clicks")));
                        String commentUselessClicksSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_useless_clicks")));
                        String commentMarkedAsSpamSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_marked_as_spam")));
                        String commentSpamCheckedSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_spam_checked")));
                        String commentSpamCheckedCommentSQL = db.quoteSmart(new OutputString().outputHTML(itemObject.getString("comment_spam_checked_comment")));

                        // Do we have the items saved from before
                        q = "SELECT _id FROM recipes_comments WHERE comment_id=" + commentIdSQL;
                        Cursor groupItems = db.rawQuery(q);
                        int cursorItemSize = groupItems.getCount();

                        if (cursorItemSize < 1) {
                            // Insert recipe comment
                            q = "INSERT INTO recipes_comments (_id, comment_id, comment_recipe_id, " +
                                    "comment_language, comment_approved, comment_datetime, " +
                                    "comment_time, comment_date_print, comment_user_id, " +
                                    "comment_user_alias, comment_user_image_path, comment_user_image_file, " +
                                    "comment_user_ip, comment_user_hostname, comment_user_agent, " +
                                    "comment_title, comment_text, comment_rating, " +
                                    "comment_helpful_clicks, comment_useless_clicks, comment_marked_as_spam, " +
                                    "comment_spam_checked, comment_spam_checked_comment) " +
                                    "VALUES (" +
                                    "NULL, " + commentIdSQL + ", " + commentRecipeIdSQL + ", " +
                                    commentLanguageSQL + ", " + commentApprovedSQL + ", " + commentDatetimeSQL + ", " +
                                    commentTimeSQL + ", " + commentDatePrintSQL + ", " + commentUserIdSQL + ", " +
                                    commentUserAliasSQL + ", " + commentUserImagePathSQL + ", " + commentUserImageFileSQL + ", " +
                                    commentUserIpSQL + ", " + commentUserHostnameSQL + ", " +
                                    commentUserAgentSQL + ", " + commentTitleSQL + ", " + commentTextSQL + ", " +
                                    commentRatingSQL + ", " + commentHelpfulClicksSQL + ", " + commentUselessClicksSQL + ", " +
                                    commentMarkedAsSpamSQL + ", " + commentSpamCheckedSQL + ", " + commentSpamCheckedCommentSQL + " " +
                                    ")";


                            db.rawQuery(q);
                        }
                        else{
                            // Update  recipe comment
                            q = "UPDATE recipes_comments SET " +
                                    "comment_recipe_id=" + commentRecipeIdSQL + ", " +
                                    "comment_language=" + commentLanguageSQL + ", " +
                                    "comment_approved=" + commentApprovedSQL + ", " +
                                    "comment_datetime=" + commentDatetimeSQL + ", " +
                                    "comment_time=" + commentTimeSQL + ", " +
                                    "comment_date_print=" + commentDatePrintSQL + ", " +
                                    "comment_user_id=" + commentUserIdSQL + ", " +
                                    "comment_user_alias=" + commentUserAliasSQL + ", " +
                                    "comment_user_image_path=" + commentUserImagePathSQL + ", " +
                                    "comment_user_image_file=" + commentUserImageFileSQL + ", " +
                                    "comment_user_ip=" + commentUserIpSQL + ", " +
                                    "comment_user_hostname=" + commentUserHostnameSQL + ", " +
                                    "comment_user_agent=" + commentUserAgentSQL + ", " +
                                    "comment_title=" + commentTitleSQL + ", " +
                                    "comment_text=" + commentTextSQL + ", " +
                                    "comment_rating=" + commentRatingSQL + ", " +
                                    "comment_helpful_clicks=" + commentHelpfulClicksSQL + ", " +
                                    "comment_useless_clicks=" + commentUselessClicksSQL + ", " +
                                    "comment_marked_as_spam=" + commentMarkedAsSpamSQL + ", " +
                                    "comment_spam_checked=" + commentSpamCheckedSQL + ", " +
                                    "comment_spam_checked_comment=" + commentSpamCheckedCommentSQL + " " +
                                    "WHERE comment_id=" + commentIdSQL;
                            db.rawQuery(q);

                        }
                    }

                } catch (JSONException e) {
                    // Server error 500
                    WriteToErrorLog log = new WriteToErrorLog(this);
                    log.writeToErrorLog("RecipesViewActivity", "answerRecipeUpdatesFromServer Error comments", "error", e.getMessage());

                }




                // Db close
                db.close();

            }
            catch (JSONException e) {
                // Server error 500
                //Toast.makeText(this, "answerRecipeUpdatesFromServer Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }

            // We will update the recipe again
            showRecipe();

        } // stringDynamicText


    } // answerRecipeUpdatesFromServer
}