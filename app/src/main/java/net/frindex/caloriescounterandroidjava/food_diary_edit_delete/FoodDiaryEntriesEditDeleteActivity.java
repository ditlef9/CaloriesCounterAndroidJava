package net.frindex.caloriescounterandroidjava.food_diary_edit_delete;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
/**
 *
 * File: FoodDiaryEntriesEditDeleteActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class FoodDiaryEntriesEditDeleteActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";


    // Class variables
    int currentEntry_Id;
    int currentEntryId;
    String currentTotalDayDate; // gets assigned in loadEntry()
    int currentEntryMealId; // gets assigned in loadEntry()

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

    // Numbers
    String currentEntryServingSizeMeasurement;
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
        setContentView(R.layout.activity_food_diary_entries_edit_delete);


        // Add food data
        Bundle b = getIntent().getExtras();
        currentEntry_Id = b.getInt("currentEntry_Id");
        currentEntryId = b.getInt("currentEntryId");


        // Toolbar
        initializeToolbar();

        // Listeners
        listeners();

        // My user
        fetchMyProfile();

        // Populate food
        loadEntry();
    }

    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.edit_delete));

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
        Intent i = new Intent(FoodDiaryEntriesEditDeleteActivity.this, MainActivity.class);
        i.putExtra("currentTotalDayDate", currentTotalDayDate);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }


    /*- Listeners ----------------------------------------------------------------------------- */
    private void listeners() {
        // Edit food
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

        // Delete
        TextView textViewDelete = findViewById(R.id.textViewDelete);
        textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewDeleteClicked();
            }
        });

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


    /*- Load Entry ------------------------------------------------------------------------------ */
    public void loadEntry(){
        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Query
        String query = "SELECT _id, entry_id, entry_user_id, entry_date, entry_meal_id, entry_food_id, entry_recipe_id, entry_name, entry_manufacturer_name, entry_serving_size, entry_serving_size_measurement, entry_energy_per_entry, entry_fat_per_entry, entry_carb_per_entry, entry_protein_per_entry, entry_text, entry_deleted, entry_updated, entry_synchronized " +
                "FROM food_diary_entires " +
                "WHERE _id=" + currentEntry_Id + "";
        // "WHERE entry_id=" + currentEntryId + "";
        Cursor cursorEntries = db.rawQuery(query);
        if(cursorEntries.getCount() == 0){
            Toast.makeText(this, "Entry (" + currentEntryId + ") not found!", Toast.LENGTH_LONG).show();
        } // cursorEntries.getCount() == 0
        else{

            currentEntry_Id = cursorEntries.getInt(0);
            currentEntryId = cursorEntries.getInt(1);
            int entryUserId = cursorEntries.getInt(2);
            currentTotalDayDate = cursorEntries.getString(3);
            currentEntryMealId = cursorEntries.getInt(4);
            int entryFoodId = cursorEntries.getInt(5);
            int entryRecipeId = cursorEntries.getInt(6);
            String entryName = cursorEntries.getString(7);
            String entryManufacturerName = cursorEntries.getString(8);
            String entryServingSize = cursorEntries.getString(9);
            String entryServingSizeMeasurement = cursorEntries.getString(10);
            int entryEnergyPerEntry = cursorEntries.getInt(11);
            int entryFatPerEntry = cursorEntries.getInt(12);
            int entryCarbPerEntry = cursorEntries.getInt(13);
            int entryProteinPerEntry = cursorEntries.getInt(14);
            String entryText = cursorEntries.getString(15);
            String entryDeleted = cursorEntries.getString(16);
            String entryUpdated = cursorEntries.getString(17);
            String entrySynchronized = cursorEntries.getString(18);

            // Name
            TextView textViewName = findViewById(R.id.textViewName);
            textViewName.setText(entryName);

            // Serving Size
            EditText editTextServingSize = findViewById(R.id.editTextServingSize);
            editTextServingSize.setText(entryServingSize);



            // Food or Recipe?
            if(entryFoodId == 0){
                // Recipe
                String q = "SELECT _id, recipe_id, recipe_introduction, recipe_image_path, recipe_image FROM recipes WHERE recipe_id=" + entryRecipeId + "";
                Cursor cursorRecipe = db.rawQuery(q);
                int recipe_Id = cursorRecipe.getInt(0);
                int recipeId = cursorRecipe.getInt(1);
                String recipeIntroduction = cursorRecipe.getString(2);
                String recipeImagePath = cursorRecipe.getString(3);
                String recipeImage = cursorRecipe.getString(4);

                // Numbers
                q = "SELECT _id, number_id, number_hundred_calories,  " +
                        "number_hundred_proteins, number_hundred_fat, number_hundred_carbs, number_serving_calories, " +
                        "number_serving_proteins, number_serving_fat, number_serving_carbs FROM recipes_numbers WHERE number_recipe_id=" + entryRecipeId + "";
                Cursor cursorNumbers = db.rawQuery(q);
                int number_Id = cursorNumbers.getInt(0);
                int numberId = cursorNumbers.getInt(1);
                currentNumberHundredCalories = cursorNumbers.getInt(2);
                currentNumberHundredProteins = cursorNumbers.getInt(3);
                currentNumberHundredFat = cursorNumbers.getInt(4);
                currentNumberHundredCarbs = cursorNumbers.getInt(5);
                currentNumberServingCalories = cursorNumbers.getInt(6);
                currentNumberServingProteins = cursorNumbers.getInt(7);
                currentNumberServingFat = cursorNumbers.getInt(8);
                currentNumberServingCarbs = cursorNumbers.getInt(9);

                currentEntryServingSizeMeasurement = "servings";

                // Introduction
                TextView textViewDescription = findViewById(R.id.textViewDescription);
                textViewDescription.setText(recipeIntroduction);



                // Image
                ImageView imageViewImageA = findViewById(R.id.imageViewImageA);
                if(recipeImage != null) {
                    if (!(recipeImage.equals("")) && !(recipeImage.equals(""))) {
                        imageViewImageA.setVisibility(View.VISIBLE);

                        // Exist in cache?
                        File file = new File(getCacheDir() + "" + File.separatorChar + recipeImage);
                        if (file.exists ()){
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewImageA.setImageBitmap(myBitmap);
                        }
                        else{
                            // Exist in res?
                            String androidImageStyle = recipeImage.replace("-", "_");
                            androidImageStyle = androidImageStyle.replace(".png", "");
                            androidImageStyle = "img" + androidImageStyle;
                            int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                            if(id != 0){
                                imageViewImageA.setImageResource(id);
                            } else {
                                // Download
                                String imageURL = websiteURL + "/" + recipeImagePath + "/" + recipeImage;
                                String imageName = recipeImage;
                                new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewImageA).execute();


                            }
                        }
                    }
                    else {
                        imageViewImageA.setVisibility(View.GONE);
                    }
                }
                else {
                    imageViewImageA.setVisibility(View.GONE);
                }
                ImageView imageViewImageB = findViewById(R.id.imageViewImageB);
                imageViewImageB.setVisibility(View.GONE);
                ImageView imageViewImageC = findViewById(R.id.imageViewImageC);
                imageViewImageC.setVisibility(View.GONE);
                ImageView imageViewImageD = findViewById(R.id.imageViewImageD);
                imageViewImageD.setVisibility(View.GONE);
                ImageView imageViewImageE = findViewById(R.id.imageViewImageE);
                imageViewImageE.setVisibility(View.GONE);

            } // recipe
            else{
                // Food
                String q = "SELECT _id, food_id, food_serving_size_gram, " +
                        "food_serving_size_gram_measurement, food_serving_size_pcs, food_serving_size_pcs_measurement, " +
                        "food_energy, food_proteins, " +
                        "food_carbohydrates, food_fat, food_energy_calculated, " +
                        "food_proteins_calculated, food_carbohydrates_calculated, food_fat_calculated," +
                        "food_image_path, food_image_a, food_image_b, food_image_c, food_image_d, food_image_e FROM food_index WHERE food_id=" + entryFoodId + "";
                Cursor cursorFood = db.rawQuery(q);

                int food_Id = cursorFood.getInt(0);
                int foodId = cursorFood.getInt(1);
                String foodServingSizeGram = cursorFood.getString(2);
                String foodServingSizeGramMeasurement = cursorFood.getString(3);
                String foodServingSizePcs = cursorFood.getString(4);
                currentEntryServingSizeMeasurement = cursorFood.getString(5);
                currentNumberHundredCalories = cursorFood.getInt(6);
                currentNumberHundredProteins = cursorFood.getInt(7);
                currentNumberHundredCarbs = cursorFood.getInt(8);
                currentNumberHundredFat = cursorFood.getInt(9);
                currentNumberServingCalories = cursorFood.getInt(10);
                currentNumberServingProteins = cursorFood.getInt(11);
                currentNumberServingCarbs = cursorFood.getInt(12);
                currentNumberServingFat = cursorFood.getInt(13);
                String foodImagePath = cursorFood.getString(14);
                String foodImageA = cursorFood.getString(15);
                String foodImageB = cursorFood.getString(16);
                String foodImageC = cursorFood.getString(17);
                String foodImageD = cursorFood.getString(18);
                String foodImageE = cursorFood.getString(19);

                // Introduction
                TextView textViewDescription = findViewById(R.id.textViewDescription);
                textViewDescription.setText(entryManufacturerName);

                // Serving size
                TextView textViewPcs = findViewById(R.id.textViewPcs);

                if(currentEntryServingSizeMeasurement.equals("g")) {
                    // We dont need the pcs button, because the food is only measured in gram
                    textViewPcs.setVisibility(View.GONE);
                }
                else{
                    textViewPcs.setText(String.valueOf(currentEntryServingSizeMeasurement));
                }



                // Image A
                ImageView imageViewImageA = findViewById(R.id.imageViewImageA);
                if(foodImageA != null) {
                    if (!(foodImageA.equals("")) && !(foodImageA.equals(""))) {
                        imageViewImageA.setVisibility(View.VISIBLE);

                        // Exist in cache?
                        File file = new File(getCacheDir() + "" + File.separatorChar + foodImageA);
                        if (file.exists ()){
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewImageA.setImageBitmap(myBitmap);
                        }
                        else{
                            // Exist in res?
                            String androidImageStyle = foodImageA.replace("-", "_");
                            androidImageStyle = androidImageStyle.replace(".png", "");
                            androidImageStyle = "img" + androidImageStyle;
                            int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                            if(id != 0){
                                imageViewImageA.setImageResource(id);
                            } else {
                                // Download
                                String imageURL = websiteURL + "/" + foodImagePath + "/" + foodImageA;
                                String imageName = foodImageA;
                                new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewImageA).execute();


                            }
                        }
                    }
                    else {
                        imageViewImageA.setVisibility(View.GONE);
                    }
                }
                else {
                    imageViewImageA.setVisibility(View.GONE);
                }


                // Image B
                ImageView imageViewImageB = findViewById(R.id.imageViewImageB);
                if(foodImageB != null) {
                    if (!(foodImageB.equals("")) && !(foodImageB.equals(""))) {
                        imageViewImageB.setVisibility(View.VISIBLE);

                        // Exist in cache?
                        File file = new File(getCacheDir() + "" + File.separatorChar + foodImageB);
                        if (file.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewImageB.setImageBitmap(myBitmap);
                        } else {
                            // Exist in res?
                            String androidImageStyle = foodImageB.replace("-", "_");
                            androidImageStyle = androidImageStyle.replace(".png", "");
                            androidImageStyle = "img" + androidImageStyle;
                            int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                            if (id != 0) {
                                imageViewImageB.setImageResource(id);
                            } else {
                                // Download
                                String imageURL = websiteURL + "/" + foodImagePath + "/" + foodImageB;
                                String imageName = foodImageB;
                                new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewImageB).execute();


                            }
                        }
                    }
                    else {
                        imageViewImageB.setVisibility(View.GONE);
                    }
                }
                else {
                    imageViewImageB.setVisibility(View.GONE);
                }

                // Image c
                ImageView imageViewImageC = findViewById(R.id.imageViewImageC);
                if(foodImageC != null) {
                    if (!(foodImageC.equals("")) && !(foodImageC.equals(""))) {
                        imageViewImageC.setVisibility(View.VISIBLE);

                        // Exist in cache?
                        File file = new File(getCacheDir() + "" + File.separatorChar + foodImageC);
                        if (file.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewImageC.setImageBitmap(myBitmap);
                        } else {
                            // Exist in res?
                            String androidImageStyle = foodImageC.replace("-", "_");
                            androidImageStyle = androidImageStyle.replace(".png", "");
                            androidImageStyle = "img" + androidImageStyle;
                            int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                            if (id != 0) {
                                imageViewImageC.setImageResource(id);
                            } else {
                                // Download
                                String imageURL = websiteURL + "/" + foodImagePath + "/" + foodImageC;
                                String imageName = foodImageC;
                                new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewImageC).execute();


                            }
                        }

                    }
                    else {
                        imageViewImageC.setVisibility(View.GONE);
                    }
                }
                else {
                    imageViewImageC.setVisibility(View.GONE);
                }

                // Image D
                ImageView imageViewImageD = findViewById(R.id.imageViewImageD);
                if(foodImageD != null) {
                    if (!(foodImageD.equals("")) && !(foodImageD.equals(""))) {
                        imageViewImageD.setVisibility(View.VISIBLE);

                        // Exist in cache?
                        File file = new File(getCacheDir() + "" + File.separatorChar + foodImageD);
                        if (file.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewImageD.setImageBitmap(myBitmap);
                        } else {
                            // Exist in res?
                            String androidImageStyle = foodImageD.replace("-", "_");
                            androidImageStyle = androidImageStyle.replace(".png", "");
                            androidImageStyle = "img" + androidImageStyle;
                            int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                            if (id != 0) {
                                imageViewImageD.setImageResource(id);
                            } else {
                                // Download
                                String imageURL = websiteURL + "/" + foodImagePath + "/" + foodImageD;
                                String imageName = foodImageD;
                                new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewImageD).execute();


                            }
                        }
                    }
                    else {
                        imageViewImageD.setVisibility(View.GONE);
                    }
                }
                else {
                    imageViewImageD.setVisibility(View.GONE);
                }

                // Image E
                ImageView imageViewImageE = findViewById(R.id.imageViewImageE);
                if(foodImageE != null) {
                    if (!(foodImageE.equals("")) && !(foodImageE.equals(""))) {
                        imageViewImageE.setVisibility(View.VISIBLE);

                        // Exist in cache?
                        File file = new File(getCacheDir() + "" + File.separatorChar + foodImageE);
                        if (file.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewImageE.setImageBitmap(myBitmap);
                        } else {
                            // Exist in res?
                            String androidImageStyle = foodImageE.replace("-", "_");
                            androidImageStyle = androidImageStyle.replace(".png", "");
                            androidImageStyle = "img" + androidImageStyle;
                            int id = getResources().getIdentifier(androidImageStyle, "drawable", getPackageName());
                            if (id != 0) {
                                imageViewImageE.setImageResource(id);
                            } else {
                                // Download
                                String imageURL = websiteURL + "/" + foodImagePath + "/" + foodImageE;
                                String imageName = foodImageE;
                                new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewImageE).execute();


                            }
                        }
                    }
                    else {
                        imageViewImageE.setVisibility(View.GONE);
                    }
                }
                else {
                    imageViewImageE.setVisibility(View.GONE);
                }

            } // food



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


        } // cursorEntries.getCount() != 0


        db.close();
    } // loadEntry


    /*- Text View Edit Clicked ------------------------------------------------------------------ */
    public void textViewGramOrPcsClicked(String gramOrPcs){
        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();


        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inpUpdated = dfhhmmss.format(Calendar.getInstance().getTime());

        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String inpEntryDate = dfyyyymmdd.format(Calendar.getInstance().getTime());



        // Serving size String
        EditText editTextServingSize = findViewById(R.id.editTextServingSize);
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


        if (gramOrPcs.equals("gram")) {
            // Gram
            stringInpEntryServingSizeMeasurement = currentEntryServingSizeMeasurement;
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
            stringInpEntryServingSizeMeasurement = currentEntryServingSizeMeasurement;
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


        // Update
        String q = "UPDATE food_diary_entires SET " +
                "entry_serving_size=" + stringInpEntryServingSizeSQL + ", " +
                "entry_serving_size_measurement=" + stringInpEntryServingSizeMeasurementSQL + ", " +
                "entry_energy_per_entry=" + intInpEntryEnergyPerEntry + ", " +
                "entry_fat_per_entry=" + intInpEntryFatPerEntry + ", " +
                "entry_carb_per_entry=" + intInpEntryCarbPerEntry + ", " +
                "entry_protein_per_entry=" + intInpEntryProteinPerEntry + ", " +
                "entry_updated='" + inpUpdated + "', "  +
                "entry_synchronized=0 " +
                "WHERE entry_id=" + currentEntryId;
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

        // Send to server
        String url    = apiFooDiaryURL + "/post_update_food_diary_edit_food_or_recipe_entry.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_entry_id", String.valueOf(currentEntryId));
        data.put("inp_entry_serving_size", stringInpEntryServingSize);
        data.put("inp_entry_serving_size_measurement", stringInpEntryServingSizeMeasurement);
        data.put("inp_entry_energy_per_entry", String.valueOf(intInpEntryEnergyPerEntry));
        data.put("inp_entry_fat_per_entry", String.valueOf(intInpEntryFatPerEntry));
        data.put("inp_entry_carb_per_entry", String.valueOf(intInpEntryCarbPerEntry));
        data.put("inp_entry_protein_per_entry", String.valueOf(intInpEntryProteinPerEntry));

        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                textViewGramOrPcsClickedSendUpdatesToServerAnswer();
            }
        });
        task.execute();

        // Close database
        db.close();
    }

    public void textViewGramOrPcsClickedSendUpdatesToServerAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        Toast.makeText(this, "Food diary updated", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(FoodDiaryEntriesEditDeleteActivity.this, MainActivity.class);
        startActivity(i);

        db.close();
    }
    /*- Text View Delete Clicked ---------------------------------------------------------------- */
    public void textViewDeleteClicked(){
        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Updated
        DateFormat dfhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inpUpdated = dfhhmmss.format(Calendar.getInstance().getTime());


        // Delete
        String q = "DELETE FROM food_diary_entires WHERE entry_id=" + currentEntryId;
        db.rawQuery(q);



        // food_diary_totals_meals :: Calculate :: Get all meals for that day, and update numbers
        String query = "SELECT _id, entry_id, entry_energy_per_entry, entry_fat_per_entry, entry_carb_per_entry, entry_protein_per_entry " +
                "FROM food_diary_entires WHERE entry_user_id=" + myUserId + " " +
                "AND entry_date='" + currentTotalDayDate + "' " +
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
                "AND total_meal_date='" + currentTotalDayDate + "' " +
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
                    "'" + currentTotalDayDate + "', " +
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
                    "AND total_meal_date='" + currentTotalDayDate + "' " +
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
                "FROM food_diary_totals_days WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + currentTotalDayDate + "'";
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
                "WHERE total_meal_user_id=" + myUserId + " AND total_meal_date='" + currentTotalDayDate + "'";
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
                "WHERE total_day_user_id=" + myUserId + " AND total_day_date='" + currentTotalDayDate + "'";
        db.rawQuery(update);

        // Send to server
        String url    = apiFooDiaryURL + "/post_update_food_diary_delete_food_or_recipe_entry.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_entry_id", String.valueOf(currentEntryId));

        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                textViewDeleteClickedSendUpdatesToServerAnswer();
            }
        });
        task.execute();

        // Close database
        db.close();
    } // textViewDeleteClicked

    public void textViewDeleteClickedSendUpdatesToServerAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        Toast.makeText(this, "Entry deleted from food diary (" + dataResult + ")", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(FoodDiaryEntriesEditDeleteActivity.this, MainActivity.class);
        startActivity(i);

        db.close();
    }
}