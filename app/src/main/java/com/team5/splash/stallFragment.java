package com.team5.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link stallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class stallFragment extends Fragment {

    Integer stallId;
    HawkerCentre hc;
    HawkerStall hs;

    RequestQueue mQueue;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public stallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment stallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static stallFragment newInstance(String param1, String param2) {
        stallFragment fragment = new stallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            stallId = bundle.getInt("stallId");
            hc = (HawkerCentre) bundle.getSerializable("centre");
            hs = (HawkerStall) bundle.getSerializable("stall");
        }

        View view = getView();

        TextView HawkerStallName = view.findViewById(R.id.HawkerStallName);
        HawkerStallName.setText(hs.getStallName());

        TextView MarketName = view.findViewById(R.id.MarketName);
        MarketName.setText(hc.getName());

        ImageView StallImage = view.findViewById(R.id.StallImage);
        Picasso.get()
                .load(hs.getStallImgUrl())
                .resize(1000, 600)
                .centerCrop()
                .into(StallImage);

        TextView StallUnitNumber = view.findViewById(R.id.StallUnitNumber);
        StallUnitNumber.setText(hs.getUnitNumber());

        TextView StallContactNumber = view.findViewById(R.id.StallContactNumber);
        StallContactNumber.setText(hs.getContactNumber());

        ListView listMenuItems = view.findViewById(R.id.listMenuItems);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        Button OpenMap = view.findViewById(R.id.OpenMap);
        OpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latLongDirections = "geo:" + hc.getLatitude() + "," + hc.getLongitude() + "q=" + hc.getLatitude() + "," + hc.getLongitude() + "(" + hc.getName() + ")";
                Uri uri = Uri.parse(latLongDirections);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);

            }
        });

        Button ReportProblem = view.findViewById(R.id.ReportProblem);
        ReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Sorry, haven't implement this yet.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stall, container, false);
    }
}