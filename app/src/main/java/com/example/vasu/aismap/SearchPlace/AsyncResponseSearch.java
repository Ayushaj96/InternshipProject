package com.example.vasu.aismap.SearchPlace;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Vasu on 26-12-2017.
 */

public interface AsyncResponseSearch {
    void processFinish(ArrayList<LatLng> output);
}
