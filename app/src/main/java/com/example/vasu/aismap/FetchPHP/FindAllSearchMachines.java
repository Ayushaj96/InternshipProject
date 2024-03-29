package com.example.vasu.aismap.FetchPHP;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.vasu.aismap.Models.MarkerModel;
import com.example.vasu.aismap.Models.NearMachines;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

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

/**
 * Created by Vasu on 25-12-2017.
 */

public class FindAllSearchMachines extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    ArrayList<NearMachines> nearList = new ArrayList<>();
    String search ;

    SharedPreferences sharedPreferencesLocation ;
    Context context ;

    public AsyncResponseFindAllSearches delegate = null;

    public FindAllSearchMachines(Context context , String search, AsyncResponseFindAllSearches delegate){
        this.context = context ;
        this.delegate = delegate ;
        this.search = search ;

    }

    @Override
    protected String doInBackground(String... params) {

        try {
            url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/find_search_machines.php");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            String data = URLEncoder.encode("search", "UTF-8")
                    + "=" + URLEncoder.encode(this.search, "UTF-8");

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

        ArrayList<MarkerModel> addressList = new ArrayList<>() ;

        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                String address = object.getString("address");
                String address_tags = object.getString("address_tags");
                String machine_serial_no = object.getString("machine_serial_no");
                String access = object.getString("access");
                String status = object.getString("status");
                String company1 = object.getString("company1");
                int company1quantity = object.getInt("company1quantity");
                String company2 = object.getString("company2");
                int company2quantity = object.getInt("company2quantity");
                String type = object.getString("type");
                LatLng ll = new LatLng(latitude,longitude) ;
                MarkerModel mm = new MarkerModel(ll,address,address_tags,machine_serial_no,access,status,company1,company1quantity,company2,company2quantity,type);
                addressList.add(mm);
            }
        } catch (Exception e) {
        }

        delegate.processFinish(addressList);

    }

}
