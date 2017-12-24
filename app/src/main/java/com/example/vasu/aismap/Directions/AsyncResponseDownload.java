package com.example.vasu.aismap.Directions;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Vasu on 24-12-2017.
 */

public interface AsyncResponseDownload {
    void processFinish(PolylineOptions[] output);
}