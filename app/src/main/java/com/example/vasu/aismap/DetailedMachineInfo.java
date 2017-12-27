package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedMachineInfo extends AppCompatActivity {

    ImageView image ;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
        Button bt1;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_machine_info);

        Intent i=getIntent();

        tv1=(TextView)findViewById(R.id.mserialno);
        tv2=(TextView)findViewById(R.id.maddress);
        tv3=(TextView)findViewById(R.id.maccess);
        tv4=(TextView)findViewById(R.id.mstatus);
        tv5=(TextView)findViewById(R.id.mtype);
        tv6=(TextView)findViewById(R.id.mcost);
        tv7=(TextView)findViewById(R.id.mcompany);
        bt1=(Button)findViewById(R.id.mdirections);

        tv1.setText(i.getStringExtra("serialno"));
        tv2.setText(i.getStringExtra("Address"));
        tv3.setText(i.getStringExtra("access"));
        tv4.setText(i.getStringExtra("status"));
        tv5.setText(i.getStringExtra("type"));
        tv6.setText(i.getStringExtra("cost"));
        tv7.setText(i.getStringExtra("company"));


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
