package net.frindex.caloriescounterandroidjava.menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.MainActivity;
import net.frindex.caloriescounterandroidjava.R;
import net.frindex.caloriescounterandroidjava.dao.DBAdapter;
import net.frindex.caloriescounterandroidjava.food.FoodAMainCategoriesActivity;
import net.frindex.caloriescounterandroidjava.food_diary_goal.MyGoalsActivity;
import net.frindex.caloriescounterandroidjava.food_diary_goal.MyProfileActivity;
import net.frindex.caloriescounterandroidjava.meal_plans.MealPlansActivity;
import net.frindex.caloriescounterandroidjava.recipes.RecipesAMainCategoriesActivity;

/**
 *
 * File: FoodBSubCategoriesActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class MenuActivity extends AppCompatActivity {

    // Class variables
    MatrixCursor matrixCursor;
    String currentLanguage = "";

    // My user
    int myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Set the language
        setCurrentLanguage();

        // Toolbar
        initializeToolbar();

        // Menu
        createMenu();

        // Find my user id
        findMyUserId();
    }


    public void setCurrentLanguage(){

        /* Database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Lang
        currentLanguage = "en";
        String queryUser = "SELECT user_language FROM users WHERE _id='1'";
        Cursor cursorUser = db.rawQuery(queryUser);
        int cursorUserCount = cursorUser.getCount();
        if(cursorUserCount != 0){
            currentLanguage = cursorUser.getString(0);
        }


        db.close();
    }


    /*- Toolbar ----------------------------------------------------------------------------- */
    public void initializeToolbar(){
        // Get toolbar
        ActionBar actionBar = getSupportActionBar();

        // Title
        actionBar.setTitle(getResources().getString(R.string.menu));

        // Show
        actionBar.show();
    } // initializeToolbar

    /*- On Create Options Menu ----------------------------------------------------------- */
    /* @About The menu on the toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    /*- On Options Item Selected --------------------------------------------------------- */
    /* @About One of the toolbar icons was clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.navigationClose) {
            // Navigate home
            Intent i = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*- Create menu ---------------------------------------------------------------------- */
    public void createMenu(){

        // Colums
        String[] columns = new String[] { "_id", "name", "icon" };

        // Cursor
        matrixCursor = new MatrixCursor(columns);

        matrixCursor.addRow(new Object[] { 1, getResources().getString(R.string.food), "ic_outline_local_dining_black_24px" });
        matrixCursor.addRow(new Object[] { 2, getResources().getString(R.string.recipes), "ic_outline_local_cafe_black_24px" });
        matrixCursor.addRow(new Object[] { 3, getResources().getString(R.string.meal_plans), "ic_outline_local_mall_black_24px" });
        matrixCursor.addRow(new Object[] { 4, getResources().getString(R.string.my_profile), "ic_person_black_24px" });
        matrixCursor.addRow(new Object[] { 5, getResources().getString(R.string.my_goal), "ic_outline_adjust_black_24px" });
        matrixCursor.addRow(new Object[] { 6, getResources().getString(R.string.synchronize), "ic_baseline_sync_black_24px" });
        matrixCursor.addRow(new Object[] { 7, getResources().getString(R.string.visit_website), "ic_outline_home_black_24px" });
        matrixCursor.addRow(new Object[] { 8, getResources().getString(R.string.discuss_on) + " Summer Slim", "ic_outline_forum_black_24px" });

        // Find ListView to populate
        ListView lvItems = findViewById(R.id.listView);

        // Setup cursor adapter using cursor from last step
        MenuActivityCursorAdapter menuAdapter = new MenuActivityCursorAdapter(this, matrixCursor);

        // Attach cursor adapter to the ListView
        try{
            lvItems.setAdapter(menuAdapter); // uses ContinensCursorAdapter
        }
        catch (Exception e){
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
        if(id.equals("1")) {
            Intent i = new Intent(MenuActivity.this, FoodAMainCategoriesActivity.class);
            startActivity(i);
            finish();
        }
        else if(id.equals("2")) {
            Intent i = new Intent(MenuActivity.this, RecipesAMainCategoriesActivity.class);
            startActivity(i);
            finish();
        }
        else if(id.equals("3")) {
            Intent i = new Intent(MenuActivity.this, MealPlansActivity.class);
            startActivity(i);
            finish();
        }
        else if(id.equals("4")) {
            Intent i = new Intent(MenuActivity.this, MyProfileActivity.class);
            startActivity(i);
            finish();
        }
        else if(id.equals("5")) {
            Intent i = new Intent(MenuActivity.this, MyGoalsActivity.class);
            startActivity(i);
            finish();
        }
        else if(id.equals("6")) {
            // Synchronization
            synchronize();
        }
        else if(id.equals("7")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/index.php?l=" + currentLanguage));
            startActivity(intent);
            finish();
        }
        else if(id.equals("8")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://summerslim.codecourses.eu/discuss/index.php?l=" + currentLanguage));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Unknown id " + id, Toast.LENGTH_LONG).show();
        }
    } // recipesListItemClicked


    /*- Find my user id ---------------------------------------------------------------------- */
    public void findMyUserId(){
        DBAdapter db = new DBAdapter(this);
        db.open();
        String q = "SELECT user_id FROM users WHERE _id='1'";
        Cursor userCursor = db.rawQuery(q);
        myUserId = userCursor.getInt(0);
        db.close();

    }

    /*- Synchronize ---------------------------------------------------------------------------- */
    private void synchronize() {
        Toast.makeText(this, "Starting Synchronization..", Toast.LENGTH_SHORT).show();

        // Delete synchronization status (user want to synchronize everything)
        DBAdapter db = new DBAdapter(this);
        db.open();
        String q = "UPDATE synchronize SET last_on_local='0'";
        db.rawQuery(q);
        db.close();

        /*
        SynchronizeAFoodCategories synchronizeAFoodCategories = new SynchronizeAFoodCategories(this, currentLanguage);
        synchronizeAFoodCategories.updateLastSynchronizedDate();

        SynchronizeIFoodDiaryGoals synchronizeIFoodDiaryGoals = new SynchronizeIFoodDiaryGoals(this);
        synchronizeIFoodDiaryGoals.updateLastSynchronizedDate();
         */

    }
}