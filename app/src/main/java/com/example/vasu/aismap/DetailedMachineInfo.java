package com.example.vasu.aismap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailedMachineInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_machine_info);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
