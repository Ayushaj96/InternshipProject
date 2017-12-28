package com.example.vasu.aismap.FetchPHP;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

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

public class UserRegistrationTask extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    String name,email,username,mobile,password,dob,profession ;

    Context context ;

    AsyncResponseUserRegistration delegate ;

    public UserRegistrationTask(){

    }

    public UserRegistrationTask(Context context,String name,String email,String username
            ,String mobile,String password,String dob,String profession,AsyncResponseUserRegistration delegate){
        this.context = context ;
        this.name = name;
        this.email = email;
        this.username = username;
        this.mobile = mobile;
        this.password = password;
        this.dob = dob;
        this.profession = profession;
        this.delegate = delegate ;

    }

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your json file resides
            // Even you can make call to php file which returns json data
            url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/UserRegistration.php");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            String data = URLEncoder.encode("full_name", "UTF-8")
                    + "=" + URLEncoder.encode(this.name, "UTF-8");

            data += "&" + URLEncoder.encode("mobile", "UTF-8") + "="
                    + URLEncoder.encode(this.mobile, "UTF-8");

            data += "&" + URLEncoder.encode("email", "UTF-8") + "="
                    + URLEncoder.encode(this.email, "UTF-8");

            data += "&" + URLEncoder.encode("username", "UTF-8") + "="
                    + URLEncoder.encode(this.username, "UTF-8");

            data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(this.password, "UTF-8");

            data += "&" + URLEncoder.encode("dob", "UTF-8") + "="
                    + URLEncoder.encode(this.dob, "UTF-8");

            data += "&" + URLEncoder.encode("profession", "UTF-8") + "="
                    + URLEncoder.encode(this.profession, "UTF-8");

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
            if (response_code == HttpURLConnection.HTTP_OK) {

                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
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
