package com.team5.HawkeRise.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.team5.HawkeRise.utilities.ListHawkerStallsAdaptor;
import com.team5.HawkeRise.utilities.MySingleton;
import com.team5.HawkeRise.R;
import com.team5.HawkeRise.models.HawkerCentre;
import com.team5.HawkeRise.models.HawkerStall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Hawkers_HawkerStallsFragment extends Fragment {

    // initialise fragment variables
    private Context mContext;
    private View view;

    // initialise views
    private ImageView back_btn;
    private ListView hawkerStalls_lv;
    private TextView hawkerStalls_txt;

    // initialise variables
    private String centreId, findHawkerStallsURL;
    private HawkerCentre hc;
    private HawkerStall hs;
    private List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();

    public Hawkers_HawkerStallsFragment() {
        // Required empty public constructor
    }

    public static Hawkers_HawkerStallsFragment newInstance(String param1, String param2) {
        Hawkers_HawkerStallsFragment fragment = new Hawkers_HawkerStallsFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // instantiate fragment variables
        view = getView();
        mContext = getContext();

        // instantiate fragment views
        hawkerStalls_txt = view.findViewById(R.id.hawkerStalls_txt);
        hawkerStalls_lv = view.findViewById(R.id.hawkerStalls_lv);
        back_btn = view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        // get bundle arguments from previous fragment
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            centreId = bundle.getString("centreId");
            hc = (HawkerCentre) bundle.getSerializable("centre");

            String numStallsText = getString(R.string.NumStalls1) + hc.getNumOfStalls() + getString(R.string.NumStalls2) + hc.getName() + getString(R.string.NumStalls3);
            hawkerStalls_txt.setText(numStallsText);
        }

        // Display list of stalls
        findHawkerStalls();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_stalls, container, false);
    }

    public void findHawkerStalls()
    {
        findHawkerStallsURL = "https://gdipsa-ad-springboot.herokuapp.com/api/findHawkerStalls/" + centreId ;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, findHawkerStallsURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                // get JSON object
                                JSONObject hawkerStallJSONObj = response.getJSONObject(i);

                                // initialise and instantiate a new empty HawkerStall object
                                HawkerStall hawkerStall = new HawkerStall();

                                // set attributes for new HawkerStall object from JSON object
                                hawkerStall.setId(hawkerStallJSONObj.getInt("id"));
                                hawkerStall.setStallName(hawkerStallJSONObj.getString("stallName"));
                                hawkerStall.setUnitNumber(hawkerStallJSONObj.getString("unitNumber"));
                                hawkerStall.setContactNumber(hawkerStallJSONObj.getString("contactNumber"));
                                hawkerStall.setStatus(hawkerStallJSONObj.getString("status"));
                                hawkerStall.setOperatingHours(hawkerStallJSONObj.getString("operatingHours"));
                                hawkerStall.setCloseHours(hawkerStallJSONObj.getString("closeHours"));
                                hawkerStall.setStallImgUrl(hawkerStallJSONObj.getString("hawkerImg"));

                                // add HawkerStall to list of hawkerStalls
                                hawkerStalls.add(hawkerStall);

                                if(i == (response.length() - 1))
                                {
                                    createListStallsView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                listHawkerCentres.setText("That didn't work!");
                Toast.makeText(mContext, "Error Retrieving Hawker Stalls", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void createListStallsView()
    {

        ListHawkerStallsAdaptor adaptor = new ListHawkerStallsAdaptor(mContext, hawkerStalls);

        if(hawkerStalls_lv !=null)
        {
            hawkerStalls_lv.setAdapter(adaptor);

            // implement onItemClick(...) for listView
            hawkerStalls_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    hs = hawkerStalls.get(i);
                    Integer hsId = hs.getId();
                    replaceFragment(hsId);
                }
            });
        }
    }

    public void replaceFragment(Integer hsId)
    {
        Bundle arguments = new Bundle();
        arguments.putInt("stallId", hsId);
        arguments.putSerializable("centre", hc);
        arguments.putSerializable("stall", hs);

        Fragment fragment = new Hawkers_StallInfoFragment();
        fragment.setArguments(arguments);

        this.getParentFragmentManager().beginTransaction()
                .add(((ViewGroup) getView().getParent()).getId(), fragment, null)
                .addToBackStack(null)
                .commit();
    }

    public void removeFragment() {
        this.getParentFragmentManager().popBackStack();
    }

}