package net.frindex.caloriescounterandroidjava.synchronize_user_data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.http.HttpRequestImageDownloadToCache;

import java.io.File;

public class CheckUserSynchronizationActivity extends AppCompatActivity {

    /* Api variables */
    String websiteURL = "https://summerslim.codecourses.eu";


    String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_synchronization);

        // Set user data
        setUserDataToDesign();

        // Synchronize
        startSynchronizationOfWorkoutDiaryPlansAndEntries();

        // Continue button listener
        continueButtonListener();
    }

    private void setUserDataToDesign() {
        DBAdapter db = new DBAdapter(this);
        db.open();
        String q = "SELECT _id, user_id, user_name FROM users WHERE _id='1'";
        Cursor userCursor = db.rawQuery(q);
        int userCursorSize = userCursor.getCount();

        String _id = userCursor.getString(0);
        String userId = userCursor.getString(1);
        String userName = userCursor.getString(2);

        // Alias
        TextView textViewUserName = (TextView)findViewById(R.id.textViewUserName);
        textViewUserName.setText(userName);


        // Img
        q = "SELECT _id, photo_id, photo_destination FROM users_profile_photo WHERE photo_user_id='" + userId + "' AND photo_profile_image='1'";
        Cursor userPhotoCursor = db.rawQuery(q);

        int userPhotoCursorSize = userPhotoCursor.getCount();
        if(userPhotoCursorSize != 0){
            String photoDestination = userPhotoCursor.getString(2);


            ImageView imageViewUserImage = (ImageView)findViewById(R.id.imageViewUserImage);

            if(photoDestination != null) {
                if (!(photoDestination.equals(""))) {

                    File file = new File(this.getCacheDir() + "" + File.separatorChar + photoDestination);

                    if (file.exists ()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imageViewUserImage.setImageBitmap(myBitmap);
                    }
                    else{
                        String imageURL = websiteURL + "/_uploads/users/images/" + userId + "/" + photoDestination;
                        String imageName = photoDestination;
                        String storeDirectory = "users";
                        new HttpRequestImageDownloadToCache(this, imageURL, imageName, imageViewUserImage).execute();


                    }

                }
            }

        } // userPhotoCursorSize != 0

        db.close();
    } // setUserDataToDesign

    private void startSynchronizationOfWorkoutDiaryPlansAndEntries() {

        // We can now synchronize Workout Diary Plans
        // But we can only start synchronization AFTER the other synchronization is finished,
        // if we don't wait - then the app will be slow


        // Check when finished
        checkIfSynchronizationOfWorkoutDiaryPlansAndEntriesIsFinished();

    }
    private void checkIfSynchronizationOfWorkoutDiaryPlansAndEntriesIsFinished() {

        DBAdapter db = new DBAdapter(this);
        db.open();


        String q = "SELECT _id, name, last_on_local, last_on_server, synchronized_week FROM synchronize WHERE name='food_diary_goals'";
        Cursor cursorMuscles = db.rawQuery(q);
        int size = cursorMuscles.getCount();
        if (size == 0) {
            // Still loading
            TextView textViewStatus = findViewById(R.id.textViewStatus);
            textViewStatus.setText("." + textViewStatus.getText().toString() + ".");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfSynchronizationOfWorkoutDiaryPlansAndEntriesIsFinished();
                }
            }, 500);

        }
        else {

            Toast.makeText(this, "Personal data synchronized!", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(CheckUserSynchronizationActivity.this, MainActivity.class);
            startActivity(i);
            finish();

        }

        db.close();

    }


    private void continueButtonListener() {
        /* Forgot password listener */
        Button buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonContinueClicked();
            }
        });
    }
    private void buttonContinueClicked(){
        Intent i = new Intent(CheckUserSynchronizationActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}