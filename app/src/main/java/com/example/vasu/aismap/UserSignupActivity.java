package com.example.vasu.aismap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vasu.aismap.FetchPHP.HttpParse;

import java.util.HashMap;

public class UserSignupActivity extends AppCompatActivity {

    Button submit;
    EditText name,email,username,password,cpassword,mnumber,dob,profession;
    String NameHolder, EmailHolder,UnameHolder, PasswordHolder,MobileHolder,DobHolder,ProfessionHolder;
    String finalResult ;
    String HttpURL = "https://aiseraintern007.000webhostapp.com/AISERA/UserRegistration.php";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

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

                // Checking whether EditText is Empty or Not
                CheckEditTextIsEmptyOrNot();

                if(CheckEditText){

                    // If EditText is not empty and CheckEditText = True then this block will execute.

                   UserRegisterFunction(NameHolder, EmailHolder,UnameHolder, PasswordHolder,MobileHolder,DobHolder,ProfessionHolder);

                }
                else {

                    // If EditText is empty then this block will execute .
                    Toast.makeText(UserSignupActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }


            }
        });


    }



    public void CheckEditTextIsEmptyOrNot(){

        NameHolder = name.getText().toString();
        EmailHolder = email.getText().toString();
        UnameHolder=username.getText().toString();
        PasswordHolder = password.getText().toString();
        MobileHolder=mnumber.getText().toString();
        DobHolder=dob.getText().toString();
        ProfessionHolder=profession.getText().toString();


        if(TextUtils.isEmpty(NameHolder) || TextUtils.isEmpty(EmailHolder) ||TextUtils.isEmpty(UnameHolder)|| TextUtils.isEmpty(PasswordHolder)
                ||TextUtils.isEmpty(MobileHolder)||TextUtils.isEmpty(DobHolder)||TextUtils.isEmpty(ProfessionHolder))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;
        }

    }

    public void UserRegisterFunction(final String NameHolder,final String EmailHolder,
                                     final String UnameHolder,
                                     final String PasswordHolder,
                                     final String MobileHolder,
                                     final String DobHolder,
                                     final String ProfessionHolder){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(UserSignupActivity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(UserSignupActivity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("full_name",params[0]);

                hashMap.put("mobile",params[1]);

                hashMap.put("email",params[2]);

                hashMap.put("username",params[3]);
                hashMap.put("password",params[4]);
                hashMap.put("dob",params[5]);
                hashMap.put("profession",params[6]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(NameHolder, EmailHolder,UnameHolder, PasswordHolder,MobileHolder,DobHolder,ProfessionHolder);
    }

}