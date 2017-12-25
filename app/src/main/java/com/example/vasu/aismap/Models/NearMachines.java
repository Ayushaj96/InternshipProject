package com.example.vasu.aismap.Models;

import com.google.android.gms.maps.model.LatLng;


public class NearMachines {

    private String name ;
    private String address ;
    private LatLng position ;

    public NearMachines(){
    }

    public NearMachines(String name , String address , LatLng position){
        this.name = name ;
        this.address = address ;
        this.position = position ;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

}
