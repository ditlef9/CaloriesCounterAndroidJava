package net.frindex.caloriescounterandroidjava.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import net.frindex.caloriescounterandroidjava.dao.DBAdapter;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * File: HttpRequestLongOperation.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class HttpRequestLongOperation  extends AsyncTask<String, Void, String> {
    private Context context;
    private String inputUrl;
    private String stringMethod;
    private String stringSend = "";
    private String stringImg = "";
    private Map<String, String> mapSend  = new HashMap<String, String>();
    private TextView textViewJSON;
    private final TaskListener taskListener; // This is the reference to the associated listener


    public interface TaskListener {
        void onFinished(String result);
    }

    /*- Constructor GET, SEND --------------------------------------------------------------- */
    public HttpRequestLongOperation(Context ctx, String url, String method, String send, TextView dynamicTextView, TaskListener listener) {
        context = ctx;
        inputUrl = url;
        stringMethod = method;
        stringSend = send;
        textViewJSON = dynamicTextView;
        this.taskListener = listener; // The listener reference is passed in through the constructor
    }
    public HttpRequestLongOperation(Context ctx, String url, String method, Map<String, String> data, TextView dynamicTextView, TaskListener listener) {
        context = ctx;
        inputUrl = url;
        stringMethod = method;
        mapSend = data;
        textViewJSON = dynamicTextView;
        this.taskListener = listener; // The listener reference is passed in through the constructor
    }
    public HttpRequestLongOperation(Context ctx, String url, String method, Map<String, String> data, String img, TextView dynamicTextView, TaskListener listener) {
        context = ctx;
        inputUrl = url;
        stringMethod = method;
        mapSend = data;
        stringImg = img;
        textViewJSON = dynamicTextView;
        this.taskListener = listener; // The listener reference is passed in through the constructor
    }

    public HttpRequestLongOperation(Context ctx, String url, String method, Map<String, String> data, TaskListener listener) {
        context = ctx;
        inputUrl = url;
        stringMethod = method;
        mapSend = data;
        this.taskListener = listener; // The listener reference is passed in through the constructor
    }


    public HttpRequestLongOperation(Context ctx, String url, String method, String send, TaskListener listener) {
        context = ctx;
        inputUrl = url;
        stringMethod = method;
        stringSend = send;
        this.taskListener = listener; // The listener reference is passed in through the constructor
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(textViewJSON != null) {
            textViewJSON.setText("Loading...");
        }
    }


    @Override
    protected String doInBackground(String... params) {
        // Run methods
        String stringResponse ="";
        try {
            if(stringMethod.equals("get")) {
                stringResponse = HttpRequest.get(inputUrl).body();
            }
            else if(stringMethod.equals("post")){
                if(!(stringSend.equals(""))){
                    int intResponse = HttpRequest.post(inputUrl).send(stringSend).code();
                    stringResponse = "" + intResponse;
                }
                else {
                    try{
                        stringResponse = HttpRequest.post(inputUrl).form(mapSend).body();
                    }
                    catch (Exception e){
                        return e.toString();
                    }
                }
            }
            else if(stringMethod.equals("post_image")){


                // Method 1 - Base 64
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bm = BitmapFactory.decodeFile(stringImg, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                } catch (Exception compresse) {
                    Toast.makeText(context, "Compress error: " + compresse.toString(), Toast.LENGTH_LONG).show();
                }
                byte[] byteImage_photo = baos.toByteArray(); // bitmap object

                String encodedImage = Base64.encodeToString(byteImage_photo,Base64.DEFAULT); //generate base64 string of image


                mapSend.put("inp_image_base", encodedImage);
                stringResponse = HttpRequest.post(inputUrl).form(mapSend).body();

            } // post_image
        }
        catch(Exception e){
            return e.toString();
        }
        return stringResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        // Set text view with result string
        if(textViewJSON == null){
            // Send it to Database
            DBAdapter db = new DBAdapter(context);
            db.open();


            SimpleDateFormat df4=new SimpleDateFormat("d");
            String day = df4.format(Calendar.getInstance().getTime());

            String sql = "INSERT INTO json_temp_data (_id, day, data) VALUES (?, ?, ?)";
            String[] dataTypes = {"null","string","string"};
            String[] values = {"NULL", day, result};
            db.insertPreparedStatement(sql, dataTypes, values);

            db.close();

        }
        else {
            textViewJSON.setText(result);
        }
        // In onPostExecute we check if the listener is valid
        if(this.taskListener != null) {

            // And if it is we call the callback function on it.
            this.taskListener.onFinished(result);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

}