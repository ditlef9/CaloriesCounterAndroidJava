package net.frindex.caloriescounterandroidjava.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.user.UserLoginActivity;
import net.frindex.caloriescounterandroidjava.user.UserSignUpActivity;

public class SetupCPermissionsCheckerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_cpermissions_checker);
        // Permission
        checkPermissionRead();
        checkPermissionWrite();

        // Insert languages
        nextButtonListener();
    }


    /*- Check permission Read ---------------------------------------------------------- */
    // Pops up message to user for reading
    private void checkPermissionRead(){
        int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    } // checkPermissionRead

    /*- Check permission Write ---------------------------------------------------------- */
    // Pops up message to user for writing
    private void checkPermissionWrite(){
        int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    } // checkPermissionWrite


    /*- Next button Listener --------------------------------------------------- */
    public void nextButtonListener(){

        // New User
        Button buttonCreateNewUser = findViewById(R.id.buttonCreateNewUser);

        buttonCreateNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCreateNewUserOnClick();
            }
        });

        // Login
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLoginOnClick();
            }
        });

    }

    public void buttonCreateNewUserOnClick(){
        // Move
        Intent i = new Intent(SetupCPermissionsCheckerActivity.this, UserSignUpActivity.class);
        startActivity(i);
        finish();
    }
    public void buttonLoginOnClick(){
        // Move
        Intent i = new Intent(SetupCPermissionsCheckerActivity.this, UserLoginActivity.class);
        startActivity(i);
        finish();
    }
}