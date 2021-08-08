package com.team5.splash;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link hawkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class hawkerFragment extends Fragment {

    RequestQueue mQueue;
    TextView listHawkerCentres;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public int getId;

    public hawkerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment hawkerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static hawkerFragment newInstance(String param1, String param2) {
        hawkerFragment fragment = new hawkerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        AppCompatButton findStallsBtn = view.findViewById(R.id.findStallsBtn);
        listHawkerCentres = view.findViewById(R.id.listHawkerCentres);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Display list of hawkers
        parseData();

        findStallsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    replaceFragment();

            }
        });
    }

    public void parseData()
    {
        String url = "http://192.168.1.177:5000/api/listCentre";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listHawkerCentres.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listHawkerCentres.setText("That didn't work!");
            }
        });

        mQueue.add(request);
    }

    public void replaceFragment()
    {
        Bundle arguments = new Bundle();
        arguments.putInt("stallId", 3);

        Fragment fragment = new listStallsFragment();
        fragment.setArguments(arguments);

        this.getParentFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        FragmentTransaction trans = fm.beginTransaction();
//        trans.hide(this).show(fragment);
//        trans.replace(getId, fragment);
//        trans.addToBackStack(null);
//        trans.commit();
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
        return inflater.inflate(R.layout.fragment_hawker, container, false);
    }
}