package net.frindex.caloriescounterandroidjava.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.common.WriteToErrorLog;
import net.frindex.caloriescounterandroidjava.synchronize_app.SynchronizeAFoodCategories;

public class SetupBPermissionsActivity extends AppCompatActivity {

    String currentLanguage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_bpermissions);

        // Language
        Bundle b = getIntent().getExtras();
        currentLanguage = b.getString("currentLanguage");


        // Insert languages
        nextButtonListener();

        // Synchronize before register/login
        synchronizeBeforeRegisterLogin();
    }

    /*- Next button Listener -------------------------------------------------------------------- */
    public void nextButtonListener(){

        // Find ListView to populate
        Button buttonPermissionsSubmit = findViewById(R.id.buttonPermissionsSubmit);


        // OnClick
        buttonPermissionsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPermissionsSubmitOnClick();
            }
        });
    } // populateLanguagesList

    public void buttonPermissionsSubmitOnClick(){


        // Move
        Intent i = new Intent(SetupBPermissionsActivity.this, SetupCPermissionsCheckerActivity.class);
        startActivity(i);
        finish();


    } // listItemClicked

    /*- Synchronize before register login ------------------------------------------------------ */
    private void synchronizeBeforeRegisterLogin() {

        WriteToErrorLog write = new WriteToErrorLog(this);
        write.writeToErrorLog("SetupBPermissionsActivity", "synchronizeBeforeRegisterLogin", "Started synchronization", "info");

        SynchronizeAFoodCategories s = new SynchronizeAFoodCategories(this, currentLanguage);
        s.updateLastSynchronizedDate();
    } // synchronizeBeforeRegisterLogin
}