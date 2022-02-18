package net.frindex.caloriescounterandroidjava.food_diary_add;
/**
 *
 * File: FoodDiaryAddRecipeAdapter.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.squareup.picasso.Picasso;

import net.frindex.caloriescounterandroidjava.R;

public class FoodDiaryAddRecipeAdapter extends CursorAdapter {
    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";



    private Context mContext;

    public FoodDiaryAddRecipeAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mContext = context;
    }

    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_food_diary_add_food_adapter, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {



        // Extract properties from cursor
        int get_Id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String recipeId = cursor.getString(cursor.getColumnIndexOrThrow("recipe_id"));
        String recipeUserId = cursor.getString(cursor.getColumnIndexOrThrow("recipe_user_id"));
        String recipeTitle = cursor.getString(cursor.getColumnIndexOrThrow("recipe_title"));
        String recipeCategoryId = cursor.getString(cursor.getColumnIndexOrThrow("recipe_category_id"));
        String recipeLanguage = cursor.getString(cursor.getColumnIndexOrThrow("recipe_language"));
        String recipeIntroduction = cursor.getString(cursor.getColumnIndexOrThrow("recipe_introduction"));
        String recipeDirections = cursor.getString(cursor.getColumnIndexOrThrow("recipe_directions"));
        String recipeImagePath = cursor.getString(cursor.getColumnIndexOrThrow("recipe_image_path"));
        String recipeImage = cursor.getString(cursor.getColumnIndexOrThrow("recipe_image"));
        String recipeThumb = cursor.getString(cursor.getColumnIndexOrThrow("recipe_thumb"));
        String recipeVideo = cursor.getString(cursor.getColumnIndexOrThrow("recipe_video"));
        String recipeDate = cursor.getString(cursor.getColumnIndexOrThrow("recipe_date"));
        String recipeTime = cursor.getString(cursor.getColumnIndexOrThrow("recipe_time"));
        String recipeCusineId = cursor.getString(cursor.getColumnIndexOrThrow("recipe_cusine_id"));
        String recipeSeasonId = cursor.getString(cursor.getColumnIndexOrThrow("recipe_season_id"));
        String recipeOccasionId = cursor.getString(cursor.getColumnIndexOrThrow("recipe_occasion_id"));
        String recipeMarkedAsSpam = cursor.getString(cursor.getColumnIndexOrThrow("recipe_marked_as_spam"));
        String recipeUniqueHits = cursor.getString(cursor.getColumnIndexOrThrow("recipe_unique_hits"));
        String recipeUniqueHitsIpBlock = cursor.getString(cursor.getColumnIndexOrThrow("recipe_unique_hits_ip_block"));
        String recipeComments = cursor.getString(cursor.getColumnIndexOrThrow("recipe_comments"));
        String recipeUserIp = cursor.getString(cursor.getColumnIndexOrThrow("recipe_user_ip"));
        String recipePassword = cursor.getString(cursor.getColumnIndexOrThrow("recipe_password"));
        String recipeLastViewed = cursor.getString(cursor.getColumnIndexOrThrow("recipe_last_viewed"));

        String numberHundredCalories = cursor.getString(cursor.getColumnIndexOrThrow("number_hundred_calories"));
        String numberHundredProteins = cursor.getString(cursor.getColumnIndexOrThrow("number_hundred_proteins"));
        String numberHundredFat = cursor.getString(cursor.getColumnIndexOrThrow("number_hundred_fat"));
        String numberHundredCarbs = cursor.getString(cursor.getColumnIndexOrThrow("number_hundred_carbs"));
        String numberServingCalories = cursor.getString(cursor.getColumnIndexOrThrow("number_serving_calories"));
        String numberServingProteins = cursor.getString(cursor.getColumnIndexOrThrow("number_serving_proteins"));
        String numberServingFat = cursor.getString(cursor.getColumnIndexOrThrow("number_serving_fat"));
        String numberServingCarbs = cursor.getString(cursor.getColumnIndexOrThrow("number_serving_carbs"));


        // Title
        TextView listViewFoodTitle = view.findViewById(R.id.listViewFoodTitle);
        listViewFoodTitle.setText(recipeTitle);


        // Per hundred

        TextView textViewHundredCalories = view.findViewById(R.id.textViewHundredCalories);
        textViewHundredCalories.setText(numberHundredCalories);

        TextView textViewHundredFat = view.findViewById(R.id.textViewHundredFat);
        textViewHundredFat.setText(numberHundredFat);

        TextView textViewHundredCarb = view.findViewById(R.id.textViewHundredCarb);
        textViewHundredCarb.setText(numberHundredCarbs);

        TextView textViewHundredProtein = view.findViewById(R.id.textViewHundredProtein);
        textViewHundredProtein.setText(numberHundredProteins);

        // Per piece

        TextView textViewPieceCalories = view.findViewById(R.id.textViewPieceCalories);
        textViewPieceCalories.setText(numberServingCalories);

        TextView textViewPieceFat = view.findViewById(R.id.textViewPieceFat);
        textViewPieceFat.setText(numberServingFat);

        TextView textViewPieceCarb = view.findViewById(R.id.textViewPieceCarb);
        textViewPieceCarb.setText(numberServingCarbs);

        TextView textViewPieceProtein = view.findViewById(R.id.textViewPieceProtein);
        textViewPieceProtein.setText(numberServingProteins);

        // Img
        ImageView imageViewFoodImage = (ImageView)view.findViewById(R.id.imageViewFoodImage);
        String imageURL = websiteURL + "/" + recipeImagePath + "/" + recipeThumb;
        Picasso.get().load(imageURL).into(imageViewFoodImage);

    }

}
