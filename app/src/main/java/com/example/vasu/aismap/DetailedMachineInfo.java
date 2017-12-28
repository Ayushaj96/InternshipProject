package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedMachineInfo extends AppCompatActivity {

    String address,machine_serial_no,access,status,company1,company2,type;
    int company1quantity , company2quantity ;

    ImageView image ;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
        Button bt1;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_machine_info);

        Intent i=getIntent();

        address = i.getStringExtra("address");
        machine_serial_no = i.getStringExtra("serialno");
        access = i.getStringExtra("access");
        status = i.getStringExtra("status");
        company1 = i.getStringExtra("company1");
        company1quantity = Integer.parseInt(i.getStringExtra("company1quantity"));
        company2 = i.getStringExtra("company2");
        company2quantity = Integer.parseInt(i.getStringExtra("company2quantity"));
        type = i.getStringExtra("type");

        tv1=(TextView)findViewById(R.id.mserialno);
        tv2=(TextView)findViewById(R.id.maddress);
        tv3=(TextView)findViewById(R.id.maccess);
        tv4=(TextView)findViewById(R.id.mstatus);
        tv5=(TextView)findViewById(R.id.mtype);
        tv6=(TextView)findViewById(R.id.mcost);
        tv7=(TextView)findViewById(R.id.mcompany);
        bt1=(Button)findViewById(R.id.mdirections);

        tv1.setText(machine_serial_no);
        tv2.setText(address);
        tv3.setText(access);
        tv4.setText(status);
        tv5.setText(type);


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedMachineInfo.this , CartActivity.class);
                intent.putExtra("address",address);
                intent.putExtra("serialno",machine_serial_no);
                intent.putExtra("access",access);
                intent.putExtra("status",status);
                intent.putExtra("type",type);
                intent.putExtra("company1",company1);
                intent.putExtra("company1quantity",company1quantity);
                intent.putExtra("company2",company2);
                intent.putExtra("company2quantity",company2quantity);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
