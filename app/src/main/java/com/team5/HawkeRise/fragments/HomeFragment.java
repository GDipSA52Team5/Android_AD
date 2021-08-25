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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team5.HawkeRise.models.HawkerCentre;
import com.team5.HawkeRise.utilities.ListHawkerStallsAdaptor;
import com.team5.HawkeRise.utilities.MySingleton;
import com.team5.HawkeRise.R;
import com.team5.HawkeRise.models.HawkerStall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // initialise fragment variables
    private Context mContext;
    private View view;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // initialise views
    private TextView home_txt;
    private ListView home_lv;
    private ProgressBar home_pb;

    // initialise variables
    private String userDisplayName;
    private List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();
    private String getHawkerCentreFromHawkerStallURL;
    private String userEmail;
    private String highestRatedStalls;
    private String recommendStalls;
    private HawkerCentre hc;
    private HawkerStall hs;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // instantiate fragment variables
        mContext = getContext();
        view = getView();

        // instantiate fragment views
        home_pb = view.findViewById(R.id.home_pb);
        home_txt = view.findViewById(R.id.home_txt);
        home_lv = view.findViewById(R.id.home_lv);

        // if guest user, display top stalls
        if (user == null)
        {
            home_txt.setText(getString(R.string.welcome_guest));
            getTopStalls();
        }
        // if logged-in user, display recommended stalls for user
        else
        {
            userDisplayName = user.getDisplayName();
            userEmail = user.getEmail();

            home_txt.setText("Hello " + userDisplayName + getString(R.string.welcome_user));

            getRecommendedStalls();
        }

    }

    public void getTopStalls()
    {
        highestRatedStalls = "https://gdipsa-ad-ml.herokuapp.com/highestRatedStalls";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, highestRatedStalls, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                // get JSON object
                                JSONObject hawkerStallObj = response.getJSONObject(i);

                                // initialise and instantiate a new empty HawkerStall object
                                HawkerStall hawkerStall = new HawkerStall();

                                // set attributes for new HawkerStall object from JSON object
                                hawkerStall.setId(hawkerStallObj.getInt("id"));
                                hawkerStall.setStallName(hawkerStallObj.getString("stall_name"));
                                hawkerStall.setUnitNumber(hawkerStallObj.getString("unit_number"));
                                hawkerStall.setContactNumber(hawkerStallObj.getString("contact_number"));
                                hawkerStall.setStatus(hawkerStallObj.getString("status"));
                                hawkerStall.setOperatingHours(hawkerStallObj.getString("operating_hours"));
                                hawkerStall.setStallImgUrl(hawkerStallObj.getString("hawker_img"));

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
                        home_pb.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home_pb.setVisibility(View.GONE);
                Toast.makeText(mContext, "Error Retrieving Top Stalls", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void getRecommendedStalls()
    {
        recommendStalls = "https://gdipsa-ad-ml.herokuapp.com/recommendStalls?uid=" + userEmail;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, recommendStalls, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                // get JSON object
                                JSONObject hawkerStallObj = response.getJSONObject(i);

                                // initialise and instantiate a new empty HawkerStall object
                                HawkerStall hawkerStall = new HawkerStall();

                                // set attributes for new HawkerStall object from JSON object
                                hawkerStall.setId(hawkerStallObj.getInt("id"));
                                hawkerStall.setStallName(hawkerStallObj.getString("stall_name"));
                                hawkerStall.setUnitNumber(hawkerStallObj.getString("unit_number"));
                                hawkerStall.setContactNumber(hawkerStallObj.getString("contact_number"));
                                hawkerStall.setStatus(hawkerStallObj.getString("status"));
                                hawkerStall.setOperatingHours(hawkerStallObj.getString("operating_hours"));
                                hawkerStall.setStallImgUrl(hawkerStallObj.getString("hawker_img"));

                                // add HawkerStall to list of hakwerStalls
                                hawkerStalls.add(hawkerStall);

                                if(i == (response.length() - 1))
                                {
                                    home_pb.setVisibility(View.GONE);
                                    createListStallsView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                ,

                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home_pb.setVisibility(View.GONE);
                Toast.makeText(mContext, "Error Retrieving Recommended Stalls", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void createListStallsView()
    {

        ListHawkerStallsAdaptor adaptor = new ListHawkerStallsAdaptor(mContext, hawkerStalls);

        if(home_lv !=null)
        {
            home_lv.setAdapter(adaptor);

//             implement onItemClick(...) for listView
            home_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (hawkerStalls.size() != 0)
                    {
                        hs = hawkerStalls.get(i);
                        Integer hsId = hs.getId();
                        hc = getHawkerCentreFromHawkerStall(hsId);

                        replaceFragment(hsId);
                    }
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
                .replace(((ViewGroup) getView().getParent()).getId(), fragment, null)
                .addToBackStack(null)
                .commit();
    }

    public HawkerCentre getHawkerCentreFromHawkerStall(Integer stallId)
    {

        getHawkerCentreFromHawkerStallURL = "https://gdipsa-ad-springboot.herokuapp.com/api/getHawkerCentreFromHawkerStall/" + stallId;

        HawkerCentre hawkerCentre = new HawkerCentre();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getHawkerCentreFromHawkerStallURL, null, new Response.Listener<JSONObject>() {
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
                Toast.makeText(mContext, "Error retrieving hawker centre!", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(mContext).addToRequestQueue(request);

        return hawkerCentre;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}