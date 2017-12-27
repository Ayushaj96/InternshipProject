package com.example.vasu.aismap.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vasu.aismap.Models.MarkerModel;
import com.example.vasu.aismap.Models.NearMachines;
import com.example.vasu.aismap.R;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Vasu on 24-12-2017.
 */

public class NearMachinesAdapter extends ArrayAdapter<MarkerModel>{

    private ArrayList<MarkerModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    public NearMachinesAdapter(ArrayList<MarkerModel> data, Context context) {
        super(context, R.layout.list_item_for_near_machines, data);
        this.dataSet = data;
        this.mContext=context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MarkerModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_for_near_machines, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.machineName);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.txtName.setText(""+dataModel.getAddress().charAt(0));
        return convertView;
    }
}