package com.example.vasu.aismap;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.vasu.aismap.CustomAdapter.CustomHistoryAdapter;
import com.example.vasu.aismap.CustomAdapter.CustomSearchListAdapter;
import com.example.vasu.aismap.CustomAdapter.NearMachinesAdapter;
import com.example.vasu.aismap.Directions.AsyncResponseDownload;
import com.example.vasu.aismap.Directions.DownloadTask;
import com.example.vasu.aismap.Directions.DownloadUrl;
import com.example.vasu.aismap.FetchPHP.AsyncResponseFindAllSearches;
import com.example.vasu.aismap.FetchPHP.AsyncResponseFindNear;
import com.example.vasu.aismap.FetchPHP.AsyncResponseFindSearch;
import com.example.vasu.aismap.FetchPHP.FindAllSearchMachines;
import com.example.vasu.aismap.FetchPHP.FindNearMachines;
import com.example.vasu.aismap.FetchPHP.SearchHistoryStatusTask;
import com.example.vasu.aismap.Models.HistoryModel;
import com.example.vasu.aismap.Models.MarkerModel;
import com.example.vasu.aismap.Sqlite.SearchHistory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
    LocationManager manager ;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LatLng position = new LatLng(28.6291027, 77.207133);
    MarkerOptions markerOptionsMyLoc;
    Marker myCurrentLocMarker, mPrevLocMarker;

    ImageButton ibMyLocation , ibSetting , ibIncludeMore , ibIncludeClose  , ibSearch;

    SearchHistory historyDatabase ;

    ArrayList<MarkerModel> allShowingMarkers = new ArrayList<>() ;

    SharedPreferences sharedPreferences ,sharedPreferencesLocation , sharedPreferencesLoginStatus;
    SharedPreferences.Editor editorLocation , editorLoginStatus;
    float zoom = 15.0f ;
    float radius = 100.0f ;
    boolean moveMyLocCamera = true ;

    Polyline pl[] = new Polyline[5] ;

    AutoCompleteTextView etSearch ;
    GridView gvNear ;
    ListView lvHistory ;

    Animation slide_down , slide_up ,slide_right , slide_left;

    View includeSearchInfo , includeBasicInfo , includeNavigation;
    TextView tvBasicMachineSerial, tvBasicMachineAddress;
    Button btnGetDirections , btnMoreInfo;
    LinearLayout llSearch , llNearMachine , llProfile , llAbout , llLogout ;

    Marker selectedMarker ;

    SweetAlertDialog pDialog ;

    BroadcastReceiver networkReceiver ;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    MaterialDialog alertInternet , alertGPS ;

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

        MaterialDialog.Builder builder1 = new MaterialDialog.Builder(this)
                .title("Internet Services Not Active")
                .content("Internet is required to run this application!")
                .cancelable(false);
        alertInternet = builder1.build();

        MaterialDialog.Builder builder2 = new MaterialDialog.Builder(this)
                .title("GPS Services Not Active")
                .content("GPS is required to run this application!");
        alertGPS = builder2.build();

        sharedPreferences =getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sharedPreferencesLocation =getApplicationContext().getSharedPreferences("MyLocation", MODE_PRIVATE);
        sharedPreferencesLoginStatus =getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        editorLocation = sharedPreferencesLocation.edit();
        editorLoginStatus = sharedPreferencesLoginStatus.edit();
        zoom = Float.parseFloat(sharedPreferences.getString("Zoom" , "15.0"));
        radius = Float.parseFloat(sharedPreferences.getString("Radius" , "100.0"));

        if (mCurrentLocation.getLatitude() == 0.0 && mCurrentLocation.getLongitude() == 0.0 ){
            mCurrentLocation.setLatitude(Double.parseDouble(sharedPreferencesLocation.getString("Latitude" , "28.6289143")));
            mCurrentLocation.setLongitude(Double.parseDouble(sharedPreferencesLocation.getString("Longitude" , "77.2065322")));
            Log.i("LOCATION" , "Shared "+mCurrentLocation.getLatitude() + "  " + mCurrentLocation.getLongitude()) ;
        }else{
        }

        /*LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Location Services Not Active")
                    .setContentText("Please enable Location Services and GPS")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        } */

        networkReceiver = new BroadcastReceiver (){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getExtras()!=null) {
                    NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                        //mCurrentLocation.setProvider(LocationManager.NETWORK_PROVIDER);
                    }else {
                        /*mCurrentLocation.setProvider(LocationManager.GPS_PROVIDER);
                        Toast.makeText(context, "disconnected", Toast.LENGTH_SHORT).show(); */
                    }
                }
            }
        };

        ibMyLocation = (ImageButton) findViewById(R.id.myLocation);
        ibSearch = (ImageButton) findViewById(R.id.searchButton);
        ibSetting = (ImageButton) findViewById(R.id.settingsButton);

        includeSearchInfo = (View) findViewById(R.id.includeBarSearch);
        ibIncludeMore = (ImageButton) includeSearchInfo.findViewById(R.id.search_more);
        ibIncludeClose = (ImageButton) includeSearchInfo.findViewById(R.id.search_close);
        etSearch = (AutoCompleteTextView) includeSearchInfo.findViewById(R.id.geo_autocomplete);
        gvNear = (GridView) includeSearchInfo.findViewById(R.id.gvNearMachines);
        lvHistory = (ListView)includeSearchInfo. findViewById(R.id.lvHistory);

        includeBasicInfo = (View) findViewById(R.id.includeBarBasicInfo);
        tvBasicMachineSerial = includeBasicInfo.findViewById(R.id.machineSerialText) ;
        tvBasicMachineAddress = includeBasicInfo.findViewById(R.id.machineAddressText) ;
        btnGetDirections = includeBasicInfo.findViewById(R.id.directions) ;
        btnMoreInfo = includeBasicInfo.findViewById(R.id.btnMoreInfo) ;

        includeNavigation= (View) findViewById(R.id.includeBarNavigation);
        llSearch = includeNavigation.findViewById(R.id.ll1) ;
        llNearMachine = includeNavigation.findViewById(R.id.ll2) ;
        llProfile = includeNavigation.findViewById(R.id.ll3) ;
        llAbout = includeNavigation.findViewById(R.id.ll4) ;
        llLogout = includeNavigation.findViewById(R.id.ll6) ;

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
        slide_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);

        historyDatabase = new SearchHistory(this) ;

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

        manager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MapsActivity.this,"Kindly Enable GPS",Toast.LENGTH_SHORT).show();
            enableLoc();
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

        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.GONE){
                    includeNavigation.setVisibility(View.VISIBLE);
                    includeNavigation.startAnimation(slide_right);
                }
            }
        });

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.VISIBLE){
                    includeNavigation.startAnimation(slide_left);
                    includeNavigation.setVisibility(View.GONE);
                }
                if (includeSearchInfo.getVisibility() == View.GONE){
                    includeSearchInfo.setVisibility(View.VISIBLE);
                    includeSearchInfo.startAnimation(slide_up);
                }
                if(includeBasicInfo.getVisibility() == View.VISIBLE){
                    includeBasicInfo.startAnimation(slide_down);
                    includeBasicInfo.setVisibility(View.GONE);
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
                if (includeSearchInfo.getVisibility() == View.VISIBLE){
                    includeSearchInfo.startAnimation(slide_down);
                    includeSearchInfo.setVisibility(View.GONE);
                }
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.VISIBLE){
                    includeNavigation.startAnimation(slide_left);
                    includeNavigation.setVisibility(View.GONE);
                }
                if (includeSearchInfo.getVisibility() == View.GONE){
                    includeSearchInfo.setVisibility(View.VISIBLE);
                    includeSearchInfo.startAnimation(slide_up);
                }
            }
        });

        llNearMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.VISIBLE){
                    includeNavigation.startAnimation(slide_left);
                    includeNavigation.setVisibility(View.GONE);
                }
                pointToNearest();
            }
        });

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.VISIBLE){
                    includeNavigation.startAnimation(slide_left);
                    includeNavigation.setVisibility(View.GONE);
                }
                startActivity(new Intent(MapsActivity.this , ProfileActivity.class));
            }
        });

        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.VISIBLE){
                    includeNavigation.startAnimation(slide_left);
                    includeNavigation.setVisibility(View.GONE);
                }

            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeNavigation.getVisibility() == View.VISIBLE){
                    includeNavigation.startAnimation(slide_left);
                    includeNavigation.setVisibility(View.GONE);
                }
                editorLoginStatus.putBoolean("LoginStatus",false);
                editorLoginStatus.commit();
                startActivity(new Intent(MapsActivity.this , MainActivity.class));
                finish();
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    pDialog = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setCancelable(false);
                    pDialog.setTitleText("Find all Machines near");
                    pDialog.setContentText(etSearch.getText().toString());
                    pDialog.show();
                    FindAllSearchMachines fasm = new FindAllSearchMachines(MapsActivity.this, etSearch.getText().toString(), new AsyncResponseFindAllSearches() {
                        @Override
                        public void processFinish(ArrayList<MarkerModel> output) {
                            hideKeyboard(MapsActivity.this);
                            if (pDialog.isShowing()){
                                pDialog.dismissWithAnimation();
                            }
                            if (output.size() > 0){
                                CustomSearchListAdapter csla = new CustomSearchListAdapter(MapsActivity.this , output);
                                DialogPlus dialog = DialogPlus.newDialog(MapsActivity.this)
                                        .setAdapter(csla)
                                        .setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                                MarkerModel mm = (MarkerModel) item ;
                                                dialog.dismiss();
                                                showSearchedMarker(mm);
                                            }
                                        })
                                        .setExpanded(true)
                                        .create();
                                dialog.show();
                            }else{
                                Toast.makeText(MapsActivity.this, "No Machines Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    fasm.execute() ;
                    return true;
                }
                return false;
            }
        });


        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoryModel hm = (HistoryModel) adapterView.getItemAtPosition(i) ;
                showSearchHistoryMarker(hm);
            }
        });

        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMarker.getPosition().latitude != mCurrentLocation.getLatitude()) {
                    pDialog = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setCancelable(false);
                    pDialog.setTitleText("Getting Directions");
                    pDialog.show();
                    LatLng origin = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    LatLng destination = selectedMarker.getPosition();
                    DownloadUrl du = new DownloadUrl();
                    String url = du.getDirectionsUrl(origin, destination);
                    DownloadTask downloadTask = new DownloadTask(new AsyncResponseDownload() {
                        @Override
                        public void processFinish(PolylineOptions[] output) {
                            if (pDialog.isShowing()) pDialog.dismissWithAnimation();
                            drawPath(output,selectedMarker,myCurrentLocMarker);
                            includeBasicInfo.startAnimation(slide_down);
                            includeBasicInfo.setVisibility(View.GONE);
                        }
                    });
                    downloadTask.execute(url);
                }
            }
        });

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String company1="",company1quantity="",company2="",company2quantity="",address="",serialno="",access="",status="",type="";
                String cost= "";
                for (MarkerModel mm : allShowingMarkers) {
                    if (mm.getMarker().getPosition().latitude == selectedMarker.getPosition().latitude) {
                        address = mm.getAddress();
                        serialno = mm.getSerial_no();
                        access = mm.getAccess();
                        status = mm.getStatus();
                        type = mm.getType();
                        company1 = mm.getCompany1();
                        company1quantity = String.valueOf(mm.getCompany1quantity()) ;
                        company2 = mm.getCompany2();
                        company2quantity = String.valueOf(mm.getCompany2quantity());
                    }}
                    Intent intent = new Intent(MapsActivity.this, DetailedMachineInfo.class);
                    intent.putExtra("address",address);
                    intent.putExtra("serialno",serialno);
                    intent.putExtra("access",access);
                    intent.putExtra("status",status);
                    intent.putExtra("type",type);
                    intent.putExtra("cost",cost);
                    intent.putExtra("company1",company1);
                    intent.putExtra("company1quantity",company1quantity);
                    intent.putExtra("company2",company2);
                    intent.putExtra("company2quantity",company2quantity);
                    startActivity(intent);

            }
        });

    }

    private void enableLoc() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        enableLoc();
                        //settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
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

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

        showHistory();

        /*MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter);*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showBasicInfo(marker);
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                showBasicInfo(marker);
            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                if(includeBasicInfo.getVisibility() == View.VISIBLE){
                    includeBasicInfo.startAnimation(slide_down);
                    includeBasicInfo.setVisibility(View.GONE);
                }

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Toast.makeText(MapsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isNetworkAvailable() ) {
            Toast.makeText(this, "Finding Nearby Machines", Toast.LENGTH_SHORT).show();
            findNearTask(1,true);
        }
    }

    public void showBasicInfo(Marker marker){
        if(includeBasicInfo.getVisibility() == View.VISIBLE){
            includeBasicInfo.startAnimation(slide_down);
            includeBasicInfo.setVisibility(View.GONE);
        }

        for (int i=0 ; i < pl.length ; i++){
            if (pl[i] != null ){
                pl[i].remove();
            }
        }

        if (mCurrentLocation.getLatitude()!=marker.getPosition().latitude) {

            if (includeBasicInfo.getVisibility() == View.GONE){
                for (MarkerModel mm : allShowingMarkers){
                    if ((String.valueOf(mm.getMarker())).equals(String.valueOf(marker))){
                        tvBasicMachineSerial.setText(""+mm.getSerial_no().toString());
                        tvBasicMachineAddress.setText(""+mm.getAddress());
                        selectedMarker = marker ;
                        break;
                    }
                }
                includeBasicInfo.setVisibility(View.VISIBLE);
                includeBasicInfo.startAnimation(slide_up);
            }}
    }

    private void drawPath(PolylineOptions[] output,Marker dest,Marker origin) {

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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(dest.getPosition());
            builder.include(origin.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveMyLocCamera = true ;
            }
        }, 2000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location Permission not given", Toast.LENGTH_SHORT).show();
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("LOCATION", "Location update started ..............: ");
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (!isNetworkAvailable()){
            if (!alertInternet.isShowing()) alertInternet.show();
        }else {
            if (alertInternet.isShowing()) alertInternet.dismiss();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if (!alertGPS.isShowing()) alertGPS.show();
        }else {
            if (alertGPS.isShowing()) alertGPS.dismiss();
        }

        mCurrentLocation = location;
        Log.i("LOCATION" , ""+mCurrentLocation.getLatitude() + "  " + mCurrentLocation.getLongitude()) ;
        editorLocation.putString("Latitude" , String.valueOf(mCurrentLocation.getLatitude()));
        editorLocation.putString("Longitude" , String.valueOf(mCurrentLocation.getLongitude()));
        editorLocation.commit();
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isNetworkAvailable() ) {
            findNearTask(1, false);
        }
        showHistory();
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (mCurrentLocation!=null) {

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

    public void findNearTask(int count , final boolean show){
        if (mCurrentLocation != null){
            final int[] km = {count};
            FindNearMachines fnm = new FindNearMachines(MapsActivity.this , mCurrentLocation, String.valueOf(km[0]) , new AsyncResponseFindNear(){
                @Override
                public void processFinish(String output) {
                    if (!output.equalsIgnoreCase("[]")){
                        if (show) Toast.makeText(MapsActivity.this, "Showing all Nearby Machines", Toast.LENGTH_SHORT).show();
                        addToNearGrid(output);
                        findNear(output);
                    }else {
                        if (km[0] < 5) findNearTask(km[0]+2 , show);
                        else {
                            if (show)Toast.makeText(MapsActivity.this, "No Nearest Machine Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            fnm.execute() ;
        }else{
            Toast.makeText(MapsActivity.this, "Getting your location..", Toast.LENGTH_SHORT).show();
        }
    }

    public void addToNearGrid(String result){
        ArrayList<MarkerModel> nearGridList = new ArrayList<>();
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i < 8){
                    JSONObject object = jsonArray.getJSONObject(i);
                    double latitude = object.getDouble("latitude");
                    double longitude = object.getDouble("longitude");
                    String address = object.getString("address");
                    String address_tags = object.getString("address_tags");
                    String machine_serial_no = object.getString("machine_serial_no");
                    String access = object.getString("access");
                    String status = object.getString("status");
                    String company1 = object.getString("company1");
                    int company1quantity = object.getInt("company1quantity");
                    String company2 = object.getString("company2");
                    int company2quantity = object.getInt("company2quantity");
                    String type = object.getString("type");
                    LatLng ll = new LatLng(latitude,longitude) ;
                    MarkerModel mm = new MarkerModel(ll,address,address_tags,machine_serial_no,access,status,company1,company1quantity,company2,company2quantity,type);
                    nearGridList.add(mm);
                }
            }
        } catch (Exception e) {
        }

        NearMachinesAdapter nma = new NearMachinesAdapter(nearGridList,MapsActivity.this);
        gvNear.setAdapter(nma);
    }

    public void findNear(String result){
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                    boolean present = false ;
                    JSONObject object = jsonArray.getJSONObject(i);
                    double latitude = object.getDouble("latitude");
                    double longitude = object.getDouble("longitude");
                    String address = object.getString("address");
                    String address_tags = object.getString("address_tags");
                    String machine_serial_no = object.getString("machine_serial_no");
                    String access = object.getString("access");
                    String status = object.getString("status");
                    String company1 = object.getString("company1");
                    int company1quantity = object.getInt("company1quantity");
                    String company2 = object.getString("company2");
                    int company2quantity = object.getInt("company2quantity");
                    String type = object.getString("type");
                    LatLng ll = new LatLng(latitude,longitude) ;
                    for (MarkerModel allMM : allShowingMarkers){
                        if (allMM.getAddress().equals(address)) present = true ;
                    }
                    if (!present) {
                        Marker m = mMap.addMarker(new MarkerOptions().title("Status : " + (status.equalsIgnoreCase("yes") ? "Working" : "Not Working")).snippet("Quantity : " + (company1quantity+company2quantity)).position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)));
                        MarkerModel mm = new MarkerModel(m,ll,address,address_tags,machine_serial_no,access,status,company1,company1quantity,company2,company2quantity,type);
                        allShowingMarkers.add(mm);
                    }
            }
        } catch (Exception e) {
        }
    }

    public void findSearchNear(String result){
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                boolean present = false ;
                JSONObject object = jsonArray.getJSONObject(i);
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                String address = object.getString("address");
                String address_tags = object.getString("address_tags");
                String machine_serial_no = object.getString("machine_serial_no");
                String access = object.getString("access");
                String status = object.getString("status");
                String company1 = object.getString("company1");
                int company1quantity = object.getInt("company1quantity");
                String company2 = object.getString("company2");
                int company2quantity = object.getInt("company2quantity");
                String type = object.getString("type");
                LatLng ll = new LatLng(latitude,longitude) ;
                for (MarkerModel allMM : allShowingMarkers){
                    if (allMM.getAddress().equals(address)) present = true ;
                }
                if (!present) {
                    Marker m = mMap.addMarker(new MarkerOptions().title("Status : " + (status.equalsIgnoreCase("yes") ? "Working" : "Not Working")).snippet("Quantity : " + (company1quantity+company2quantity)).position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)));
                    MarkerModel mm = new MarkerModel(m, ll, address, address_tags, machine_serial_no, access, status,company1,company1quantity,company2,company2quantity,type);
                    allShowingMarkers.add(mm);
                }
            }
        } catch (Exception e) {
        }
    }

    public void pointToNearest(){

        if (!allShowingMarkers.isEmpty()){
            float minDist = Float.MAX_VALUE;
            double minLat = 0 ;
            double minLong = 0;
            for (MarkerModel mm : allShowingMarkers){
                double latitude = mm.getLatLng().latitude ;
                double longitude = mm.getLatLng().longitude ;
                float[] dist = new float[2] ;
                Location.distanceBetween(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude() ,
                        latitude , longitude , dist);
                if (dist[0] <= minDist){
                    minDist = dist[0] ;
                    minLat = latitude ;
                    minLong = longitude ;
                }
            }

            final LatLng minLL = new LatLng(minLat,minLong) ;

            for (MarkerModel m : allShowingMarkers){
                if (m.getMarker().getPosition().latitude == minLL.latitude){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(minLL, 18)) ;
                    m.getMarker().showInfoWindow();
                    showBasicInfo(m.getMarker());
                }
            }
        }else{
            Toast.makeText(this, "No Nearby Machines! You can Kindly Search", Toast.LENGTH_SHORT).show();
        }


    }

    public void showHistory(){
        Cursor data ;
        ArrayList<HistoryModel> historyList = new ArrayList<>();
        data = historyDatabase.getListContents();
        while (data.moveToNext()){
            historyList.add(new HistoryModel(data.getString(1),data.getString(2),data.getString(3)
                    ,data.getString(4),data.getString(5) ));
        }
        CustomHistoryAdapter cha = new CustomHistoryAdapter(MapsActivity.this , historyList) ;
        cha.notifyDataSetChanged();
        lvHistory.setAdapter(cha);
    }

    private void showSearchHistoryMarker(HistoryModel hm) {
        boolean present = false ;
        final Marker m;
        if (includeSearchInfo.getVisibility() == View.VISIBLE){
            includeSearchInfo.startAnimation(slide_down);
            includeSearchInfo.setVisibility(View.GONE);
        }
        for (MarkerModel allMM : allShowingMarkers){
            if (String.valueOf(allMM.getLatLng()).equals(hm.getLatLng())) {
                present = true ;
                selectedMarker = allMM.getMarker() ;
                break;
            }
        }
        if (present){
            selectedMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 16));
            showBasicInfo(selectedMarker);
        }else {
            pDialog = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setCancelable(false);
            pDialog.setTitleText("Getting the machine current status");
            pDialog.show();
            SearchHistoryStatusTask shst = new SearchHistoryStatusTask(MapsActivity.this, hm.getSerial_no(), new AsyncResponseFindSearch() {
                @Override
                public void processFinish(String output) {
                    try {
                        JSONArray jsonArray =new JSONArray(output);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            double latitude = object.getDouble("latitude");
                            double longitude = object.getDouble("longitude");
                            String address = object.getString("address");
                            String address_tags = object.getString("address_tags");
                            String machine_serial_no = object.getString("machine_serial_no");
                            String access = object.getString("access");
                            String status = object.getString("status");
                            String company1 = object.getString("company1");
                            int company1quantity = object.getInt("company1quantity");
                            String company2 = object.getString("company2");
                            int company2quantity = object.getInt("company2quantity");
                            String type = object.getString("type");
                            LatLng ll = new LatLng(latitude,longitude) ;
                            Marker m = mMap.addMarker(new MarkerOptions().title("Status : " + (status.equalsIgnoreCase("yes") ? "Working" : "Not Working")).snippet("Quantity : " + (company1quantity+company2quantity)).position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)));
                            MarkerModel mm = new MarkerModel(m, ll, address, address_tags, machine_serial_no, access, status,company1,company1quantity,company2,company2quantity,type);
                            allShowingMarkers.add(mm);
                            selectedMarker = m ;
                            selectedMarker.showInfoWindow();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 16));
                            showBasicInfo(selectedMarker);
                        }
                    } catch (Exception e) {
                        Log.i("EXCEPTION" , e.toString()) ;
                    }
                    if (pDialog.isShowing()){
                        pDialog.dismissWithAnimation();
                    }
                }
            });
            shst.execute() ;
        }
    }

    public void showSearchedMarker(MarkerModel mmAddress){
        boolean present = false ;
        final Marker m;
        etSearch.setText("");
        if (includeSearchInfo.getVisibility() == View.VISIBLE){
            includeSearchInfo.startAnimation(slide_down);
            includeSearchInfo.setVisibility(View.GONE);
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm") ;
        String time = sdf.format(date) ;
        historyDatabase.addData(mmAddress.getSerial_no() ,String.valueOf(mmAddress.getLatLng()),mmAddress.getAddress()
                ,mmAddress.getAddress_tags(),time) ;

        LatLng ll = mmAddress.getLatLng() ;
        for (MarkerModel allMM : allShowingMarkers){
            if (allMM.getAddress().equals(mmAddress.getAddress())) {
                present = true ;
                selectedMarker = allMM.getMarker() ;
                break;
            }
        }
        if (!present) {
            m = mMap.addMarker(new MarkerOptions().title("Status : " + (mmAddress.getStatus().equalsIgnoreCase("yes") ? "Working" : "Not Working")).snippet("Quantity : " + (mmAddress.getCompany1quantity()+mmAddress.getCompany2quantity()) ).position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine)));
            mmAddress.setMarker(m);
            allShowingMarkers.add(mmAddress);
            selectedMarker = m ;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mmAddress.getLatLng(), 16));
            mmAddress.getMarker().showInfoWindow() ;
        }else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 16));
            selectedMarker.showInfoWindow() ;
        }
        showBasicInfo(selectedMarker);
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        /*if (isNetworkAvailable())
            temp  = new Location(LocationManager.NETWORK_PROVIDER);
        else
           temp  = new Location(LocationManager.GPS_PROVIDER); */
        temp.setLatitude(mmAddress.getLatLng().latitude);
        temp.setLongitude(mmAddress.getLatLng().longitude);
        FindNearMachines fnm = new FindNearMachines(MapsActivity.this , temp , "1", new AsyncResponseFindNear(){
            @Override
            public void processFinish(String output) {
                findSearchNear(output) ;
            }
        });
        fnm.execute() ;

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onBackPressed() {
        if (includeSearchInfo.getVisibility() == View.VISIBLE){
            includeSearchInfo.startAnimation(slide_down);
            includeSearchInfo.setVisibility(View.GONE);
        }else if(includeBasicInfo.getVisibility() == View.VISIBLE){
            includeBasicInfo.startAnimation(slide_down);
            includeBasicInfo.setVisibility(View.GONE);
        }else if (includeNavigation.getVisibility() == View.VISIBLE){
            includeNavigation.startAnimation(slide_left);
            includeNavigation.setVisibility(View.GONE);
        }else{
            for (int i=0 ; i < pl.length ; i++){
                if (pl[i] != null ){
                    pl[i].remove();
                }
            }
            //  super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to close this activity?")
                    .setIcon(R.drawable.ic_info_outline)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}