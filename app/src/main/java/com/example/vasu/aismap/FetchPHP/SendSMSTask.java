package com.example.vasu.aismap.FetchPHP;

import android.content.Context;
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

/**
 * Created by AYUSH on 12/30/2017.
 */

public class SendSMSTask extends AsyncTask<String,String,String> {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        ArrayList<NearMachines> nearList = new ArrayList<>();
        String mobile,message ;
        Context context ;

public AsyncResponseFindSearch delegate = null;

public SendSMSTask(Context context , String mobile, String message , AsyncResponseFindSearch delegate){
        this.context = context ;
        this.mobile=mobile;
        this.message=message;
        this.delegate = delegate ;
        }

@Override
protected String doInBackground(String... params) {
        try {

        // Enter URL address where your json file resides
        // Even you can make call to php file which returns json data
        url = new URL("http://sms.thinkbuyget.com/api.php?username=Aiseratech&password=224679&sender=DGENIT&sendto="+this.mobile+"&message=%"+this.message);

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

        delegate.processFinish(result);

        }
        }

