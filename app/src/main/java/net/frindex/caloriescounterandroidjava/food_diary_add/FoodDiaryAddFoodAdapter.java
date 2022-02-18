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
 * File: FoodDiaryAddFoodAdapter.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class FoodDiaryAddFoodAdapter extends CursorAdapter {
    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";



    private Context mContext;

    public FoodDiaryAddFoodAdapter(Context context, Cursor cursor) {
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
        String foodId = cursor.getString(cursor.getColumnIndexOrThrow("food_id"));
        String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
        String foodManufacturerName = cursor.getString(cursor.getColumnIndexOrThrow("food_manufacturer_name"));
        String foodDescription = cursor.getString(cursor.getColumnIndexOrThrow("food_description"));

        String foodServingSizeGram = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_gram"));
        String foodServingSizeGramMeasurement = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_gram_measurement"));
        String foodServingSizePcs = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_pcs"));
        String foodServingSizePcsMeasurement = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_pcs_measurement"));
        String foodEnergy = cursor.getString(cursor.getColumnIndexOrThrow("food_energy"));
        String foodProteins = cursor.getString(cursor.getColumnIndexOrThrow("food_proteins"));
        String foodCarbohydrates = cursor.getString(cursor.getColumnIndexOrThrow("food_carbohydrates"));
        String foodFat = cursor.getString(cursor.getColumnIndexOrThrow("food_fat"));
        String foodEnergyCalculated = cursor.getString(cursor.getColumnIndexOrThrow("food_energy_calculated"));
        String foodProteinsCalculated = cursor.getString(cursor.getColumnIndexOrThrow("food_proteins_calculated"));
        String foodCarbohydratesCalculated = cursor.getString(cursor.getColumnIndexOrThrow("food_carbohydrates_calculated"));
        String foodFatCalculated = cursor.getString(cursor.getColumnIndexOrThrow("food_fat_calculated"));
        String foodImagePath = cursor.getString(cursor.getColumnIndexOrThrow("food_image_path"));
        String foodThumb = cursor.getString(cursor.getColumnIndexOrThrow("food_thumb_small"));

        // Title
        TextView listViewFoodTitle = view.findViewById(R.id.listViewFoodTitle);
        listViewFoodTitle.setText(foodManufacturerName + " " + foodName);


        // Per hundred
        TextView textViewHundred = view.findViewById(R.id.textViewHundred);
        textViewHundred.setText(foodServingSizeGram + " " + foodServingSizeGramMeasurement);

        TextView textViewHundredCalories = view.findViewById(R.id.textViewHundredCalories);
        textViewHundredCalories.setText(foodEnergy);

        TextView textViewHundredFat = view.findViewById(R.id.textViewHundredFat);
        textViewHundredFat.setText(foodFat);

        TextView textViewHundredCarb = view.findViewById(R.id.textViewHundredCarb);
        textViewHundredCarb.setText(foodCarbohydrates);

        TextView textViewHundredProtein = view.findViewById(R.id.textViewHundredProtein);
        textViewHundredProtein.setText(foodProteins);

        // Per piece
        TextView textViewPiece = view.findViewById(R.id.textViewPiece);
        textViewPiece.setText(foodServingSizePcs + " " + foodServingSizePcsMeasurement);

        TextView textViewPieceCalories = view.findViewById(R.id.textViewPieceCalories);
        textViewPieceCalories.setText(foodEnergyCalculated);

        TextView textViewPieceFat = view.findViewById(R.id.textViewPieceFat);
        textViewPieceFat.setText(foodFatCalculated);

        TextView textViewPieceCarb = view.findViewById(R.id.textViewPieceCarb);
        textViewPieceCarb.setText(foodCarbohydratesCalculated);

        TextView textViewPieceProtein = view.findViewById(R.id.textViewPieceProtein);
        textViewPieceProtein.setText(foodProteinsCalculated);

        // Img
        ImageView imageViewFoodImage = (ImageView)view.findViewById(R.id.imageViewFoodImage);
        String imageURL = websiteURL + "/" + foodImagePath + "/" + foodThumb;
        Picasso.get().load(imageURL).into(imageViewFoodImage);


    }
}
