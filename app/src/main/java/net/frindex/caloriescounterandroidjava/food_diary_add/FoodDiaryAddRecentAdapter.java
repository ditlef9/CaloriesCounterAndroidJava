package net.frindex.caloriescounterandroidjava.food_diary_add;

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

/**
 *
 * File: FoodDiaryAddRecentAdapter.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class FoodDiaryAddRecentAdapter extends CursorAdapter {
    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";



    private Context mContext;

    public FoodDiaryAddRecentAdapter(Context context, Cursor cursor) {
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
        int current_id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        int currentLastUsedId = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_id"));
        int currentLastUsedUserId = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_user_id"));
        int currentLastUsedDayOfWeek = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_day_of_week"));
        int currentLastUsedMealId = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_meal_id"));
        int currentLastUsedFoodId = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_food_id"));
        int currentLastUsedRecipeId = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_recipe_id"));
        double currentLastServingSize = cursor.getDouble(cursor.getColumnIndexOrThrow("last_used_serving_size"));
        int currentLastUsedTimes = cursor.getInt(cursor.getColumnIndexOrThrow("last_used_times"));
        String currentLastUsedDate = cursor.getString(cursor.getColumnIndexOrThrow("last_used_date"));
        String currentLastUsedUpdated = cursor.getString(cursor.getColumnIndexOrThrow("last_used_updated"));
        String currentLastUsedSynchronized = cursor.getString(cursor.getColumnIndexOrThrow("last_used_synchronized"));
        String lastUsedName = cursor.getString(cursor.getColumnIndexOrThrow("last_used_name"));
        String lastUsedManufacturer = cursor.getString(cursor.getColumnIndexOrThrow("last_used_manufacturer"));
        String lastUsedImagePath = cursor.getString(cursor.getColumnIndexOrThrow("last_used_image_path"));
        String lastUsedImageThumb = cursor.getString(cursor.getColumnIndexOrThrow("last_used_image_thumb"));
        String lastUsedNetContent = cursor.getString(cursor.getColumnIndexOrThrow("last_used_net_content"));
        String lastUsedNetContentMeasurement = cursor.getString(cursor.getColumnIndexOrThrow("last_used_net_content_measurement"));
        String lastUsedServingSizeGram = cursor.getString(cursor.getColumnIndexOrThrow("last_used_serving_size_gram"));
        String lastUsedServingSizeGramMeasurement = cursor.getString(cursor.getColumnIndexOrThrow("last_used_serving_size_gram_measurement"));
        String lastUsedServingSizePcs = cursor.getString(cursor.getColumnIndexOrThrow("last_used_serving_size_pcs"));
        String lastUsedServingSizePcsMeasurement = cursor.getString(cursor.getColumnIndexOrThrow("last_used_serving_size_pcs_measurement"));
        String lastUsedCaloriesPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_calories_per_hundred"));
        String lastUsedFatPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_fat_per_hundred"));
        String lastUsedSaturatedFattyAcidsPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_saturated_fatty_acids_per_hundred"));
        String lastUsedCarbsPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_carbs_per_hundred"));
        String lastUsedSugarPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_sugar_per_hundred"));
        String lastUsedProteinsPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_proteins_per_hundred"));
        String lastUsedSaltPerHundred = cursor.getString(cursor.getColumnIndexOrThrow("last_used_salt_per_hundred"));
        String lastUsedCaloriesPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_calories_per_serving"));
        String lastUsedFatPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_fat_per_serving"));
        String lastUsedSaturatedFattyAcidsPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_saturated_fatty_acids_per_serving"));
        String lastUsedCarbsPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_carbs_per_serving"));
        String lastUsedSugarPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_sugar_per_serving"));
        String lastUsedProteinsPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_proteins_per_serving"));
        String lastUsedSaltPerServing = cursor.getString(cursor.getColumnIndexOrThrow("last_used_salt_per_serving"));

        // Title
        TextView listViewFoodTitle = view.findViewById(R.id.listViewFoodTitle);
        if (lastUsedManufacturer.equals("")) {
            listViewFoodTitle.setText(lastUsedName);
        }
        else {
            listViewFoodTitle.setText(lastUsedManufacturer + " " + lastUsedName);
        }

        // Per hundred
        TextView textViewHundred = view.findViewById(R.id.textViewHundred);
        textViewHundred.setText(lastUsedServingSizeGram + " " + lastUsedServingSizeGramMeasurement);

        TextView textViewHundredCalories = view.findViewById(R.id.textViewHundredCalories);
        textViewHundredCalories.setText(lastUsedCaloriesPerHundred);

        TextView textViewHundredFat = view.findViewById(R.id.textViewHundredFat);
        textViewHundredFat.setText(lastUsedFatPerHundred);

        TextView textViewHundredCarb = view.findViewById(R.id.textViewHundredCarb);
        textViewHundredCarb.setText(lastUsedCarbsPerHundred);

        TextView textViewHundredProtein = view.findViewById(R.id.textViewHundredProtein);
        textViewHundredProtein.setText(lastUsedProteinsPerHundred);

        // Per piece
        TextView textViewPiece = view.findViewById(R.id.textViewPiece);
        textViewPiece.setText(lastUsedServingSizePcs + " " + lastUsedServingSizePcsMeasurement);

        TextView textViewPieceCalories = view.findViewById(R.id.textViewPieceCalories);
        textViewPieceCalories.setText(lastUsedCaloriesPerServing);

        TextView textViewPieceFat = view.findViewById(R.id.textViewPieceFat);
        textViewPieceFat.setText(lastUsedFatPerServing);

        TextView textViewPieceCarb = view.findViewById(R.id.textViewPieceCarb);
        textViewPieceCarb.setText(lastUsedCarbsPerServing);

        TextView textViewPieceProtein = view.findViewById(R.id.textViewPieceProtein);
        textViewPieceProtein.setText(lastUsedProteinsPerServing);

        // Img
        ImageView imageViewFoodImage = (ImageView)view.findViewById(R.id.imageViewFoodImage);
        String imageURL = websiteURL + "/" + lastUsedImagePath + "/" + lastUsedImageThumb;
        Picasso.get().load(imageURL).into(imageViewFoodImage);

    }
}
