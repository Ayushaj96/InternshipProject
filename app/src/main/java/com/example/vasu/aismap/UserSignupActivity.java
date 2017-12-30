package com.example.vasu.aismap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vasu.aismap.FetchPHP.AsyncResponseFindSearch;
import com.example.vasu.aismap.FetchPHP.AsyncResponseUserRegistration;
import com.example.vasu.aismap.FetchPHP.SendSMSTask;
import com.example.vasu.aismap.FetchPHP.UserRegistrationTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserSignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Button submit;
    EditText name,email,username,mnumber,dob,profession;
    TextInputEditText password,cpassword;
    String NameHolder, EmailHolder,UnameHolder, PasswordHolder,CPasswordHolder,MobileHolder,DobHolder,ProfessionHolder;

    SharedPreferences sharedPreferences , sharedPreferencesMyInfo;
    SharedPreferences.Editor editor , editorMyInfo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyLoginStatus", MODE_PRIVATE);
        sharedPreferencesMyInfo=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editorMyInfo = sharedPreferencesMyInfo.edit();

        //Assign Id'S
        name = (EditText)findViewById(R.id.fullname);
        email = (EditText)findViewById(R.id.email);
        username=(EditText)findViewById(R.id.username);
        password= (TextInputEditText) findViewById(R.id.password);
        cpassword = (TextInputEditText) findViewById(R.id.confirmpassword);
        mnumber= (EditText)findViewById(R.id.mobilenumber);
        dob= (EditText)findViewById(R.id.DateOfbirth);
        profession = (EditText)findViewById(R.id.Profession);
        submit = (Button)findViewById(R.id.submit);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                         UserSignupActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)

                );

                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mnumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Adding Click Listener on button.
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addData();
                sendOTP(mnumber.getText().toString());
            }
        });

    }

    public void sendOTP(String mobile){
        int tempOTP = 0 ;
        Random rd = new Random() ;
        while (tempOTP < 100000){
            tempOTP = rd.nextInt(1000000) ;
        }
        String otp = String.valueOf(tempOTP) ;
        StringBuilder sb=new StringBuilder(mobile+"-"+otp);
        sb.insert(0,"/%");
        String message = sb.toString() ;
        SendSMSTask sendSMSTask=new SendSMSTask(UserSignupActivity.this, ""+mobile, ""+message, new AsyncResponseFindSearch() {
            @Override
            public void processFinish(String output) {
                Toast.makeText(UserSignupActivity.this, ""+output, Toast.LENGTH_SHORT).show();
            }
        });
        sendSMSTask.execute();
    }



    public void addData(){

        String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

                "\\@" +

                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                "(" +

                "\\." +

                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

                ")+";


        NameHolder = name.getText().toString();
        EmailHolder = email.getText().toString();
        UnameHolder=username.getText().toString();
        PasswordHolder = password.getText().toString();
        CPasswordHolder=cpassword.getText().toString();
        MobileHolder=mnumber.getText().toString();
        DobHolder=dob.getText().toString();
        ProfessionHolder=profession.getText().toString();

        Matcher matcher= Pattern.compile(validemail).matcher(EmailHolder);

        if(TextUtils.isEmpty(NameHolder) || TextUtils.isEmpty(EmailHolder) ||
                TextUtils.isEmpty(UnameHolder)|| TextUtils.isEmpty(PasswordHolder)
                ||TextUtils.isEmpty(MobileHolder)||TextUtils.isEmpty(DobHolder)||TextUtils.isEmpty(ProfessionHolder)) {
            Toast.makeText(this, "Something is Empty", Toast.LENGTH_SHORT).show();
        }
        else
            if (PasswordHolder.length()<5) {
                 password.setError("password should be more than 5");

            }
            else
            if (!PasswordHolder.equals(CPasswordHolder)) {
                //Toast.makeText(this, "Password and Confirm Password Does not match", Toast.LENGTH_SHORT).show();
                cpassword.setError("Does not match password");
            }
            else
                if(!matcher.matches())
                {
                  //  Toast.makeText(this, "INVALID EMAIL", Toast.LENGTH_LONG).show();
                    email.setError("Invalid Email" );
                }

                else
                    if(mnumber.length()>10||mnumber.length()<10)
                    {
                        //Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                        mnumber.setError("Invalid Mobile Number");
                    }
        else {   final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Registering User");
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
                    else
                    {
                        pDialog.hide();
                    }
                }
            });
            urt.execute();
        }

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date =year+"/"+monthOfYear+"/"+dayOfMonth;
        dob.setText(date);
    }
}