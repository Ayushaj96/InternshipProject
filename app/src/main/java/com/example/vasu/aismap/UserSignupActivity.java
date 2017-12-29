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
import com.example.vasu.aismap.FetchPHP.UserRegistrationTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserSignupActivity extends AppCompatActivity {

    Button submit;
    EditText name,email,username,password,cpassword,mnumber,dob,profession;
    String NameHolder, EmailHolder,UnameHolder, PasswordHolder,MobileHolder,DobHolder,ProfessionHolder;

    SharedPreferences sharedPreferences , sharedPreferencesMyInfo;
    SharedPreferences.Editor editor , editorMyInfo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regis);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        sharedPreferencesMyInfo=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editorMyInfo = sharedPreferencesMyInfo.edit();

        //Assign Id'S
        name = (EditText)findViewById(R.id.fullname);
        email = (EditText)findViewById(R.id.email);
        username=(EditText)findViewById(R.id.username);
        password= (EditText)findViewById(R.id.password);
        cpassword = (EditText)findViewById(R.id.confirmpassword);
        mnumber= (EditText)findViewById(R.id.mobilenumber);
        dob= (EditText)findViewById(R.id.DateOfbirth);
        profession = (EditText)findViewById(R.id.Profession);
        submit = (Button)findViewById(R.id.submit);

        //Adding Click Listener on button.
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

    }

    public void addData(){

        NameHolder = name.getText().toString();
        EmailHolder = email.getText().toString();
        UnameHolder=username.getText().toString();
        PasswordHolder = password.getText().toString();
        MobileHolder=mnumber.getText().toString();
        DobHolder=dob.getText().toString();
        ProfessionHolder=profession.getText().toString();


        if(TextUtils.isEmpty(NameHolder) || TextUtils.isEmpty(EmailHolder) ||TextUtils.isEmpty(UnameHolder)|| TextUtils.isEmpty(PasswordHolder)
                ||TextUtils.isEmpty(MobileHolder)||TextUtils.isEmpty(DobHolder)||TextUtils.isEmpty(ProfessionHolder)) {
            Toast.makeText(this, "Something is Empty", Toast.LENGTH_SHORT).show();
        }
        else {   final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(true);
            pDialog.show();
            UserRegistrationTask urt = new UserRegistrationTask(UserSignupActivity.this, NameHolder, EmailHolder, UnameHolder
                    ,MobileHolder , PasswordHolder, DobHolder, ProfessionHolder, new AsyncResponseUserRegistration() {
                @Override
                public void processFinish(String output) {
                    Toast.makeText(UserSignupActivity.this, ""+output, Toast.LENGTH_LONG).show();
                    if (output.equalsIgnoreCase("Registration Successfully")){
                        editorMyInfo.putString("FullName" , NameHolder) ;
                        editorMyInfo.putString("Email" , EmailHolder) ;
                        editorMyInfo.putString("Mobile" , MobileHolder) ;
                        editorMyInfo.putString("Username" , UnameHolder) ;
                        editorMyInfo.putString("DOB" , DobHolder) ;
                        editorMyInfo.putString("Profession" , ProfessionHolder) ;
                        editorMyInfo.commit();
                        editor.putBoolean("LoginStatus",true);
                        editor.commit();
                        startActivity(new Intent(UserSignupActivity.this , MapsActivity.class));
                        pDialog.hide();
                        finish();
                    }
                }
            });
            urt.execute();
        }

    }

}