package com.example.vasu.aismap.Models;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Vasu on 26-12-2017.
 */

public class MarkerModel {

    private Marker marker;
    private String address;
    private String availibility ;

    public MarkerModel(){

    }

    public MarkerModel(Marker marker, String address, String availibility){
        this.marker = marker ;
        this.address = address ;
        this.availibility = availibility ;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvailibility() {
        return availibility;
    }

    public void setAvailibility(String availibility) {
        this.availibility = availibility;
    }

}
