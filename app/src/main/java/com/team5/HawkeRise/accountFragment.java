package com.team5.HawkeRise;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link accountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class accountFragment extends Fragment{

    private Button btnLogout;

    private Context mContext;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public accountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment accountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static accountFragment newInstance(String param1, String param2) {
        accountFragment fragment = new accountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        mContext = getContext();

        View view = getView();

        btnLogout = view.findViewById(R.id.buttonLogout);


        if (user == null) {
            TextView textViewAccountWelcome = view.findViewById(R.id.textViewAccountWelcome);
            textViewAccountWelcome.setText("Please login to enjoy more features!");
            btnLogout.setText("Create An Account Now!");
        } else {
            String name = user.getDisplayName();

            TextView textViewAccountWelcome = view.findViewById(R.id.textViewAccountWelcome);
            textViewAccountWelcome.setText("Welcome, " + name);
        }
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

