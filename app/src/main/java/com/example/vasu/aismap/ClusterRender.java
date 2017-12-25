package com.example.vasu.aismap;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Vasu on 25-12-2017.
 */

public class ClusterRender extends DefaultClusterRenderer {

    public ClusterRender(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        Log.e("CLUSTER" , "Rendering") ;
    }

    @Override
    protected void onClusterRendered(Cluster cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);

        Log.e("CLUSTER" , cluster.getItems().toString()) ;

    }
}
