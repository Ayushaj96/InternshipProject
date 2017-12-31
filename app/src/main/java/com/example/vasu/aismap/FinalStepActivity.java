package com.example.vasu.aismap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasu.aismap.FetchPHP.AsyncResponseFindSearch;
import com.example.vasu.aismap.FetchPHP.SendSMSTask;

public class FinalStepActivity extends AppCompatActivity {

    String mobile,otp ;
    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_step);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);

        TextView tvOtp = findViewById(R.id.tvOtp);

        Intent i = getIntent();

        otp = i.getStringExtra("OTP") ;
        mobile = sharedPreferences.getString("Mobile" , "");
        String message = "Thank You for purchasing the product. Your one time password (OTP) for your mobile number ("+mobile+") is : "+otp ;

        SendSMSTask sendSMSTask=new SendSMSTask(FinalStepActivity.this, ""+mobile,message, new AsyncResponseFindSearch() {
            @Override
            public void processFinish(String output) {

            }
        });
        sendSMSTask.execute();

        Log.i("TRANSACTION" , "Final otp : " + otp ) ;
        tvOtp.setText(otp);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FinalStepActivity.this , MapsActivity.class));
        finish();
    }
}
