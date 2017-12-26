package com.example.vasu.aismap;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vasu.aismap.CustomAdapter.GeoAutoCompleteAdapter;
import com.example.vasu.aismap.CustomAdapter.NearMachinesAdapter;
import com.example.vasu.aismap.Directions.AsyncResponseDownload;
import com.example.vasu.aismap.Directions.DownloadTask;
import com.example.vasu.aismap.Directions.DownloadUrl;
import com.example.vasu.aismap.FetchPHP.AsyncResponseFindNear;
import com.example.vasu.aismap.FetchPHP.FindNearMachines;
import com.example.vasu.aismap.InfoWindow.MarkerInfoWindowAdapter;
import com.example.vasu.aismap.Models.NearMachines;
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

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        ,GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation = new Location("My Location");

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LatLng position = new LatLng(28.6291027, 77.207133);
    MarkerOptions markerOptionsMyLoc;
    Marker myCurrentLocMarker, mPrevLocMarker;
    Marker[] markerArray ;
    Circle mCircle , mPrevCircle;

    ImageButton ibMyLocation , ibSearch , ibNearest , ibIncludeMore , ibIncludeClose;

    //MachineDatabase machineDatabase;
    //Cursor data ;

    ArrayList<Marker> nearMarkersList = new ArrayList<>() ;

    SharedPreferences sharedPreferences ,sharedPreferencesLocation ;
    SharedPreferences.Editor editorLocation ;
    float zoom = 15.0f ;
    float radius = 100.0f ;
    boolean moveMyLocCamera = true ;

    Polyline pl[] = new Polyline[5] ;

    boolean locationUpdated = false ;
    boolean nearMachineExecuted = false ;
    RelativeLayout mRoot ;
    LinearLayout llSearchBar ;
    DelayAutoCompleteTextView etSearch ;
    GridView gvNear ;

    Animation slide_down , slide_up ;

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

        ibMyLocation = (ImageButton) findViewById(R.id.myLocation);
        ibSearch = (ImageButton) findViewById(R.id.searchButton);
        ibNearest = (ImageButton) findViewById(R.id.findNearest);
        ibIncludeMore = (ImageButton) findViewById(R.id.search_more);
        ibIncludeClose = (ImageButton) findViewById(R.id.search_close);

        mRoot = (RelativeLayout) findViewById(R.id.rlMaps);
        llSearchBar = (LinearLayout) findViewById(R.id.includeBar);
        etSearch = (DelayAutoCompleteTextView) findViewById(R.id.geo_autocomplete);
        gvNear = (GridView) findViewById(R.id.gvNearMachines);

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        sharedPreferences =getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sharedPreferencesLocation =getApplicationContext().getSharedPreferences("MyLocation", MODE_PRIVATE);
        editorLocation = sharedPreferencesLocation.edit();
        zoom = Float.parseFloat(sharedPreferences.getString("Zoom" , "15.0"));
        radius = Float.parseFloat(sharedPreferences.getString("Radius" , "100.0"));

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

        ibMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentLocation != null) {
                    LatLng ll = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
                    moveMyLocCamera = false;
                }else {
                    Toast.makeText(MapsActivity.this, "Please wait while fetching your lcoation", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSearchBar.getVisibility() == View.GONE){
                    llSearchBar.setVisibility(View.VISIBLE);
                    llSearchBar.startAnimation(slide_up);
                }
            }
        });

        ibNearest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentLocation != null){
                    Toast.makeText(MapsActivity.this, "Finding Nearest Machine", Toast.LENGTH_SHORT).show();
                    FindNearMachines fnm = new FindNearMachines(MapsActivity.this , mCurrentLocation , new AsyncResponseFindNear(){
                        @Override
                        public void processFinish(String output) {
                            pointToNearest(output);
                        }
                    });
                    fnm.execute() ;
                }else{
                    Toast.makeText(MapsActivity.this, "Getting your location..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ibIncludeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ibIncludeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSearchBar.getVisibility() == View.VISIBLE){
                    llSearchBar.startAnimation(slide_down);
                    llSearchBar.setVisibility(View.GONE);
                }
            }
        });

        etSearch.setThreshold(2);
        etSearch.setAdapter(new GeoAutoCompleteAdapter(this)); // 'this' is Activity instance

        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GeoSearchResult result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                String address = result.getAddress() ;
                LatLng addLl = getLocationFromAddress(MapsActivity.this , address) ;
                Marker m = mMap.addMarker(new MarkerOptions().position(addLl).title(address)) ;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addLl , 16));
                etSearch.setText("");
                if (llSearchBar.getVisibility() == View.VISIBLE){
                    llSearchBar.startAnimation(slide_down);
                    llSearchBar.setVisibility(View.GONE);
                }
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String address = etSearch.getText().toString() ;
                    LatLng addLl = getLocationFromAddress(MapsActivity.this , address) ;
                    Marker m = mMap.addMarker(new MarkerOptions().position(addLl).title(address)) ;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addLl , 16));
                    etSearch.setText("");
                    if (llSearchBar.getVisibility() == View.VISIBLE){
                        llSearchBar.startAnimation(slide_down);
                        llSearchBar.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });

    }

    /*public void show_machines_on_map(LatLng latLng){
        this.mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)).title("Machine"));

    }*/

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

        if (mCurrentLocation.getLatitude() == 0.0 && mCurrentLocation.getLongitude() == 0.0 ){
            mCurrentLocation.setLatitude((double) sharedPreferencesLocation.getFloat("Latitude" , 0.0f));
            mCurrentLocation.setLongitude((double) sharedPreferencesLocation.getFloat("Longitude" , 0.0f));
        }else{
        }

        MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getPosition().latitude != mCurrentLocation.getLatitude()) {
                    LatLng origin = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    LatLng destination = marker.getPosition();
                    DownloadUrl du = new DownloadUrl();
                    String url = du.getDirectionsUrl(origin, destination);
                    DownloadTask downloadTask = new DownloadTask(new AsyncResponseDownload() {
                        @Override
                        public void processFinish(PolylineOptions[] output) {
                            drawPath(output);
                        }
                    });
                    downloadTask.execute(url);
                }
                return false;
            }
        });

        FindNearMachines fnm = new FindNearMachines(MapsActivity.this , mCurrentLocation , new AsyncResponseFindNear(){
            @Override
            public void processFinish(String output) {
                addToNearGrid(output);
                findNear(output) ;
            }
        });
        fnm.execute() ;

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
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (mCurrentLocation!=null) {

    //        int strokeColor = 0xffff0000; //red outline
      //      int shadeColor = 0x44ff0000; //opaque red fill
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
            myCurrentLocMarker = mMap.addMarker(markerOptionsMyLoc.flat(true).rotation(mCurrentLocation.getBearing()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            mPrevLocMarker = myCurrentLocMarker ;

        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
      Toast.makeText(MapsActivity.this,"Clicked by user",Toast.LENGTH_LONG).show();

    }

    public void addToNearGrid(String result){
        ArrayList<NearMachines> nearList = new ArrayList<>();
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i < 8){
                    JSONObject object = jsonArray.getJSONObject(i);
                    double latitude = object.getDouble("latitude");
                    double longitude = object.getDouble("longitude");
                    LatLng ll = new LatLng(latitude,longitude) ;
                    NearMachines nm = new NearMachines("M" , "Address" , ll );
                    nearList.add(nm);
                }
            }
        } catch (Exception e) {
        }

        NearMachinesAdapter nma = new NearMachinesAdapter(nearList,MapsActivity.this);
        gvNear.setAdapter(nma);
    }

    public void findNear(String result){
        /*mMap.clear();
        nearMarkersList.clear();*/
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    double latitude = object.getDouble("latitude");
                    double longitude = object.getDouble("longitude");
                    LatLng ll = new LatLng(latitude,longitude) ;
                    Marker m = mMap.addMarker(new MarkerOptions().title("Machine").position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine))) ;
                    nearMarkersList.add(m);
            }
        } catch (Exception e) {
        }

    }

    public void pointToNearest(String result){

        float minDist = Float.MAX_VALUE;
        double minLat = 0 ;
        double minLong = 0;
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                float[] dist = new float[2] ;
                Location.distanceBetween(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude() ,
                        latitude , longitude , dist);
                if (dist[0] <= minDist){
                    minDist = dist[0] ;
                    minLat = latitude ;
                    minLong = longitude ;
                }
            }
        } catch (Exception e) {
        }

        final LatLng minLL = new LatLng(minLat,minLong) ;

        for (Marker m : nearMarkersList){
            if (m.getPosition() == minLL){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(minLL, 18)) ;
                m.showInfoWindow();
            }
        }

    }


    @Override
    public void onBackPressed() {
        if (llSearchBar.getVisibility() == View.VISIBLE){
            llSearchBar.startAnimation(slide_down);
            llSearchBar.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }


}