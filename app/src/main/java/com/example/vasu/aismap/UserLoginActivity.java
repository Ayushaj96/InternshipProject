package com.example.vasu.aismap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vasu.aismap.FetchPHP.AsyncResponseUserRegistration;
import com.example.vasu.aismap.FetchPHP.UserLoginTask;
import com.example.vasu.aismap.FetchPHP.UserRegistrationTask;

import java.util.HashMap;

/**
 * Created by AYUSH on 12/28/2017.
 */

public class UserLoginActivity  extends AppCompatActivity {

    EditText Email, Password;
    RelativeLayout LogIn ;
    String PasswordHolder, EmailHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        Email = (EditText)findViewById(R.id.email);
        Password = (EditText)findViewById(R.id.password);
        LogIn = (RelativeLayout) findViewById(R.id.login);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();

            }
        });
    }
    public void loginUser(){

        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        if(TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder))
        {
            Toast.makeText(this, "Something Is missing", Toast.LENGTH_SHORT).show();
        }
        else {
            UserLoginTask ult = new UserLoginTask(UserLoginActivity.this, EmailHolder, PasswordHolder, new AsyncResponseUserRegistration() {
                @Override
                public void processFinish(String output) {
                    Toast.makeText(UserLoginActivity.this, ""+output, Toast.LENGTH_SHORT).show();
                    if (output.equalsIgnoreCase("Login Success")){
                        startActivity(new Intent(UserLoginActivity.this , MapsActivity.class));
                        finish();
                    }
                }
            });
            ult.execute();
        }
    }


}