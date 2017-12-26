package com.example.vasu.aismap.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vasu.aismap.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AYUSH on 12/26/2017.
 */

public class CustomSearchListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<String> mAddress;


    public CustomSearchListAdapter(Context context, List<String> address) {
        super(context,R.layout.search_result_item, address);
        this.mContext = context;
        this.mAddress = new ArrayList<>(address);
         }

    public int getCount() {
        return mAddress.size();
    }



    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String temp = mAddress.get(position) ;

        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.search_result_item, parent, false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.search_result_text);
            name.setText(temp);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


}
