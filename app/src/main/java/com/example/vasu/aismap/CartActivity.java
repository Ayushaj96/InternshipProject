package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    String address,machine_serial_no,access,status,company1,company2,type;
    int company1quantity , company2quantity ;

    Spinner companySpinner ;

    ImageButton ibIncQuantity , ibDecQuantity ;
    TextView tvCompany,tvType,tvQuantity,tvCost,tvTotalCost ;
    Button btnPay ;

    int cost = 0 ;
    int chosenCompany = 0 ;

    CardView cardDetails ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cardDetails = findViewById(R.id.cardView) ;

        companySpinner = findViewById(R.id.comapnySpinner);
        ibIncQuantity = findViewById(R.id.ibIncQuantity);
        ibDecQuantity = findViewById(R.id.ibDecQuantity);

        tvCompany = findViewById(R.id.tvCompany);
        tvType = findViewById(R.id.tvType);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvCost = findViewById(R.id.tvCost);
        tvTotalCost = findViewById(R.id.tvTotalCost);

        btnPay = findViewById(R.id.btnPay);

        Intent i = getIntent();
        company1 = i.getStringExtra("company1");
        company1quantity = i.getIntExtra("company1quantity" , 0) ;
        company2 = i.getStringExtra("company2");
        company2quantity = i.getIntExtra("company2quantity" , 0) ;

        ArrayList<String> companies = new ArrayList<>();
        companies.add("Select a Company") ;
        companies.add(company1) ;
        companies.add(company2) ;

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(spinnerAdapter);

       companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               String temp = ""+adapterView.getItemAtPosition(i) ;
                if (!temp.equalsIgnoreCase("Select a Company")){
                    if (i == 1){
                        chosenCompany = 1 ;
                    }else if(i == 2){
                        chosenCompany = 2 ;
                    }
                    setValues(temp);
                }else {
                    if (cardDetails.getVisibility() == View.VISIBLE){
                        cardDetails.setVisibility(View.INVISIBLE);
                    }
                }

           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

       ibIncQuantity.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               int temp = Integer.parseInt(tvQuantity.getText().toString());
               if (temp < (chosenCompany == 1 ? company1quantity : company2quantity)){
                   temp += 1 ;
                   int totalCost = temp*cost ;
                   tvQuantity.setText(""+temp);
                   tvTotalCost.setText("Total Cost : " + totalCost+"");
               }else {
                   Toast.makeText(CartActivity.this, "Cannot go grater than quantity", Toast.LENGTH_SHORT).show();
               }
           }
       });

        ibDecQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = Integer.parseInt(tvQuantity.getText().toString());
                if (temp > 0){
                    temp -= 1 ;
                    int totalCost = temp*cost ;
                    tvQuantity.setText(""+temp);
                    tvTotalCost.setText("Total Cost : " + totalCost+"");
                }else {
                    Toast.makeText(CartActivity.this, "Cannot go less than 0", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    public void setValues(String company){

        if (cardDetails.getVisibility() == View.INVISIBLE){
            cardDetails.setVisibility(View.VISIBLE);
        }

        tvCompany.setText(company.substring(0,company.indexOf("-")));

        if (company.contains("hd")){
            tvType.setText("Heavy Days");
            tvCost.setText("Rs. 10 per product");
            cost = 10 ;
        }else {
            tvType.setText("Light Days");
            tvCost.setText("Rs. 5 per product");
            cost = 5 ;
        }

        tvTotalCost.setText("0");
        tvQuantity.setText("0");


    }

}
