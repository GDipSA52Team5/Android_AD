package com.team5.splash;

import android.app.Activity;
import android.content.Context;
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

        TextView hawkerCentre_name = view.findViewById(R.id.hawkerCentre_name);
        hawkerCentre_name.setText(hawkerStalls.get(pos).getCentre());

//        ImageView hawkerCentre_image = view.findViewById(R.id.hawkerCentre_image);
//        Picasso.get()
//                .load(hawkerStalls.get(pos).getImgUrl())
//                .resize(150, 80)
//                .centerCrop()
//                .into(hawkerCentre_image);

        return view;
    }
}
