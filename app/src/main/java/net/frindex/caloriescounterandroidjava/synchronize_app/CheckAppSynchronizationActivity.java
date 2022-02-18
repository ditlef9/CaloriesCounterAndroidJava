package net.frindex.caloriescounterandroidjava.synchronize_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.synchronize_user_data.CheckUserSynchronizationActivity;
import net.frindex.caloriescounterandroidjava.synchronize_user_data.SynchronizeIFoodDiaryGoals;

public class CheckAppSynchronizationActivity extends AppCompatActivity {

    String newOrOldUser = "new_user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_app_synchronization);


        // New or old user
        Bundle b = getIntent().getExtras();
        newOrOldUser = b.getString("newOrOldUser");



        // Start synchronization check process
        checkIfSynchronizationIsFinished();
    }

    /*- Check if synchronization is finished ---------------------------------------------------- */
    /**
     * This is what needs to be synchronized by now:
     * SynchronizeAFood
     * SynchronizeB
     * SynchronizeC
     * SynchronizeD
     * SynchronizeE
     */
    private void checkIfSynchronizationIsFinished() {
        // Check muscles
        DBAdapter db = new DBAdapter(this);
        db.open();


        String q = "SELECT _id, name, last_on_local, last_on_server, synchronized_week FROM synchronize WHERE name='food_stores'";
        Cursor cursorMuscles = db.rawQuery(q);
        int size = cursorMuscles.getCount();
        int lastOnLocal = 0;
        int lastOnServer = 0;
        if (size == 0) {
            // Still loading
            TextView textViewStatus = findViewById(R.id.textViewStatus);
            textViewStatus.setText("<" + textViewStatus.getText().toString() + ">");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfSynchronizationIsFinished();
                }
            }, 500);

        }
        else {
            lastOnLocal = cursorMuscles.getInt(2);
            lastOnServer = cursorMuscles.getInt(3);

            if(lastOnLocal >= lastOnServer && lastOnLocal != 0){

                // Move to
                goToMainOrUserSync();
            }
            else{
                // Still loading
                TextView textViewStatus = findViewById(R.id.textViewStatus);
                textViewStatus.setText(textViewStatus.getText().toString() + ".");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkIfSynchronizationIsFinished();
                    }
                }, 500);
            }
        }

        db.close();

    }

    public void goToMainOrUserSync(){
        // We need to know if it is a new user or a old user
        // This is a new user and has nothing stored,
        // We can move the person directly to find plan

        if(newOrOldUser.equals("new_user")){
            Toast.makeText(this, "Synchronization finished", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(CheckAppSynchronizationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else if(newOrOldUser.equals("old_user")) {
            Toast.makeText(this, "Now synchronizing personal data", Toast.LENGTH_SHORT).show();
            SynchronizeIFoodDiaryGoals sync = new SynchronizeIFoodDiaryGoals(this);
            sync.updateLastSynchronizedDate();

            Intent i = new Intent(CheckAppSynchronizationActivity.this, CheckUserSynchronizationActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(CheckAppSynchronizationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}