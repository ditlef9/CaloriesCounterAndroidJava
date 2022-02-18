package net.frindex.caloriescounterandroidjava.food_diary_goal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
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
public class MyProfileActivity extends AppCompatActivity {


    // Class variables
    MatrixCursor matrixCursor;
    String currentLanguage = "";

    // My user
    int myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Set the language
        setCurrentLanguage();

        // Toolbar
        initializeToolbar();

        // Menu
        createMenu();
    }

    public void setCurrentLanguage() {

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Lang
        currentLanguage = "en";
        String queryUser = "SELECT user_language FROM users WHERE _id='1'";
        Cursor cursorUser = db.rawQuery(queryUser);
        int cursorUserCount = cursorUser.getCount();
        if (cursorUserCount != 0) {
            currentLanguage = cursorUser.getString(0);
        }


        db.close();
    }


    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar() {
        /* Toolbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.my_profile));

        // Back icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Show
        actionBar.show();


    } // initializeToolbar

    /* One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Up button
        Intent i = new Intent(MyProfileActivity.this, MainActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }

    /*- Create menu ---------------------------------------------------------------------- */
    public void createMenu() {

        // Colums
        String[] columns = new String[]{"_id", "name", "icon"};

        // Cursor
        matrixCursor = new MatrixCursor(columns);

        matrixCursor.addRow(new Object[]{1, getResources().getString(R.string.height), "ic_outline_straighten_black_24px"});
        matrixCursor.addRow(new Object[]{2, getResources().getString(R.string.gender), "ic_wc_black_24px"});
        matrixCursor.addRow(new Object[]{3, getResources().getString(R.string.date_of_birth), "ic_outline_cake_black_24px"});

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);

        // Setup cursor adapter using cursor from last step
        MyProfileCursorAdapter menuAdapter = new MyProfileCursorAdapter(this, matrixCursor);

        // Attach cursor adapter to the ListView
        try {
            lvItems.setAdapter(menuAdapter); // uses ContinensCursorAdapter
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                listItemClicked(arg2);
            }
        });
    }


    /*- Recipe List Item Clicked ------------------------------------------------------ */
    public void listItemClicked(int listItemIDClicked) {
        matrixCursor.moveToPosition(listItemIDClicked);
        String id = matrixCursor.getString(0);

        // Go to that URL
        if (id.equals("1")) {
            Intent i = new Intent(MyProfileActivity.this, MyProfileHeightActivity.class);
            startActivity(i);
        }
        else if (id.equals("2")) {
            Intent i = new Intent(MyProfileActivity.this, MyProfileGenderActivity.class);
            startActivity(i);
        }
        else if (id.equals("3")) {
            Intent i = new Intent(MyProfileActivity.this, MyProfileDobActivity.class);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "Unknown id " + id, Toast.LENGTH_LONG).show();
        }
    } // recipesListItemClicked


}