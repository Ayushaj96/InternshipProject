package com.example.vasu.aismap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vasu.aismap.FetchPHP.AsyncResponseUserRegistration;
import com.example.vasu.aismap.FetchPHP.UserLoginTask;

import cn.pedant.SweetAlert.SweetAlertDialog;



public class UserLoginActivity  extends AppCompatActivity {

    EditText Email, Password;
    Button LogIn ;
    String PasswordHolder, EmailHolder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Email = (EditText)findViewById(R.id.email);
        Password = (EditText)findViewById(R.id.password);
        LogIn = (Button)findViewById(R.id.login);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();

            }
        });
    }
    public void loginUser(){
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(true);
        pDialog.show();

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

                    if (output.equalsIgnoreCase("Login Success")){
                        editor.putBoolean("LoginStatus",true);
                        editor.commit();
                        startActivity(new Intent(UserLoginActivity.this , MapsActivity.class));
                        pDialog.hide();
                        finish();
                    }
                    else {
                        pDialog.hide();
                        Toast.makeText(UserLoginActivity.this, "Invalid email Id or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ult.execute();
        }
    }


}