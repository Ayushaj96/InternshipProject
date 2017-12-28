package com.example.vasu.aismap;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profilePhoto ;
    TextView tvName,tvUsername,tvMobile,tvEmail,tvDob,tvProfession ;

    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);

        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);
        tvDob = findViewById(R.id.tvDob);
        tvProfession = findViewById(R.id.tvProfession);

        profilePhoto = findViewById(R.id.profile_image);

        tvName.setText(sharedPreferences.getString("FullName" , ""));
        tvUsername.setText(sharedPreferences.getString("Username" , ""));
        tvMobile.setText(sharedPreferences.getString("Mobile" , ""));
        tvEmail.setText(sharedPreferences.getString("Email" , ""));
        tvDob.setText(sharedPreferences.getString("DOB" , ""));
        tvProfession.setText(sharedPreferences.getString("Profession" , ""));

    }
}
