package com.example.vasu.aismap.FetchPHP;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;

import com.example.vasu.aismap.Models.NearMachines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Vasu on 25-12-2017.
 */

public class FindNearMachines extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    ArrayList<NearMachines> nearList = new ArrayList<>();
    double lat , lang ;
    String km ;

    SharedPreferences sharedPreferencesLocation ;
    Context context ;

    public AsyncResponseFindNear delegate = null;

    public FindNearMachines(Context context , Location mCurrentLocation , String km , AsyncResponseFindNear delegate){
        this.context = context ;
        this.delegate = delegate ;
        this.km = km ;
        sharedPreferencesLocation = this.context.getSharedPreferences("MyLocation", MODE_PRIVATE);
        if (mCurrentLocation != null){
            this.lat = mCurrentLocation.getLatitude();
            this.lang = mCurrentLocation.getLongitude();
        }else{
            this.lat = sharedPreferencesLocation.getFloat("Latitude" , 0.0f);
            this.lang = sharedPreferencesLocation.getFloat("Longitude" , 0.0f);
        }

    }

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your json file resides
            // Even you can make call to php file which returns json data
            url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/get_near_machines.php");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            String data = URLEncoder.encode("lat", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(this.lat), "UTF-8");

            data += "&" + URLEncoder.encode("lang", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(this.lang), "UTF-8");

            data += "&" + URLEncoder.encode("km", "UTF-8") + "="
                    + URLEncoder.encode(km, "UTF-8");

            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            // setDoOutput to true as we recieve data from json file
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return e1.toString();
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                return (result.toString());

            } else {

                return ("unsuccessful");
            }

        } catch (IOException e) {
            return e.toString();
        } finally {
            conn.disconnect();
        }


    }

    @Override
    protected void onPostExecute(String result) {

        delegate.processFinish(result);

    }
}
