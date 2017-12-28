package com.example.vasu.aismap.FetchPHP;

import com.example.vasu.aismap.Models.MarkerModel;

import java.util.ArrayList;

/**
 * Created by Vasu on 24-12-2017.
 */

public interface AsyncResponseFindAllSearches {
    void processFinish(ArrayList<MarkerModel> output);
}