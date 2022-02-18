package net.frindex.caloriescounterandroidjava.food_diary_goal;

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
 * File: FoodAMainCategoriesActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class MyGoalsCursorAdapter extends CursorAdapter {


    private LayoutInflater inflater;
    private Context mContext;

    public MyGoalsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

        inflater = LayoutInflater.from(context);
        mContext = context;
    }
    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_my_goals_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Extract properties from cursor
        String goalId = cursor.getString(cursor.getColumnIndexOrThrow("goal_id"));
        String goalUserId = cursor.getString(cursor.getColumnIndexOrThrow("goal_user_id"));
        String goalCurrentWeight = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_weight"));
        String goalCurrentFatPercentage = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_fat_percentage"));
        String goalTargetWeight = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_weight"));
        String goalTargetFatPercentage = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_fat_percentage"));
        String goalIWantTo = cursor.getString(cursor.getColumnIndexOrThrow("goal_i_want_to"));
        String goalWeeklyGoal = cursor.getString(cursor.getColumnIndexOrThrow("goal_weekly_goal"));
        String goalDate = cursor.getString(cursor.getColumnIndexOrThrow("goal_date"));
        String goalActivityLevel = cursor.getString(cursor.getColumnIndexOrThrow("goal_activity_level"));

        String goalCurrentBmi = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_bmi"));
        String goalTargetBmi = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_bmi"));
        String goalCurrentBmrCalories = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_bmr_calories"));
        String goalCurrentBmrFat = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_bmr_fat"));
        String goalCurrentBmrCarbs = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_bmr_carbs"));
        String goalCurrentBmrProteins = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_bmr_proteins"));
        String goalCurrentSedentaryCalories = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_sedentary_calories"));
        String goalCurrentSedentaryFat = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_sedentary_fat"));
        String goalCurrentSedentaryCarbs = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_sedentary_carbs"));
        String goalCurrentSedentaryProteins = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_sedentary_proteins"));
        String goalCurrentWithActivityCalories = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_with_activity_calories"));
        String goalCurrentWithActivityFat = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_with_activity_fat"));
        String goalCurrentWithActivityCarbs = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_with_activity_carbs"));
        String goalCurrentWithActivityProteins = cursor.getString(cursor.getColumnIndexOrThrow("goal_current_with_activity_proteins"));
        String goalTargetBmrCalories = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_bmr_calories"));
        String goalTargetBmrFat = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_bmr_fat"));
        String goalTargetBmrCarbs = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_bmr_carbs"));
        String goalTargetBmrProteins = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_bmr_proteins"));
        String goalTargetSedentaryCalories = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_sedentary_calories"));
        String goalTargetSedentaryFat = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_sedentary_fat"));
        String goalTargetSedentaryCarbs = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_sedentary_carbs"));
        String goalTargetSedentaryProteins = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_sedentary_proteins"));
        String goalTargetWithActivityCalories = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_with_activity_calories"));
        String goalTargetWithActivityFat = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_with_activity_fat"));
        String goalTargetWithActivityCarbs = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_with_activity_carbs"));
        String goalTargetWithActivityProteins = cursor.getString(cursor.getColumnIndexOrThrow("goal_target_with_activity_proteins"));
        String goalUpdated = cursor.getString(cursor.getColumnIndexOrThrow("goal_updated"));
        String goalSynchronized = cursor.getString(cursor.getColumnIndexOrThrow("goal_synchronized"));
        String goalNotes = cursor.getString(cursor.getColumnIndexOrThrow("goal_notes"));





        // Date
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewDate);

        String year = "";
        String month = "";
        String day = "";
        try {
            String[] items1 = goalDate.split("-");
            year = items1[0];
            month = items1[1];
            day = items1[2];
        }
        catch(Exception e){
        }
        if(month.equals("01")){
            month = context.getString(R.string.january);
        }
        else if(month.equals("02")){
            month = context.getString(R.string.february);
        }
        else if(month.equals("03")){
            month = context.getString(R.string.march);
        }
        else if(month.equals("04")){
            month = context.getString(R.string.april);
        }
        else if(month.equals("05")){
            month = context.getString(R.string.may);
        }
        else if(month.equals("06")){
            month = context.getString(R.string.june);
        }
        else if(month.equals("07")){
            month = context.getString(R.string.july);
        }
        else if(month.equals("08")){
            month = context.getString(R.string.august);
        }
        else if(month.equals("09")){
            month = context.getString(R.string.september);
        }
        else if(month.equals("10")){
            month = context.getString(R.string.october);
        }
        else if(month.equals("11")){
            month = context.getString(R.string.november);
        }
        else if(month.equals("12")){
            month = context.getString(R.string.december);
        }
        textViewDate.setText(day + " " + month + " " + year);

        // From - To
        TextView textViewFromTo = (TextView) view.findViewById(R.id.textViewFromTo);
        String fromTo = context.getString(R.string.from);
        fromTo = fromTo + " " + goalCurrentWeight + " " + context.getString(R.string.kg_lowercase);
        fromTo = fromTo + " " + context.getString(R.string.to_lowercase);
        fromTo = fromTo + " " + goalTargetWeight + " " + context.getString(R.string.kg_lowercase);
        textViewFromTo.setText(fromTo);

        // Active

        TextView textViewStayTheSameWeightActiveHeadline = (TextView) view.findViewById(R.id.textViewStayTheSameWeightActiveHeadline);
        TextView textViewLoseWeightActiveHeadline = (TextView) view.findViewById(R.id.textViewLoseWeightActiveHeadline);

        if(goalActivityLevel.equals("1.2")){
            textViewStayTheSameWeightActiveHeadline.setText(context.getString(R.string.sedentary));
            textViewLoseWeightActiveHeadline.setText(context.getString(R.string.sedentary));
        }
        else if(goalActivityLevel.equals("1.375")){
            textViewStayTheSameWeightActiveHeadline.setText(context.getString(R.string.lightly));
            textViewLoseWeightActiveHeadline.setText(context.getString(R.string.lightly));
        }
        else if(goalActivityLevel.equals("1.55")){
            textViewStayTheSameWeightActiveHeadline.setText(context.getString(R.string.moderately));
            textViewLoseWeightActiveHeadline.setText(context.getString(R.string.moderately));
        }
        else if(goalActivityLevel.equals("1.725")){
            textViewStayTheSameWeightActiveHeadline.setText(context.getString(R.string.very_active));
            textViewLoseWeightActiveHeadline.setText(context.getString(R.string.very_active));
        }
        else if(goalActivityLevel.equals("1.9")){
            textViewStayTheSameWeightActiveHeadline.setText(context.getString(R.string.extra_active));
            textViewLoseWeightActiveHeadline.setText(context.getString(R.string.extra_active));
        }

        // Same weight : BMR
        TextView textViewStayTheSameWeightBMRCalories = view.findViewById(R.id.textViewStayTheSameWeightBMRCalories);
        textViewStayTheSameWeightBMRCalories.setText(goalCurrentBmrCalories);

        TextView textViewStayTheSameWeightBMRCarbs = view.findViewById(R.id.textViewStayTheSameWeightBMRCarbs);
        textViewStayTheSameWeightBMRCarbs.setText(goalCurrentBmrCarbs);

        TextView textViewStayTheSameWeightBMRFat = view.findViewById(R.id.textViewStayTheSameWeightBMRFat);
        textViewStayTheSameWeightBMRFat.setText(goalCurrentBmrFat);

        TextView textViewStayTheSameWeightBMRProtein = view.findViewById(R.id.textViewStayTheSameWeightBMRProtein);
        textViewStayTheSameWeightBMRProtein.setText(goalCurrentBmrProteins);

        // Same weight : Sedentary
        TextView textViewStayTheSameWeightSedentaryCalories = view.findViewById(R.id.textViewStayTheSameWeightSedentaryCalories);
        textViewStayTheSameWeightSedentaryCalories.setText(goalCurrentSedentaryCalories);

        TextView textViewStayTheSameWeightSedentaryCarbs = view.findViewById(R.id.textViewStayTheSameWeightSedentaryCarbs);
        textViewStayTheSameWeightSedentaryCarbs.setText(goalCurrentSedentaryCarbs);

        TextView textViewStayTheSameWeightSedentaryFat = view.findViewById(R.id.textViewStayTheSameWeightSedentaryFat);
        textViewStayTheSameWeightSedentaryFat.setText(goalCurrentSedentaryFat);

        TextView textViewStayTheSameWeightSedentaryProtein = view.findViewById(R.id.textViewStayTheSameWeightSedentaryProtein);
        textViewStayTheSameWeightSedentaryProtein.setText(goalCurrentSedentaryProteins);

        // Same weight : Active
        TextView textViewStayTheSameWeightActiveCalories = view.findViewById(R.id.textViewStayTheSameWeightActiveCalories);
        textViewStayTheSameWeightActiveCalories.setText(goalCurrentWithActivityCalories);

        TextView textViewStayTheSameWeightActiveCarbs = view.findViewById(R.id.textViewStayTheSameWeightActiveCarbs);
        textViewStayTheSameWeightActiveCarbs.setText(goalCurrentWithActivityCarbs);

        TextView textViewStayTheSameWeightActiveFat = view.findViewById(R.id.textViewStayTheSameWeightActiveFat);
        textViewStayTheSameWeightActiveFat.setText(goalCurrentWithActivityFat);

        TextView textViewStayTheSameWeightActiveProtein = view.findViewById(R.id.textViewStayTheSameWeightActiveProtein);
        textViewStayTheSameWeightActiveProtein.setText(goalCurrentWithActivityProteins);




        // Lose weight : BMR
        TextView textViewLoseWeightBMRCalories = view.findViewById(R.id.textViewLoseWeightBMRCalories);
        textViewLoseWeightBMRCalories.setText(goalTargetBmrCalories);

        TextView textViewLoseWeightBMRCarbs = view.findViewById(R.id.textViewLoseWeightBMRCarbs);
        textViewLoseWeightBMRCarbs.setText(goalTargetBmrCarbs);

        TextView textViewLoseWeightBMRFat = view.findViewById(R.id.textViewLoseWeightBMRFat);
        textViewLoseWeightBMRFat.setText(goalTargetBmrFat);

        TextView textViewLoseWeightBMRProtein = view.findViewById(R.id.textViewLoseWeightBMRProtein);
        textViewLoseWeightBMRProtein.setText(goalTargetBmrProteins);

        // Lose weight : Sedentary
        TextView textViewLoseWeightSedentaryCalories = view.findViewById(R.id.textViewLoseWeightSedentaryCalories);
        textViewLoseWeightSedentaryCalories.setText(goalTargetSedentaryCalories);

        TextView textViewLoseWeightSedentaryCarbs = view.findViewById(R.id.textViewLoseWeightSedentaryCarbs);
        textViewLoseWeightSedentaryCarbs.setText(goalTargetSedentaryCarbs);

        TextView textViewLoseWeightSedentaryFat = view.findViewById(R.id.textViewLoseWeightSedentaryFat);
        textViewLoseWeightSedentaryFat.setText(goalTargetSedentaryFat);

        TextView textViewLoseWeightSedentaryProtein = view.findViewById(R.id.textViewLoseWeightSedentaryProtein);
        textViewLoseWeightSedentaryProtein.setText(goalTargetSedentaryProteins);

        // Lose weight : Active
        TextView textViewLoseWeightActiveCalories = view.findViewById(R.id.textViewLoseWeightActiveCalories);
        textViewLoseWeightActiveCalories.setText(goalTargetWithActivityCalories);

        TextView textViewLoseWeightActiveCarbs = view.findViewById(R.id.textViewLoseWeightActiveCarbs);
        textViewLoseWeightActiveCarbs.setText(goalTargetWithActivityCarbs);

        TextView textViewLoseWeightActiveFat = view.findViewById(R.id.textViewLoseWeightActiveFat);
        textViewLoseWeightActiveFat.setText(goalTargetWithActivityFat);

        TextView textViewLoseWeightActiveProtein = view.findViewById(R.id.textViewLoseWeightActiveProtein);
        textViewLoseWeightActiveProtein.setText(goalTargetWithActivityProteins);

    }
}