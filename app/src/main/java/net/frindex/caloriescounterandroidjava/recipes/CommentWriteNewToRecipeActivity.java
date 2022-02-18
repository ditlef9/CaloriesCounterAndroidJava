package net.frindex.caloriescounterandroidjava.recipes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.common.OutputString;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;
import net.frindex.caloriescounterandroidjava.user.UserLoginActivity;

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
public class CommentWriteNewToRecipeActivity extends AppCompatActivity {
    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiURL       = "https://summerslim.codecourses.eu/recipes/api"; // Without ending slash
    String apiPassword  = "w7Vdwenb";


    // My user
    String myUserId = "";
    String myUserPassword = "";


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

    // Comment
    int currentCommentRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_write_new_to_recipe);

        // Get recipe ID
        Bundle b = getIntent().getExtras();
        currentRecipeId = b.getInt("currentRecipeId");

        /* Get my user id */
        myUserId = getMyUserId();

        // Recipe
        showRecipeTitle();

        // listeners
        listeners();

        // Check if I can comment
        checkIfICanComment();
    }

    /*- Get my user id ----------------------------------------------------------------- */
    public String getMyUserId(){
        String myUserID = "";

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Database: Count rows in user table
        int numberUsersRows = db.count("users");

        // Database: close
        db.close();

        if(numberUsersRows >= 1) {
            // Am I fully registered ?
            db.open();
            long rowID = 1;
            String fieldsUser[] = new String[] { "_id", "user_id", "user_password", "user_registered", "user_rank" };
            Cursor cursorUser = db.select("users", fieldsUser, "_id", rowID);
            String stringUserId         = cursorUser.getString(1);
            String stringUserPassword   = cursorUser.getString(2);
            String stringUserRegistered = cursorUser.getString(3);
            String stringUserRank       = cursorUser.getString(4);

            // Database: Close
            db.close();

            if(stringUserRegistered == null){
                // I am not finished with my registration...
                Intent i = new Intent(CommentWriteNewToRecipeActivity.this, UserLoginActivity.class);
                startActivity(i);
            }
            else {
                // All ok
                myUserID = stringUserId;
                myUserPassword = stringUserPassword;
            }
        }

        return myUserID;
    }

    /*- Check if I can comment --------------------------------------------------- */
    public void checkIfICanComment(){

        String stringURL    = apiURL + "/check_if_i_can_comment.php";
        String stringMethod = "post";

        TextView textViewDynamicText = (TextView)findViewById(R.id.textViewDynamicText);
        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_recipe_id", String.valueOf(currentRecipeId));
        data.put("inp_user_id", myUserId);
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringURL, stringMethod, data, textViewDynamicText, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                checkIfICanCommentPHPPost();
            }
        });
        task.execute();
    }
    public void checkIfICanCommentPHPPost(){
        // Read answer from server
        TextView textViewDynamicText = (TextView)findViewById(R.id.textViewDynamicText);
        String answer = textViewDynamicText.getText().toString();

        if(answer.equals("ipblock")) {

            Toast.makeText(this, "IP Block\nPlease wait one hour before commenting", Toast.LENGTH_LONG).show();

            // Move user to recipe
            Intent i = new Intent(CommentWriteNewToRecipeActivity.this, RecipesCViewRecipeActivity.class);
            i.putExtra("currentRecipe_Id", "");
            i.putExtra("currentRecipeId", currentRecipeId);
            startActivity(i);
            finish();

        }

    }

    /*- Show recipe title ----------------------------------------------------------------------- */
    private void showRecipeTitle() {

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Cursor
        String query = "SELECT recipes._id, recipes.recipe_id, recipes.recipe_user_id, " + // 2
                "recipes.recipe_title, recipes.recipe_category_id, recipes.recipe_language, " + // 5
                "recipes.recipe_introduction, recipes.recipe_directions, recipes.recipe_image_path, " + // 8
                "recipes.recipe_image, recipes.recipe_thumb, recipes.recipe_video, " + // 11
                "recipes.recipe_date, recipes.recipe_time, recipes.recipe_cusine_id, " + // 14
                "recipes.recipe_season_id, recipes.recipe_occasion_id, recipes.recipe_marked_as_spam, " + // 17
                "recipes.recipe_unique_hits, recipes.recipe_unique_hits_ip_block, recipes.recipe_comments, " + // 20
                "recipes.recipe_user_ip, recipes.recipe_password, recipes.recipe_last_viewed " +  // 23
                "FROM recipes WHERE recipes.recipe_id=" + currentRecipeId + "";
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

        // Title
        TextView textViewRecipeTitle = findViewById(R.id.textViewRecipeTitle);
        currentRecipeTitle = new OutputString().outputHTML(currentRecipeTitle);
        textViewRecipeTitle.setText(currentRecipeTitle);



        db.close();
    } // showRecipeTitle





    /*- Submit button watcher ----------------------------------------------------- */
    public void listeners() {
        // Stars
        ImageView imageViewStar1 = findViewById(R.id.imageViewStar1);
        imageViewStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeRating(1);
            }
        });

        ImageView imageViewStar2 = findViewById(R.id.imageViewStar2);
        imageViewStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeRating(2);
            }
        });

        ImageView imageViewStar3 = findViewById(R.id.imageViewStar3);
        imageViewStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeRating(3);
            }
        });

        ImageView imageViewStar4 = findViewById(R.id.imageViewStar4);
        imageViewStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeRating(4);
            }
        });

        ImageView imageViewStar5 = findViewById(R.id.imageViewStar5);
        imageViewStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeRating(5);
            }
        });

        // Submit
        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSubmitOnClick();
            }
        });

    }
    /*- Recipe Rating ------------------------------------------------------------ */
    public void recipeRating(int selectedRating){
        // Transfer
        currentCommentRating = selectedRating;


        // get ref for imgs
        ImageView imageViewStar1 = findViewById(R.id.imageViewStar1);
        ImageView imageViewStar2 = findViewById(R.id.imageViewStar2);
        ImageView imageViewStar3 = findViewById(R.id.imageViewStar3);
        ImageView imageViewStar4 = findViewById(R.id.imageViewStar4);
        ImageView imageViewStar5 = findViewById(R.id.imageViewStar5);



        if(selectedRating == 1) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if(selectedRating == 2) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if(selectedRating == 3) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if(selectedRating == 4) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if(selectedRating == 5){
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_black_24px);
        }
    }

    /*- Submit button on click ----------------------------------------------------- */
    public void buttonSubmitOnClick(){
        // Guess error
        int error = 0;

        // Comment
        EditText editTextCommentTitle = (EditText)findViewById(R.id.editTextCommentTitle);
        String stringCommentTitle= editTextCommentTitle.getText().toString().trim();
        if(stringCommentTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_LONG).show();
        }

        // Rating
        if(currentCommentRating == 0){
            Toast.makeText(this, "Please rate by selecting stars", Toast.LENGTH_LONG).show();
        }

        // Text
        EditText editTextComment = (EditText)findViewById(R.id.editTextComment);
        String stringCommentText = editTextComment.getText().toString().trim();
        if(stringCommentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_LONG).show();
        }



        else {
            // Send comment to server
            TextView textViewDynamicText = (TextView)findViewById(R.id.textViewDynamicText);

            // Send Edits to PHP
            String stringURL    = apiURL + "/post_new_recipe_comment.php";
            String stringMethod = "post";

            Map<String, String> data = new HashMap<String, String>();
            data.put("inp_recipe_id", String.valueOf(currentRecipeId));
            data.put("inp_user_id", myUserId);
            data.put("inp_user_password", myUserPassword);
            data.put("inp_title", stringCommentTitle);
            data.put("inp_rating", String.valueOf(currentCommentRating));
            data.put("inp_text", stringCommentText);
            HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringURL, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    sendCommentToPHPPost();
                }
            });
            task.execute();
        }
    }

    public void sendCommentToPHPPost(){

        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String data = cursorData.getString(1);
        db.close();

        Toast.makeText(this, data, Toast.LENGTH_LONG).show();


        // Move user to recipe
        Intent i = new Intent(CommentWriteNewToRecipeActivity.this, RecipesCViewRecipeActivity.class);
        i.putExtra("currentRecipeId", currentRecipeId);
        startActivity(i);
        finish();

    }
}