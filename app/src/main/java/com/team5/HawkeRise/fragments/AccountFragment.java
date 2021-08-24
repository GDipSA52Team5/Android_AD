package com.team5.HawkeRise.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team5.HawkeRise.activities.LoginActivity;
import com.team5.HawkeRise.R;
import com.team5.HawkeRise.activities.SignupActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment{

    // Initialize necessary variable
    private Button btnLogout;
    private Button btnLogin;
    private Context mContext;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        mContext = getContext();

        View view = getView();

        btnLogout = view.findViewById(R.id.buttonLogout);
        btnLogin = view.findViewById(R.id.buttonAccountLogin);


        if (user == null) {
            TextView textViewAccountWelcome = view.findViewById(R.id.textViewAccountWelcome);
            textViewAccountWelcome.setText("Please login to enjoy more features!");
            btnLogout.setText("Create An Account Now!");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            String name = user.getDisplayName();

            TextView textViewAccountWelcome = view.findViewById(R.id.textViewAccountWelcome);
            textViewAccountWelcome.setText("Welcome, " + name);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_account, container, false);


        Button buttonLogout = (Button) view.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user != null){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;

    }

}

