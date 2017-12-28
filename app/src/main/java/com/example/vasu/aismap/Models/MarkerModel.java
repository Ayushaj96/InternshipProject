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
    private String company1;
    private int company1quantity;
    private String company2;
    private int company2quantity;
    private String type ;

    public MarkerModel(){

    }

    public MarkerModel(Marker marker, String address, String access){
        this.marker = marker ;
        this.address = address ;
        this.access = access ;
    }

    public MarkerModel(Marker marker,LatLng latLng ,String address,String address_tags,String serial_no
            ,String access,String status,String company1,int company1quantity,String company2,int company2quantity,String type){

        this.marker = marker ;
        this.address = address ;
        this.address_tags = address_tags ;
        this.serial_no = serial_no ;
        this.access = access ;
        this.status = status ;
        this.company1 = company1;
        this.company1quantity = company1quantity;
        this.company2 = company2;
        this.company2quantity = company2quantity;
        this.type = type ;
        this.latLng = latLng ;
    }

    public MarkerModel(LatLng latLng ,String address,String address_tags,String serial_no
            ,String access,String status,String company1,int company1quantity,String company2,int company2quantity,String type){

        this.address = address ;
        this.address_tags = address_tags ;
        this.serial_no = serial_no ;
        this.access = access ;
        this.status = status ;
        this.company1 = company1;
        this.company1quantity = company1quantity;
        this.company2 = company2;
        this.company2quantity = company2quantity;
        this.type = type ;
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

    public String getCompany1() {
        return company1;
    }

    public void setCompany1(String company1) {
        this.company1 = company1;
    }

    public int getCompany1quantity() {
        return company1quantity;
    }

    public void setCompany1quantity(int company1quantity) {
        this.company1quantity = company1quantity;
    }

    public String getCompany2() {
        return company2;
    }

    public void setCompany2(String company2) {
        this.company2 = company2;
    }

    public int getCompany2quantity() {
        return company2quantity;
    }

    public void setCompany2quantity(int company2quantity) {
        this.company2quantity = company2quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
