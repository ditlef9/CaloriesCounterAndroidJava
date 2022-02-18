package net.frindex.caloriescounterandroidjava.user;

import static android.widget.Toast.makeText;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;
import net.frindex.caloriescounterandroidjava.synchronize_app.CheckAppSynchronizationActivity;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class UserSignUpActivity extends AppCompatActivity {

    /* Api variables */
    String apiURL       = "https://summerslim.codecourses.eu/users/api"; // Without ending slash
    String apiPassword  = "w7Vdwenb";

    /* Languages */
    String[] arraySpinnerLanguage = new String[] { "English", "Spanish", "Norwegian" };
    String[] arrayLanguageIso2    = new String[] { "en", "es", "no" };


    /*- No need for edit below here -*/

    /* Class variables */
    private int currentStepId = 0;
    private String currentStepName = "";

    /* Variables */
    private String[] arraySpinnerDOBDay = new String[31];
    private String[] arraySpinnerDOBMonth = new String[12];
    private String[] arraySpinnerDOBYear = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        // Setup listeners
        initializeListeners();

        // Hide fields
        initializeHideFields();

        // We will start with langauge
        signUpLanguageSelect();

        setLanguage();

    }

    /*- Change language ----------------------------------------------- */
    public void setLanguage(){
        // Get user language
        String lang3 = Locale.getDefault().getISO3Language();
        String lang2 = lang3.substring(0, 2);

        // Get recourse
        Resources res = this.getResources();

        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang3.toLowerCase())); // API 17+ only.
    } // setLanguage

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


    /*- Initialize listeners ------------------------------------------ */
    public void initializeListeners() {


        /* Next listener */
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNextClicked();
            }
        });

    } // public voild initializeListeners

    /*- Initialize hide fields ------------------------------------------- */
    public void initializeHideFields(){

        TableRow tableRowEmail = findViewById(R.id.tableRowEmail);
        tableRowEmail.setVisibility(View.GONE);

        TableRow tableRowUserName = findViewById(R.id.tableRowUserName);
        tableRowUserName.setVisibility(View.GONE);

        TableRow tableRowPassword = findViewById(R.id.tableRowPassword);
        tableRowPassword.setVisibility(View.GONE);

        TableRow tableRowGender = findViewById(R.id.tableRowGender);
        tableRowGender.setVisibility(View.GONE);

        TableRow tableRowDOBDay = findViewById(R.id.tableRowDOBDay);
        tableRowDOBDay.setVisibility(View.GONE);

        TableRow tableRowDOBMonth = findViewById(R.id.tableRowDOBMonth);
        tableRowDOBMonth.setVisibility(View.GONE);

        TableRow tableRowDOBYear = findViewById(R.id.tableRowDOBYear);
        tableRowDOBYear.setVisibility(View.GONE);

    } // initializeHideFields

    /*- Flow ------------------------------------------------------------ */
    /*0: signUpLanguageSelect
    1: signUpLanguageValidate
    2: signUpEmailSelect
    2A: signUpEmailPreValidate
    2B: signUpEmailPreValidateCheck
    3: signUpEmailValidate
    4: signUpUsername
    5: signUpUsernameValidate
    4: signUpPasswordSelect
    5: signUpPasswordValidate
    6: signUpDateOfBirth
    7: signUpDateOfBirthValidate
    8: signUpGender
    9: signUpGenderValidate
    10: signUpHeightWeight
    11: signUpHeightWeightValidate
    12: signUpActivityLevel
    13: signUpActivityLevelValidate
    14: signUpGoal*/


    /*- Button Next clicked ---------------------------------------------- */
    public void buttonNextClicked(){

        if(currentStepName.equals("signUpLanguageSelect")) {
            // Check e-mail before continuing
            signUpLanguageValidate();
        }
        else if(currentStepName.equals("signUpEmailSelect")) {
            // Check e-mail before continuing
            signUpEmailPreValidate();
        }
        else if(currentStepName.equals("signUpEmailValidate")){
            // We want to log in
            signUpEmailValidate();
        }
        else if(currentStepName.equals("signUpUserNameSelect")){
            // Check alias before continuing
            signUpUserNamePreValidate();
        }
        else if(currentStepName.equals("signUpUserNameValidate")){
            signUpUserNameValidate();
        }
        else if(currentStepName.equals("signUpPasswordSelect") || currentStepName.equals("signUpPasswordValidate")){
            // We selecting entering DOB
            signUpPasswordValidate();
        }
        else if(currentStepName.equals("signUpDateOfBirth") || currentStepName.equals("signUpDateOfBirthValidate ")){
            // We are entering gender
            signUpDateOfBirthValidate();
        }
        else if(currentStepName.equals("signUpGender")|| currentStepName.equals("signUpGenderValidate")){
            // We are sign up measurement
            signUpGenderValidate();
        }
        else {
            makeText(this, "Error. Unknown step\n currentStepId: " + currentStepId + "\n currentStepName: " + currentStepName, Toast.LENGTH_SHORT).show();
        }
    } // buttonNextClicked

    /*- 0: signUpLanguageSelect ------------------------------------------------------ */
    public void signUpLanguageSelect(){
        // Set current step
        currentStepName = "signUpLanguageSelect";
        currentStepId   = 0;


        // Set headline
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(this.getResources().getString(R.string.please_select_language_and_measurement_unit));

        // Language
        Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);
        // -> Set as class variable String[] arraySpinnerLanguage = new String[] { "English", "German", "Norwegian" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinnerLanguage);
        spinnerLanguage.setAdapter(adapter);


        // Measurement
        Spinner spinnerMeasurement = findViewById(R.id.spinnerMeasurement);
        String[] arraySpinnerrMeasurement = new String[] { "Kg / Meter", "LBS / Inches" };
        ArrayAdapter<String> adapterMeasurement = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinnerrMeasurement);
        spinnerMeasurement.setAdapter(adapterMeasurement);



        // Select correct index: Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Select correct index: Check if I exists, if I do.. then select
        long rowId = 1;
        String query = "SELECT _id, user_language, user_measurement FROM users WHERE _id='1'";
        Cursor cursorUser = db.rawQuery(query);
        int cursorUserCount = cursorUser.getCount();
        if(cursorUserCount == 1){
            // Language
            String userLanguage = cursorUser.getString(1);
            int spinnerLanguageIndex = 0;
            for(int x=0;x<arrayLanguageIso2.length;x++){
                if(userLanguage.equals(arrayLanguageIso2[x])){
                    spinnerLanguageIndex = x;
                    break;
                }
            }
            spinnerLanguage.setSelection(spinnerLanguageIndex);


            // Measurement
            String userMeasurement = cursorUser.getString(2);
            int spinnerMeasurementIndex = 0;
            if(userMeasurement != null) {
                if (userMeasurement.equals("metric")) {
                    spinnerMeasurementIndex = 0;
                } else {
                    spinnerMeasurementIndex = 1;
                }
            }
            spinnerMeasurement.setSelection(spinnerMeasurementIndex);


        }

        // Select correct index: Close db
        db.close();

    } // signUpLanguageSelect


    /*- 1: signUpLanguageMeasurementValidate ------------------------------------------------------ */
    public void signUpLanguageValidate(){
        // Set current step
        currentStepName = "signUpLanguageValidate";
        currentStepId   = 1;

        // Language
        Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);
        int spinnerLanguageIndex = spinnerLanguage.getSelectedItemPosition();
        String stringLanguage = arrayLanguageIso2[spinnerLanguageIndex];


        // Measurement
        Spinner spinnerMeasurement = findViewById(R.id.spinnerMeasurement);
        int spinnerMeasurementIndex = spinnerMeasurement.getSelectedItemPosition();
        String stringMeasurement = "";
        if(spinnerMeasurementIndex == 0){
            stringMeasurement = "metric";
        }
        else{
            stringMeasurement = "imperial";
        }



        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Ready SQL
        String stringLanguageSQL = db.quoteSmart(stringLanguage);
        String stringMeasurementSQL = db.quoteSmart(stringMeasurement);

        // Update or Insert
        long rowId = 1;
        String fieldsUser[] = new String[] {  "_id"};
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowId);
        int cursorUserCount = cursorUser.getCount();
        if(cursorUserCount == 1){
            // Update
            db.update("users", "_id", rowId, "user_language", stringLanguageSQL);
            db.update("users", "_id", rowId, "user_measurement", stringMeasurementSQL);
        }
        else{
            // Insert
            String inpFields = "NULL, " + stringLanguageSQL + ", " + stringMeasurementSQL + ", 'user'";
            db.insert("users", "_id, user_language, user_measurement, user_rank", inpFields);
        }


        // Close db connection
        db.close();

        // Set language
        setLocale(stringLanguage);

        // Update next button language
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setText(R.string.proceed);

        // Toast
        //Toast.makeText(this, "Changing language to " + stringLanguage, Toast.LENGTH_LONG).show();

        // Navigate
        signUpEmailSelect();

    } // signUpLanguageValidate



    /* 2: signUpEmailSelect ---------------------------------------------- */
    public void signUpEmailSelect(){
        // Set current step
        currentStepName = "signUpEmailSelect";
        currentStepId   = 2;

        // Set headline
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(this.getResources().getString(R.string.what_is_your_email_address_question_mark));

        // Hide language
        TableRow tableRowLanguage = findViewById(R.id.tableRowLanguage);
        tableRowLanguage.setVisibility(View.GONE);

        // Hide Measurement
        TableRow tableRowMeasurement = findViewById(R.id.tableRowMeasurement);
        tableRowMeasurement.setVisibility(View.GONE);

        // Show Email
        TableRow tableRowEmail = findViewById(R.id.tableRowEmail);
        tableRowEmail.setVisibility(View.VISIBLE);

        // Get refrence to text field
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.requestFocus();

        // Clear json text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");


        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Check if I exists, if I do.. then update
        long rowId = 1;
        String fieldsUser[] = new String[] {  "_id", "user_email" };
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowId);
        int cursorUserCount = cursorUser.getCount();
        if(cursorUserCount == 1){
            // Fill with data from db
            editTextEmail.setText(cursorUser.getString(1));
        }

        // Close db
        db.close();

    } // signUpEmailSelect


    /*- 2A: Email pre validate  ------------------------------------------------ */
    public void signUpEmailPreValidate() {
        // Email taken : Check that it is avaible at nettport.com/stram

        // Text view JSON
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);

        // Get email
        EditText editTextInpText = findViewById(R.id.editTextEmail);
        String stringEditTextInpText = editTextInpText.getText().toString();

        // Call HTTP request
        String stringApiUrl = apiURL + "/check_user_email.php?user_email=" + stringEditTextInpText;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringApiUrl, stringMethod, stringSend, textViewDynamicText, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                signUpEmailPreValidateCheck();
            }
        });
        task.execute();


    }
    /*- 2B: Email post pre validate  ------------------------------------------------ */
    public void signUpEmailPreValidateCheck(){
        // Email taken : Check for answer
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        String stringDynamicText = textViewDynamicText.getText().toString();
        if (stringDynamicText.equals("Email is taken")) {
            Toast.makeText(this, "The email already exists. Please log in.", Toast.LENGTH_SHORT).show();
        }
        else if (stringDynamicText.equals("Email is available")) {
            // makeText(this, "E-mail ok", Toast.LENGTH_SHORT).show();
            signUpEmailValidate();
        }
        else if (stringDynamicText.equals("Missing variable user_email")) {
            Toast.makeText(this, "Please enter your e-mail", Toast.LENGTH_SHORT).show();
        }
        else if (stringDynamicText.equals("Timeout")) {
            Toast.makeText(this, "No connection to server. Continuing in offline mode.", Toast.LENGTH_SHORT).show();
            signUpEmailValidate();
        }
        else if (stringDynamicText.equals("")) {
            Toast.makeText(this, "Offline mode activated", Toast.LENGTH_SHORT).show();
            signUpEmailValidate();
        }
    }

    /*- 3: Email validate  ------------------------------------------------ */
    public void signUpEmailValidate(){
        // Set current step
        currentStepName = "signUpEmailValidate";
        currentStepId   = 3;

        // Error?
        int error = 0;

        // Clear json text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");

        // Get editText
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        String stringEditTextEmail = editTextEmail.getText().toString();
        if(stringEditTextEmail.equals("")){
            error = 1;
            Toast.makeText(this, "Please enter a e-mail address", Toast.LENGTH_SHORT).show();
        }

        // Validate e-mail
        if(error == 0){
            String at  = "@";
            if (!stringEditTextEmail.contains(at)) {
                error = 1;
                Toast.makeText(this, "Invalid e-mail address " + stringEditTextEmail, Toast.LENGTH_SHORT).show();
            }
        }




        // Insert into db
        if(error == 0){

            // Open db connection
            DBAdapter db = new DBAdapter(this);
            db.open();

            // Check if I exists, if I do.. then update
            String stringEditTextEmailSQL = db.quoteSmart(stringEditTextEmail);
            int numberRows = db.count("users");

            if(numberRows < 1){
                String inpFields = "NULL, " + stringEditTextEmailSQL;
                db.insert("users", "_id, user_email", inpFields);

            }
            else{
                long userID = 1;
                db.update("users", "_id", userID, "user_email", stringEditTextEmailSQL);

            }

            // Close db connection
            db.close();

            // Navigate
            signUpUserNameSelect();
        }


    } // signUpEmailValidate


    /*- 4: signUpUserNameSelect -------------------------------------------------- */
    public void signUpUserNameSelect(){

        // Set current step
        currentStepName = "signUpUserNameSelect";
        currentStepId   = 4;

        // Set headline
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(this.getResources().getString(R.string.what_is_your_desired_alias_question_mark));

        // Hide e-mail
        TableRow tableRowEmail = findViewById(R.id.tableRowEmail);
        tableRowEmail.setVisibility(View.GONE);

        // Show userName
        TableRow tableRowUserName = findViewById(R.id.tableRowUserName);
        tableRowUserName.setVisibility(View.VISIBLE);

        // Pointer to text field
        EditText editTextUserName = findViewById(R.id.editTextUserName);
        editTextUserName.requestFocus();

        // Clear json text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");


        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Check if I exists, if I do.. then update
        long rowId = 1;
        String fieldsUser[] = new String[] {  "_id", "user_name" };
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowId);
        int cursorUserCount = cursorUser.getCount();
        if(cursorUserCount == 1){
            // Fill with data from db
            editTextUserName.setText(cursorUser.getString(1));
        }

        // Close db
        db.close();
    }


    /*- 4A: User name pre validate  ------------------------------------------------ */
    public void signUpUserNamePreValidate() {
        // Email taken : Check that it is avaible at nettport.com

        // Text view JSON
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);

        // Get email
        EditText editTextUserName = findViewById(R.id.editTextUserName);
        String stringUserName = editTextUserName.getText().toString();

        // Call HTTP request
        String stringApiUrl = apiURL + "/check_user_name.php?user_name=" + stringUserName;
        String stringMethod = "get";
        String stringSend   = "";
        HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringApiUrl, stringMethod, stringSend, textViewDynamicText, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                signUpUsernamePreValidateCheck();
            }
        });
        task.execute();


    }
    /*- 4B: Username post pre validate  ------------------------------------------------ */
    public void signUpUsernamePreValidateCheck(){
        // Email taken : Check for answer
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        String stringDynamicText = textViewDynamicText.getText().toString();
        if (stringDynamicText.equals("User name is taken")) {
            Toast.makeText(this, "The user name already exists. Please select a new one.", Toast.LENGTH_SHORT).show();
        }
        else if (stringDynamicText.equals("User name is available")) {
            signUpUserNameValidate();
        }
        else if (stringDynamicText.equals("Missing variable user_name")) {
            Toast.makeText(this, "Please enter your alias", Toast.LENGTH_SHORT).show();
        }
        else if (stringDynamicText.equals("Timeout")) {
            Toast.makeText(this, "No connection to server. Continuing in offline mode.", Toast.LENGTH_SHORT).show();
            signUpUserNameValidate();
        }
        else if (stringDynamicText.equals("")) {
            Toast.makeText(this, "Offline mode activated", Toast.LENGTH_SHORT).show();
            signUpUserNameValidate();
        }
    }

    /*- 5: Alias validate  ------------------------------------------------ */
    public void signUpUserNameValidate(){
        // Set current step
        currentStepName = "signUpUserNameValidate";
        currentStepId   = 5;

        // Error?
        int error = 0;

        // Clear json text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");

        // Get editText
        EditText editTextUsername = findViewById(R.id.editTextUserName);
        String stringUsername = editTextUsername.getText().toString();
        if(stringUsername.equals("")){
            error = 1;
            Toast.makeText(this, "Please enter a user name", Toast.LENGTH_SHORT).show();
        }


        // Insert into db
        if(error == 0){

            // Open db connection
            DBAdapter db = new DBAdapter(this);
            db.open();

            // Check if I exists, if I do.. then update
            String stringUsernameSQL = db.quoteSmart(stringUsername);
            int numberRows = db.count("users");

            long userID = 1;
            db.update("users", "_id", userID, "user_name", stringUsernameSQL); // Username and alias are the same
            db.update("users", "_id", userID, "user_alias", stringUsernameSQL); // Username and alias are the same


            // Close db connection
            db.close();

            // Navigate
            signUpPasswordSelect();
        }


    } // signUpAliasValidate

    /*- 6: signUpPasswordSelect -------------------------------------------------- */
    public void signUpPasswordSelect(){
        // Set current step
        currentStepName = "signUpPasswordSelect";
        currentStepId   = 6;

        // Clear JSON
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");

        // Set headline
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(this.getResources().getString(R.string.wanted_password));

        // Remove user name
        TableRow tableRowUsername = findViewById(R.id.tableRowUserName);
        tableRowUsername.setVisibility(View.GONE);

        // Show password
        TableRow tableRowPassword = findViewById(R.id.tableRowPassword);
        tableRowPassword.setVisibility(View.VISIBLE);

        // Pointer password
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.requestFocus();

        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Check if I exists, if I do.. then update
        long rowId = 1;
        String fieldsUser[] = new String[] {  "_id", "user_password" };
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowId);
        int cursorUserCount = cursorUser.getCount();
        if(cursorUserCount == 1){
            // Fill with data from db
            editTextPassword.setText(cursorUser.getString(1));
        }

        // Close db
        db.close();

    }

    /*- 7: signUpPasswordValidate -------------------------------------------------- */
    public void signUpPasswordValidate(){
        // Set current step
        currentStepName = "signUpPasswordValidate";
        currentStepId   = 7;

        // Error?
        int error = 0;

        // Get editText
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        String stringPassword = editTextPassword.getText().toString();
        if(stringPassword.equals("")){
            error = 1;
            makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }


        // Update db
        if(error == 0){
            // Open db connection
            DBAdapter db = new DBAdapter(this);
            db.open();

            // Check if I exists, if I do.. then update
            String stringEditTextInpTextSQL = db.quoteSmart(stringPassword);
            long userID = 1;
            db.update("users", "_id", userID, "user_password", stringEditTextInpTextSQL);

            // Toast
            //Toast.makeText(this, "Password saved", Toast.LENGTH_SHORT).show();

            // Close db connection
            db.close();

            // Navigate
            signUpDateOfBirth();
        }
    } // signUpPasswordValidate

    /*- 8: signUpDateOfBirth -------------------------------------------------- */
    public void signUpDateOfBirth() {
        // Set current step
        currentStepName = "signUpDateOfBirth";
        currentStepId   = 8;

        // Clear json text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");

        // Set headline
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(this.getResources().getString(R.string.date_of_birth));

        // Hide password
        TableRow tableRowPassword = findViewById(R.id.tableRowPassword);
        tableRowPassword.setVisibility(View.GONE);

        // Show DOB
        TableRow tableRowDOBDay = findViewById(R.id.tableRowDOBDay);
        tableRowDOBDay.setVisibility(View.VISIBLE);

        TableRow tableRowDOBMonth = findViewById(R.id.tableRowDOBMonth);
        tableRowDOBMonth.setVisibility(View.VISIBLE);

        TableRow tableRowDOBYear = findViewById(R.id.tableRowDOBYear);
        tableRowDOBYear.setVisibility(View.VISIBLE);



        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Check if I exists, if I do.. then update
        long rowId = 1;
        String fieldsUser[] = new String[] {  "_id", "user_dob" };
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowId);
        String stringUserDob = cursorUser.getString(1);

        /* DOB */
        String stringUserDobYear = "";
        String stringUserDobMonth = "";
        String stringUserDobYDay = "";
        try {
            String[] items1 = stringUserDob.split("-");
            stringUserDobYear = items1[0];
            stringUserDobMonth = items1[1];
            stringUserDobYDay = items1[2];
        }
        catch(Exception e){
        }


        /* Fill numbers for date of birth days */
        int spinnerDOBDaySelectedIndex = 0;
        int human_counter = 0;
        for(int x=0;x<31;x++){
            human_counter=x+1;
            this.arraySpinnerDOBDay[x] = "" + human_counter;

            if(stringUserDobYDay.equals("0" + human_counter) || stringUserDobYDay.equals(""+human_counter)){
                spinnerDOBDaySelectedIndex = x;
                //Toast.makeText(getActivity(), "Day: " + stringUserDobYDay + " Index: " + spinnerDOBDaySelectedIndex, Toast.LENGTH_LONG).show();
            }
        }
        Spinner spinnerDOBDay = findViewById(R.id.spinnerDOBDay);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBDay);
        spinnerDOBDay.setAdapter(adapter);
        spinnerDOBDay.setSelection(spinnerDOBDaySelectedIndex); // Select index
        spinnerDOBDay.requestFocus();


        /* DOB: Month */
        arraySpinnerDOBMonth[0] = getResources().getString(R.string.january);
        arraySpinnerDOBMonth[1] = getResources().getString(R.string.february);
        arraySpinnerDOBMonth[2] = getResources().getString(R.string.march);
        arraySpinnerDOBMonth[3] = getResources().getString(R.string.april);
        arraySpinnerDOBMonth[4] = getResources().getString(R.string.may);
        arraySpinnerDOBMonth[5] = getResources().getString(R.string.june);
        arraySpinnerDOBMonth[6] = getResources().getString(R.string.july);
        arraySpinnerDOBMonth[7] = getResources().getString(R.string.august);
        arraySpinnerDOBMonth[8] = getResources().getString(R.string.september);
        arraySpinnerDOBMonth[9] = getResources().getString(R.string.november);
        arraySpinnerDOBMonth[10] = getResources().getString(R.string.october);
        arraySpinnerDOBMonth[11] = getResources().getString(R.string.december);


        int intUserDobMonth = 0;
        stringUserDobYDay.replace("0", "");
        try {
            intUserDobMonth = Integer.parseInt(stringUserDobMonth);
            intUserDobMonth = intUserDobMonth-1;
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        Spinner spinnerDOBMonth = findViewById(R.id.spinnerDOBMonth);

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBMonth);
        spinnerDOBMonth.setAdapter(adapterMonth);
        spinnerDOBDay.setSelection(intUserDobMonth); // Select index



        /* Fill numbers for date of birth year */
        // get current year?month and day
        int spinnerDOBYearSelectedIndex = 0;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int end = year-100;
        int index = 0;
        for(int x=year;x>end;x--){
            this.arraySpinnerDOBYear[index] = "" + x;
            // Toast.makeText(this, "x = " + x, Toast.LENGTH_SHORT).show();

            try {
                if (stringUserDobYear.equals("" + x)) {
                    spinnerDOBYearSelectedIndex = index;
                    //Toast.makeText(getActivity(), "Year: " + x + " Index: " + spinnerDOBYearSelectedIndex, Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e){

            }
            index++;
        }

        Spinner spinnerDOBYear = findViewById(R.id.spinnerDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBYear);
        spinnerDOBYear.setAdapter(adapterYear);
        spinnerDOBYear.setSelection(spinnerDOBYearSelectedIndex); // Select index


        // Close db
        db.close();

    } // signUpDateOfBirth


    /*- 9: signUpDateOfBirthValidate -------------------------------------------------- */
    public void signUpDateOfBirthValidate(){
        // Set current step
        currentStepName = "signUpDateOfBirthValidate";
        currentStepId   = 9;

        // Error
        int error = 0;


        // Date of Birth Day
        Spinner spinnerDOBDay = findViewById(R.id.spinnerDOBDay);
        String stringDOBDay = spinnerDOBDay.getSelectedItem().toString();
        int intDOBDay = 0;
        try {
            intDOBDay = Integer.parseInt(stringDOBDay);

            if(intDOBDay < 10){
                stringDOBDay = "0" + stringDOBDay;
            }

        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            makeText(this, "Please select a day for your birthday.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Date of Birth Month
        String stringDOBMonth = "";
        if(error == 0) {
            Spinner spinnerDOBMonth = findViewById(R.id.spinnerDOBMonth);
            stringDOBMonth = spinnerDOBMonth.getSelectedItem().toString();
            int positionDOBMonth = spinnerDOBMonth.getSelectedItemPosition();
            int month = positionDOBMonth + 1;
            if (month < 10) {
                stringDOBMonth = "0" + month;
            } else {
                stringDOBMonth = "" + month;
            }
        }

        // Date of Birth Year
        int intDOBYear = 0;
        if(error == 0) {
            Spinner spinnerDOBYear = findViewById(R.id.spinnerDOBYear);
            String stringDOBYear = spinnerDOBYear.getSelectedItem().toString();
            try {
                intDOBYear = Integer.parseInt(stringDOBYear);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
                makeText(this, "Please select a year for your birthday.", Toast.LENGTH_SHORT).show();
            }
        }


        // Insert to SQL
        if(error == 0){
            // Open db connection
            DBAdapter db = new DBAdapter(this);
            db.open();

            // Put date of birth togheter
            String dateOfBirth = intDOBYear + "-" + stringDOBMonth + "-" + stringDOBDay;
            String dateOfBirthSQL = db.quoteSmart(dateOfBirth);
            long userID = 1;
            db.update("users", "_id", userID, "user_dob", dateOfBirthSQL);

            // Close db connection
            db.close();

            // Navigate
            signUpGender();
        }
    } // signUpDateOfBirthValidate

    /*- 10: signUpGender ------------------------------------------------------ */
    public void signUpGender(){
        // Set current step
        currentStepName = "signUpGender";
        currentStepId   = 10;

        // Set headline
        TextView textViewHeadline = findViewById(R.id.textViewHeadline);
        textViewHeadline.setText(this.getResources().getString(R.string.gender));

        // Hide DOB
        TableRow tableRowDOBDay = findViewById(R.id.tableRowDOBDay);
        tableRowDOBDay.setVisibility(View.GONE);

        TableRow tableRowDOBMonth = findViewById(R.id.tableRowDOBMonth);
        tableRowDOBMonth.setVisibility(View.GONE);

        TableRow tableRowDOBYear = findViewById(R.id.tableRowDOBYear);
        tableRowDOBYear.setVisibility(View.GONE);

        // Show Gender
        TableRow tableRowGender = findViewById(R.id.tableRowGender);
        tableRowGender.setVisibility(View.VISIBLE);

        // Pointer radio buttons
        RadioButton radioButtonInpA = findViewById(R.id.radioButtonInpA);
        RadioButton radioButtonInpB = findViewById(R.id.radioButtonInpB);
        radioButtonInpA.requestFocus();


        // Select
        DBAdapter db = new DBAdapter(this);
        db.open();

        long rowId = 1;
        String fieldsUser[] = new String[] {  "_id", "user_gender" };
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowId);
        String stringUserGender = "";
        stringUserGender = cursorUser.getString(1);
        try{
            if(stringUserGender.startsWith("m")){
                radioButtonInpA.setChecked(true);
                radioButtonInpB.setChecked(false);
            }
            else{
                radioButtonInpA.setChecked(false);
                radioButtonInpB.setChecked(true);
            }
        }
        catch (Exception e){

        }
        db.close();


    } // signUpGender

    /*- 11: signUpGenderValidate ------------------------------------------------------ */
    public void signUpGenderValidate(){
        // Set current step
        currentStepName = "signUpGenderValidate";
        currentStepId   = 11;

        // Gender
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupInpGroup);
        int radioButtonID = radioGroupGender.getCheckedRadioButtonId(); // get selected radio button from radioGroup
        View radioButtonGender = radioGroupGender.findViewById(radioButtonID);
        int position = radioGroupGender.indexOfChild(radioButtonGender); // If you want position of Radiobutton

        String stringGender = "";
        if(position == 0){
            stringGender = "male";
        }
        else{
            stringGender = "female";
        }

        // Open db connection
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Put date of birth togheter
        String stringGenderSQL = db.quoteSmart(stringGender);
        long userID = 1;
        db.update("users", "_id", userID, "user_gender", stringGenderSQL);

        // Close db connection
        db.close();

        // Navigate
        signUpComplete();
    } // signUpGenderValidate

    /*- signUpComplete ------------------------------------------------------ */
    public void signUpComplete(){

        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Row
        long rowID = 1;


        String fieldsUser[] = new String[] { "_id", "user_id", "user_email",
                "user_alias", "user_password", "user_salt",
                "user_language", "user_gender", "user_measurement",
                "user_dob", "user_registered"  };
        Cursor cursorUser = db.select("users", fieldsUser, "_id", rowID);
        String stringUser_Id = cursorUser.getString(0);
        String stringUserId = cursorUser.getString(1);
        String stringUserEmail = cursorUser.getString(2);
        String stringUserAlias = cursorUser.getString(3);
        String stringUserPassword = cursorUser.getString(4);
        String stringUserSalt = cursorUser.getString(5);
        String stringUserLanguage = cursorUser.getString(6);
        String stringUserGender  = cursorUser.getString(7);
        String stringUserMeasurement  = cursorUser.getString(8);
        String stringUserDob = cursorUser.getString(9);
        String stringUserRegistered = cursorUser.getString(10);


        // Encrypt password
        String stringUserPasswordEncrypted = encryptPassword(stringUserPassword);
        String stringUserPasswordEncryptedSQL = db.quoteSmart(stringUserPasswordEncrypted);
        db.update("users", "_id", rowID, "user_password", stringUserPasswordEncryptedSQL);

        // Generate salt
        stringUserSalt = getRandomString(6);
        String stringUserSaltSQL = db.quoteSmart(stringUserSalt);
        db.update("users", "_id", rowID, "user_salt", stringUserSaltSQL);

        // Registered
        DateFormat dfY = new SimpleDateFormat("yyyy-MM-dd");//format date
        stringUserRegistered = dfY.format(Calendar.getInstance().getTime());
        String stringUserRegisteredSQL = db.quoteSmart(stringUserRegistered);
        db.update("users", "_id", rowID, "user_registered", stringUserRegisteredSQL);

        // Database: close
        db.close();


        // Dynamic text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        textViewDynamicText.setText("");


        // Send to PHP
        // Device
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName = manufacturer + " " + model;


        // Goal: Call HTTP request
        String stringURL    = apiURL + "/post_registration_user_receive.php";
        String stringMethod = "post";

        Map<String, String> data = new HashMap<String, String>();
        data.put("inp_api_password", apiPassword);
        data.put("inp_user_email", stringUserEmail);
        data.put("inp_user_password", stringUserPasswordEncrypted);
        data.put("inp_user_salt", stringUserSalt);
        data.put("inp_user_alias", stringUserAlias);
        data.put("inp_user_dob", stringUserDob);
        data.put("inp_user_gender", stringUserGender);
        data.put("inp_user_registered", stringUserRegistered);
        data.put("inp_user_language", stringUserLanguage);
        data.put("inp_user_measurement", stringUserMeasurement);
        data.put("inp_device_source", "Fitness Life Calories Counter for Android");
        data.put("inp_device_id", encryptPassword(androidId));
        data.put("inp_device_name", deviceName);

        HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringURL, stringMethod, data, textViewDynamicText, new HttpRequestLongOperation.TaskListener() {
            @Override
            public void onFinished(String result) {
                // Do Something after the task has finished
                sendUserToPHPPost();
            }
        });
        task.execute();

    } // signUpComplete

    /* - Get User ID from PHP --------------------------------------------------------- */
    public void sendUserToPHPPost(){

        // Set loading text to next button
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setText("Loading");
        buttonNext.setBackgroundResource(R.drawable.btn_warning_normal);


        // Database
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Text view Dynamic text
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        String stringDynamicText = textViewDynamicText.getText().toString();
        String stringUserIDSQL = db.quoteSmart(stringDynamicText);

        // Synchronized user
        DateFormat dfY = new SimpleDateFormat("yyyy-MM-dd");
        String userSynchronized = dfY.format(Calendar.getInstance().getTime());
        String userSynchronizedSQL = db.quoteSmart(userSynchronized);

        // Update user
        long longUser_id = 1;
        db.update("users", "_id", longUser_id, "user_id", stringUserIDSQL);
        db.update("users", "_id", longUser_id, "user_synchronized", userSynchronizedSQL);

        // Database: close
        db.close();

        // Send user to MainActivity
        Intent i = new Intent(UserSignUpActivity.this, CheckAppSynchronizationActivity.class);
        i.putExtra("newOrOldUser", "new_user");
        startActivity(i);
        finish();
    }


    /*- Get random string ----------------------------------------------------- */
    private static String getRandomString(final int sizeOfRandomString)
    {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    } // getRandomString

    /*- Encrypt password ------------------------------------------------------------- */
    private static String encryptPassword(String password) {
        MessageDigest crypt;

        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            try{
                crypt.update(password.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException uee){
                return uee.toString();
            }
        }
        catch(NoSuchAlgorithmException nsae){
            return nsae.toString();
        }

        return new BigInteger(1, crypt.digest()).toString(16);
    }
}