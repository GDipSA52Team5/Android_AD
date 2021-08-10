package com.team5.splash.placeholder;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.team5.splash.HawkerCentre;
import com.team5.splash.R;

import java.util.List;

public class ListHawkerStallsAdaptor extends ArrayAdapter {

    private final Context context;
    List<HawkerStall> hawkerStalls;

    public ListHawkerStallsAdaptor(Context context, List<HawkerStall> hawkerStalls) {
        super(context, R.layout.hawker_stall_list);

        this.context = context;
        this.hawkerStalls = hawkerStalls;

        for(int i=0; i <hawkerStalls.size(); i++)
        {
            add(null);
        }
    }


}
