package com.example.vasu.aismap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
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

        TextView place = (TextView) v.findViewById(R.id.text1);
        CircleImageView green = (CircleImageView) v.findViewById(R.id.greendot);
        CircleImageView red = (CircleImageView) v.findViewById(R.id.reddot);

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String knownName = addresses.get(0).getFeatureName();
            place.setText(knownName);

            if (latLng.latitude == 28.7389592 || latLng.latitude == 28.7399352 || latLng.latitude == 28.7389592) {
                green.setVisibility(v.INVISIBLE);
            } else

            {
                red.setVisibility(v.INVISIBLE);
            }


        } catch (Exception e) {

        }

        return v;
    }}
