package com.example.vasu.aismap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.example.vasu.aismap.CustomAdapter.NearMachinesAdapter;
import com.example.vasu.aismap.Directions.AsyncResponseDownload;
import com.example.vasu.aismap.Directions.DownloadTask;
import com.example.vasu.aismap.Directions.DownloadUrl;
import com.example.vasu.aismap.Models.ClusteringItem;
import com.example.vasu.aismap.Models.NearMachines;
import com.example.vasu.aismap.Models.OwnClusterIconRendered;
import com.example.vasu.aismap.Sqlite.MachineDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Collection;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation = new Location("My Location");

    FloatingActionButton floatingActionButton;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LatLng position = new LatLng(28.6291027, 77.207133);
    MarkerOptions markerOptionsMyLoc;
    Marker myCurrentLocMarker, mPrevLocMarker;
    Circle mCircle , mPrevCircle;

    MachineDatabase machineDatabase;
    Cursor data ;

    SharedPreferences sharedPreferences ,sharedPreferencesLocation ;
    SharedPreferences.Editor editorLocation ;
    float zoom = 15.0f ;
    float radius = 100.0f ;
    boolean moveMyLocCamera = true ;

    private ClusterManager<ClusteringItem> mClusterManager;

    Polyline pl[] = new Polyline[5] ;

    boolean locationUpdated = false ;
    boolean nearMachineExecuted = false ;
    GridView gvNear ;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View includedLayout = findViewById(R.id.includeBar);
        gvNear = (GridView) includedLayout.findViewById(R.id.gvNearMachines);

        machineDatabase = new MachineDatabase(this);
        data = machineDatabase.getListContents();

        sharedPreferences =getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sharedPreferencesLocation =getApplicationContext().getSharedPreferences("MyLocation", MODE_PRIVATE);
        editorLocation = sharedPreferencesLocation.edit();
        zoom = Float.parseFloat(sharedPreferences.getString("Zoom" , "15.0"));
        radius = Float.parseFloat(sharedPreferences.getString("Radius" , "100.0"));

        if(sharedPreferencesLocation.getFloat("Latitude" , 0.0f) != 0.0f){
            LatLng ll = new LatLng((double) sharedPreferencesLocation.getFloat("Latitude" , 0.0f) , (double) sharedPreferencesLocation.getFloat("Longitude" , 0.0f));
            mCurrentLocation.setLatitude(ll.latitude);
            mCurrentLocation.setLongitude(ll.longitude);
            new GetNearMachines().execute();
        }

        //new GetNearMachines().execute();

        createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


    }

   public void show_machines_on_map(LatLng latLng){
        this.mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)).title("Machine"));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; 
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoom  = mMap.getCameraPosition().zoom;
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

        mClusterManager = new ClusterManager<ClusteringItem>(this, mMap);

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mMap.setOnCameraIdleListener(mClusterManager);
        try {
            readItems();
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusteringItem>() {
            @Override
            public boolean onClusterClick(Cluster<ClusteringItem> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(), (float) Math.floor(mMap.getCameraPosition().zoom + 1)), 300, null);
                return true;
            }
        });

        mClusterManager.setRenderer(new OwnClusterIconRendered(this.getApplicationContext(), mMap, mClusterManager));

        MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusteringItem>() {
            @Override
            public boolean onClusterItemClick(ClusteringItem clusteringItem) {

                if (clusteringItem.getPosition().latitude == mCurrentLocation.getLatitude()){
                    Toast.makeText(MapsActivity.this, ""+mCurrentLocation, Toast.LENGTH_SHORT).show();
                }

                LatLng origin = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()) ;
                LatLng destination = clusteringItem.getPosition() ;
                DownloadUrl du = new DownloadUrl();
                String url = du.getDirectionsUrl(origin,destination );
                DownloadTask downloadTask = new DownloadTask(new AsyncResponseDownload() {
                    @Override
                    public void processFinish(PolylineOptions[] output) {
                        drawPath(output);
                    }
                });
                downloadTask.execute(url);
                return false;
            }
        });

         /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "Marker", Toast.LENGTH_SHORT).show();
                return false;
            }
        }); */

    }

    private void drawPath(PolylineOptions[] output) {

        for (int i=0 ; i < pl.length ; i++){
            if (pl[i] != null ){
                pl[i].remove();
            }
        }

        int count = 0 ;
        while (output[count] != null){
            pl[count] = mMap.addPolyline(output[count]);
            count++ ;
        }
    }


    private void readItems() throws JSONException {
        data.moveToFirst() ;
        while (data.moveToNext()){
            double lat = Double.parseDouble(data.getString(2)) ;
            double lng = Double.parseDouble(data.getString(1)) ;
            ClusteringItem offsetItem = new ClusteringItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }
    
    public void getNearestMachine(){
        data.moveToFirst();
        float[] directDistance = new float[2] ; 
        float minDis = Float.MAX_VALUE ;
        LatLng minLL = null;
        final boolean[] cameraAnimating = {false};

        while (data.moveToNext()){
            Location.distanceBetween(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude() , 
                    Double.parseDouble(data.getString(2)) , Double.parseDouble(data.getString(1)) , directDistance );
            
            if (directDistance[0] <= minDis){
                minDis = directDistance[0] ;
                minLL = new LatLng( Double.parseDouble(data.getString(2)) , Double.parseDouble(data.getString(1)) ) ;
            }
            
        }

        // Toast.makeText(this, ""+minDis+" m", Toast.LENGTH_SHORT).show();

        final LatLng finalMinLL = minLL;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(minLL, 18),500 , new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Collection<Marker> mm = mClusterManager.getMarkerCollection().getMarkers();
                        Iterator<Marker> itr = mm.iterator();

                        while (itr.hasNext()) {
                            Marker marker = itr.next();
                            if (finalMinLL.latitude == marker.getPosition().latitude && finalMinLL.longitude == marker.getPosition().longitude) {
                                marker.showInfoWindow();
                                //break outer;
                            }
                        }
                    }
                }, 1000);
            }

            @Override
            public void onCancel() {

            }
        });



        /*LatLng origin = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()) ;
        String url = getDirectionsUrl(origin,minLL);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url); */

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        editorLocation.putFloat("Latitude" , (float) mCurrentLocation.getLatitude());
        editorLocation.putFloat("Longitude" , (float) mCurrentLocation.getLongitude());
        editorLocation.commit();
        if (!nearMachineExecuted){
            new GetNearMachines().execute();
        }
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {

            int strokeColor = 0xffff0000; //red outline
            int shadeColor = 0x44ff0000; //opaque red fill

            Double lat = mCurrentLocation.getLatitude();
            Double lng = mCurrentLocation.getLongitude();

            LatLng ll = new LatLng(lat, lng);

            if (moveMyLocCamera) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
                moveMyLocCamera = false ;
            }


            markerOptionsMyLoc = new MarkerOptions().position(ll).title("My Location");
            if (mPrevLocMarker != null){
                mPrevLocMarker.remove();
            }
            /*if (mPrevCircle != null){
                mPrevCircle.remove();
            }*/

            myCurrentLocMarker = mMap.addMarker(markerOptionsMyLoc.flat(true).rotation(mCurrentLocation.getBearing()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            mPrevLocMarker = myCurrentLocMarker ;
            /*CircleOptions circleOptions = new CircleOptions().center(ll).radius(radius).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(3);
            mCircle = mMap.addCircle(circleOptions);
            mPrevCircle = mCircle ;
 */


        } else {
            Log.d(TAG, "location is null ...............");
        }

    }

    class GetNearMachines extends AsyncTask<String,String,String>{

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        ArrayList<NearMachines> nearList = new ArrayList<>();
        double lat , lang ;

        public GetNearMachines(){
            if (mCurrentLocation != null){
                this.lat = mCurrentLocation.getLatitude();
                this.lang = mCurrentLocation.getLongitude();
            }else{
                this.lat = sharedPreferences.getFloat("Latitude" , 0.0f);
                this.lang = sharedPreferences.getFloat("Longitude" , 0.0f);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                url = new URL("https://aiseraintern007.000webhostapp.com/AISERA/get_near_machines.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                String data = URLEncoder.encode("lat", "UTF-8")
                        + "=" + URLEncoder.encode(String.valueOf(this.lat), "UTF-8");

                data += "&" + URLEncoder.encode("lang", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(this.lang), "UTF-8");

                data += "&" + URLEncoder.encode("km", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(5), "UTF-8");

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

            try {
                JSONArray jsonArray =new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    if (i < 8){
                        JSONObject object = jsonArray.getJSONObject(i);
                        double latitude = object.getDouble("latitude");
                        double longitude = object.getDouble("longitude");
                        NearMachines nm = new NearMachines("M" , "Address" , new LatLng(latitude,longitude));
                        nearList.add(nm);
                    }
                }
            } catch (Exception e) {

            }
            nearMachineExecuted = true ;
            NearMachinesAdapter nma = new NearMachinesAdapter(nearList,MapsActivity.this);
            gvNear.setAdapter(nma);
        }
    }


}
