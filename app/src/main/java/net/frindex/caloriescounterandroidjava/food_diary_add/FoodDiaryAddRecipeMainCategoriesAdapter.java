package net.frindex.caloriescounterandroidjava.food_diary_add;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import net.frindex.caloriescounterandroidjava.R;

/**
 *
 * File: FoodDiaryAddRecipeMainCategoriesAdapter.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */

public class FoodDiaryAddRecipeMainCategoriesAdapter extends CursorAdapter {


    private LayoutInflater inflater;
    private Context mContext;

    public FoodDiaryAddRecipeMainCategoriesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

        inflater = LayoutInflater.from(context);
        mContext = context;
    }
    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_food_diary_add_food_main_categories_adapter, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Extract properties from cursor
        String title = cursor.getString(cursor.getColumnIndexOrThrow("category_translation_value"));


        // Title
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);



    }
}
