package com.example.vasu.aismap.InfoWindow;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vasu.aismap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Locale;


public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter ,GoogleMap.OnInfoWindowClickListener{

    private Context context;

    public MarkerInfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.map_marker_info_window, null);

        LatLng latLng = arg0.getPosition();
        ImageView ivavail = (ImageView) v.findViewById(R.id.ivAvailibility);
        ImageView ivMachine = (ImageView) v.findViewById(R.id.imageViewMachine);
        TextView place = (TextView) v.findViewById(R.id.tvPlace);
        TextView access = (TextView) v.findViewById(R.id.tvAccess);
        TextView directions = (TextView) v.findViewById(R.id.GetDirections);

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String knownName = addresses.get(0).getFeatureName();
            place.setText(knownName);

        } catch (Exception e) {

        }

        if (latLng.latitude == 28.7389592 || latLng.latitude == 28.7399352 || latLng.latitude == 28.7389592) {
            ivavail.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        } else{
            ivavail.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        }



        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(, "Please wait while fetching your lcoation", Toast.LENGTH_SHORT).show();
        Log.i("DIRECTIONSCLICK" , "YES") ;
    }
}
