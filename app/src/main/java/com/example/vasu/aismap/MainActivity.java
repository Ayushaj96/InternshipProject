package com.example.vasu.aismap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button b1,b2,b3;
    EditText ed1,ed2;
    SharedPreferences sharedPreferences;
    String link = "https://aiseraintern007.000webhostapp.com/AISERA/get_machines.php" ;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b1=(Button)findViewById(R.id.button5);
        b2=(Button)findViewById(R.id.button6);
        b3=(Button)findViewById(R.id.button7);

        ed1=(EditText)findViewById(R.id.editText);
        ed2=(EditText)findViewById(R.id.editText2);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Zoom",12);
        editor.putInt("distance",1000);
        editor.apply();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new get_machine_details().execute() ;
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override  
            public void onClick(View v) {

            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences=getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Zoom",ed1.getText().toString());
                editor.putString("distance",ed2.getText().toString());
                editor.apply();

                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    class get_machine_details extends AsyncTask<String,String,String>{

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(link);
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdLoading.dismiss();
            //Toast.makeText(MainActivity.this, ""+s, Toast.LENGTH_SHORT).show();

            try {

                JSONObject obj1 = new JSONObject(s) ;
                JSONArray arr1 = new JSONArray(obj1);
                Toast.makeText(MainActivity.this, ""+arr1, Toast.LENGTH_SHORT).show();


            }catch (Exception e){
                Toast.makeText(MainActivity.this, "Manupulate error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }


    }

}
