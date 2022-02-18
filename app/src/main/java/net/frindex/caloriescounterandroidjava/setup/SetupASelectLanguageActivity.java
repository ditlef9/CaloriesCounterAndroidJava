package net.frindex.caloriescounterandroidjava.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.common.OutputString;
import net.frindex.caloriescounterandroidjava.common.WriteToErrorLog;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;
import net.frindex.caloriescounterandroidjava.synchronize_app.CheckAppSynchronizationActivity;
import net.frindex.caloriescounterandroidjava.synchronize_app.SynchronizeAFoodCategories;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetupASelectLanguageActivity extends AppCompatActivity {

    /* Api variables */
    String apiURL = "https://summerslim.codecourses.eu/users/api"; // Without ending slash
    String apiPassword  = "w7Vdwenb";

    /* Languages */
    String[] arrayLanguage = new String[] { "English", "Norwegian" };
    String[] arrayLanguageIso2    = new String[] { "en", "no" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_aselect_language);

        // Insert languages
        populateLanguagesList();

        // Check auto login
        checkAutoLogin();
    }

    /*- View Languages --------------------------------------------------- */
    public void populateLanguagesList(){

        // Find ListView to populate
        ListView listViewLanguages = findViewById(R.id.listViewLanguages);

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_list_item_medium_white, arrayLanguage);

        // Set
        listViewLanguages.setAdapter(adapter);


        // OnClick
        listViewLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                listItemClicked(arg2);
            }
        });

    } // populateLanguagesList

    public void listItemClicked(int listItemIDClicked){

        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();


        // Language
        String language = arrayLanguageIso2[listItemIDClicked];
        String languageSQL = db.quoteSmart(language);
        //Toast.makeText(this, arrayLanguageIso2[listItemIDClicked], Toast.LENGTH_SHORT).show();

        // Change language
        setLocale(language);

        // Database: Count rows in user table
        int numberUsersRows = db.count("users");


        if(numberUsersRows < 1) {
            // Insert
            String values = "NULL, " + languageSQL;
            db.insert("users", "_id, user_language", values);
        }
        else{
            long rowID = 1;
            db.update("users", "_id", rowID, "user_language", languageSQL);
        }
        // Database close
        db.close();

        // Move
        Intent i = new Intent(SetupASelectLanguageActivity.this, SetupBPermissionsActivity.class);
        i.putExtra("currentLanguage", language);
        startActivity(i);
        finish();


    } // listItemClicked


    /*- Set locale ------------------------------------------------------------ */
    // Changes screen to specified language
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    /*- Check auto login ------------------------------------------------------------------------ */
    private void checkAutoLogin() {
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName = manufacturer + " " + model;

        String stringUrl = apiURL + "/post_fetch_api_session_from_server.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_device_id", encryptPassword(androidId));
        data.put("inp_device_name", deviceName);
        data.put("inp_device_source", "Fitness Life Calories Counter for Android");

        HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringUrl, stringMethod, data, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                answerCheckAutoLogin();
            }
        });
        task.execute();
    }
    public void answerCheckAutoLogin(){
        DBAdapter db = new DBAdapter(this);
        db.open();

        String query = "SELECT _id, data FROM json_temp_data ORDER BY _id DESC LIMIT 0,1";
        Cursor cursorData = db.rawQuery(query);
        String dataResult = cursorData.getString(1);

        db.close();


        if(dataResult.equals("")) {
        }
        else if(dataResult.equals("No session")){
        }
        else if(dataResult.equals("Missing variable")){

        }
        else{

            try {
                db.open();


                JSONObject json = new JSONObject(dataResult);

                // User
                JSONObject userObject = json.getJSONObject("user");
                String userIdSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_id"))));
                String userEmailSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_email"))));
                String userNameSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_name"))));
                String userAliasSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_alias"))));
                String userPasswordSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_password"))));
                String userPasswordDateSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_password_date"))));
                String userSaltSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_salt"))));
                String userSecuritySQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_security"))));
                String userLanguage = new OutputString().outputHTML((userObject.getString("user_language")));
                String userLanguageSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_language"))));
                String userGenderSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_gender"))));
                String userHeightSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_height"))));
                String userMeasurementSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_measurement"))));
                String userDobSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_dob"))));
                String userDateFormatSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_date_format"))));
                String userRegisteredSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_registered"))));
                String userRegisteredTimeSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_registered_time"))));
                String userLastOnlineSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_last_online"))));
                String userLastOnlineTimeSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_last_online_time"))));
                String userRankSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_rank"))));
                String userPointsSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_points"))));
                String userPointsRankSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_points_rank"))));
                String userLikesSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_likes"))));
                String userDislikesSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_dislikes"))));
                String userStatusSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_status"))));
                String userLoginTriesSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_login_tries"))));
                String userLastIpSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_last_ip"))));
                String userSynchronizedSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_synchronized"))));
                String userVerifiedByModeratorSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_verified_by_moderator"))));
                String userNotesSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_notes"))));

                String q = "SELECT _id FROM users WHERE _id='1'";
                Cursor cursorCheck = db.rawQuery(q);
                int size = cursorCheck.getCount();
                if(size == 0) {
                    q = "INSERT INTO users(_id, user_id, user_email, " +
                            "user_name, user_alias, user_password, " +
                            "user_password_replacement, user_password_date, user_salt, " +
                            "user_security, user_language, user_gender, " +
                            "user_height, user_measurement, user_dob, " +
                            "user_date_format, user_registered, user_registered_time, " +
                            "user_last_online, user_last_online_time, user_rank, " +
                            "user_points, user_points_rank, user_likes, " +
                            "user_dislikes, user_status, user_login_tries, " +
                            "user_last_ip, user_synchronized, user_verified_by_moderator, " +
                            "user_notes, user_marked_as_spammer) " +
                            " VALUES (" +
                            "NULL, " + userIdSQL + ", " +userEmailSQL + ", " +
                            userNameSQL + ", " + userAliasSQL + ", " + userPasswordSQL + ", " +
                            "'', " + userPasswordDateSQL + ", " + userSaltSQL + ", " +
                            userSecuritySQL  + ", " + userLanguageSQL + ", " + userGenderSQL + ", " +
                            userHeightSQL + ", " + userMeasurementSQL + ", " + userDobSQL + ", " +
                            userDateFormatSQL  + ", " + userRegisteredSQL + ", " + userRegisteredTimeSQL + ", " +

                            userLastOnlineSQL  + ", " + userLastOnlineTimeSQL + ", " + userRankSQL + ", " +
                            userPointsSQL  + ", " + userPointsRankSQL + ", " + userLikesSQL + ", " +
                            userDislikesSQL  + ", " + userStatusSQL + ", " + userLoginTriesSQL + ", " +
                            userLastIpSQL  + ", " + userSynchronizedSQL + ", " + userVerifiedByModeratorSQL + ", " +
                            userNotesSQL  + ", " + "'')";
                    db.rawQuery(q);
                }
                else {
                    q = "UPDATE users SET " +
                            "user_id=" + userIdSQL + "," +
                            "user_email=" + userEmailSQL + "," +
                            "user_name=" + userNameSQL + "," +
                            "user_alias=" + userAliasSQL + "," +
                            "user_password=" + userPasswordSQL + "," +
                            "user_password_date=" + userPasswordDateSQL + "," +
                            "user_salt=" + userSaltSQL + "," +
                            "user_security=" + userSecuritySQL + "," +
                            "user_language=" + userLanguageSQL + "," +
                            "user_gender=" + userGenderSQL + "," +
                            "user_height=" + userHeightSQL + "," +
                            "user_measurement=" + userMeasurementSQL + "," +
                            "user_dob=" + userDobSQL + "," +
                            "user_date_format=" + userDateFormatSQL + "," +
                            "user_registered=" + userRegisteredSQL + "," +
                            "user_registered_time=" + userRegisteredTimeSQL + "," +
                            "user_last_online=" + userLastOnlineSQL + "," +
                            "user_last_online_time=" + userLastOnlineTimeSQL + "," +
                            "user_rank=" + userRankSQL + "," +
                            "user_points=" + userPointsSQL + "," +
                            "user_points_rank=" + userPointsRankSQL + "," +
                            "user_likes=" + userLikesSQL + "," +
                            "user_dislikes=" + userDislikesSQL + "," +
                            "user_status=" + userStatusSQL + "," +
                            "user_login_tries=" + userLoginTriesSQL + "," +
                            "user_last_ip=" + userLastIpSQL + "," +
                            "user_synchronized=" + userSynchronizedSQL + "," +
                            "user_verified_by_moderator=" + userVerifiedByModeratorSQL + "," +
                            "user_notes=" + userNotesSQL +
                            " WHERE _id=1";
                    db.rawQuery(q);
                }


                // Profile
                JSONObject profileObject = json.getJSONObject("profile");
                String profileIdSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_id")));
                String profileFirstNameSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_first_name")));
                String profileCitySQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_city")));
                String profileCountrySQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_country")));
                String profileWorkSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_work")));
                String profileUniversitySQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_university")));
                String profileHighSchoolSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_high_school")));
                String profileLanguagesSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_languages")));
                String profileWebsiteSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_website")));
                String profileInterestedInSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_interested_in")));
                String profileRelationshipSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_relationship")));
                String profileAboutSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_about")));
                String profileViewsSQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_views")));
                String profilePrivacySQL = db.quoteSmart(new OutputString().outputHTML(profileObject.getString("profile_privacy")));
                q = "SELECT _id FROM users_profile WHERE profile_user_id=" + userIdSQL;
                Cursor usersProfileCursor = db.rawQuery(q);
                int cursorLenght = usersProfileCursor.getCount();
                if (cursorLenght < 1) {

                    // We don't have it, insert it
                    q = "INSERT INTO users_profile(_id, profile_id, profile_user_id, profile_first_name, profile_city, " +
                            "profile_country, profile_work, profile_university, profile_high_school, profile_languages, " +
                            "profile_website, profile_interested_in, profile_relationship, profile_about, " +
                            "profile_views, profile_privacy) " +
                            " VALUES (" +
                            "NULL, " +
                            profileIdSQL + ", " +
                            userIdSQL + ", " +
                            profileFirstNameSQL + ", " +
                            profileCitySQL + ", " +
                            profileCountrySQL + ", " +
                            profileWorkSQL + ", " +
                            profileUniversitySQL + ", " +
                            profileHighSchoolSQL + ", " +
                            profileLanguagesSQL + ", " +
                            profileWebsiteSQL + ", " +
                            profileInterestedInSQL + ", " +
                            profileRelationshipSQL + ", " +
                            profileAboutSQL + ", " +
                            profileViewsSQL + ", " +
                            profilePrivacySQL +
                            ")";
                    try {
                        db.rawQuery(q);
                    } catch (SQLException sqle) {
                        Toast.makeText(this, "Could not insert into profile: " + sqle.toString(), Toast.LENGTH_LONG).show();
                    }
                } // new user
                else {
                    // Update user
                    q = "UPDATE users_profile SET " +
                            "profile_first_name=" + profileFirstNameSQL + "," +
                            "profile_city=" + profileCitySQL + "," +
                            "profile_country=" + profileCountrySQL + "," +
                            "profile_work=" + profileWorkSQL + "," +
                            "profile_university=" + profileUniversitySQL + "," +
                            "profile_high_school=" + profileHighSchoolSQL + "," +
                            "profile_languages=" + profileLanguagesSQL + "," +
                            "profile_website=" + profileWebsiteSQL + "," +
                            "profile_interested_in=" + profileInterestedInSQL + "," +
                            "profile_relationship=" + profileRelationshipSQL + "," +
                            "profile_about=" + profileAboutSQL  + "," +
                            "profile_views=" + profileViewsSQL + "," +
                            "profile_privacy=" + profilePrivacySQL +
                            " WHERE profile_user_id=" + userIdSQL;
                    try {
                        db.rawQuery(q);
                    } catch (SQLException sqle) {
                        Toast.makeText(this, "Could not update users_profile: " + sqle.toString(), Toast.LENGTH_LONG).show();
                    }
                }


                db.close();


                // Start synchronization
                WriteToErrorLog write = new WriteToErrorLog(this);
                write.writeToErrorLog("SetupASelectLanguageActivity", "synchronizeBeforeRegisterLogin", "Started synchronization", "info");

                SynchronizeAFoodCategories sync = new SynchronizeAFoodCategories(this, userLanguage);
                sync.updateLastSynchronizedDate();

                // Move to
                Intent i = new Intent(SetupASelectLanguageActivity.this, CheckAppSynchronizationActivity.class);
                i.putExtra("newOrOldUser", "old_user");
                startActivity(i);
                finish();

            } catch (org.json.JSONException e) {
                // No user (null)
                // Toast.makeText(this, "SetupASelectLanguageActivity" + e.toString(), Toast.LENGTH_LONG).show();
            }
        } // user log in


    } // answerCheckAutoLogin




    /*- Encrypt password ------------------------------------------------------------- */
    private static String encryptPassword(String password) {
        MessageDigest crypt;

        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            try {
                crypt.update(password.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                return uee.toString();
            }
        } catch (NoSuchAlgorithmException nsae) {
            return nsae.toString();
        }

        return new BigInteger(1, crypt.digest()).toString(16);
    }
}