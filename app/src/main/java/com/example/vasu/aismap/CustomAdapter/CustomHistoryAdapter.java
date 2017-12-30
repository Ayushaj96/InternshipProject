package com.example.vasu.aismap.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vasu.aismap.Models.HistoryModel;
import com.example.vasu.aismap.Models.MarkerModel;
import com.example.vasu.aismap.R;

import java.util.ArrayList;

/**
 * Created by AYUSH on 12/26/2017.
 */

public class CustomHistoryAdapter extends ArrayAdapter<HistoryModel> {


    private final Context mContext;
    private final ArrayList<HistoryModel> mprevAddress;


    public CustomHistoryAdapter(Context context, ArrayList<HistoryModel> address) {
        super(context, R.layout.history_list_item, address);
        this.mContext = context;
        this.mprevAddress = new ArrayList<>(address);
    }

    public int getCount() {
        return mprevAddress.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HistoryModel historyModel=getItem(position);

        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.history_list_item, parent, false);
            }
            TextView mname = (TextView) convertView.findViewById(R.id.machineName);
            TextView  mAddress= (TextView) convertView.findViewById(R.id.machineAddress);
            TextView mtime = (TextView) convertView.findViewById(R.id.time);

            mname.setText(historyModel.getSerial_no());
            mAddress.setText(historyModel.getAddress());
            mtime.setText("Time");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}