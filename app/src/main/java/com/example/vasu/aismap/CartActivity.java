package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    String company1 = "" , company2 = "" ;
    int company1Quantity = 0 , company2Quantity = 0 ;

    Spinner companySpinner ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        companySpinner = findViewById(R.id.comapnySpinner);

        Intent i = getIntent();
        company1 = i.getStringExtra("company1");
        company1Quantity = Integer.parseInt(i.getStringExtra("company1quantity"));
        company2 = i.getStringExtra("company2");
        company2Quantity = Integer.parseInt(i.getStringExtra("company2quantity"));

        ArrayList<String> companies = new ArrayList<>();
        companies.add(company1) ;
        companies.add(company2) ;

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(spinnerAdapter);


    }
}
