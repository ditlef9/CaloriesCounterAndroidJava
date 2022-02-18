package net.frindex.caloriescounterandroidjava.user;

import androidx.appcompat.app.ActionBar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.common.OutputString;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;
import net.frindex.caloriescounterandroidjava.setup.SetupCPermissionsCheckerActivity;
import net.frindex.caloriescounterandroidjava.synchronize_app.CheckAppSynchronizationActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserLoginActivity extends AppCompatActivity {

    /* Api variables */
    String apiURL = "https://summerslim.codecourses.eu/users/api"; // Without ending slash
    String apiPassword  = "w7Vdwenb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        /* Hide password */
        // hidePasswordField();

        /* Listen */
        listenLoginSignUpButtons();

        /* Toolbar */
        toolbarTitle();

        // Hide password field
        hidePasswordField();
    }

    /* Hide password field */
    private void hidePasswordField() {
        TableRow tableRowPassword = findViewById(R.id.tableRowPassword);
        tableRowPassword.setVisibility(View.GONE);
    }


    /*- Toolbar Title ---------------------------------------------------------- */
    // Makes changes to the toolbar
    public void toolbarTitle(){
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.login));
        actionBar.show();

        // Back icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Show
        actionBar.show();
    }

    /* One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Up button
        // If we have entered e-mail then we want to reload class

        EditText editTextEmail = findViewById(R.id.editTextEmail);
        String stringEmail = editTextEmail.getText().toString().trim();
        if(stringEmail == "") {
            Intent i = new Intent(UserLoginActivity.this, SetupCPermissionsCheckerActivity.class);
            startActivity(i);
            finish();
        }
        else{
            // If e-mail field is visible, then we want to go back
            TableRow tableRowEmail = findViewById(R.id.tableRowEmail);
            if(tableRowEmail.getVisibility() == View.VISIBLE){
                Intent i = new Intent(UserLoginActivity.this, SetupCPermissionsCheckerActivity.class);
                startActivity(i);
                finish();
            }
            else{
                // We have entered e-mail, so reload class
                tableRowEmail.setVisibility(View.VISIBLE);

                TableRow tableRowPassword = findViewById(R.id.tableRowPassword);
                tableRowPassword.setVisibility(View.GONE);


            }

        }

        return super.onOptionsItemSelected(item);
    }


    /*- Check if we are registered ------------------------------------------ */
    public void checkIfWeAreRegistered() {

        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();

        long rowID = 1;
        String fieldsUser[] = new String[]{"_id", "user_registered", "user_language"};
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowID);
        try {
            String stringUserRegistered = cursorUser.getString(1);
            String stringUserLanguage = cursorUser.getString(2);

            if (stringUserRegistered != null) {
                // Toast
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();

                // I am finished with my registration...
                Intent i = new Intent(UserLoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        } catch (Exception e) {
        }
        db.close();
    }

    /*- Set locale ------------------------------------------------------------ */
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //Intent refresh = new Intent(this, SignUp.class);
        //startActivity(refresh);
    }

    /*- Change language ----------------------------------------------- */
    public void setLanguage() {
        // Get user language
        String lang3 = Locale.getDefault().getISO3Language();
        String lang2 = lang3.substring(0, 2);

        // Get recourse
        Resources res = this.getResources();

        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang3.toLowerCase())); // API 17+ only.
    }

    /*- Listen login sign up buttons --------------------------------------------------- */
    public void listenLoginSignUpButtons() {
        /* Login listener */
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });

        /* Forgot password listener */
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordClicked();
            }
        });

        /* Register listener */
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpClicked();
            }
        });


    } // listenLoginRegisterButtons

    /*- Signup clicked ------------------------------------------------------------------- */
    public void signUpClicked() {
        Intent i = new Intent(UserLoginActivity.this, UserSignUpActivity.class);
        startActivity(i);
    } // signUpClicked


    /*- Login clicked ------------------------------------------------------------------- */
    public void loginClicked() {
        // Error?
        int error = 0;

        // Email
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        String stringEmail = editTextEmail.getText().toString().trim();
        if (stringEmail.equals("")) {
            error = 1;
            Toast.makeText(this, "Please enter your e-mail address", Toast.LENGTH_SHORT).show();
        } else {
            // Password
            EditText editTextPassword = findViewById(R.id.editTextPassword);
            String stringPassword = editTextPassword.getText().toString();
            String stringPasswordEncrypted = encryptPassword(stringPassword);
            if (stringPassword.equals("") && error == 0) {
                error = 1;
                // Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();

                // Hide email field
                TableRow tableRowEmail = findViewById(R.id.tableRowEmail);
                tableRowEmail.setVisibility(View.GONE);

                // Display password field
                TableRow tableRowPassword = findViewById(R.id.tableRowPassword);
                tableRowPassword.setVisibility(View.VISIBLE);

                // Set focus to password
                editTextPassword.requestFocus();





            } // Password blank
            else {

                // HTTP request: User does not exists in db
                if (error == 0) {
                    // Device
                    String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    String deviceName = manufacturer + " " + model;


                    TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
                    String stringUrl = apiURL + "/login_user.php"; // login_user.php";
                    String stringMethod = "post";

                    Map<String, String> data = new HashMap<String, String>();
                    data.put("inp_api_password", apiPassword);
                    data.put("inp_user_email", stringEmail);
                    data.put("inp_user_password", stringPasswordEncrypted);
                    data.put("inp_device_id", encryptPassword(androidId));
                    data.put("inp_device_name", deviceName);
                    data.put("inp_device_source", "Fitness Life Calories Counter for Android");

                    HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringUrl, stringMethod, data, textViewDynamicText, new HttpRequestLongOperation.TaskListener() {
                        @Override
                        public void onFinished(String result) {
                            // Do Something after the task has finished
                            loginClickedCheckUserAgainstPHP();
                        }
                    });
                    task.execute();

                }
            } // Password not blank
        } // E-mail blank
    }

    public void loginClickedCheckUserAgainstPHP() {
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        String stringJSON = textViewDynamicText.getText().toString();


        if (stringJSON.equals("E-mail not found")) {
            Toast.makeText(this, "E-mail not found", Toast.LENGTH_SHORT).show();

            // Show e-mail field
            EditText editTextEmail = findViewById(R.id.editTextEmail);
            editTextEmail.setVisibility(View.VISIBLE);


        } else if (stringJSON.equals("Wrong password")) {
            // Clear password
            EditText editTextPassword = findViewById(R.id.editTextPassword);
            editTextPassword.setText("");

            // Toast
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();

        } else if (stringJSON.equals("Replacement password expired")) {
            Toast.makeText(this, "Replacement password expired", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(this, "Logging you in...", Toast.LENGTH_SHORT).show();


            updateUserInformationFromPHP();
        }
    }

    /*- Get User Data from PHP ------------------------------------------------------ */
    private void updateUserInformationFromPHP() {
        // Json get text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        String stringJSON = textViewDynamicText.getText().toString();

        try {
            DBAdapter db = new DBAdapter(this);
            db.open();


            JSONObject json = new JSONObject(stringJSON);

            // User
            JSONObject userObject = json.getJSONObject("user");
            String userIdSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_id"))));
            String userEmailSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_email"))));
            String userNameSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_name"))));
            String userAliasSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_alias"))));

            String userPasswordDateSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_password_date"))));
            String userSaltSQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_salt"))));
            String userSecuritySQL = db.quoteSmart(new OutputString().outputHTML((userObject.getString("user_security"))));
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

            // Password
            EditText editTextPassword = findViewById(R.id.editTextPassword);
            String stringPassword = editTextPassword.getText().toString();
            String stringPasswordEncrypted = encryptPassword(stringPassword);
            String stringPasswordEncryptedSQL = db.quoteSmart(stringPasswordEncrypted);

            String q = "UPDATE users SET " +
                    "user_id=" + userIdSQL + "," +
                    "user_email=" + userEmailSQL + "," +
                    "user_name=" + userNameSQL + "," +
                    "user_alias=" + userAliasSQL + "," +
                    "user_password=" + stringPasswordEncryptedSQL + "," +
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


            try {
                db.rawQuery(q);
            } catch (SQLException e) {
                Toast.makeText(this, "users: " + e.toString(), Toast.LENGTH_LONG).show();
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

            JSONObject photoObject = json.getJSONObject("photo");
            String photoIdSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_id")));
            String photoProfileImageSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_profile_image")));
            String photoDestinationSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_destination")));
            String photoUploadeSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_uploaded")));
            String photoUploadedIpSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_uploaded_ip")));
            String photoViewsSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_views")));
            String photoLikesSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_likes")));
            String photoCommentsSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_comments")));
            String photoXOffsetSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_x_offset")));
            String photoYOffsetSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_y_offset")));
            String photoTextSQL = db.quoteSmart(new OutputString().outputHTML(photoObject.getString("photo_text")));
            q = "SELECT _id FROM users_profile_photo WHERE photo_id=" + photoIdSQL + " AND photo_user_id=" + userIdSQL;
            Cursor usersProfilePohotoCursor = db.rawQuery(q);
            cursorLenght = usersProfilePohotoCursor.getCount();
            if (cursorLenght < 1) {

                // We don't have it, insert it
                q = "INSERT INTO users_profile_photo(_id, photo_id, photo_user_id, photo_profile_image, photo_destination, " +
                        "photo_uploaded, photo_uploaded_ip, photo_views, photo_likes, photo_comments, " +
                        "photo_x_offset, photo_y_offset, photo_text) " +
                        " VALUES (" +
                        "NULL, " +
                        photoIdSQL + ", " +
                        userIdSQL + ", " +
                        photoProfileImageSQL + ", " +
                        photoDestinationSQL + ", " +
                        photoUploadeSQL + ", " +
                        photoUploadedIpSQL + ", " +
                        photoViewsSQL + ", " +
                        photoLikesSQL + ", " +
                        photoCommentsSQL + ", " +
                        photoXOffsetSQL + ", " +
                        photoYOffsetSQL + ", " +
                        photoTextSQL +
                        ")";
                try {
                    db.rawQuery(q);
                } catch (SQLException sqle) {
                    // Toast.makeText(this, "Could not insert into photo: " + sqle.toString(), Toast.LENGTH_LONG).show();
                }
            } // new photo
            else {
                // Update user
                q = "UPDATE users_profile_photo SET " +
                        "photo_destination=" + photoDestinationSQL +
                        " WHERE photo_id=" + photoIdSQL + " AND photo_user_id=" + userIdSQL;
                try {
                    db.rawQuery(q);
                } catch (SQLException sqle) {
                    // Toast.makeText(this, "Could not update photo: " + sqle.toString(), Toast.LENGTH_LONG).show();
                }
            }




            db.close();
        } catch (org.json.JSONException e) {
            // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        // We can now synchronize Workout Diary Plans
        // But we can only start synchronization AFTER the other synchronization is finished,
        // if we don't wait - then the app will be slow
        // SynchronizeBWorkoutDiaryPlans sync = new SynchronizeBWorkoutDiaryPlans(this, "after login");
        // sync.updateLastSynchronizedDate();


        // Move to
        Intent i = new Intent(UserLoginActivity.this, CheckAppSynchronizationActivity.class);
        i.putExtra("newOrOldUser", "old_user");
        startActivity(i);
        finish();

    } // getUserDataFromPHP

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


    /*- Forgot Password clicked ---------------------------------------------------------------- */
    public void forgotPasswordClicked() {

        Intent i = new Intent(UserLoginActivity.this, UserForgotPasswordActivity.class);
        startActivity(i);

    } // signUpClicked


}