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

import java.util.List;

public class ListHawkerCentresAdaptor extends ArrayAdapter {

    private final Context context;
    List<HawkerCentre> hawkerCentres;

    public ListHawkerCentresAdaptor(Context context, List<HawkerCentre> hawkerCentres) {
        super(context, R.layout.hawker_centre);

        this.context = context;
        this.hawkerCentres = hawkerCentres;

        for(int i=0; i <hawkerCentres.size(); i++)
        {
            add(null);
        }
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent)
    {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.hawker_centre, parent, false);
        }

        TextView hawkerCentreName = view.findViewById(R.id.hawkerCentre_name);
        hawkerCentreName.setText(hawkerCentres.get(pos).getName());

        TextView hawkerCentreAddress = view.findViewById(R.id.hawkerCentre_address);
        hawkerCentreAddress.setText(hawkerCentres.get(pos).getAddress());

        TextView hawkerCentreNumOfStalls = view.findViewById(R.id.hawkerCentre_numOfStalls);
        hawkerCentreNumOfStalls.setText(String.valueOf(hawkerCentres.get(pos).getNumOfStalls()) + " " + context.getString(R.string.stalls));

        ImageView hawkerCentre_image = view.findViewById(R.id.hawkerCentre_image);
        Picasso.get()
                .load(hawkerCentres.get(pos).getImgUrl())
                .resize(150, 80)
                .centerCrop()
                .into(hawkerCentre_image);

        return view;
    }
}