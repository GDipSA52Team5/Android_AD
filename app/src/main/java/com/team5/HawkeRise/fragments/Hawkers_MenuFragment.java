package com.team5.HawkeRise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.team5.HawkeRise.R;
import com.team5.HawkeRise.models.HawkerCentre;
import com.team5.HawkeRise.models.HawkerStall;
import com.team5.HawkeRise.models.MenuItem;

public class Hawkers_MenuFragment extends Fragment {

    // initialise fragment variables
    private View view;

    // initialise views
    private TextView menuItemName, hsName, menuItemPrice, menuItemStatus, menuItemDesc;
    private ImageView menuItemImg, backBtn;

    // initialise variables
    private Integer menuItemId;
    private MenuItem menuItem;
    private HawkerCentre hc;
    private HawkerStall hs;

    public Hawkers_MenuFragment() {
        // Required empty public constructor
    }

    public static Hawkers_MenuFragment newInstance() {
        Hawkers_MenuFragment fragment = new Hawkers_MenuFragment();
        return fragment;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();

        // instantiate fragment variables
        view = getView();

        // get bundle arguments from previous fragment
        if (bundle != null) {
            menuItemId = bundle.getInt("menuItemId");
            hc = (HawkerCentre) bundle.getSerializable("centre");
            hs = (HawkerStall) bundle.getSerializable("stall");
            menuItem = (MenuItem) bundle.getSerializable("menuItem");
        }

        // instantiate fragment views
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

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_item, container, false);
    }

    public void removeFragment() {
        this.getParentFragmentManager().popBackStack();
    }
}