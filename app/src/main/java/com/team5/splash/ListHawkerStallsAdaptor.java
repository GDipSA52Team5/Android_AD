package com.team5.splash;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.team5.splash.HawkerStall;
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

    public View getView(int pos, View view, @NonNull ViewGroup parent)
    {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.hawker_stall_list, parent, false);
        }

        TextView hawkerStall_name = view.findViewById(R.id.hawkerStall_name);
        hawkerStall_name.setText(hawkerStalls.get(pos).getStallName());

        TextView hawkerStall_unitNumber = view.findViewById(R.id.hawkerStall_unitNumber);
        hawkerStall_unitNumber.setText(hawkerStalls.get(pos).getUnitNumber());

        TextView hawkerStall_status = view.findViewById(R.id.hawkerStall_status);
        if(hawkerStalls.get(pos).getStatus() == "Open")
        {
            hawkerStall_status.setText(hawkerStalls.get(pos).getStatus());
            hawkerStall_status.setTextColor(getContext().getResources().getColor(R.color.green));
            hawkerStall_status.setTypeface(null, Typeface.BOLD);
        }
        else if(hawkerStalls.get(pos).getStatus() == "Closed")
        {
            hawkerStall_status.setText(hawkerStalls.get(pos).getStatus());
            hawkerStall_status.setTextColor(getContext().getResources().getColor(R.color.error_red));
        }

        ImageView hawkerStall_image = view.findViewById(R.id.hawkerStall_image);
        Picasso.get()
                .load(hawkerStalls.get(pos).getStallImgUrl())
                .resize(150, 80)
                .centerCrop()
                .into(hawkerStall_image);

        return view;
    }
}