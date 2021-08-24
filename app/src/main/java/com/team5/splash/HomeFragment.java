package com.team5.splash;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team5.splash.models.HawkerStall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private Context mContext;
    private View view;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userDisplayName;
    private List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();
    private TextView homeTxt;
    private ListView listHawkerStalls;
    private String userEmail;
    private ProgressBar progressBarHome;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public void onStart() {
        super.onStart();

        mContext = getContext();
        view = getView();

        progressBarHome = view.findViewById(R.id.progressBarHome);

        homeTxt = view.findViewById(R.id.homeTxt);
        listHawkerStalls = view.findViewById(R.id.listRecommended);

        if (user == null)
        {
            homeTxt.setText(getString(R.string.welcome_guest));
            getTopStalls();
        }
        else
        {
            userDisplayName = user.getDisplayName();
            userEmail = user.getEmail();

            homeTxt.setText("Hello " + userDisplayName + getString(R.string.welcome_user));

            getRecommendedStalls();

        }

    }

    public void getTopStalls()
    {
        String url = "https://gdipsa-ad-ml.herokuapp.com/highestRatedStalls";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                JSONObject hawkerStallObj = response.getJSONObject(i);

                                HawkerStall hawkerStall = new HawkerStall();

                                hawkerStall.setId(hawkerStallObj.getInt("id"));
                                hawkerStall.setStallName(hawkerStallObj.getString("stall_name"));
                                hawkerStall.setUnitNumber(hawkerStallObj.getString("unit_number"));
                                hawkerStall.setContactNumber(hawkerStallObj.getString("contact_number"));
                                hawkerStall.setStatus(hawkerStallObj.getString("status"));
                                hawkerStall.setOperatingHours(hawkerStallObj.getString("operating_hours"));
                                hawkerStall.setStallImgUrl(hawkerStallObj.getString("hawker_img"));

                                hawkerStalls.add(hawkerStall);

                                if(i == (response.length() - 1))
                                {
                                    createListStallsView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressBarHome.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHome.setVisibility(View.GONE);
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
        String url = "https://gdipsa-ad-ml.herokuapp.com/recommendStalls?uid=" + userEmail;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                JSONObject hawkerStallObj = response.getJSONObject(i);

                                HawkerStall hawkerStall = new HawkerStall();

                                hawkerStall.setId(hawkerStallObj.getInt("id"));
                                hawkerStall.setStallName(hawkerStallObj.getString("stall_name"));
                                hawkerStall.setUnitNumber(hawkerStallObj.getString("unit_number"));
                                hawkerStall.setContactNumber(hawkerStallObj.getString("contact_number"));
                                hawkerStall.setStatus(hawkerStallObj.getString("status"));
                                hawkerStall.setOperatingHours(hawkerStallObj.getString("operating_hours"));
                                hawkerStall.setStallImgUrl(hawkerStallObj.getString("hawker_img"));

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

        if(listHawkerStalls !=null)
        {
            listHawkerStalls.setAdapter(adaptor);

//             implement onItemClick(...) for listView
            listHawkerStalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (hawkerStalls.size() != 0)
                    {
                        // replace fragment
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}