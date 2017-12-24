package com.example.vasu.aismap.Directions;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Vasu on 24-12-2017.
 */

// Fetches data from url passed
public class DownloadTask extends AsyncTask<String, Void, String> {

    public AsyncResponseDownload delegate = null;

    public DownloadTask(){

    }

    public DownloadTask(AsyncResponseDownload delegate){
        this.delegate = delegate;
    }

    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try{
            // Fetching the data from web service
            DownloadUrl du = new DownloadUrl();
            data = du.downloadUrl(url[0]);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask pt = new ParserTask(new AsyncResponseParser() {
            @Override
            public void processFinish(PolylineOptions[] output) {
                //Log.e("POLYLINE" , output[0].toString()) ;
                delegate.processFinish(output);
            }
        }) ;

        pt.execute(result);

    }
}