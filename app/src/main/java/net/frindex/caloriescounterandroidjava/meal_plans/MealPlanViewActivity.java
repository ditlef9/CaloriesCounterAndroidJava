package net.frindex.caloriescounterandroidjava.meal_plans;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.food.FoodDViewFoodActivity;
import net.frindex.caloriescounterandroidjava.http.HttpRequestImageDownloadToCache;
import net.frindex.caloriescounterandroidjava.recipes.RecipesCViewRecipeActivity;

import java.io.File;
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
public class MealPlanViewActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL       = "https://summerslim.codecourses.eu";
    String apiMealPlansURL  = "https://summerslim.codecourses.eu/meal_plans/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // Class variables
    int currentMealPlanId;
    int currentMealPlanUser_id;
    String currentMealPlanLanguage;
    String currentMealPlanTitle;
    String currentMealPlanTitleClean;
    int currentMealPlanNumberOfDays;
    String currentMealPlanIntroduction;

    int currentMealPlanTotalEnergyWithoutTraining;
    int currentMealPlanTotalFatWithoutTraining;
    int currentMealPlanTotalCarbWithoutTraining;
    int currentMealPlanTotalProteinWithoutTraining;

    int currentMealPlanTotalEnergyWithTraining;
    int currentMealPlanTotalFatWithTraining;
    int currentMealPlanTotalCarbWithTraining;
    int currentMealPlanTotalProteinWithTraining;

    int currentMealPlanAverageKcalWithoutTraining;
    int currentMealPlanAverageFatWithoutTraining;
    int currentMealPlanAverageCarbWithoutTraining;
    int currentMealPlanAverageProteinWithoutTraining;

    int currentMealPlanAverageKcalWithTraining;
    int currentMealPlanAverageFatWithTraining;
    int currentMealPlanAverageCarbWithTraining;
    int currentMealPlanAverageProteinWithTraining;

    String currentMealCreated;
    String currentMealUpdated;
    String currentMealImagePath;
    String currentMealImageThumb;
    String currentMealImageFile;
    int currentMealViews;
    int currentMealLikes;
    int currentMealDislikes;
    int currentMealRating;
    int currentMealComments;


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
        setContentView(R.layout.activity_meal_plan_view);

        Bundle b = getIntent().getExtras();
        currentMealPlanId = b.getInt("currentMealPlanId");

        // My user
        fetchMyProfile();

        // MealPlan
        showMealPlan();


        // Toolbar
        initializeToolbar();
    }

    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.meal_plans) + " > " + currentMealPlanTitle);

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
        Intent i = new Intent(MealPlanViewActivity.this, MealPlansActivity.class);
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
    public void showMealPlan() {
        // DB
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT _id, meal_plan_id, meal_plan_user_id, " +
                "meal_plan_language, meal_plan_title, meal_plan_title_clean, " +
                "meal_plan_number_of_days, meal_plan_introduction, meal_plan_total_energy_without_training, " +
                "meal_plan_total_fat_without_training, meal_plan_total_carb_without_training, meal_plan_total_protein_without_training, " +
                "meal_plan_total_energy_with_training, meal_plan_total_fat_with_training, meal_plan_total_carb_with_training, " +
                "meal_plan_total_protein_with_training, meal_plan_average_kcal_without_training, meal_plan_average_fat_without_training, " +
                "meal_plan_average_carb_without_training, meal_plan_average_protein_without_training, meal_plan_average_kcal_with_training, " +
                "meal_plan_average_fat_with_training, meal_plan_average_carb_with_training, meal_plan_average_protein_with_training, " +
                "meal_plan_created, meal_plan_updated, meal_plan_image_path, " +
                "meal_plan_image_thumb, meal_plan_image_file," +
                " meal_plan_views, meal_plan_likes, meal_plan_dislikes, " +
                "meal_plan_rating, meal_plan_comments " +
                "FROM meal_plans " +
                " WHERE meal_plan_id=" + currentMealPlanId + "";
        Cursor cursorMealPlan = db.rawQuery(query);

        int currentMealPlan_Id = cursorMealPlan.getInt(0);
        currentMealPlanId = cursorMealPlan.getInt(1);
        currentMealPlanUser_id = cursorMealPlan.getInt(2);
        currentMealPlanLanguage = cursorMealPlan.getString(3);
        currentMealPlanTitle = cursorMealPlan.getString(4);
        currentMealPlanTitleClean = cursorMealPlan.getString(5);
        currentMealPlanNumberOfDays = cursorMealPlan.getInt(6);
        currentMealPlanIntroduction = cursorMealPlan.getString(7);

        currentMealPlanTotalEnergyWithoutTraining = cursorMealPlan.getInt(8);
        currentMealPlanTotalFatWithoutTraining = cursorMealPlan.getInt(9);
        currentMealPlanTotalCarbWithoutTraining = cursorMealPlan.getInt(10);
        currentMealPlanTotalProteinWithoutTraining = cursorMealPlan.getInt(11);

        currentMealPlanTotalEnergyWithTraining = cursorMealPlan.getInt(12);
        currentMealPlanTotalFatWithTraining = cursorMealPlan.getInt(13);
        currentMealPlanTotalCarbWithTraining = cursorMealPlan.getInt(14);
        currentMealPlanTotalProteinWithTraining = cursorMealPlan.getInt(15);

        currentMealPlanAverageKcalWithoutTraining = cursorMealPlan.getInt(16);
        currentMealPlanAverageFatWithoutTraining = cursorMealPlan.getInt(17);
        currentMealPlanAverageCarbWithoutTraining = cursorMealPlan.getInt(18);
        currentMealPlanAverageProteinWithoutTraining = cursorMealPlan.getInt(19);

        currentMealPlanAverageKcalWithTraining = cursorMealPlan.getInt(20);
        currentMealPlanAverageFatWithTraining = cursorMealPlan.getInt(21);
        currentMealPlanAverageCarbWithTraining = cursorMealPlan.getInt(22);
        currentMealPlanAverageProteinWithTraining = cursorMealPlan.getInt(23);

        currentMealCreated = cursorMealPlan.getString(24);
        currentMealUpdated = cursorMealPlan.getString(25);
        currentMealImagePath = cursorMealPlan.getString(26);
        currentMealImageThumb = cursorMealPlan.getString(27);
        currentMealImageFile = cursorMealPlan.getString(28);
        currentMealViews = cursorMealPlan.getInt(29);
        currentMealLikes = cursorMealPlan.getInt(30);
        currentMealDislikes = cursorMealPlan.getInt(31);
        currentMealRating = cursorMealPlan.getInt(32);
        currentMealComments = cursorMealPlan.getInt(33);

        // Image
        ImageView imageViewMealPlan = findViewById(R.id.imageViewMealPlan);
        if(currentMealImageFile != null) {
            if (!(currentMealImageFile.equals("")) && !(currentMealImageFile.equals(""))) {

                // Exist in cache?
                File file = new File(getCacheDir() + "" + File.separatorChar + currentMealImageFile);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageViewMealPlan.setImageBitmap(myBitmap);
                } else {
                    // Exist in res?
                    String androidImageStyle = currentMealImageFile.replace("-", "_");
                    androidImageStyle = androidImageStyle.replace(".png", "");
                    androidImageStyle = "img" + androidImageStyle;
                    int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                    if (id != 0) {
                        imageViewMealPlan.setImageResource(id);
                    } else {
                        // Download
                        String imageURL = websiteURL + "/" + currentMealImagePath + "/" + currentMealImageFile;
                        String imageName = currentMealImageFile;
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewMealPlan).execute();


                    }
                }
            }
            else {
                imageViewMealPlan.setVisibility(View.GONE);
                Toast.makeText(this, "No picture", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            imageViewMealPlan.setVisibility(View.GONE);
            Toast.makeText(this, "Picture is null", Toast.LENGTH_SHORT).show();
        }

        // Title
        TextView textViewMealPlanTitle = findViewById(R.id.textViewMealPlanTitle);
        textViewMealPlanTitle.setText(currentMealPlanTitle);



        // Description
        TextView textViewMealPlanIntroduction = findViewById(R.id.textViewMealPlanIntroduction);
        textViewMealPlanIntroduction.setText(currentMealPlanIntroduction);

        // Close db
        db.close();

        // Show meals
        showMeals();

    } // showMealPlan

    public void showMeals(){

        // DB
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Meals
        // LinearLayout linearLayoutMeals = findViewById(R.id.linearLayoutMeals);
        TableLayout tableLayoutMeals = findViewById(R.id.tableLayoutMeals);


        for(int mealNumber=0;mealNumber<7;mealNumber++){


            // Totals meal
            String query = "SELECT _id, meal_id, meal_meal_plan_id, meal_day_number, meal_energy, meal_fat, meal_carb, meal_protein " +
                    "FROM meal_plans_meals WHERE meal_meal_plan_id=" + currentMealPlanId + " AND meal_day_number=1 AND meal_number=" + mealNumber;
            Cursor cursorMeals = db.rawQuery(query);
            int meal_Id = cursorMeals.getInt(0);
            int mealMealId = cursorMeals.getInt(1);
            int mealMealPlanId = cursorMeals.getInt(2);
            int mealMealDayNumber = cursorMeals.getInt(3);
            int mealEnergy = cursorMeals.getInt(4);
            int mealFat = cursorMeals.getInt(5);
            int mealCarb = cursorMeals.getInt(6);
            int mealProtein = cursorMeals.getInt(7);

            // Tr 1 :: Day number
            TableRow tableRowDayNumberCaloriesHeadline = new TableRow(this); // Add row
            tableRowDayNumberCaloriesHeadline.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRowDayNumberCaloriesHeadline.setBackgroundResource(R.drawable.tr_headcell);
            tableRowDayNumberCaloriesHeadline.setPadding(10, 10, 10, 10);

            // Tr 1 :: Day number :: Add day number
            TextView TextViewDayNumber = new TextView(this); // Add TextView Group title
            TextViewDayNumber.setPadding(0, 0, 10, 0);
            if(mealNumber == 0){
                TextViewDayNumber.setText(getString(R.string.breakfast));
            }
            else if(mealNumber == 1){
                TextViewDayNumber.setText(getString(R.string.lunch));
            }
            else if(mealNumber == 2){
                TextViewDayNumber.setText(getString(R.string.before_training));
            }
            else if(mealNumber == 3){
                TextViewDayNumber.setText(getString(R.string.after_training));
            }
            else if(mealNumber == 4){
                TextViewDayNumber.setText(getString(R.string.dinner));
            }
            else if(mealNumber == 5){
                TextViewDayNumber.setText(getString(R.string.snacks));
            }
            else if(mealNumber == 6){
                TextViewDayNumber.setText(getString(R.string.supper));
            }
            else{
                TextViewDayNumber.setText("x out of range");
            }
            TextViewDayNumber.setTextSize(20);
            TextViewDayNumber.setTextColor(Color.BLACK);
            TableRow.LayoutParams paramsTextLeft = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            tableRowDayNumberCaloriesHeadline.addView(TextViewDayNumber, paramsTextLeft);

            // Tr 1 :: Day number :: Add Calories headline
            TextView TextViewCaloriesHeadline = new TextView(this);
            TextViewCaloriesHeadline.setGravity(Gravity.CENTER);
            TextViewCaloriesHeadline.setText(" ");
            TextViewCaloriesHeadline.setTextSize(18);
            TextViewCaloriesHeadline.setTextColor(Color.BLACK);
            tableRowDayNumberCaloriesHeadline.addView(TextViewCaloriesHeadline);

            // Tr 1 :: Day number :: Add to table
            tableLayoutMeals.addView(tableRowDayNumberCaloriesHeadline);


            // Entries
            query = "SELECT _id, entry_id, entry_food_id, entry_recipe_id, entry_weight, entry_name, entry_manufacturer_name, entry_serving_size, entry_serving_size_measurement, entry_energy_per_entry, entry_fat_per_entry, entry_carb_per_entry, entry_protein_per_entry, entry_text " +
                    "FROM meal_plans_entries WHERE entry_meal_plan_id=" + currentMealPlanId + " " +
                    "AND entry_day_number=1 AND entry_meal_number=" + mealNumber + " ORDER BY entry_weight ASC";
            Cursor cursorEntries = db.rawQuery(query);
            int cursorEntriesCount = cursorEntries.getCount();
            for(int x=0;x<cursorEntriesCount;x++) {
                int entry_Id = cursorEntries.getInt(0);
                int entryId = cursorEntries.getInt(1);
                final int entryFoodId = cursorEntries.getInt(2);
                final int entryRecipeId = cursorEntries.getInt(3);
                int entryWeight = cursorEntries.getInt(4);
                String entryFoodName = cursorEntries.getString(5);
                String entryManufacturerName = cursorEntries.getString(6);
                String entryFoodServingSize = cursorEntries.getString(7);
                String entryFoodServingSizeMeasurement = cursorEntries.getString(8);
                int entryFoodEnergyPerEntry = cursorEntries.getInt(9);
                int entryFoodFatPerEntry = cursorEntries.getInt(10);
                int entryFoodCarbPerEntry = cursorEntries.getInt(11);
                int entryFoodProteinPerEntry = cursorEntries.getInt(12);
                String entryFoodText = cursorEntries.getString(13);



                // Tr X
                TableRow tableRowXFoodRecipe = new TableRow(this); // Add row
                tableRowXFoodRecipe.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableRowXFoodRecipe.setPadding(10, 10, 10, 10);

                // Tr X :: Size Manufactor Name
                TextView TextViewName = new TextView(this);
                TextViewName.setPadding(0, 0, 10, 0);
                if(!(entryManufacturerName.equals(""))) {
                    TextViewName.setText(entryFoodServingSize + " " + entryFoodServingSizeMeasurement + " " + entryManufacturerName + " " + entryFoodName);
                }
                else{
                    TextViewName.setText(entryFoodServingSize + " " + entryFoodServingSizeMeasurement + " " + entryFoodName);
                }
                TextViewName.setTextSize(18);
                TextViewName.setTextColor(Color.BLACK);
                tableRowXFoodRecipe.addView(TextViewName, paramsTextLeft);

                // Tr X :: Calories
                TextView TextViewCalories = new TextView(this);
                TextViewCalories.setGravity(Gravity.CENTER);
                TextViewCalories.setText(String.valueOf(entryFoodEnergyPerEntry));
                TextViewCalories.setTextSize(18);
                TextViewCalories.setTextColor(Color.BLACK);
                tableRowXFoodRecipe.addView(TextViewCalories);

                // Tr X :: Add to table
                tableLayoutMeals.addView(tableRowXFoodRecipe);

                // Tr X :: Add listeners
                tableRowXFoodRecipe.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        v.setBackgroundColor(getResources().getColor(android.R.color.background_light));

                        // Get the row text
                        TableRow row = (TableRow)v;
                        TextView tv = (TextView)row.getChildAt(0);

                        // Send it to edit
                        String tvText ="" + tv.getText(); // Lot VALUE Selected
                        tableRowXFoodRecipeClicked(entryFoodId, entryRecipeId);
                    }
                });


                cursorEntries.moveToNext();
            } // cursorEntriesCount

            // TR Sum
            TableRow tableRowSum = new TableRow(this); // Add row
            tableRowSum.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRowSum.setPadding(10, 10, 10, 40);


            TextView TextViewSumLeft = new TextView(this);
            TextViewDayNumber.setPadding(0, 0, 10, 20);
            TextViewSumLeft.setText(" ");
            TextViewSumLeft.setTextSize(18);
            TextViewSumLeft.setTextColor(Color.BLACK);

            TextView TextViewSumRight = new TextView(this);
            TextViewSumRight.setPadding(0, 0, 0, 20);
            TextViewSumRight.setGravity(Gravity.CENTER);
            TextViewSumRight.setText(String.valueOf(mealEnergy));
            TextViewSumRight.setTextSize(18);
            TextViewSumRight.setTextColor(Color.BLACK);
            TextViewSumRight.setTypeface(TextViewSumRight.getTypeface(), Typeface.ITALIC);
            tableRowSum.addView(TextViewSumLeft, paramsTextLeft);
            tableRowSum.addView(TextViewSumRight);


            tableLayoutMeals.addView(tableRowSum);


        } // for meals

        // Close db
        db.close();
    } // showMeals

    public void tableRowXFoodRecipeClicked(int foodId, int recipeId){
        if(foodId == 0){
            // Recipe
            Intent i = new Intent(MealPlanViewActivity.this, RecipesCViewRecipeActivity.class);
            i.putExtra("currentRecipeId", recipeId);
            startActivity(i);
        }
        else{
            // Food
            Intent i = new Intent(MealPlanViewActivity.this, FoodDViewFoodActivity.class);
            i.putExtra("currentFoodId", foodId);
            startActivity(i);

        }


    } // tableRowXFoodRecipeClicked
}