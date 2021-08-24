package com.team5.splash;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.team5.splash.models.HawkerCentre;
import com.team5.splash.models.HawkerStall;

public class menuItemFragment extends Fragment {

    Integer menuItemId;
    MenuItem menuItem;
    HawkerCentre hc;
    HawkerStall hs;
    TextView menuItemName, hsName, menuItemPrice, menuItemStatus, menuItemDesc;
    ImageView menuItemImg, backBtn;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public menuItemFragment() {
        // Required empty public constructor
    }

    public static menuItemFragment newInstance(String param1, String param2) {
        menuItemFragment fragment = new menuItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_menu_item, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();

        if (bundle != null) {
            menuItemId = bundle.getInt("menuItemId");
            hc = (HawkerCentre) bundle.getSerializable("centre");
            hs = (HawkerStall) bundle.getSerializable("stall");
            menuItem = (MenuItem) bundle.getSerializable("menuItem");
        }

        View view = getView();

        backBtn = view.findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        menuItemName = view.findViewById(R.id.menuItemName);
        menuItemName.setText(menuItem.getName());

        hsName = view.findViewById(R.id.hsName);
        hsName.setText(hs.getStallName());

        menuItemImg = view.findViewById(R.id.menuItemImg);
        Picasso.get()
                .load(menuItem.getLocalUrl())
                .resize(1000, 600)
                .centerCrop()
                .into(menuItemImg);

        menuItemPrice = view.findViewById(R.id.menuItemPrice);
        menuItemPrice.setText(getString(R.string.menuItemPrice) + menuItem.getPrice());

        menuItemStatus = view.findViewById(R.id.menuItemStatus);
        menuItemStatus.setText(getString(R.string.menuItemStatus, menuItem.getStatus()));

        menuItemDesc = view.findViewById(R.id.menuItemDesc);
        menuItemDesc.setText(getString(R.string.menuItemDesc, menuItem.getDescription()));
    }

    public void removeFragment() {
        this.getParentFragmentManager().popBackStack();
    }
}