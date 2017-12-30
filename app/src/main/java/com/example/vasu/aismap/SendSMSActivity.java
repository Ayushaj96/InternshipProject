package com.example.vasu.aismap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vasu.aismap.FetchPHP.AsyncResponseFindSearch;
import com.example.vasu.aismap.FetchPHP.SendSMSTask;

public class SendSMSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        SendSMSTask sendSMSTask=new SendSMSTask(SendSMSActivity.this, "9811384601", "message sent", new AsyncResponseFindSearch() {
            @Override
            public void processFinish(String output) {

            }
        });
        sendSMSTask.execute();
    }
}
