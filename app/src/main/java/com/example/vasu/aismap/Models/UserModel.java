package com.example.vasu.aismap.Models;

/**
 * Created by Vasu on 28-12-2017.
 */

public class UserModel {

    private String full_name;
    private String email;
    private String username;
    private String mobile;
    private String dob;
    private String profession;

    public UserModel(){

    }

    public UserModel(String full_name,String email,String username,String mobile,String dob,String profession){
        this.full_name = full_name ;
        this.email = email ;
        this.username = username ;
        this.mobile = mobile ;
        this.dob = dob ;
        this.profession = profession ;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

}
