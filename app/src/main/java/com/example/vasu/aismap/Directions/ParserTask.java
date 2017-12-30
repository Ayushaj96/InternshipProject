package com.example.vasu.aismap.Directions;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vasu on 24-12-2017.
 */

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

    ArrayList<Float> distanceList = new ArrayList<>() ;

    public AsyncResponseParser delegate = null;

    public ParserTask(){

    }

    public ParserTask(AsyncResponseParser delegate){
        this.delegate = delegate;
    }


    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;


        try{
            jObject = new JSONObject(jsonData[0]);
            DirectionJSONParser parser = new DirectionJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
            this.distanceList = parser.getTravelDistance(jObject) ;
        }catch(Exception e){
            Log.e("MYGENERATEDERROR" , e.toString());
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions[] = new PolylineOptions[5];
        MarkerOptions markerOptions = new MarkerOptions();

        ArrayList<Float> tempDistance = this.distanceList ;
        Collections.sort(tempDistance);

        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions[i] = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions[i].addAll(points);
            lineOptions[i].width(10);
            /*if (this.distanceList.get(i) == tempDistance.get(0)){
                lineOptions[i].color(Color.GREEN);
            }else if (this.distanceList.get(i) == tempDistance.get(tempDistance.size()-1)){
                lineOptions[i].color(Color.RED);
            }else{
                lineOptions[i].color(Color.BLUE);
            }*/

            if (this.distanceList.get(i) == tempDistance.get(0)){
                lineOptions[i].color(Color.BLUE);
            }else{
                lineOptions[i].color(Color.DKGRAY);
            }

        }

        delegate.processFinish(lineOptions);

    }
}