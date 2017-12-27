package com.example.vasu.aismap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasu.aismap.CustomAdapter.CustomHistoryAdapter;
import com.example.vasu.aismap.CustomAdapter.CustomSearchListAdapter;
import com.example.vasu.aismap.CustomAdapter.NearMachinesAdapter;
import com.example.vasu.aismap.Directions.AsyncResponseDownload;
import com.example.vasu.aismap.Directions.DownloadTask;
import com.example.vasu.aismap.Directions.DownloadUrl;
import com.example.vasu.aismap.FetchPHP.AsyncResponseFindAllSearches;
import com.example.vasu.aismap.FetchPHP.AsyncResponseFindNear;
import com.example.vasu.aismap.FetchPHP.FindAllSearchMachines;
import com.example.vasu.aismap.FetchPHP.FindNearMachines;
import com.example.vasu.aismap.Models.HistoryModel;
import com.example.vasu.aismap.Models.MarkerModel;
import com.example.vasu.aismap.SearchPlace.AsyncResponseSearch;
import com.example.vasu.aismap.SearchPlace.GetSearchLocation;
import com.example.vasu.aismap.Sqlite.SearchHistory;
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
import com.google.android.gms.maps.model.LatLng;
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
    Location mCurrentLocation = new Location("My Location");

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LatLng position = new LatLng(28.6291027, 77.207133);
    MarkerOptions markerOptionsMyLoc;
    Marker myCurrentLocMarker, mPrevLocMarker;

    ImageButton ibMyLocation , ibSearch , ibNearest , ibIncludeMore , ibIncludeClose;

    SearchHistory historyDatabase ;

    ArrayList<MarkerModel> nearMarkersList = new ArrayList<>() ;
    ArrayList<MarkerModel> nearSearchMarkersList = new ArrayList<>() ;

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
    AutoCompleteTextView etSearch ;
    GridView gvNear ;
    ListView lvHistory ;

    Animation slide_down , slide_up ;

    View includeBasicInfo ;
    TextView tvBasicMachineSerial, tvBasicMachineAddress;
    Button btnGetDirections , btnMoreInfo;

    Marker selectedMarker ;

    SweetAlertDialog pDialog ;

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
        etSearch = (AutoCompleteTextView) findViewById(R.id.geo_autocomplete);
        gvNear = (GridView) findViewById(R.id.gvNearMachines);
        lvHistory = (ListView) findViewById(R.id.lvHistory);

        includeBasicInfo = (View) findViewById(R.id.includeBarBasicInfo);
        tvBasicMachineSerial = includeBasicInfo.findViewById(R.id.machineSerialText) ;
        tvBasicMachineAddress = includeBasicInfo.findViewById(R.id.machineAddressText) ;
        btnGetDirections = includeBasicInfo.findViewById(R.id.directions) ;
        btnMoreInfo = includeBasicInfo.findViewById(R.id.btnMoreInfo) ;

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        sharedPreferences =getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sharedPreferencesLocation =getApplicationContext().getSharedPreferences("MyLocation", MODE_PRIVATE);
        editorLocation = sharedPreferencesLocation.edit();
        zoom = Float.parseFloat(sharedPreferences.getString("Zoom" , "15.0"));
        radius = Float.parseFloat(sharedPreferences.getString("Radius" , "100.0"));

        historyDatabase = new SearchHistory(this) ;

        pDialog = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCancelable(false);

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

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    FindAllSearchMachines fasm = new FindAllSearchMachines(MapsActivity.this, etSearch.getText().toString(), new AsyncResponseFindAllSearches() {
                        @Override
                        public void processFinish(ArrayList<String> output) {
                            hideKeyboard(MapsActivity.this);
                            CustomSearchListAdapter csla = new CustomSearchListAdapter(MapsActivity.this , output);
                            DialogPlus dialog = DialogPlus.newDialog(MapsActivity.this)
                                    .setAdapter(csla)
                                    .setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                            dialog.dismiss();
                                            String address = item.toString() ;
                                            showSearchedMarker(address,position);
                                        }
                                    })
                                    .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                    .create();
                            dialog.show();
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
                String address = hm.getAddress();
                showSearchedMarker(address,i);
            }
        });

        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMarker.getPosition().latitude != mCurrentLocation.getLatitude()) {
                    LatLng origin = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    LatLng destination = selectedMarker.getPosition();
                    DownloadUrl du = new DownloadUrl();
                    String url = du.getDirectionsUrl(origin, destination);
                    DownloadTask downloadTask = new DownloadTask(new AsyncResponseDownload() {
                        @Override
                        public void processFinish(PolylineOptions[] output) {
                            drawPath(output);
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
                String company="",address="",serialno="",access="",status="",type="";
                String cost= "";
                for (MarkerModel mm : nearMarkersList) {
                    if (mm.getMarker().getPosition().latitude == selectedMarker.getPosition().latitude) {
                         address = mm.getAddress();
                         serialno = mm.getSerial_no();
                        access = mm.getAccess();
                        status = mm.getStatus();
                        type = mm.getType();
                        cost = String.valueOf(mm.getCost());
                        company = mm.getCompany();
                    }}
                    Intent intent = new Intent(MapsActivity.this, DetailedMachineInfo.class);
                    intent.putExtra("Address",address);
                    intent.putExtra("serialno",serialno);
                    intent.putExtra("access",access);
                        intent.putExtra("status",status);
                        intent.putExtra("type",type);
                        intent.putExtra("cost",cost);
                        intent.putExtra("company",company);
                    startActivity(intent);

            }
        });

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

        if (mCurrentLocation.getLatitude() == 0.0 && mCurrentLocation.getLongitude() == 0.0 ){
            mCurrentLocation.setLatitude((double) sharedPreferencesLocation.getFloat("Latitude" , 0.0f));
            mCurrentLocation.setLongitude((double) sharedPreferencesLocation.getFloat("Longitude" , 0.0f));
        }else{
        }

        showHistory();

        /*MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter);*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(includeBasicInfo.getVisibility() == View.VISIBLE){
                    includeBasicInfo.startAnimation(slide_down);
                    includeBasicInfo.setVisibility(View.GONE);
                }

                if (mCurrentLocation.getLatitude()!=marker.getPosition().latitude) {

                if (includeBasicInfo.getVisibility() == View.GONE){
                    for (MarkerModel mm : nearMarkersList){
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
                    int quantity = object.getInt("quantity");
                    String type = object.getString("type");
                    float cost = (float) object.getDouble("cost");
                    String company = object.getString("company");
                    LatLng ll = new LatLng(latitude,longitude) ;
                    MarkerModel mm = new MarkerModel(address,address_tags,machine_serial_no,access,status,quantity,type,cost,company);
                    nearGridList.add(mm);
                }
            }
        } catch (Exception e) {
        }

        NearMachinesAdapter nma = new NearMachinesAdapter(nearGridList,MapsActivity.this);
        gvNear.setAdapter(nma);
    }

    public void findNear(String result){
        /*mMap.clear();
        nearMarkersList.clear();*/
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Log.i("ARRAY" , String.valueOf(object)) ;
                    double latitude = object.getDouble("latitude");
                    double longitude = object.getDouble("longitude");
                    String address = object.getString("address");
                    String address_tags = object.getString("address_tags");
                    String machine_serial_no = object.getString("machine_serial_no");
                    String access = object.getString("access");
                    String status = object.getString("status");
                    int quantity = object.getInt("quantity");
                    String type = object.getString("type");
                    float cost = (float) object.getDouble("cost");
                    String company = object.getString("company");
                    LatLng ll = new LatLng(latitude,longitude) ;
                    Marker m = mMap.addMarker(new MarkerOptions().title("Status : " + (status.equalsIgnoreCase("yes") ? "Working" : "Not Working")).snippet("Quantity : " + quantity).position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine))) ;
                    MarkerModel mm = new MarkerModel(m,address,address_tags,machine_serial_no,access,status,quantity,type,cost,company);
                    nearMarkersList.add(mm);
            }
        } catch (Exception e) {
        }

    }

    public void findSearchNear(String result){
        for (MarkerModel mm : nearSearchMarkersList){
            mm.getMarker().remove();
        }
        nearSearchMarkersList.clear();
        try {
            JSONArray jsonArray =new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                String address = object.getString("address");
                String address_tags = object.getString("address_tags");
                String machine_serial_no = object.getString("machine_serial_no");
                String access = object.getString("access");
                String status = object.getString("status");
                int quantity = object.getInt("quantity");
                String type = object.getString("type");
                float cost = (float) object.getDouble("cost");
                String company = object.getString("company");
                LatLng ll = new LatLng(latitude,longitude) ;
                Marker m = mMap.addMarker(new MarkerOptions().title("Status : " + (status.equalsIgnoreCase("yes") ? "Working" : "Not Working")).snippet("Quantity : " + quantity).position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.machine))) ;
                MarkerModel mm = new MarkerModel(m,address,address_tags,machine_serial_no,access,status,quantity,type,cost,company);
                nearSearchMarkersList.add(mm);
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
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final LatLng minLL = new LatLng(minLat,minLong) ;

        for (MarkerModel m : nearMarkersList){
            if (m.getMarker().getPosition().latitude == minLL.latitude){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(minLL, 18)) ;
                m.getMarker().showInfoWindow();
            }else {
                //Toast.makeText(this, "NOT FOUND" + m.getMarker().getPosition(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void showHistory(){
        Cursor data ;
        ArrayList<HistoryModel> historyList = new ArrayList<>();
        data = historyDatabase.getListContents();
        while (data.moveToNext()){
            historyList.add(new HistoryModel(data.getString(1),data.getString(2),data.getString(3)));
        }
        CustomHistoryAdapter cha = new CustomHistoryAdapter(MapsActivity.this , historyList) ;
        cha.notifyDataSetChanged();
        lvHistory.setAdapter(cha);
    }

    public void showSearchedMarker(String address , int position){
        final Marker[] m = new Marker[1];
        pDialog.setTitleText("Finding the address");
        pDialog.show();
        etSearch.setText("");
        if (llSearchBar.getVisibility() == View.VISIBLE){
            llSearchBar.startAnimation(slide_down);
            llSearchBar.setVisibility(View.GONE);
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm") ;
        String time = sdf.format(date) ;
        historyDatabase.addData("A Machine" , address , time) ;
        GetSearchLocation gsl = new GetSearchLocation(MapsActivity.this, address, new AsyncResponseSearch() {
            @Override
            public void processFinish(LatLng output) {
                pDialog.dismissWithAnimation();
                if (output != null){
                    m[0] = mMap.addMarker(new MarkerOptions().position(output).title("Machine").icon(BitmapDescriptorFactory.fromResource(R.drawable.machine))) ;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m[0].getPosition() , 16));
                    m[0].showInfoWindow() ;
                    selectedMarker = m[0] ;
                    Location temp = new Location(LocationManager.GPS_PROVIDER);
                    temp.setLatitude(m[0].getPosition().latitude);
                    temp.setLongitude(m[0].getPosition().longitude);
                    FindNearMachines fnm = new FindNearMachines(MapsActivity.this , temp , new AsyncResponseFindNear(){
                        @Override
                        public void processFinish(String output) {
                            findSearchNear(output) ;
                        }
                    });
                    fnm.execute() ;
                }else{
                    Toast.makeText(MapsActivity.this, "Cant find Location ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gsl.execute() ;
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
        if (llSearchBar.getVisibility() == View.VISIBLE){
            llSearchBar.startAnimation(slide_down);
            llSearchBar.setVisibility(View.GONE);
        }else if(includeBasicInfo.getVisibility() == View.VISIBLE){
            includeBasicInfo.startAnimation(slide_down);
            includeBasicInfo.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

}