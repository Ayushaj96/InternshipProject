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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;



public class UserLoginActivity  extends AppCompatActivity {

    EditText Email, Password;
    Button LogIn ;
    String PasswordHolder, EmailHolder;
    SharedPreferences sharedPreferences , sharedPreferencesMyInfo;
    SharedPreferences.Editor editor , editorMyInfo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        sharedPreferencesMyInfo=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editorMyInfo = sharedPreferencesMyInfo.edit();

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

        String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

                "\\@" +

                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                "(" +

                "\\." +

                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

                ")+";



        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        Matcher matcher= Pattern.compile(validemail).matcher(EmailHolder);

        if(TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder))
        {
            Toast.makeText(this, "Something Is missing", Toast.LENGTH_SHORT).show();
        }

        else
            if(!matcher.matches())
            {
                Email.setError("Invalid Email" );

            }
        else {

                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Checking Credentials");
                pDialog.setCancelable(true);
                pDialog.show();
            UserLoginTask ult = new UserLoginTask(UserLoginActivity.this, EmailHolder, PasswordHolder, new AsyncResponseUserRegistration() {


                @Override
                public void processFinish(String output) {
                    if (output.startsWith("Login Success")){



                        String result = output.substring(output.indexOf("[") , output.length()) ;
                        try {
                            JSONArray jsonArray =new JSONArray(result);
                            JSONObject object = jsonArray.getJSONObject(0);
                            String full_name = object.getString("full_name");
                            String email = object.getString("email");
                            String mobile = object.getString("mobile");
                            String username = object.getString("username");
                            String dob = object.getString("dob");
                            String profession = object.getString("profession");
                            editorMyInfo.putString("FullName" , full_name) ;
                            editorMyInfo.putString("Email" , email) ;
                            editorMyInfo.putString("Mobile" , mobile) ;
                            editorMyInfo.putString("Username" , username) ;
                            editorMyInfo.putString("DOB" , dob) ;
                            editorMyInfo.putString("Profession" , profession) ;
                            editorMyInfo.commit();
                        } catch (Exception e) {
                        }

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