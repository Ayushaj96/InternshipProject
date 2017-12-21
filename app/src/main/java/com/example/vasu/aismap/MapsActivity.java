package com.example.vasu.aismap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;

    FloatingActionButton floatingActionButton;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LatLng position = new LatLng(28.6291027, 77.207133);
    MarkerOptions markerOptionsMyLoc;
    Marker myCurrentLocMarker, mPrevLocMarker;
    float zoom = 14;
    Circle mCircle , mPrevCircle;


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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d(TAG, "onCreate ...............................");
        floatingActionButton = (FloatingActionButton)findViewById(R.id.imageButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoom  = mMap.getCameraPosition().zoom;
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
        MarkerOptions markerOptions = new MarkerOptions().position(position);
        this.mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)).title("Machine"));

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(this, "Permission not given", Toast.LENGTH_SHORT).show();
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
       // Toast.makeText(this, "Location Updated", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {

            double radiusInMeters = 1000.0;
            int strokeColor = 0xffff0000; //red outline
            int shadeColor = 0x44ff0000; //opaque red fill

            Double lat = mCurrentLocation.getLatitude();
            Double lng = mCurrentLocation.getLongitude();

            LatLng ll = new LatLng(lat, lng);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));

            markerOptionsMyLoc = new MarkerOptions().position(ll).title("My Location");
            if (mPrevLocMarker != null){
                mPrevLocMarker.remove();
            }
            if (mPrevCircle != null){
                mPrevCircle.remove();
            }

            myCurrentLocMarker = mMap.addMarker(markerOptionsMyLoc.flat(true).rotation(mCurrentLocation.getBearing()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            mPrevLocMarker = myCurrentLocMarker ;
            CircleOptions circleOptions = new CircleOptions().center(ll).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(3);
            mCircle = mMap.addCircle(circleOptions);
            mPrevCircle = mCircle ;
 
            float[] distance = new float[5] ;
            Location.distanceBetween(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),position.latitude,position.longitude,distance);
           // Toast.makeText(this, ""+distance[0], Toast.LENGTH_SHORT).show();

        } else {
            Log.d(TAG, "location is null ...............");
        }

    }



}
