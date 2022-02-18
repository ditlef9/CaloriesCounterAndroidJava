package net.frindex.caloriescounterandroidjava.food_diary_add;
/**
 *
 * File: FoodDiaryAddBarcodeActivity.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.frindex.caloriescounterandroidjava.R;

public class FoodDiaryAddBarcodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_add_barcode);


        // Listeners
        listeners();
    }

    private void listeners() {

        // Navigation
        Button scanBtn = (Button)findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonScanClicked();
            }
        });


    }
    public void buttonScanClicked(){

    } // buttonScanClicked
}