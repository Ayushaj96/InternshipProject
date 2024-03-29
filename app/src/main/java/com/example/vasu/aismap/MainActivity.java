package com.example.vasu.aismap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasu.aismap.FetchPHP.AsyncResponseFindSearch;
import com.example.vasu.aismap.FetchPHP.SendSMSTask;

public class MainActivity extends AppCompatActivity {

    Button  Rsignup;
    EditText mnumber;
    TextView Rlogin ;
    String mobile;
    SharedPreferences sharedPreferences,sharedPreferences2;
    SharedPreferences.Editor editor;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regis);

        mnumber = (EditText) findViewById(R.id.edMobile);

        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("LoginStatus",false)){
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);
            finish();
        }


        Rsignup=(Button) findViewById(R.id.Signup);

        Rsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String num="123456";
                mobile= mnumber.getText().toString();

                if(TextUtils.isEmpty(mobile))
                {
                    Toast.makeText(MainActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();


                }
                else
                if(mobile.length()>10||mobile.length()<10)
                {
                    Toast.makeText(MainActivity.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendSMSTask sendSMSTask = new SendSMSTask(MainActivity.this, mobile, String.valueOf(num), new AsyncResponseFindSearch() {
                        @Override
                        public void processFinish(String output) {
                            Intent intent = new Intent(MainActivity.this, SignUpOTPActivity.class);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("otp", num);
                            startActivity(intent);
                        }
                    });
                    sendSMSTask.execute();
                }
            }
        });


        Rlogin=(TextView) findViewById(R.id.Login);
        Rlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UserLoginActivity.class);
                startActivity(intent);
            }
        });

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission not Given", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
