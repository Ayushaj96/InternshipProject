package com.example.vasu.aismap.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Vasu on 29-12-2017.
 */

public class HistoryModel {

    private String latLng ;
    private String address;
    private String address_tags;
    private String serial_no ;
    private String time ;

    public HistoryModel(){

    }

    public HistoryModel(String serial_no,String latLng,String address,String address_tags,String time){

        this.time = time ;
        this.address = address ;
        this.address_tags = address_tags ;
        this.serial_no = serial_no ;
        this.latLng = latLng ;

    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_tags() {
        return address_tags;
    }

    public void setAddress_tags(String address_tags) {
        this.address_tags = address_tags;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
