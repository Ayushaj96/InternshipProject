package com.example.vasu.aismap.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Vasu on 26-12-2017.
 */

public class MarkerModel{

    private Marker marker;
    private LatLng latLng ;
    private String address;
    private String address_tags;
    private String serial_no ;
    private String access ;
    private String status ;
    private int quantity ;
    private String type ;
    private float cost ;
    private String company ;

    public MarkerModel(){

    }

    public MarkerModel(Marker marker, String address, String access){
        this.marker = marker ;
        this.address = address ;
        this.access = access ;
    }

    public MarkerModel(Marker marker,LatLng latLng ,String address,String address_tags,String serial_no ,String access ,String status ,int quantity ,
                       String type ,float cost ,String company){

        this.marker = marker ;
        this.address = address ;
        this.address_tags = address_tags ;
        this.serial_no = serial_no ;
        this.access = access ;
        this.status = status ;
        this.quantity = quantity ;
        this.type = type ;
        this.cost = cost ;
        this.company = company ;
        this.latLng = latLng ;

    }

    public MarkerModel(LatLng latLng , String address, String address_tags, String serial_no , String access , String status , int quantity ,
                       String type , float cost , String company){

        this.address = address ;
        this.address_tags = address_tags ;
        this.serial_no = serial_no ;
        this.access = access ;
        this.status = status ;
        this.quantity = quantity ;
        this.type = type ;
        this.cost = cost ;
        this.company = company ;
        this.latLng = latLng ;

    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
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

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


}
