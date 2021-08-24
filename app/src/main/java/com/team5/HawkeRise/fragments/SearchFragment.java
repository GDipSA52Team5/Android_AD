package com.team5.HawkeRise.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private RequestQueue queue;
    private Context mContext;

    private List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();

    private EditText searchInput;
    private ListView listSearchStalls;
    private HawkerCentre hc;
    private HawkerStall hs;
    private String reqs;
    private Button searchBtn;
    private ProgressBar progressBarSearch;
    private TextView textViewSearch;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment searchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        queue = MySingleton.getInstance(mContext.getApplicationContext()).getRequestQueue();

        View view = getView();

        listSearchStalls = view.findViewById(R.id.listSearchStalls);
        searchInput = view.findViewById(R.id.searchInput);
        searchBtn = view.findViewById(R.id.searchBtn);
        progressBarSearch = view.findViewById(R.id.progressBarSearch);
        textViewSearch = view.findViewById(R.id.textViewSearch);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarSearch.setVisibility(View.VISIBLE);
                reqs = searchInput.getText().toString();
                searchStalls(reqs);
            }
        });
    }

    public void searchStalls(String reqs)
    {

//        String url = "http://10.40.1.56:8080/api/searchStalls/" + reqs;
        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/searchStalls/" + reqs;
        hawkerStalls = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 0)
                        {
                            createListSearchStallsView();
                            Toast.makeText(mContext, "No stalls found!", Toast.LENGTH_LONG).show();
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
                                    createListSearchStallsView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        textViewSearch.setVisibility(View.VISIBLE);
                        progressBarSearch.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarSearch.setVisibility(View.GONE);
                textViewSearch.setVisibility(View.VISIBLE);
                textViewSearch.setText("No stalls found, please try again!");
                Toast.makeText(mContext, "Error!", Toast.LENGTH_LONG).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void createListSearchStallsView()
    {
        ListHawkerStallsAdaptor adaptor = new ListHawkerStallsAdaptor(mContext, hawkerStalls);

        if(listSearchStalls !=null)
        {
            listSearchStalls.setAdapter(adaptor);

            // implement onItemClick(...) for listView
            listSearchStalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    hs = hawkerStalls.get(i);
                    Integer hsId = hs.getId();
                    hc = findBelongCentre(hsId);

                    replaceFragment(hsId);
                }
            });
        }
    }

    public HawkerCentre findBelongCentre(Integer stallId)
    {

        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/findBelongCentre/" + stallId;
//        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/findBelongCentre/" + stallId;

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
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}