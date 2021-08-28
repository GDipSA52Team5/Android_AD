package com.team5.HawkeRise.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class FavouritesFragment extends Fragment {

    // Initialize necessary variable
    private RequestQueue queue;
    private Context mContext;
    private List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ListView listFavouriteStalls;
    private HawkerCentre hc;
    private HawkerStall hs;
    private ProgressBar progressBarFavourite;


    public FavouritesFragment() {
        // Required empty public constructor
    }

    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Instantiate necessary variable
        View view = getView();
        mContext = getContext();
        queue = MySingleton.getInstance(mContext.getApplicationContext()).getRequestQueue();
        progressBarFavourite = view.findViewById(R.id.progressBarFavourite);
        listFavouriteStalls = view.findViewById(R.id.listFavourites);


        // Determine if user is logged in
        if (user != null)
        {
            progressBarFavourite.setVisibility(View.VISIBLE);
            listFavourites(user.getEmail());
        }
        else{
            TextView textViewAccountWelcome = view.findViewById(R.id.favouriteHawker_info);
            textViewAccountWelcome.setText("Please Login to view your favourite stalls!");
            listFavouriteStalls.setVisibility(View.GONE);
        }

    }

    public void listFavourites(String email)
    {
        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/listFavourites/" + email;
        hawkerStalls = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 0)
                        {
                            progressBarFavourite.setVisibility(View.GONE);
                            createListFavouriteStallsView();
                            Toast.makeText(mContext, "No favourite stalls!", Toast.LENGTH_LONG).show();
                        }


                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                JSONObject hawkerStallJSONObj = response.getJSONObject(i);

                                HawkerStall hawkerStall = new HawkerStall();
                                hawkerStall.setId(hawkerStallJSONObj.getInt("id"));
                                hawkerStall.setStallName(hawkerStallJSONObj.getString("stallName"));
                                hawkerStall.setUnitNumber(hawkerStallJSONObj.getString("unitNumber"));
                                hawkerStall.setContactNumber(hawkerStallJSONObj.getString("contactNumber"));
                                hawkerStall.setStatus(hawkerStallJSONObj.getString("status"));
                                hawkerStall.setOperatingHours(hawkerStallJSONObj.getString("operatingHours"));
                                hawkerStall.setCloseHours(hawkerStallJSONObj.getString("closeHours"));
                                hawkerStall.setStallImgUrl(hawkerStallJSONObj.getString("hawkerImg"));

                                hawkerStalls.add(hawkerStall);

                                if(i == (response.length() - 1))
                                {
                                    createListFavouriteStallsView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressBarFavourite.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void createListFavouriteStallsView()
    {
        ListHawkerStallsAdaptor adaptor = new ListHawkerStallsAdaptor(mContext, hawkerStalls);

        if(listFavouriteStalls !=null)
        {
            listFavouriteStalls.setAdapter(adaptor);

            // implement onItemClick(...) for listView
            listFavouriteStalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    hs = hawkerStalls.get(i);
                    Integer hsId = hs.getId();
                    hc = findBelongCentre(hsId);

//                    String stallName = hs.getStallName();
                    replaceFragment(hsId);
                }
            });
        }
    }

    public HawkerCentre findBelongCentre(Integer stallId)
    {
        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/getHawkerCentreFromHawkerStall/" + stallId;
        HawkerCentre hawkerCentre = new HawkerCentre();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    hawkerCentre.setId(response.getString("id"));
                    hawkerCentre.setName(response.getString("name"));
                    hawkerCentre.setAddress(response.getString("address"));
                    hawkerCentre.setNumOfStalls(response.getInt("numOfStalls"));
                    hawkerCentre.setLatitude(response.getDouble("latitude"));
                    hawkerCentre.setLongitude(response.getDouble("longitude"));
                    hawkerCentre.setImgUrl(response.getString("imgUrl"));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Retrieving Centre Error!", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);

        return hawkerCentre;
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
                .replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }
}