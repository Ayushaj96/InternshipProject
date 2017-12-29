package com.example.vasu.aismap.FetchPHP;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Vasu on 25-12-2017.
 */

public class UserLogTask extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    String mobile,machine_id,quality,quantity,company,trans_start_time,
            trans_end_time,trans_mode,trans_id,trans_status,encrypted_code; ;
    float cost ;

    Context context ;

    AsyncResponseUserLog delegate ;

    public UserLogTask(){

    }

    public UserLogTask(Context context,String mobile,String machine_id,String quality,String quantity,float cost,
                       String company,String trans_start_time,String trans_end_time,String trans_mode,String trans_id
                        ,String trans_status,String encrypted_code, AsyncResponseUserLog delegate){
        this.context = context ;
        this.mobile = mobile;
        this.machine_id = machine_id;
        this.quality = quality;
        this.quantity = quantity;
        this.cost = cost;
        this.company = company;
        this.trans_start_time = trans_start_time;
        this.trans_end_time = trans_end_time;
        this.trans_mode = trans_mode;
        this.trans_id = trans_id;
        this.trans_status = trans_status;
        this.encrypted_code = encrypted_code;
        this.delegate = delegate ;

    }

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your json file resides
            // Even you can make call to php file which returns json data
            url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/InsertUserLog.php");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            String data = URLEncoder.encode("user_id", "UTF-8")
                    + "=" + URLEncoder.encode(this.mobile, "UTF-8");

            data += "&" + URLEncoder.encode("machine_id", "UTF-8") + "="
                    + URLEncoder.encode(this.machine_id, "UTF-8");

            data += "&" + URLEncoder.encode("quality", "UTF-8") + "="
                    + URLEncoder.encode(this.quality, "UTF-8");

            data += "&" + URLEncoder.encode("quantity", "UTF-8") + "="
                    + URLEncoder.encode(this.quantity, "UTF-8");

            data += "&" + URLEncoder.encode("cost", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(this.cost), "UTF-8");

            data += "&" + URLEncoder.encode("company", "UTF-8") + "="
                    + URLEncoder.encode(this.company, "UTF-8");

            data += "&" + URLEncoder.encode("trans_start_time", "UTF-8") + "="
                    + URLEncoder.encode(this.trans_start_time, "UTF-8");

            data += "&" + URLEncoder.encode("trans_end_time", "UTF-8") + "="
                    + URLEncoder.encode(this.trans_end_time, "UTF-8");

            data += "&" + URLEncoder.encode("trans_mode", "UTF-8") + "="
                    + URLEncoder.encode(this.trans_mode, "UTF-8");

            data += "&" + URLEncoder.encode("trans_id", "UTF-8") + "="
                    + URLEncoder.encode(this.trans_id, "UTF-8");

            data += "&" + URLEncoder.encode("trans_status", "UTF-8") + "="
                    + URLEncoder.encode(this.trans_status, "UTF-8");

            data += "&" + URLEncoder.encode("encrypted_code", "UTF-8") + "="
                    + URLEncoder.encode(this.encrypted_code, "UTF-8");

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
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
