package net.frindex.caloriescounterandroidjava.user;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.http.HttpRequestLongOperation;

import java.util.HashMap;
import java.util.Map;

public class UserForgotPasswordActivity extends AppCompatActivity {

    /* Api variables */
    String apiURL = "https://summerslim.codecourses.eu/users/api"; // Without ending slash
    String apiPassword  = "w7Vdwenb";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);

        /* Toolbar */
        toolbarTitle();
        /* Send password button listener */
        Button buttonSendNewPassword = findViewById(R.id.buttonSendNewPassword);
        buttonSendNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewPasswordClicked();
            }
        });
    }

    /*- Toolbar Title ---------------------------------------------------------- */
    // Makes changes to the toolbar
    public void toolbarTitle(){
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.forgot_password_question_mark));
        actionBar.show();
    } // toolbarTitle


    /*- Send new password ------------------------------------------------------------ */
    public void sendNewPasswordClicked(){
        // Get e-mail
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        String stringEmail = editTextEmail.getText().toString();

        if(stringEmail.isEmpty()){
            Toast.makeText(this, "Please enter your e-mail address", Toast.LENGTH_SHORT).show();
        }
        else{
            // Create a new password
            // String stringPasswordReplacement = getRandomString(10);

            // Send to PHP
            TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
            String stringUrl = apiURL + "/send_new_password.php";
            String stringMethod = "post";

            Map<String, String> data = new HashMap<String, String>();
            data.put("user_email", stringEmail);

            HttpRequestLongOperation task = new HttpRequestLongOperation(this, stringUrl, stringMethod, data, textViewDynamicText, new HttpRequestLongOperation.TaskListener() {
                @Override
                public void onFinished(String result) {
                    // Do Something after the task has finished
                    loginClickedCheckUserEmailAgainstPHP();
                }
            });
            task.execute();


        }


    } // sendNewPasswordClicked


    public void loginClickedCheckUserEmailAgainstPHP(){
        TextView textViewDynamicText = findViewById(R.id.textViewDynamicText);
        String stringJSON = textViewDynamicText.getText().toString();


        if(stringJSON.equals("E-mail not found")){
            Toast.makeText(this, "E-mail not found", Toast.LENGTH_SHORT).show();
        }
        else if(stringJSON.equals("New password sent")){
            Toast.makeText(this, "New password sent, please check your inbox.", Toast.LENGTH_SHORT).show();

            // Go to login
            Intent i = new Intent(UserForgotPasswordActivity.this, UserLoginActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(this, "" + stringJSON, Toast.LENGTH_SHORT).show();
        }
    }
}