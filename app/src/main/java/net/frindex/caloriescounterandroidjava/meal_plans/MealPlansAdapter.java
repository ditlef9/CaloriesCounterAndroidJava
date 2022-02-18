package net.frindex.caloriescounterandroidjava.meal_plans;

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
 * File: FoodBSubCategoriesActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class MealPlansAdapter extends CursorAdapter {
    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";



    private Context mContext;

    public MealPlansAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mContext = context;
    }

    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_meal_plans_adapter, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {



        // Extract properties from cursor
        int get_Id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String mealPlanId = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_id"));
        String mealPlanTitle = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_title"));
        String mealPlanIntroduction = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_introduction"));
        String mealPlanTotalEnergyWithoutTraining = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_total_energy_without_training"));

        String mealPlanTotalEnergyWithTraining = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_total_energy_with_training"));
        String mealPlanAverageKcalWithoutTraining = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_average_kcal_without_training"));
        String mealPlanAverageKcalWithTraining = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_average_kcal_with_training"));
        String mealPlanImagePath = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_image_path"));
        String mealPlanImageThumb = cursor.getString(cursor.getColumnIndexOrThrow("meal_plan_image_thumb"));

        // Title
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(mealPlanTitle);

        // Kcal
        TextView textViewKcal = view.findViewById(R.id.textViewKcal);
        textViewKcal.setText(mealPlanTotalEnergyWithTraining + " " + context.getString(R.string.kcal_lowercase));


        // Img
        ImageView imageViewImage = (ImageView)view.findViewById(R.id.imageViewImage);
        String imageURL = websiteURL + "/" + mealPlanImagePath + "/" + mealPlanImageThumb;
        Picasso.get().load(imageURL).into(imageViewImage);
    }

}
