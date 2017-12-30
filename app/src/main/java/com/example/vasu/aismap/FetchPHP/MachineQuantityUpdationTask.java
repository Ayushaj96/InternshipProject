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

public class MachineQuantityUpdationTask extends AsyncTask<String,String,String> {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    String machine_id,quantity,company_no;
    Context context ;

    AsyncResponseUserLog delegate ;

    public MachineQuantityUpdationTask(){

    }

    public MachineQuantityUpdationTask(Context context, String machine_id, String quantity, String company_no, AsyncResponseUserLog delegate){
        this.context = context ;
        this.machine_id = machine_id;
        this.quantity = quantity;
        this.company_no = company_no ;
        this.delegate = delegate ;

    }

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your json file resides
            // Even you can make call to php file which returns json data
            url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/update_machine.php");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            String data = URLEncoder.encode("company_no", "UTF-8")
                    + "=" + URLEncoder.encode(this.company_no, "UTF-8");

            data += "&" + URLEncoder.encode("machine_id", "UTF-8") + "="
                    + URLEncoder.encode(this.machine_id, "UTF-8");

            data += "&" + URLEncoder.encode("quantity", "UTF-8") + "="
                    + URLEncoder.encode(this.quantity, "UTF-8");

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
