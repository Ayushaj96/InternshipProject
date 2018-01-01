package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpOTPActivity extends AppCompatActivity {

    EditText otp;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_otp);

        otp=(EditText)findViewById(R.id.etOtp);
         final String Otp=getIntent().getStringExtra("otp");

        final String number=getIntent().getStringExtra("mobile");
        submit=(Button)findViewById(R.id.btnsubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Otp.equals(otp.getText().toString())) {
                    Intent intent = new Intent(SignUpOTPActivity.this, UserSignupActivity.class);
                    intent.putExtra("mobile", number);
                    startActivity(intent);
                }

                else
                {
                    Toast.makeText(SignUpOTPActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
