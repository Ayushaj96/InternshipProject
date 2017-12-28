package com.example.vasu.aismap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    RelativeLayout Rsignup,Rlogin;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("LoginStatus",false)){
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);
            finish();
        }

        Rsignup=(RelativeLayout)findViewById(R.id.Signup);
        Rsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UserSignupActivity.class);
                startActivity(intent);
            }
        });
        Rlogin=(RelativeLayout)findViewById(R.id.Login);
        Rlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UserLoginActivity.class);
                startActivity(intent);
            }
        });

    }

}
