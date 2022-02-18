package net.frindex.caloriescounterandroidjava.food_diary_goal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
public class FoodDiaryGoalNewGActivityLevelActivity extends AppCompatActivity {
    /* Api variables */
    String apiUsersURL      = "https://summerslim.codecourses.eu/users/api"; // Without ending slash
    String apiFooDiaryURL   = "https://summerslim.codecourses.eu/food_diary/api"; // Without ending slash
    String apiPassword      = "w7Vdwenb";

    // My user
    int myUserId;
    String myUserAlias;
    String myUserEmail;
    String myUserPassword;
    String myUserLanguage;
    String myUserGender;
    int myUserHeight;
    String myUserMeasurement;
    String myUserDob;

    // My Goal
    int myGoal_Id;
    int myGoalId;
    int myGoalCurrentWeight;
    int myGoalCurrentFatPercentage;
    int myGoalTargetWeight;
    String myGoalIWantTo;
    String myGoalDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_goal_new_glevel);

        // Fetch my profile and goal
        fetchMyProfileAndGoal();

        // Measurement and measurement button
        initializeActivityListeners();
    }

    /*- Select action to start with ------------------------------------------------------------- */
    private void fetchMyProfileAndGoal() {

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // My Profile
        String q = "SELECT user_id, user_alias, user_email, user_password, user_language, user_gender, user_height, user_measurement, user_dob FROM users WHERE _id=1";
        Cursor cursorUser = db.rawQuery(q);

        myUserId = cursorUser.getInt(0);
        myUserAlias = cursorUser.getString(1);
        myUserEmail = cursorUser.getString(2);
        myUserPassword = cursorUser.getString(3);
        myUserLanguage = cursorUser.getString(4);
        myUserGender = cursorUser.getString(5);
        myUserHeight = cursorUser.getInt(6);
        myUserMeasurement = cursorUser.getString(7);
        myUserDob = cursorUser.getString(8);

        // My Goal
        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String dateyyyyMMdd = dfyyyymmdd.format(Calendar.getInstance().getTime());

        String query = "SELECT _id, goal_id, goal_current_weight, goal_current_fat_percentage, goal_target_weight, goal_i_want_to, goal_date " +
                "FROM food_diary_goals WHERE goal_user_id='" + myUserId + "' AND goal_date='" + dateyyyyMMdd + "'";
        Cursor goalCursor = db.rawQuery(query);

        myGoal_Id = goalCursor.getInt(0);
        myGoalId = goalCursor.getInt(1);
        myGoalCurrentWeight = goalCursor.getInt(2);
        myGoalCurrentFatPercentage = goalCursor.getInt(3);
        myGoalTargetWeight = goalCursor.getInt(4);
        myGoalIWantTo = goalCursor.getString(5);
        myGoalDate = goalCursor.getString(6);

        // DB Close
        db.close();


    } // fetchMyProfileAndGoal




    /*- Initialize Listeners -------------------------------------------------------------------- */
    private void initializeActivityListeners() {
        /* Next listener */
        TextView textViewActivityLevelA = findViewById(R.id.textViewActivityLevelA);
        textViewActivityLevelA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { activityLevelSelected(1.2);
            }
        });

        TextView textViewActivityLevelB = findViewById(R.id.textViewActivityLevelB);
        textViewActivityLevelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { activityLevelSelected(1.375);
            }
        });

        TextView textViewActivityLevelC = findViewById(R.id.textViewActivityLevelC);
        textViewActivityLevelC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { activityLevelSelected(1.55);
            }
        });

        TextView textViewActivityLevelD = findViewById(R.id.textViewActivityLevelD);
        textViewActivityLevelD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { activityLevelSelected(1.725);
            }
        });

        TextView textViewActivityLevelE = findViewById(R.id.textViewActivityLevelE);
        textViewActivityLevelE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { activityLevelSelected(1.9);
            }
        });

    }

    /*- Button next clicked ----------------------------------------------------------------------- */
    private void activityLevelSelected(double doubleActivityLevelSelected) {

        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();


        // Calculate age
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDateMyUserDob = LocalDate.parse(myUserDob, formatter);
        LocalDate currentLocalDate = LocalDate.now();
        int age = calculateAge(localDateMyUserDob, currentLocalDate);


        /* BMR */
        double doubleBmrCalories;
        if(myUserGender.equals("male")){
            // BMR = 66.5 + (13.75 x kg body weight) + (5.003 x height in cm) - (6.755 x age)
            doubleBmrCalories = 66.5+(13.75*myGoalCurrentWeight)+(5.003*myUserHeight)-(6.755*age);
        }
        else{
            // BMR = 55.1 + (9.563 x kg body weight) + (1.850 x height in cm) - (4.676 x age)
            doubleBmrCalories = 655+(9.563*myGoalCurrentWeight)+(1.850*myUserHeight)-(4.676*age);
        }
        int intBmrCalories = (int)doubleBmrCalories;

        double doubleBmrFat = (doubleBmrCalories*13)/100;
        int intBmrFat = (int)doubleBmrFat;

        double doubleBmrCarbs = (doubleBmrCalories*44)/100;
        int intBmrCarbs = (int)doubleBmrCarbs;

        double doubleBmrProtein = (doubleBmrCalories*43)/100;
        int intBmrProtein = (int)doubleBmrProtein;


        /* Sedentary */
        double doubleSedentaryCalories = doubleBmrCalories*1.2;
        int intSendentaryCalories = (int)doubleSedentaryCalories;

        double doubleSedentaryFat = (doubleSedentaryCalories*13)/100;
        int intSedentaryFat = (int)doubleSedentaryFat;

        double doubleSedentaryCarbs = (doubleSedentaryCalories*44)/100;
        int intSedentaryCarbs = (int)doubleSedentaryCarbs;

        double doubleSedentaryProtein = (doubleSedentaryCalories*43)/100;
        int intSedentaryProtein = (int)doubleSedentaryProtein;


        /* My activity level */
        double doubleWithActivityCalories = doubleBmrCalories*doubleActivityLevelSelected;
        int intWithActivityCalories = (int)doubleWithActivityCalories;

        double doubleWithActivityFat = (doubleWithActivityCalories*13)/100;
        int intWithActivityFat = (int)doubleWithActivityFat;

        double doubleWithActivityCarbs = (doubleWithActivityCalories*44)/100;
        int intWithActivityCarbs = (int)doubleWithActivityCarbs;

        double doubleWithActivityProtein = (doubleWithActivityCalories*43)/100;
        int intWithActivityProtein = (int)doubleWithActivityProtein;




        // Update current weight
        String qUpdate = "UPDATE food_diary_goals SET " +
                "goal_activity_level=" + doubleActivityLevelSelected + "," +
                "goal_current_bmr_calories=" + intBmrCalories + ", " +
                "goal_current_bmr_fat=" + intBmrFat + ", " +
                "goal_current_bmr_carbs=" + intBmrFat + ", " +
                "goal_current_bmr_proteins=" + intBmrProtein + ", " +
                "goal_current_sedentary_calories=" + intSendentaryCalories + ", " +
                "goal_current_sedentary_fat=" + intSedentaryFat + ", " +
                "goal_current_sedentary_carbs=" + intSedentaryCarbs + ", " +
                "goal_current_sedentary_proteins=" + intSedentaryProtein + " " +
                "WHERE _id=" + myGoal_Id + "";
        db.rawQuery(qUpdate);

        qUpdate = "UPDATE food_diary_goals SET " +
                "goal_current_with_activity_calories=" + intWithActivityCalories + ", " +
                "goal_current_with_activity_fat=" + intWithActivityFat + ", " +
                "goal_current_with_activity_carbs=" + intWithActivityCarbs + ", " +
                "goal_current_with_activity_proteins=" + intWithActivityProtein + " " +
                "WHERE _id=" + myGoal_Id + "";
        db.rawQuery(qUpdate);


        /* Loose or gain weight? */
        double doubleTargetBmrCalories = 0.0;
        int intTargetBmrCalories = 0;
        int intTargetSedentaryCalories = 0;
        double doubleTargetSedentaryCalories = 0.0;
        int intTargetWithActivityCalories = 0;
        double doubleTargetWithActivityCalories = 0.0;
        if(myGoalIWantTo.equals("loose_weight")) {
            double doubleKcalPerWeek = 7700*0.25;
            double doubleKcalPerDay  = doubleKcalPerWeek/7;

            doubleTargetBmrCalories = doubleBmrCalories - doubleKcalPerDay;
            intTargetBmrCalories = (int)doubleTargetBmrCalories;

            doubleTargetSedentaryCalories = doubleSedentaryCalories - doubleKcalPerDay;
            intTargetSedentaryCalories = (int)doubleTargetSedentaryCalories;

            doubleTargetWithActivityCalories = doubleWithActivityCalories - doubleKcalPerDay;
            intTargetWithActivityCalories = (int)doubleTargetWithActivityCalories;

        }
        else if(myGoalIWantTo.equals("gain_weight")) {
            double doubleKcalPerWeek = 7700*0.25;
            double doubleKcalPerDay  = doubleKcalPerWeek/7;

            doubleTargetBmrCalories = doubleBmrCalories + doubleKcalPerDay;
            intTargetBmrCalories = (int)doubleTargetBmrCalories;

            doubleTargetSedentaryCalories = doubleSedentaryCalories + doubleKcalPerDay;
            intTargetSedentaryCalories = (int)doubleTargetSedentaryCalories;

            doubleTargetWithActivityCalories = doubleWithActivityCalories + doubleKcalPerDay;
            intTargetWithActivityCalories = (int)doubleTargetWithActivityCalories;

        }
        else{
            intTargetBmrCalories = 0;

        }

        double doubleTargetBmrFat = (doubleTargetBmrCalories*13)/100; // 13 % fat
        int intTargetBmrFat = (int)doubleTargetBmrFat;

        double doubleTargetBmrCarbs = (doubleTargetBmrCalories*44)/100; // 44 % carbs
        int intTargetBmrCarbs = (int)doubleTargetBmrCarbs;

        double doubleTargetBmrProtein = (doubleTargetBmrCalories*43)/100; // 43 % protein
        int intTargetBmrProtein = (int)doubleTargetBmrProtein;



        /* Loose or gain weight?: Sedentary */
        double doubleTargetSedentaryFat = (doubleTargetSedentaryCalories*13)/100; // 13 % fat
        int intTargetSedentaryFat = (int)doubleTargetSedentaryFat;

        double doubleTargetSedentaryCarbs = (doubleTargetSedentaryCalories*44)/100; // 44 % fat
        int intTargetSedentaryCarbs = (int)doubleTargetSedentaryCarbs;

        double doubleTargetSedentaryProtein = (doubleTargetSedentaryCalories*43)/100; // 43 % protein
        int intTargetSedentaryProtein = (int)doubleTargetSedentaryProtein;


        /* Loose or gain weight?:  My activity level */
        double doubleTargetWithActivityFat = (doubleTargetWithActivityCalories*13)/100; // 13 % fat
        int intTargetWithActivityFat = (int)doubleTargetWithActivityFat;

        double doubleTargetWithActivityCarbs = (doubleTargetWithActivityCalories*44)/100; // 44 % carbs
        int intTargetWithActivityCarbs = (int)doubleTargetWithActivityCarbs;

        double doubleTargetWithActivityProtein = (doubleTargetWithActivityCalories*43)/100; // 44 % protein
        int intTargetWithActivityProtein = (int)doubleTargetWithActivityProtein;


        // Update target weight
        qUpdate  = "UPDATE food_diary_goals SET " +
                "goal_target_bmr_calories=" + intTargetBmrCalories + ", " +
                "goal_target_bmr_fat=" + intTargetBmrFat + ", " +
                "goal_target_bmr_carbs=" + intTargetBmrCarbs + ", " +
                "goal_target_bmr_proteins=" + intTargetBmrProtein + ", " +
                "goal_target_sedentary_calories=" + intTargetSedentaryCalories + ", " +
                "goal_target_sedentary_fat=" + intTargetSedentaryFat + ", " +
                "goal_target_sedentary_carbs=" + intTargetSedentaryCarbs + ", " +
                "goal_target_sedentary_proteins=" + intTargetSedentaryProtein + " " +
                "WHERE _id=" + myGoal_Id + "";
        db.rawQuery(qUpdate);

        qUpdate  = "UPDATE food_diary_goals SET " +
                "goal_target_with_activity_calories=" + intTargetWithActivityCalories + ", " +
                "goal_target_with_activity_fat=" + intTargetWithActivityFat + ", " +
                "goal_target_with_activity_carbs=" + intTargetWithActivityCarbs + ", " +
                "goal_target_with_activity_proteins=" + intTargetWithActivityProtein + " " +
                "WHERE _id=" + myGoal_Id + "";
        db.rawQuery(qUpdate);


        // Create or update todays food_diary_totals_days
        DateFormat dfyyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
        String stringTodaysDate = dfyyyymmdd.format(Calendar.getInstance().getTime());

        String q = "SELECT _id, total_day_id, total_day_consumed_energy, total_day_consumed_fat, " +
                "total_day_consumed_carb, total_day_consumed_protein FROM food_diary_totals_days WHERE total_day_date='" + stringTodaysDate + "'";
        Cursor cursorToday = db.rawQuery(q);
        if(cursorToday.getCount() == 0){
            // We dont need to insert, because it will be created in MainActivity
        }
        else{
            int totalDay_Id = cursorToday.getInt(0);
            int totalDayId = cursorToday.getInt(1);
            int totalDayConsumedEnergy = cursorToday.getInt(2);
            int totalDayConsumedFat = cursorToday.getInt(3);
            int totalDayConsumedCarb = cursorToday.getInt(4);
            int totalDayConsumedProtein = cursorToday.getInt(5);

            // Calculate new values
            int diffSedentaryEnergy = intTargetSedentaryCalories-totalDayConsumedEnergy;
            int diffSedentaryFat = intTargetSedentaryFat-totalDayConsumedFat;
            int diffSedentaryCarb = intTargetSedentaryCarbs-totalDayConsumedCarb;
            int diffSedentaryProtein = intTargetSedentaryProtein-totalDayConsumedProtein;

            int diffWithActivityEnergy = intTargetWithActivityCalories-totalDayConsumedEnergy;
            int diffWithActivityFat = intTargetWithActivityFat-totalDayConsumedFat;
            int diffWithActivityCarb = intTargetWithActivityCarbs-totalDayConsumedCarb;
            int diffWithActivityProtein = intTargetWithActivityProtein-totalDayConsumedProtein;

            // Update today
            String update = "UPDATE food_diary_totals_days SET " +
                    "total_day_target_sedentary_energy=" + intTargetSedentaryCalories + ", " +
                    "total_day_target_sedentary_fat=" + intTargetSedentaryFat + ", " +
                    "total_day_target_sedentary_carb=" + intTargetSedentaryCarbs + ", " +
                    "total_day_target_sedentary_protein=" + intTargetSedentaryProtein + ", " +

                    "total_day_target_with_activity_energy=" + intTargetWithActivityCalories + ", " +
                    "total_day_target_with_activity_fat=" + intTargetWithActivityFat + ", " +
                    "total_day_target_with_activity_carb=" + intTargetWithActivityCarbs + ", " +
                    "total_day_target_with_activity_protein=" + intTargetWithActivityProtein + ", " +

                    "total_day_diff_sedentary_energy=" + diffSedentaryEnergy + ", " +
                    "total_day_diff_sedentary_fat=" + diffSedentaryFat + ", " +
                    "total_day_diff_sedentary_carb=" + diffSedentaryCarb + ", " +
                    "total_day_diff_sedentary_protein=" + diffSedentaryProtein + ", " +
                    "total_day_diff_with_activity_energy=" + diffWithActivityEnergy + ", " +
                    "total_day_diff_with_activity_fat=" + diffWithActivityFat + ", " +
                    "total_day_diff_with_activity_carb=" + diffWithActivityCarb + ", " +
                    "total_day_diff_with_activity_protein=" + diffWithActivityProtein + " " +
                    "WHERE total_day_date='" + stringTodaysDate + "'";
            db.rawQuery(update);
            // Todo: send update to server
        }



        // Send to PHP
        // We will get the ID of the food diary goal back
        String url    = apiFooDiaryURL + "/post_user_update_food_diary_goal_activity_level.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_id", String.valueOf(myUserId));
        data.put("inp_user_password", myUserPassword);
        data.put("inp_goal_id", String.valueOf(myGoalId));

        data.put("inp_goal_activity_level", String.valueOf(doubleActivityLevelSelected));
        data.put("inp_goal_current_bmr_calories", String.valueOf(intBmrCalories));
        data.put("inp_goal_current_bmr_fat", String.valueOf(intBmrFat));
        data.put("inp_goal_current_bmr_carbs", String.valueOf(intBmrCarbs));
        data.put("inp_goal_current_bmr_proteins", String.valueOf(intBmrProtein));

        data.put("inp_goal_current_sedentary_calories", String.valueOf(intSendentaryCalories));
        data.put("inp_goal_current_sedentary_fat", String.valueOf(intSedentaryFat));
        data.put("inp_goal_current_sedentary_carbs", String.valueOf(intSedentaryCarbs));
        data.put("inp_goal_current_sedentary_proteins", String.valueOf(intSedentaryProtein));

        data.put("inp_goal_current_with_activity_calories", String.valueOf(intWithActivityCalories));
        data.put("inp_goal_current_with_activity_fat", String.valueOf(intWithActivityFat));
        data.put("inp_goal_current_with_activity_carbs", String.valueOf(intWithActivityCarbs));
        data.put("inp_goal_current_with_activity_proteins", String.valueOf(intWithActivityProtein));

        data.put("inp_goal_target_bmr_calories", String.valueOf(intTargetBmrCalories));
        data.put("inp_goal_target_bmr_fat", String.valueOf(intTargetBmrFat));
        data.put("inp_goal_target_bmr_carbs", String.valueOf(intTargetBmrCarbs));
        data.put("inp_goal_target_bmr_proteins", String.valueOf(intTargetBmrProtein));

        data.put("inp_goal_target_sedentary_calories", String.valueOf(intTargetSedentaryCalories));
        data.put("inp_goal_target_sedentary_fat", String.valueOf(intTargetSedentaryFat));
        data.put("inp_goal_target_sedentary_carbs", String.valueOf(intTargetSedentaryCarbs));
        data.put("inp_goal_target_sedentary_proteins", String.valueOf(intTargetSedentaryProtein));

        data.put("inp_goal_target_with_activity_calories", String.valueOf(intTargetWithActivityCalories));
        data.put("inp_goal_target_with_activity_fat", String.valueOf(intTargetWithActivityFat));
        data.put("inp_goal_target_with_activity_carbs", String.valueOf(intTargetWithActivityCarbs));
        data.put("inp_goal_target_with_activity_proteins", String.valueOf(intTargetWithActivityProtein));
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, url, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                buttonNextClickedAnswer();
            }
        });
        task.execute();

        // Close db
        db.close();

        // Move to next
        Intent i = new Intent(FoodDiaryGoalNewGActivityLevelActivity.this, FoodDiaryGoalNewHResultsActivity.class);
        startActivity(i);
        finish();

    } // buttonNextClicked


    public void buttonNextClickedAnswer(){
        // Dynamic Text
        DBAdapter db = new DBAdapter(this);
        db.open();
        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        Toast.makeText(this, dataResult + " for Goal Id " + myGoalId, Toast.LENGTH_SHORT).show();

        // Close db
        db.close();

    } // buttonNextClickedAnswer



    public int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
}