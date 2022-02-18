package net.frindex.caloriescounterandroidjava.dao;
/**
 *
 * File: DBAdapater.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {

    /* 01 Variables ---------------------------------------- */
    private static final String databaseName = "calories_counter";
    private static final int databaseVersion = 74;

    /* 02 Database variables ------------------------------- */
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    /* 03 Class DbAdapter ---------------------------------- */
    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    /* 04 DatabaseHelper ------------------------------------ */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, databaseName, null, databaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db){

            /* Synchronize */
            db.execSQL("CREATE TABLE IF NOT EXISTS synchronize (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name INTEGER, " +
                    "last_on_local INTEGER, " +
                    "last_on_server VARCHAR, " +
                    "synchronized_year INTEGER, " +
                    "synchronized_month INTEGER, " +
                    "synchronized_day INTEGER, " +
                    "synchronized_week INTEGER, " +
                    "synchronized_hour INTEGER, " +
                    "synchronized_minute INTEGER, " +
                    "synchronized_second INTEGER, " +
                    "synchronized_date DATE, " +
                    "synchronized_datetime DATETIME, " +
                    "note VARCHAR);");

            db.execSQL("CREATE TABLE IF NOT EXISTS app_statistics (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "app_started_date DATE, " +
                    "app_last_used_date DATE, " +
                    "app_years_used INT, " +
                    "app_months_used INT, " +
                    "app_days_used INT);");

            // Create table json_temp_data
            db.execSQL("CREATE TABLE IF NOT EXISTS json_temp_data (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " day VARCHAR, " +
                    " name VARCHAR, " +
                    " data TEXT);");

            // Create table app error log
            db.execSQL("CREATE TABLE IF NOT EXISTS app_error_log (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " datetime VARCHAR, " +
                    " class VARCHAR, " +
                    " method VARCHAR, " +
                    " type VARCHAR, " +
                    " error TEXT);");



            /* Users start */
            // Create table users
            db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " user_id INTEGER, " +
                    " user_email VARCHAR," +
                    " user_name VARCHAR," +
                    " user_alias VARCHAR," +
                    " user_password VARCHAR, " +
                    " user_password_replacement VARCHAR, " +
                    " user_password_date DATE, " +
                    " user_salt VARCHAR, " +
                    " user_security INT, " +
                    " user_language VARHCAR," +
                    " user_gender VARHCAR, " +
                    " user_height INT, " +
                    " user_measurement VARHCAR, " +
                    " user_dob DATE, " +
                    " user_date_format VARHCAR, " +
                    " user_registered DATETIME," +
                    " user_registered_time TIME," +
                    " user_last_online DATETIME," +
                    " user_last_online_time TIME," +
                    " user_rank VARHCAR, " +
                    " user_points INT, " +
                    " user_points_rank VARHCAR, " +
                    " user_likes INT, " +
                    " user_dislikes INT, " +
                    " user_status VARHCAR, " +
                    " user_login_tries VARHCAR, " +
                    " user_last_ip VARHCAR, " +
                    " user_synchronized DATE, "+
                    " user_verified_by_moderator VARCHAR, "+
                    " user_notes VARCHAR, "+
                    " user_marked_as_spammer VARCHAR);");

            // Create table users_profile
            db.execSQL("CREATE TABLE IF NOT EXISTS users_profile (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " profile_id INTEGER, " +
                    " profile_user_id INTEGER," +
                    " profile_first_name VARCHAR," +
                    " profile_middle_name VARCHAR," +
                    " profile_last_name VARCHAR, " +
                    " profile_address_line_a VARCHAR, " +
                    " profile_address_line_b VARCHAR, " +
                    " profile_zip VARCHAR, " +
                    " profile_city VARHCAR," +
                    " profile_country VARHCAR, " +
                    " profile_phone VARHCAR, " +
                    " profile_work VARHCAR, " +
                    " profile_university VARHCAR, " +
                    " profile_high_school VARHCAR, " +
                    " profile_languages VARHCAR, " +
                    " profile_website VARHCAR, " +
                    " profile_interested_in VARHCAR, " +
                    " profile_relationship VARHCAR, " +
                    " profile_about VARHCAR, " +
                    " profile_newsletter VARHCAR, " +
                    " profile_views INTEGER, " +
                    " profile_views_ip_block VARHCAR, " +
                    " profile_privacy VARCHAR);");

            // Create table users_profile_photo
            db.execSQL("CREATE TABLE IF NOT EXISTS users_profile_photo (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " photo_id INTEGER, " +
                    " photo_user_id INTEGER," +
                    " photo_profile_image INTEGER," +
                    " photo_destination VARCHAR," +
                    " photo_uploaded DATETIME, " +
                    " photo_uploaded_ip VARCHAR, " +
                    " photo_views INTEGER, " +
                    " photo_views_ip_block VARCHAR, " +
                    " photo_likes INTEGER," +
                    " photo_comments INTEGER, " +
                    " photo_x_offset INTEGER, " +
                    " photo_y_offset INTEGER, " +
                    " photo_text VARCHAR);");

            // Create table users_friends
            db.execSQL("CREATE TABLE IF NOT EXISTS users_friends (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "friend_id INT, " +
                    "friend_user_id_a INT, " +
                    "friend_user_id_b INT, " +
                    "friend_user_alias_a VARCHAR, " +
                    "friend_user_alias_b VARCHAR, " +
                    "friend_user_image_a VARCHAR, " +
                    "friend_user_image_b VARCHAR, " +
                    "friend_text_a TEXT, " +
                    "friend_text_b TEXT, " +
                    "friend_datetime DATETIME);");

            // Create table users_status
            db.execSQL("CREATE TABLE IF NOT EXISTS users_status (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "status_id INT, " +
                    "status_user_id INT, " +
                    "status_created_by_user_id INT, " +
                    "status_created_by_user_alias VARCHAR(200), " +
                    "status_created_by_user_image VARCHAR(200), " +
                    "status_created_by_ip VARCHAR(200), " +
                    "status_text TEXT, " +
                    "status_photo VARCHAR(70), " +
                    "status_datetime DATETIME, " +
                    "status_datetime_print VARCHAR(200), " +
                    "status_time VARCHAR(200), " +
                    "status_language VARCHAR(6), " +
                    "status_likes INT, " +
                    "status_comments INT, " +
                    "status_reported INT, " +
                    "status_reported_checked INT, " +
                    "status_reported_reason TEXT, " +
                    "status_seen INT);");

            // Create table users_status_likes
            db.execSQL("CREATE TABLE IF NOT EXISTS users_status_likes (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "like_id INT, " +
                    "like_status_id INT, " +
                    "like_user_id INT, " +
                    "like_user_alias VARCHAR);");


            // Create table comments
            db.execSQL("CREATE TABLE IF NOT EXISTS comments (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " comment_id INT, " +
                    " comment_user_id INT, " +
                    " comment_language VARCHAR, " +
                    " comment_object VARCHAR, " +
                    " comment_object_id INT, " +
                    " comment_parent_id INT, " +
                    " comment_user_ip VARCHAR, " +
                    " comment_user_name VARCHAR, " +
                    " comment_user_avatar VARCHAR, " +
                    " comment_user_email VARCHAR, " +
                    " comment_user_subscribe INT, " +
                    " comment_created DATETIME, " +
                    " comment_updated DATETIME, " +
                    " comment_text TEXT, " +
                    " comment_likes INT, " +
                    " comment_dislikes INT, " +
                    " comment_reported INT, " +
                    " comment_reported_by_user_id INT, " +
                    " comment_reported_reason VARCHAR, " +
                    " comment_report_checked VARCHAR, " +
                    " comment_seen INT, " +
                    " comment_approved INT);");



            /* Start Food */
            // Create table food_categories
            db.execSQL("CREATE TABLE IF NOT EXISTS food_categories (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_id INT, " +
                    "category_user_id VARCHAR, " +
                    "category_name VARCHAR, " +
                    "category_age_restriction INT, " +
                    "category_parent_id INT, " +
                    "category_icon INT, " +
                    "category_last_updated DATETIME, " +
                    "exercise_updated_datetime DATETIME, " +
                    "category_note VARCHAR);");

            // Create table food_categories_translations
            db.execSQL("CREATE TABLE IF NOT EXISTS food_categories_translations (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_translation_id INT,  " +
                    "category_id INT,  " +
                    "category_translation_language VARCHAR,  " +
                    "category_translation_value VARCHAR, " +
                    "category_translation_no_food INT,  " +
                    "category_translation_last_updated DATETIME, " +
                    "category_calories_min DOUBLE, " +
                    "category_calories_med DOUBLE, " +
                    "category_calories_max DOUBLE, " +
                    "category_fat_min DOUBLE, " +
                    "category_fat_med DOUBLE, " +
                    "category_fat_max DOUBLE, " +
                    "category_fat_of_which_saturated_fatty_acids_min DOUBLE, " +
                    "category_fat_of_which_saturated_fatty_acids_med DOUBLE, " +
                    "category_fat_of_which_saturated_fatty_acids_max DOUBLE, " +
                    "category_carb_min DOUBLE, " +
                    "category_carb_med DOUBLE, " +
                    "category_carb_max DOUBLE, " +
                    "category_carb_of_which_dietary_fiber_min DOUBLE, " +
                    "category_carb_of_which_dietary_fiber_med DOUBLE, " +
                    "category_carb_of_which_dietary_fiber_max DOUBLE, " +
                    "category_carb_of_which_sugars_min DOUBLE, " +
                    "category_carb_of_which_sugars_med DOUBLE, " +
                    "category_carb_of_which_sugars_max DOUBLE, " +
                    "category_proteins_min DOUBLE, " +
                    "category_proteins_med DOUBLE, " +
                    "category_proteins_max DOUBLE, " +
                    "category_salt_min DOUBLE, " +
                    "category_salt_med DOUBLE, " +
                    "category_salt_max DOUBLE);");

            // Create table food_index
            db.execSQL("CREATE TABLE IF NOT EXISTS food_index (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "food_id INT, " +
                    "food_user_id INT, " +
                    "food_name VARCHAR, " +
                    "food_clean_name VARCHAR, " +
                    "food_manufacturer_name VARCHAR, " +
                    "food_manufacturer_name_and_food_name VARCHAR, " +
                    "food_description VARCHAR, " +
                    "food_country VARCHAR, " +
                    "food_net_content DOUBLE, " +
                    "food_net_content_measurement VARCHAR, " +
                    "food_serving_size_gram DOUBLE, " +
                    "food_serving_size_gram_measurement VARCHAR, " +
                    "food_serving_size_pcs DOUBLE, " +
                    "food_serving_size_pcs_measurement VARCHAR, " +
                    "food_energy DOUBLE, " +
                    "food_fat DOUBLE, " +
                    "food_fat_of_which_saturated_fatty_acids DOUBLE, " +
                    "food_carbohydrates DOUBLE, " +
                    "food_carbohydrates_of_which_dietary_fiber DOUBLE, " +
                    "food_carbohydrates_of_which_sugars DOUBLE, " +
                    "food_proteins DOUBLE, " +
                    "food_salt DOUBLE, " +
                    "food_score INT, " +
                    "food_energy_calculated DOUBLE, " +
                    "food_fat_calculated DOUBLE, " +
                    "food_fat_of_which_saturated_fatty_acids_calculated DOUBLE, " +
                    "food_carbohydrates_calculated DOUBLE, " +
                    "food_carbohydrates_of_which_dietary_fiber_calculated DOUBLE, " +
                    "food_carbohydrates_of_which_sugars_calculated DOUBLE, " +
                    "food_proteins_calculated DOUBLE, " +
                    "food_salt_calculated DOUBLE, " +
                    "food_barcode VARCHAR, " +
                    "food_category_id INT, " +
                    "food_image_path VARCHAR, " +
                    "food_thumb_small VARCHAR, " +
                    "food_thumb_medium VARCHAR, " +
                    "food_thumb_large VARCHAR, " +
                    "food_image_a VARCHAR, " +
                    "food_image_b VARCHAR, " +
                    "food_image_c VARCHAR, " +
                    "food_image_d VARCHAR, " +
                    "food_image_e VARCHAR, " +
                    "food_last_used DATE, " +
                    "food_language VARCHAR, " +
                    "food_synchronized DATE, " +
                    "food_accepted_as_master INT, " +
                    "food_notes TEXT, " +
                    "food_unique_hits INT, " +
                    "food_unique_hits_ip_block TEXT, " +
                    "food_comments INT, " +
                    "food_likes INT, " +
                    "food_dislikes INT, " +
                    "food_likes_ip_block TEXT, " +
                    "food_user_ip VARCHAR, " +
                    "food_date DATE, " +
                    "food_time TIME, " +
                    "food_last_viewed DATE);");

            // Create table food_prices
            db.execSQL("CREATE TABLE IF NOT EXISTS food_index_prices (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "food_price_id INT, " +
                    "food_price_food_id INT,  " +
                    "food_price_store_id INT, " +
                    "food_price_store_name VARCHAR,  " +
                    "food_price_price DOUBLE, " +
                    "food_price_currency VARCHAR,  " +
                    "food_price_offer INT, " +
                    "food_price_offer_valid_from DATETIME, " +
                    "food_price_offer_valid_to DATETIME, " +
                    "food_price_user_id INT,  " +
                    "food_price_user_ip VARCHAR, " +
                    "food_price_added_datetime DATETIME, " +
                    "food_price_added_datetime_print VARCHAR, " +
                    "food_price_updated DATETIME,  " +
                    "food_price_updated_print VARCHAR, " +
                    "food_price_reported INT, " +
                    "food_price_reported_checked INT);");

            // Create table food_index_stores
            db.execSQL("CREATE TABLE IF NOT EXISTS food_index_stores (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "food_store_id INT, " +
                    "food_store_food_id INT, " +
                    "food_store_store_id INT," +
                    "food_store_store_name VARCHAR, " +
                    "food_store_store_logo VARCHAR, " +
                    "food_store_store_price DOUBLE," +
                    "food_store_store_currency VARCHAR, " +
                    "food_store_user_id INT," +
                    "food_store_user_ip VARCHAR, " +
                    "food_store_updated DATETIME);");

            // Create table food_ads
            db.execSQL("CREATE TABLE IF NOT EXISTS food_index_ads (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ad_id INT, " +
                    "ad_food_language VARCHAR, " +
                    "ad_food_id INT, " +
                    "ad_text TEXT, " +
                    "ad_url VARCHAR, " +
                    "ad_food_created_datetime DATETIME, " +
                    "ad_food_created_by_user_id INT, " +
                    "ad_food_updated_datetime DATETIME, " +
                    "ad_food_updated_by_user_id INT, " +
                    "ad_food_unique_clicks INT, " +
                    "ad_food_unique_clicks_ip_block TEXT);");



            // Create table food_tags
            db.execSQL("CREATE TABLE IF NOT EXISTS food_index_tags (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tag_id INT, " +
                    "tag_language VARCHAR, " +
                    "tag_food_id INT, " +
                    "tag_title VARCHAR, " +
                    "tag_title_clean VARCHAR, " +
                    "tag_user_id INT);");


            // Create table food_stores
            db.execSQL("CREATE TABLE IF NOT EXISTS food_stores (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "store_id INT, " +
                    "store_user_id INT,  " +
                    "store_name VARCHAR,  " +
                    "store_country VARCHAR,  " +
                    "store_language VARCHAR, " +
                    "store_website VARCHAR,  " +
                    "store_logo VARCHAR,  " +
                    "store_added_datetime DATETIME,  " +
                    "store_added_datetime_print VARCHAR,  " +
                    "store_updatet_datetime DATETIME,  " +
                    "store_updatet_datetime_print VARCHAR, " +
                    "store_user_ip VARCHAR, " +
                    "store_reported VARCHAR, " +
                    "store_reported_checked VARCHAR);");


            // Create table food_prices_currencies
            db.execSQL("CREATE TABLE IF NOT EXISTS food_prices_currencies (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "currency_id INT, " +
                    "currency_name VARCHAR, " +
                    "currency_code VARCHAR, " +
                    "currency_symbol VARCHAR, " +
                    "currency_country_id VARCHAR, " +
                    "currency_country_name VARCHAR, " +
                    "currency_last_used_language VARCHAR);");

            // Create table food_measurements
            db.execSQL("CREATE TABLE IF NOT EXISTS food_measurements (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "measurement_id INT, " +
                    "measurement_name VARCHAR, " +
                    "measurement_last_updated DATETIME);");



            // Create table food_measurements_translations
            db.execSQL("CREATE TABLE IF NOT EXISTS food_measurements_translations (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "measurement_translation_id INT, " +
                    "measurement_id INT, " +
                    "measurement_translation_language VARCHAR,  " +
                    "measurement_translation_value VARCHAR,  " +
                    "measurement_translation_last_updated DATETIME);");

            // Create table food_favorites
            db.execSQL("CREATE TABLE IF NOT EXISTS food_favorites (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "food_favorite_id INT, " +
                    "food_favorite_food_id INT,  " +
                    "food_favorite_user_id INT, " +
                    "food_favorite_comment VARCHAR);");

            /* Start food diary */
            // Create table food_diary_goals
            db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_goals (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "goal_id INT, " +
                    "goal_user_id INT, " +
                    "goal_current_weight INT, " +
                    "goal_current_fat_percentage INT, " +
                    "goal_target_weight INT, " +
                    "goal_target_fat_percentage INT, " +
                    "goal_i_want_to VARCHAR(50), " +
                    "goal_weekly_goal VARCHAR(50), " +
                    "goal_date DATE, " +
                    "goal_activity_level DOUBLE, " +
                    "goal_current_bmi INT, " +
                    "goal_target_bmi INT, " +
                    "goal_current_bmr_calories INT, " +
                    "goal_current_bmr_fat INT, " +
                    "goal_current_bmr_carbs INT, " +
                    "goal_current_bmr_proteins INT, " +
                    "goal_current_sedentary_calories INT, " +
                    "goal_current_sedentary_fat INT, " +
                    "goal_current_sedentary_carbs INT, " +
                    "goal_current_sedentary_proteins INT, " +
                    "goal_current_with_activity_calories INT, " +
                    "goal_current_with_activity_fat INT, " +
                    "goal_current_with_activity_carbs INT, " +
                    "goal_current_with_activity_proteins INT, " +
                    "goal_target_bmr_calories INT, " +
                    "goal_target_bmr_fat INT, " +
                    "goal_target_bmr_carbs INT, " +
                    "goal_target_bmr_proteins INT, " +
                    "goal_target_sedentary_calories INT, " +
                    "goal_target_sedentary_fat INT, " +
                    "goal_target_sedentary_carbs INT, " +
                    "goal_target_sedentary_proteins INT, " +
                    "goal_target_with_activity_calories INT, " +
                    "goal_target_with_activity_fat INT, " +
                    "goal_target_with_activity_carbs INT, " +
                    "goal_target_with_activity_proteins INT, " +
                    "goal_updated DATETIME, " +
                    "goal_synchronized VARCHAR(50), " +
                    "goal_notes VARCHAR);");




            // Create table food_diary_entries
            db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_entires (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "entry_id INT, " +
                    "entry_user_id INT, " +
                    "entry_date DATE, " +
                    "entry_meal_id INT, " +
                    "entry_food_id INT, " +
                    "entry_recipe_id INT, " +
                    "entry_name VARCHAR(250), " +
                    "entry_manufacturer_name VARCHAR(250), " +
                    "entry_serving_size DOUBLE, " +
                    "entry_serving_size_measurement VARCHAR(250), " +
                    "entry_energy_per_entry INT, " +
                    "entry_fat_per_entry INT, " +
                    "entry_carb_per_entry INT, " +
                    "entry_protein_per_entry INT, " +
                    "entry_text TEXT, " +
                    "entry_deleted INT, " +
                    "entry_updated DATETIME, " +
                    "entry_synchronized VARCHAR(50));");


            // Create table food_diary_totals_meals
            db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_totals_meals (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "total_meal_id INT, " +
                    "total_meal_user_id INT, " +
                    "total_meal_date DATE, " +
                    "total_meal_meal_id INT, " +
                    "total_meal_energy INT, " +
                    "total_meal_fat INT, " +
                    "total_meal_carb INT, " +
                    "total_meal_protein INT, " +
                    "total_meal_updated DATETIME, " +
                    "total_meal_synchronized VARCHAR(50));");


            // Create table food_diary_totals_days
            db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_totals_days (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "total_day_id INT, " +
                    "total_day_user_id INT, " +
                    "total_day_date DATE, " +
                    "total_day_consumed_energy INT, " +
                    "total_day_consumed_fat INT, " +
                    "total_day_consumed_carb INT, " +
                    "total_day_consumed_protein INT, " +
                    "total_day_target_sedentary_energy INT, " +
                    "total_day_target_sedentary_fat INT, " +
                    "total_day_target_sedentary_carb INT, " +
                    "total_day_target_sedentary_protein INT, " +
                    "total_day_target_with_activity_energy INT, " +
                    "total_day_target_with_activity_fat INT, " +
                    "total_day_target_with_activity_carb INT, " +
                    "total_day_target_with_activity_protein INT, " +
                    "total_day_diff_sedentary_energy INT, " +
                    "total_day_diff_sedentary_fat INT, " +
                    "total_day_diff_sedentary_carb INT, " +
                    "total_day_diff_sedentary_protein INT, " +
                    "total_day_diff_with_activity_energy INT, " +
                    "total_day_diff_with_activity_fat INT, " +
                    "total_day_diff_with_activity_carb INT, " +
                    "total_day_diff_with_activity_protein INT, " +
                    "total_day_updated DATETIME, " +
                    "total_day_synchronized VARCHAR(50));");

            // Create table food_diary_last_used
            db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_last_used (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "last_used_id INT, " +
                    "last_used_user_id INT, " +
                    "last_used_day_of_week INT, " +
                    "last_used_meal_id INT, " +
                    "last_used_food_id INT, " +
                    "last_used_recipe_id INT, " +
                    "last_used_serving_size DOUBLE, " +
                    "last_used_times INT, " +
                    "last_used_date DATE, " +
                    "last_used_updated DATETIME, " +
                    "last_used_synchronized VARCHAR(50), " +
                    "last_used_name VARCHAR(200), " +
                    "last_used_manufacturer VARCHAR(200), " +
                    "last_used_image_path VARCHAR(200), " +
                    "last_used_image_thumb VARCHAR(50), " +

                    "last_used_net_content DOUBLE, " +
                    "last_used_net_content_measurement VARCHAR(50), " +
                    "last_used_serving_size_gram DOUBLE, " +
                    "last_used_serving_size_gram_measurement VARCHAR(50), " +
                    "last_used_serving_size_pcs DOUBLE, " +
                    "last_used_serving_size_pcs_measurement VARCHAR(50), " +

                    "last_used_calories_per_hundred INT, " +
                    "last_used_fat_per_hundred INT, " +
                    "last_used_saturated_fatty_acids_per_hundred INT, " +
                    "last_used_carbs_per_hundred INT, " +
                    "last_used_sugar_per_hundred INT, " +
                    "last_used_proteins_per_hundred  INT, " +
                    "last_used_salt_per_hundred INT, " +

                    "last_used_calories_per_serving INT, " +
                    "last_used_fat_per_serving INT, " +
                    "last_used_saturated_fatty_acids_per_serving INT, " +
                    "last_used_carbs_per_serving INT, " +
                    "last_used_sugar_per_serving INT, " +
                    "last_used_proteins_per_serving  INT, " +
                    "last_used_salt_per_serving INT);");

            /* Start recipes */

            // Create categories
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_categories (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " category_id INT, " +
                    " category_name VARCHAR, " +
                    " category_age_restriction INT, " +
                    " category_image_path VARCHAR, " +
                    " category_image VARCHAR, " +
                    " category_image_updated_week INT, " +
                    " category_last_updated DATETIME);");

            // Create categories_translations
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_categories_translations (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " category_translation_id INT, " +
                    " category_id INT, " +
                    " category_translation_language VARCHAR, " +
                    " category_translation_value VARCHAR, " +
                    " category_translation_no_recipes INT, " +
                    " category_translation_last_updated DATETIME);");


            // Create category for favorite recipes
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_my_favorites_categories (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " category_title VARCHAR, " +
                    " user_id INT);");


            // Create favorite recipes
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_my_favorites (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " recipe_id INT, " +
                    " category_id INT, " +
                    " user_id INT);");

            // Create table recipes
            db.execSQL("CREATE TABLE IF NOT EXISTS recipe_vote_limit_tabel (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " recipe_id INT, " +
                    " user_id INT);");

            // Create table recipes
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " recipe_id INT, " +
                    " recipe_user_id INT, " +
                    " recipe_title VARCHAR, " +
                    " recipe_category_id INT, " +
                    " recipe_language VARCHAR, " +
                    " recipe_country VARCHAR, " +
                    " recipe_introduction TEXT, " +
                    " recipe_directions TEXT, " +
                    " recipe_image_path VARCHAR, " +
                    " recipe_image VARCHAR, " +
                    " recipe_thumb VARCHAR, " +
                    " recipe_video VARCHAR, " +
                    " recipe_date DATE, " +
                    " recipe_time TIME, " +
                    " recipe_cusine_id INT, " +
                    " recipe_season_id INT, " +
                    " recipe_occasion_id INT, " +
                    " recipe_marked_as_spam VARCHAR, " +
                    " recipe_unique_hits INT, " +
                    " recipe_unique_hits_ip_block VARCHAR, " +
                    " recipe_comments INT, " +
                    " recipe_user_ip VARCHAR, " +
                    " recipe_password VARCHAR, " +
                    " recipe_last_viewed DATETIME);");

            // Create table recipes_groups
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_groups (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " group_id INT, " +
                    " group_recipe_id INT, " +
                    " group_title VARHCAR);");

            // Create table recipes_items
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_items (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " item_id INT, " +
                    " item_recipe_id INT, " +
                    " item_group_id INT, " +
                    " item_amount DOUBLE, " +
                    " item_measurement VARCHAR(50), " +
                    " item_grocery VARCHAR(250), " +
                    " item_grocery_explanation VARCHAR(250), " +
                    " item_food_id INT, " +
                    " item_calories_per_hundred INT, " +
                    " item_fat_per_hundred INT, " +
                    " item_fat_of_which_saturated_fatty_acids_per_hundred INT, " +
                    " item_carbs_per_hundred INT, " +
                    " item_carbs_of_which_dietary_fiber_hundred INT, " +
                    " item_carbs_of_which_sugars_per_hundred INT, " +
                    " item_proteins_per_hundred INT, " +
                    " item_salt_per_hundred INT, " +
                    " item_calories_calculated INT, " +
                    " item_fat_calculated INT, " +
                    " item_fat_of_which_saturated_fatty_acids_calculated INT, " +
                    " item_carbs_calculated INT, " +
                    " item_carbs_of_which_dietary_fiber_calculated INT, " +
                    " item_carbs_of_which_sugars_calculated INT, " +
                    " item_proteins_calculated INT, " +
                    " item_salt_calculated INT);");


            // Create table numbers
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_numbers (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " number_id INT, " +

                    " number_recipe_id INT, " +
                    " number_hundred_calories INT, " +
                    " number_hundred_proteins INT, " +
                    " number_hundred_fat INT, " +
                    " number_hundred_fat_of_which_saturated_fatty_acids INT, " +
                    " number_hundred_carbs INT, " +
                    " number_hundred_carbs_of_which_dietary_fiber INT, " +
                    " number_hundred_carbs_of_which_sugars INT, " +

                    " number_serving_calories INT, " +
                    " number_serving_proteins INT, " +
                    " number_serving_fat INT, " +
                    " number_serving_fat_of_which_saturated_fatty_acids INT, " +

                    " number_serving_carbs INT, " +
                    " number_serving_carbs_of_which_dietary_fiber INT, " +
                    " number_serving_carbs_of_which_sugars INT, " +
                    " number_serving_salt INT, " +

                    " number_total_weight INT, " +
                    " number_total_calories INT, " +
                    " number_total_proteins INT, " +
                    " number_total_fat INT, " +
                    " number_total_fat_of_which_saturated_fatty_acids INT, " +
                    " number_total_carbs INT, " +
                    " number_total_carbs_of_which_dietary_fiber INT, " +
                    " number_total_carbs_of_which_sugars INT, " +
                    " number_total_salt INT, " +

                    " number_servings INT);");


            // Create table recipes_rating
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_rating (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " rating_recipe_id INT, " +
                    " rating_1 INT, " +
                    " rating_2 INT, " +
                    " rating_3 INT, " +
                    " rating_4 INT, " +
                    " rating_5 INT, " +
                    " rating_total_votes INT, " +
                    " rating_average INT, " +
                    " rating_popularity INT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_links (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " link_id INT, " +
                    " link_language VARCHAR, " +
                    " link_recipe_id INT, " +
                    " link_title VARCHAR, " +
                    " link_url VARCHAR, " +
                    " link_unique_click VARCHAR, " +
                    " link_unique_click_ipblock VARCHAR, " +
                    " link_user_id INT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_tags (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " tag_id INT, " +
                    " tag_language VARCHAR, " +
                    " tag_recipe_id INT, " +
                    " tag_title VARCHAR, " +
                    " tag_title_clean VARCHAR, " +
                    " tag_user_id INT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_tags_unique (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " tag_language VARCHAR, " +
                    " tag_title VARCHAR, " +
                    " tag_title_clean VARCHAR, " +
                    " tag_number_of_recipes INT);");

            // recipe comments
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes_comments (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " comment_id INT, " +
                    " comment_recipe_id INT, " +
                    " comment_language VARCHAR(20), " +
                    " comment_approved INT, " +
                    " comment_datetime DATETIME, " +
                    " comment_time VARCHAR(200), " +
                    " comment_date_print VARCHAR(200), " +
                    " comment_user_id INT, " +
                    " comment_user_alias VARCHAR(250), " +
                    " comment_user_image_path VARCHAR(250), " +
                    " comment_user_image_file VARCHAR(250), " +
                    " comment_user_ip VARCHAR(250), " +
                    " comment_user_hostname VARCHAR(250), " +
                    " comment_user_agent VARCHAR(250), " +
                    " comment_title VARCHAR(250), " +
                    " comment_text TEXT, " +
                    " comment_rating INT,  " +
                    " comment_helpful_clicks INT, " +
                    " comment_useless_clicks INT, " +
                    " comment_marked_as_spam INT, " +
                    " comment_spam_checked INT, " +
                    " comment_spam_checked_comment TEXT);");


            /*- Start meal plans ---------------------------------------------------------------- */
            db.execSQL("CREATE TABLE IF NOT EXISTS meal_plans (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "meal_plan_id  INT, " +
                    "meal_plan_user_id INT, " +
                    "meal_plan_language VARCHAR(50), " +
                    "meal_plan_title VARCHAR(250), " +
                    "meal_plan_title_clean VARCHAR(250), " +
                    "meal_plan_number_of_days INT, " +
                    "meal_plan_introduction TEXT, " +

                    "meal_plan_total_energy_without_training INT, " +
                    "meal_plan_total_fat_without_training INT, " +
                    "meal_plan_total_carb_without_training INT, " +
                    "meal_plan_total_protein_without_training INT, " +

                    "meal_plan_total_energy_with_training INT, " +
                    "meal_plan_total_fat_with_training INT, " +
                    "meal_plan_total_carb_with_training INT, " +
                    "meal_plan_total_protein_with_training INT, " +

                    "meal_plan_average_kcal_without_training INT, " +
                    "meal_plan_average_fat_without_training INT, " +
                    "meal_plan_average_carb_without_training INT, " +
                    "meal_plan_average_protein_without_training INT, " +

                    "meal_plan_average_kcal_with_training INT, " +
                    "meal_plan_average_fat_with_training INT, " +
                    "meal_plan_average_carb_with_training INT, " +
                    "meal_plan_average_protein_with_training INT, " +

                    "meal_plan_created DATETIME, " +
                    "meal_plan_updated DATETIME, " +
                    "meal_plan_user_ip VARCHAR(250), " +
                    "meal_plan_image_path VARCHAR(250), " +
                    "meal_plan_image_thumb VARCHAR(250), " +
                    "meal_plan_image_file VARCHAR(250), " +
                    "meal_plan_views INT, " +
                    "meal_plan_views_ip_block TEXT, " +
                    "meal_plan_likes INT, " +
                    "meal_plan_dislikes INT, " +
                    "meal_plan_rating INT, " +
                    "meal_plan_rating_ip_block TEXT, " +
                    "meal_plan_comments INT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS meal_plans_days (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "day_id INT, " +
                    "day_meal_plan_id INT, " +
                    "day_number INT, " +

                    "day_energy_without_training DOUBLE, " +
                    "day_fat_without_training DOUBLE, " +
                    "day_carb_without_training DOUBLE, " +
                    "day_protein_without_training DOUBLE, " +

                    "day_sum_without_training DOUBLE, " +
                    "day_fat_without_training_percentage INT, " +
                    "day_carb_without_training_percentage INT, " +
                    "day_protein_without_training_percentage INT, " +

                    "day_energy_with_training DOUBLE, " +
                    "day_fat_with_training DOUBLE, " +
                    "day_carb_with_training DOUBLE, " +
                    "day_protein_with_training DOUBLE, " +

                    "day_sum_with_training DOUBLE, " +
                    "day_fat_with_training_percentage INT, " +
                    "day_carb_with_training_percentage INT, " +
                    "day_protein_with_training_percentage INT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS meal_plans_meals (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " meal_id INT, " +
                    "meal_meal_plan_id INT, " +
                    "meal_day_number INT, " +
                    "meal_number INT, " +
                    "meal_energy DOUBLE, " +
                    "meal_fat DOUBLE, " +
                    "meal_carb DOUBLE, " +
                    "meal_protein DOUBLE);");

            db.execSQL("CREATE TABLE IF NOT EXISTS meal_plans_entries (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "entry_id INT, " +
                    "entry_meal_plan_id INT, " +
                    "entry_day_number INT, " +
                    "entry_meal_number INT, " +
                    "entry_weight INT, " +
                    "entry_food_id INT, " +
                    "entry_recipe_id INT, " +
                    "entry_name VARCHAR(250), " +
                    "entry_manufacturer_name VARCHAR(250), " +
                    "entry_serving_size DOUBLE, " +
                    "entry_serving_size_measurement VARCHAR(250), " +
                    "entry_energy_per_entry DOUBLE, " +
                    "entry_fat_per_entry DOUBLE, " +
                    "entry_carb_per_entry DOUBLE, " +
                    "entry_protein_per_entry DOUBLE, " +
                    "entry_text TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            // ! All tables that are going to be dropped need to be listed here
            db.execSQL("DROP TABLE IF EXISTS synchronize");
            db.execSQL("DROP TABLE IF EXISTS json_temp_data");
            db.execSQL("DROP TABLE IF EXISTS app_statistics");
            db.execSQL("DROP TABLE IF EXISTS app_error_log");



            db.execSQL("DROP TABLE IF EXISTS food_categories");
            db.execSQL("DROP TABLE IF EXISTS food_categories_translations");
            db.execSQL("DROP TABLE IF EXISTS food_index");
            db.execSQL("DROP TABLE IF EXISTS food_index_stores");
            db.execSQL("DROP TABLE IF EXISTS food_stores");
            db.execSQL("DROP TABLE IF EXISTS food_index_prices");
            db.execSQL("DROP TABLE IF EXISTS food_prices_currencies");
            db.execSQL("DROP TABLE IF EXISTS food_measurements");
            db.execSQL("DROP TABLE IF EXISTS food_measurements_translations");

            db.execSQL("DROP TABLE IF EXISTS food_favorites");
            db.execSQL("DROP TABLE IF EXISTS food_index_ads");
            db.execSQL("DROP TABLE IF EXISTS food_index_tags");

            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS users_profile");
            db.execSQL("DROP TABLE IF EXISTS users_profile_photo");
            db.execSQL("DROP TABLE IF EXISTS users_friends");
            db.execSQL("DROP TABLE IF EXISTS users_status");
            db.execSQL("DROP TABLE IF EXISTS users_status_likes");

            db.execSQL("DROP TABLE IF EXISTS comments");

            db.execSQL("DROP TABLE IF EXISTS food_diary_goals");
            db.execSQL("DROP TABLE IF EXISTS food_diary_entires");
            db.execSQL("DROP TABLE IF EXISTS food_diary_totals_meals");
            db.execSQL("DROP TABLE IF EXISTS food_diary_totals_days");
            db.execSQL("DROP TABLE IF EXISTS food_diary_last_used");


            db.execSQL("DROP TABLE IF EXISTS recipes_categories");
            db.execSQL("DROP TABLE IF EXISTS recipes_categories_translations");
            db.execSQL("DROP TABLE IF EXISTS recipes_my_favorites_categories");
            db.execSQL("DROP TABLE IF EXISTS recipes_my_favorites");
            db.execSQL("DROP TABLE IF EXISTS recipe_vote_limit_tabel");
            db.execSQL("DROP TABLE IF EXISTS recipes");
            db.execSQL("DROP TABLE IF EXISTS recipes_groups");
            db.execSQL("DROP TABLE IF EXISTS recipes_items");
            db.execSQL("DROP TABLE IF EXISTS recipes_numbers");
            db.execSQL("DROP TABLE IF EXISTS recipes_rating");
            db.execSQL("DROP TABLE IF EXISTS recipes_links");
            db.execSQL("DROP TABLE IF EXISTS recipes_tags");
            db.execSQL("DROP TABLE IF EXISTS recipes_tags_unique");
            db.execSQL("DROP TABLE IF EXISTS recipes_comments");

            db.execSQL("DROP TABLE IF EXISTS meal_plans");
            db.execSQL("DROP TABLE IF EXISTS meal_plans_days");
            db.execSQL("DROP TABLE IF EXISTS meal_plans_meals");
            db.execSQL("DROP TABLE IF EXISTS meal_plans_entries");

            onCreate(db);

            String TAG = "Tag";
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

        } // end public void onUpgrade
    } // DatabaseHelper


    /* 05 Open database --------------------------------------------------------- */
    public DBAdapter open() throws SQLException {

        db = DBHelper.getWritableDatabase();

        return this;
    }

    /* 06 Close database --------------------------------------------------------- */
    public void close() {
        DBHelper.close();
    }

    /* 07 Quote smart ------------------------------------------------------------ */
    public String quoteSmart(String value){
        // Is numeric?
        /*
        boolean isNumeric = false;
        try {
            double myDouble = Double.parseDouble(value);
            isNumeric = true;
        }
        catch(NumberFormatException nfe) {

        }
        */
        // Escapes special characters in a string for use in an SQL statement
        if (value != null && value.length() > 0) {
            value = value.replace("\\", "\\\\");
            value = value.replace("'", "\\'");
            value = value.replace("\0", "\\0");
            value = value.replace("\n", "\\n");
            value = value.replace("\r", "\\r");
            value = value.replace("\"", "\\\"");
            value = value.replace("\\x1a", "\\Z");
            value = value.replace ("'","&#39;");
        }

        value = "'" + value + "'";

        // Remove 'null'
        value = value.replace("'null'", "NULL");

        return value;
    }
    public double quoteSmart(double value) { return value; }
    public int quoteSmart(int value) { return value; }
    public long quoteSmart(long value) { return value; }

    /* 08 Insert data ------------------------------------------------------------ */
    public void insert(String table, String fields, String values){

        try {
            db.execSQL("INSERT INTO " + table +  "(" + fields + ") VALUES (" + values + ")");
        }
        catch(SQLiteException e){
            System.out.println("Insert error: " + e.toString());
            Toast.makeText(context, "Error: " +  e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void insertPreparedStatement(String sql, String[] dataTypes, String[] values) {
        try {
            SQLiteStatement statement = db.compileStatement(sql);
            for (int x = 0; x < values.length; x++) {
                if(dataTypes[x].equals("long")){
                    statement.bindLong(x + 1, Long.parseLong(values[x]));
                }
                else if(dataTypes[x].equals("null")) {
                    statement.bindNull(x + 1);
                }
                else {
                    statement.bindString(x + 1, values[x]);
                }
            }
            statement.executeInsert();
        } catch (SQLiteException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.d("cicolife", Log.getStackTraceString(new Exception()));
        }

    }

    /* 09 Count ------------------------------------------------------------------ */
    public int count(String table)
    {
        try {
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + table + "", null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        }
        catch(SQLiteException e){
            return -1;
        }
    }
    public int count(String table, String whereClause, long whereCondition)
    {
        try {
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + table + " WHERE " + whereClause + "=" + whereCondition, null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        }
        catch(SQLiteException e){
            return -1;
        }
    }
    public int count(String table, String whereClause, String whereCondition)
    {
        try {
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + table + " WHERE " + whereClause + "=" + whereCondition, null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        }
        catch(SQLiteException e){
            return -1;
        }
    }

    /* 10 Select ----------------------------------------------------------------- */
    /* Select example:
           Cursor allCategories;
           String fields[] = new String[] {
                                "category_id",
                                "category_name",
                                "category_parent_id"
             };
            allCategories = db.select("categories", fields);
    */
    // Select
    public Cursor select(String table, String[] fields) throws SQLException
    {
        Cursor mCursor = db.query(table, fields, null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // Select All where (String)
    public Cursor select(String table, String[] fields, String whereClause, String whereCondition) throws SQLException
    {
        Cursor mCursor = db.query(table, fields, whereClause + "=" + whereCondition, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // Select All where (String)
    public Cursor select(String table, String[] fields, String[] whereClause, String[] whereCondition, String[] whereAndOr) throws SQLException
    {
        /*
        Cursor cursorFdce;
        String fieldsFdce[] = new String[] {
                "_id",
                "fdce_id",
                "fdce_date",
                "fdce_meal_no",
                "fdce_eaten_energy",
                "fdce_eaten_proteins",
                "fdce_eaten_carbs",
                "fdce_eaten_fat"
        };
        String whereClause[] = new String[]{
                "fdce_date",
                "fdce_meal_no"
        };
        String whereCondition[] = new String[]{
                stringDateSQL,
                stringMealNumberSQL
        };
        String whereAndOr[] = new String[]{
                "AND"
        };*/
        String where = "";
        int arraySize = whereClause.length;
        for(int x=0;x<arraySize;x++) {
            if(where.equals("")) {
                where = whereClause[x] + "=" + whereCondition[x];
            }
            else{
                where = where + " " + whereAndOr[x-1] + " " + whereClause[x] + "=" + whereCondition[x];
            }
        }
        //Toast.makeText(context, where, Toast.LENGTH_SHORT).show();

        Cursor mCursor = db.query(table, fields, where, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // Select All where (Long)
    public Cursor select(String table, String[] fields, String whereClause, long whereCondition) throws SQLException {
        Cursor mCursor = db.query(table, fields, whereClause + "=" + whereCondition, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    // Select with order
    public Cursor select(String table, String[] fields, String whereClause, String whereCondition, String orderBy, String OrderMethod) throws SQLException
    {
        Cursor mCursor = null;
        if(whereClause.equals("")) {
            // We dont want to se where
            mCursor = db.query(table, fields, null, null, null, null, orderBy + " " + OrderMethod, null);
        }
        else {
            mCursor = db.query(table, fields, whereClause + "=" + whereCondition, null, null, null, orderBy + " " + OrderMethod, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // Select random
    /*        String fieldsRecipes = "_id, recipe_id, recipe_title, recipe_image";
        Cursor cursorRecipes;
        try {
            cursorRecipes = db.selectRandom("recipes", fieldsRecipes);*/
    public Cursor selectRandom(String table, String fields) throws SQLException
    {
        Cursor mCursor = db.rawQuery("SELECT " + fields + " FROM " + table + " ORDER BY RANDOM() LIMIT 1" , null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /*- Raw query ---------------------------------------------------------------- */
    public Cursor rawQuery(String query) throws SQLException
    {
        Cursor mCursor = db.rawQuery(query, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    /* 11 Update ----------------------------------------------------------------- */
        /* Update example:
        long id = 1;
        String value = "xxt@doesthiswork.com";
        String valueSQL = db.quoteSmart(value);
        db.update("users", "user_id", id, "user_email", valueSQL);
         */
        /*String updateFields[] = new String[] {
                    "fd_serving_size_gram",
                    "fd_serving_size_pcs",
                    "fd_energy_calculated",
                    "fd_protein_calculated",
                    "fd_carbohydrates_calculated",
                    "fd_fat_calculated"
            };
            String updateValues[] = new String[] {
                    fdServingSizeGramSQL,
                    stringFdServingSizePcsSQL,
                    stringFdEnergyCalcualtedSQL,
                    stringFdProteinsCalcualtedSQL,
                    stringFdCarbohydratesCalcualtedSQL,
                    stringFdFatCalcualtedSQL
            };
            long longPrimaryKey = Long.parseLong(currentFdId);
            db.update("food_diary", "_id", longPrimaryKey, updateFields, updateValues);
            */
    public boolean update(String table, String primaryKey, long rowId, String field, String value) throws SQLException {
        // Toast.makeText(context, "UPDATE " + table + " SET " + field + "=" + value + " WHERE " + primaryKey + "=" + rowId, Toast.LENGTH_SHORT).show();

        // Remove first and last value of value
        value = value.substring(1, value.length()-1); // removes apostrophe after running quote smart

        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
    public boolean update(String table, String primaryKey, String rowId, String field, String value) throws SQLException {
        // Toast.makeText(context, "UPDATE " + table + " SET " + field + "=" + value + " WHERE " + primaryKey + "=" + rowId, Toast.LENGTH_SHORT).show();

        // Remove first and last value of value
        value = value.substring(1, value.length()-1); // removes apostrophe after running quote smart

        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
    public boolean update(String table, String primaryKey, long rowId, String field, double value) throws SQLException {
        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
    public boolean update(String table, String primaryKey, long rowId, String field, int value) throws SQLException {
        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
    public boolean update(String table, String primaryKey, long rowID, String fields[], String values[]) throws SQLException {

        ContentValues args = new ContentValues();
        int arraySize = fields.length;
        for(int x=0;x<arraySize;x++){
            // Remove first and last value of value
            values[x] = values[x].substring(1, values[x].length()-1); // removes apostrophe after running quote smart

            // Put
            args.put(fields[x], values[x]);

            // Toast.makeText(context, fields[x].toString() + "=" + values[x].toString(), Toast.LENGTH_SHORT).show();
        }

        return db.update(table, args, primaryKey + "=" + rowID, null) > 0;
    }
    public boolean update(String table, String primaryKey, String rowID, String fields[], String values[]) throws SQLException {

        ContentValues args = new ContentValues();
        int arraySize = fields.length;
        for(int x=0;x<arraySize;x++){
            // Remove first and last value of value
            values[x] = values[x].substring(1, values[x].length()-1); // removes apostrophe after running quote smart

            // Put
            args.put(fields[x], values[x]);

            // Toast.makeText(context, fields[x].toString() + "=" + values[x].toString(), Toast.LENGTH_SHORT).show();
        }

        return db.update(table, args, primaryKey + "=" + rowID, null) > 0;
    }

    /* 12 Delete ----------------------------------------------------------------- */
    // Delete a particular record
    public int delete(String table, String primaryKey, long rowID) throws SQLException {
        return db.delete(table, primaryKey + "=" + rowID, null);
    }
    public int delete(String table, String primaryKey, String rowID) throws SQLException {
        return db.delete(table, primaryKey + "=" + rowID, null);
    }
    public void truncate(String table){
        db.execSQL("delete from "+ table);
    }
}
