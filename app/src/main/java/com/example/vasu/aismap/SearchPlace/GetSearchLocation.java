package com.example.vasu.aismap.SearchPlace ;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.vasu.aismap.Models.NearMachines;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Vasu on 25-12-2017.
 */

public class GetSearchLocation extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    ArrayList<NearMachines> nearList = new ArrayList<>();
    double lat , lang ;

    SharedPreferences sharedPreferencesLocation ;
    Context context ;
    String place ;

    public AsyncResponseSearch delegate = null;

    public GetSearchLocation(Context context ,String place , AsyncResponseSearch delegate){
        this.context = context ;
        this.delegate = delegate ;
        this.place = place ;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String temp = this.place.replace(" " , "+") ;
            url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + temp + "&key=AIzaSyDaea36hvrsM31qH6Q1-QHhbqPtEkLTro8");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("GET");

            // setDoOutput to true as we recieve data from json file
            conn.setDoOutput(true);

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

        ArrayList<LatLng> locationList = new ArrayList<>() ;

        try {
            JSONObject obj1 = new JSONObject(result) ;
            JSONArray arr = obj1.getJSONArray("results") ;
            for(int i=0;i<arr.length();i++) {

                JSONObject obj2 = arr.getJSONObject(i);
                JSONObject obj3=obj2.getJSONObject("geometry");
                JSONObject obj4=obj3.getJSONObject("location");
                double latitude = obj4.getDouble("lat");
                double longitude = obj4.getDouble("lng");
                LatLng ll = new LatLng(latitude, longitude);
                locationList.add(ll);
            }
        } catch (JSONException e) {
            Log.i("SEARCH" , e.toString());
        }

        this.delegate.processFinish(locationList);


    }
}
