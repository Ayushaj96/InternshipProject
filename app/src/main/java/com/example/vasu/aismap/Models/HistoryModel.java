package com.example.vasu.aismap.Models;

/**
 * Created by Vasu on 26-12-2017.
 */

public class HistoryModel {

    String name ;
    String address ;
    String time ;

    public HistoryModel(){

    }

    public HistoryModel(String name,String address , String time){
        this.name = name ;
        this.address = address ;
        this.time = time ;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
