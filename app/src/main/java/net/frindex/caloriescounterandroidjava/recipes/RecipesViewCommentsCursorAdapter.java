package net.frindex.caloriescounterandroidjava.recipes;

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
public class RecipesViewCommentsCursorAdapter extends CursorAdapter {
    /* Api variables */
    String websiteURL   = "https://summerslim.codecourses.eu";




    public RecipesViewCommentsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_recipes_view_comments, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Extract properties from cursor
        String commentDatePrint = cursor.getString(cursor.getColumnIndexOrThrow("comment_date_print"));
        String commentUserId = cursor.getString(cursor.getColumnIndexOrThrow("comment_user_id"));
        String commentUserAlias = cursor.getString(cursor.getColumnIndexOrThrow("comment_user_alias"));
        String commentUserImagePath = cursor.getString(cursor.getColumnIndexOrThrow("comment_user_image_path"));
        String commentUserImageFile = cursor.getString(cursor.getColumnIndexOrThrow("comment_user_image_file"));
        String commentTitle = cursor.getString(cursor.getColumnIndexOrThrow("comment_title"));
        String commentText = cursor.getString(cursor.getColumnIndexOrThrow("comment_text"));
        int commentRating = cursor.getInt(cursor.getColumnIndexOrThrow("comment_rating"));


        // Find fields to populate in inflated template


        // Rating

        // get ref for imgs
        ImageView imageViewStar1 = view.findViewById(R.id.imageViewStar1);
        ImageView imageViewStar2 = view.findViewById(R.id.imageViewStar2);
        ImageView imageViewStar3 = view.findViewById(R.id.imageViewStar3);
        ImageView imageViewStar4 = view.findViewById(R.id.imageViewStar4);
        ImageView imageViewStar5 = view.findViewById(R.id.imageViewStar5);


        if (commentRating == 1) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if (commentRating == 2) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if (commentRating == 3) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_border_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if (commentRating == 4) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_border_black_24px);
        } else if (commentRating == 5) {
            imageViewStar1.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar2.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar3.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar4.setImageResource(R.drawable.ic_star_black_24px);
            imageViewStar5.setImageResource(R.drawable.ic_star_black_24px);
        }

        TextView textViewCommentTitle = (TextView) view.findViewById(R.id.textViewCommentTitle);
        textViewCommentTitle.setText(commentTitle);

        TextView textViewCommentAlias = (TextView) view.findViewById(R.id.textViewCommentAlias);
        textViewCommentAlias.setText(commentUserAlias);

        TextView textViewCommentDate = (TextView) view.findViewById(R.id.textViewCommentDate);
        textViewCommentDate.setText(commentDatePrint);

        TextView listViewCommentText = (TextView)view.findViewById(R.id.listViewCommentText);
        listViewCommentText.setText(commentText);

        // Img
        ImageView listViewCommenterImage = (ImageView)view.findViewById(R.id.listViewCommenterImage);
        if(commentUserImageFile != null) {
            if (!(commentUserImageFile.equals(""))) {
                String imageURL = websiteURL + "/" + commentUserImagePath + "/" + commentUserImageFile;
                Picasso.get().load(imageURL).into(listViewCommenterImage);
            }
        }
    }

}
