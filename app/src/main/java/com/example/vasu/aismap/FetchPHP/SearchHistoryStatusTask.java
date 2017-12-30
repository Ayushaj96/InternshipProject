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

public class SearchHistoryStatusTask extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    ArrayList<NearMachines> nearList = new ArrayList<>();
    String machine_id ;
    Context context ;

    public AsyncResponseFindSearch delegate = null;

    public SearchHistoryStatusTask(Context context , String machine_id , AsyncResponseFindSearch delegate){
        this.context = context ;
        this.machine_id = machine_id ;
        this.delegate = delegate ;
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your json file resides
            // Even you can make call to php file which returns json data
            url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/search_marker.php");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            String data = URLEncoder.encode("machine_id", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(this.machine_id), "UTF-8");

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

        result = result.replace("\\n" , "").replace("\\r" , "").replace("\\t" , "");
        delegate.processFinish(result);

    }
}
