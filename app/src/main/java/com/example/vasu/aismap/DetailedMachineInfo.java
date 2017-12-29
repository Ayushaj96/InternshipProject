package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedMachineInfo extends AppCompatActivity {

    String address,machine_serial_no,access,status,company1,company2,type;
    int company1quantity , company2quantity ;

    ImageView image ;
    TextView tvMachineSerial,tvAddress,tvAccess,tvStatus,tvCompany1,tvCompany2;
    ImageButton bt1;
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

        tvMachineSerial=(TextView)findViewById(R.id.mserialno);
        tvAddress=(TextView)findViewById(R.id.maddress);
        tvAccess=(TextView)findViewById(R.id.maccess);
        tvStatus=(TextView)findViewById(R.id.mstatus);
        tvCompany1=(TextView)findViewById(R.id.mcompany1);
        tvCompany2=(TextView)findViewById(R.id.mcompany2);

        bt1=(ImageButton)findViewById(R.id.mproceed);

        tvMachineSerial.setText(machine_serial_no);
        tvAddress.setText(address);
        tvAccess.setText(access);
        tvStatus.setText(status);
        tvCompany1.setText(""+company1.replace("ld","Light Days").replace("hd","Heavy Days") + "-" + company1quantity);
        tvCompany2.setText(""+company2.replace("ld","Light Days").replace("hd","Heavy Days") + "-" + company2quantity);

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
