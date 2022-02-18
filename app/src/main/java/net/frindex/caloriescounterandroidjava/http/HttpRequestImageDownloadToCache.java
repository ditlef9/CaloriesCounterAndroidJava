package net.frindex.caloriescounterandroidjava.http;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.dao.DBAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * File: HttpRequestImageDownloadToCache.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */

public class HttpRequestImageDownloadToCache extends AsyncTask<Void, Void, Bitmap> {

    private Context context;
    private String imageURL;
    private String imageName;

    // The variables under are to load the image when it is completed
    private Resources resources;
    private LinearLayout linearLayout;
    private ImageView imageView;


    public HttpRequestImageDownloadToCache(Context ctx, String imageURL, String imageName) {
        this.context = ctx;
        this.imageURL = imageURL;
        this.imageName = imageName;
    }

    public HttpRequestImageDownloadToCache(Context ctx, String imageURL, String imageName, LinearLayout linearLayout) {
        this.context = ctx;
        this.imageURL = imageURL;
        this.imageName = imageName;

        this.linearLayout = linearLayout;
    }

    public HttpRequestImageDownloadToCache(Context ctx, String imageURL, String imageName, ImageView imageView) {
        this.context = ctx;
        this.imageURL = imageURL;
        this.imageName = imageName;

        this.imageView = imageView;
    }


    public interface TaskListener {
        void onFinished(String result);
    }


    @Override
    protected Bitmap doInBackground(Void... params) {

        try {
            URL urlConnection = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            saveImage(myBitmap);

            return myBitmap;
        } catch(Exception e){
            e.printStackTrace();

            // Dynamic Text
            /*
            DBAdapter db = new DBAdapter(context);
            db.open();
            String sql = "INSERT INTO json_temp_data (_id, day, data) VALUES (NULL, '1', 'Error image: "  + imageName + " " + e.toString() + "')";
            db.rawQuery(sql);
            db.close();*/
        }

        return null;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        if(imageView != null) {
            imageView.setImageBitmap(result);
        }
        else {
            if (linearLayout != null) {
                BitmapDrawable background = new BitmapDrawable(resources, result);
                linearLayout.setBackground(background);
            }
        }


        // Finished with download, do nothing

    }


    private void saveImage(Bitmap finalBitmap) {

        // Make Folder
        File dir = new File(context.getCacheDir() + "");
        if (!(dir.exists())) {
            dir.mkdir();
        }

        // Ready File
        File file = new File(context.getCacheDir() + "" + File.separatorChar + imageName);

        // Write to SQL
        DBAdapter db = new DBAdapter(context);
        db.open();
        String sql = "INSERT INTO json_temp_data (_id, day, data) VALUES (NULL, '0', 'Saving to " + context.getCacheDir() + imageName + "')";
        db.rawQuery(sql);


        if(imageName == null) {
            // Dynamic Text
            sql = "INSERT INTO json_temp_data (_id, day, data) VALUES (NULL, '0', 'imageName is null')";
            db.rawQuery(sql);
        }
        else{
            // if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                // Dynamic Text
                db.open();
                sql = "INSERT INTO json_temp_data (_id, day, data) VALUES (NULL, '0', 'Cant save image: " + imageURL + " Error: " + e.toString() + "')";
                db.rawQuery(sql);

                Toast.makeText(context, "Cant save image\n" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        db.close();
    } // saveImage


}
