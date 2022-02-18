package net.frindex.caloriescounterandroidjava.menu;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

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
public class MenuActivityCursorAdapter extends CursorAdapter {


    private LayoutInflater inflater;
    private Context mContext;

    public MenuActivityCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

        inflater = LayoutInflater.from(context);
        mContext = context;
    }
    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_menu_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView imageViewImage = (ImageView)view.findViewById(R.id.imageViewImage);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);

        // Extract properties from cursor
        String title = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow("icon"));

        // Populate fields with extracted properties
        textViewTitle.setText(title);

        // Img
        if(icon.equalsIgnoreCase("ic_person_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_person_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_outline_adjust_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_outline_adjust_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_outline_local_dining_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_outline_local_dining_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_outline_local_cafe_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_outline_local_cafe_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_outline_local_mall_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_outline_local_mall_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_baseline_sync_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_baseline_sync_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_outline_home_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_outline_home_black_24px);
        }
        else if(icon.equalsIgnoreCase("ic_outline_forum_black_24px")){
            imageViewImage.setImageResource(R.drawable.ic_outline_forum_black_24px);
        }
        else{
            imageViewImage.setImageResource(R.drawable.ic_close_white_24px);
        }



    }
}