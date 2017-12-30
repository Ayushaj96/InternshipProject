package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class FinalStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_step);

        TextView tvOtp = findViewById(R.id.tvOtp);

        Intent i = getIntent();

        Log.i("TRANSACTION" , i.getStringExtra("OTP")) ;
        tvOtp.setText(i.getStringExtra("OTP"));

    }

}
