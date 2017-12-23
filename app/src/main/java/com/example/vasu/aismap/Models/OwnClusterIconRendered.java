package com.example.vasu.aismap.Models;

import android.content.Context;

import com.example.vasu.aismap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Vasu on 23-12-2017.
 */

public class OwnClusterIconRendered extends DefaultClusterRenderer<ClusteringItem> {

    public OwnClusterIconRendered(Context context, GoogleMap map,
                                  ClusterManager<ClusteringItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusteringItem item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.machine));
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}