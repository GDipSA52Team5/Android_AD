package com.team5.HawkeRise.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.team5.HawkeRise.utilities.ListHawkerCentresAdaptor;
import com.team5.HawkeRise.R;
import com.team5.HawkeRise.models.HawkerCentre;
import com.team5.HawkeRise.utilities.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HawkersFragment extends Fragment implements View.OnClickListener {

    // initialise fragment variables
    private Context mContext;
    private View view;
    private FusedLocationProviderClient mFusedLocationClient;

    // initialise views
    private ListView listHawkerCentres;
    private ProgressBar progressBarHawker;
    private Button oneKm_btn;
    private Button threeKm_btn;
    private Button fiveKm_btn;
    private Button allStalls_btn;

    // initialise variables
    private List<HawkerCentre> hawkerCentres = new ArrayList<HawkerCentre>();
    private final int PERMISSION_ID = 44;
    private String lat;
    private String lon;
    private String distFrom;
    private String getAllHawkerCentresURL;
    private String getHawkerCentresByDistanceURL;

    public HawkersFragment() {
        // Required empty public constructor
    }

    public static HawkersFragment newInstance(String param1, String param2) {
        HawkersFragment fragment = new HawkersFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // instantiate fragment variables
        mContext = getContext();
        view = getView();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        // instantiate fragment views
        listHawkerCentres = view.findViewById(R.id.hawkers_lv);
        progressBarHawker = view.findViewById(R.id.hawkers_pb);

        oneKm_btn = view.findViewById(R.id.oneKm_btn);
        oneKm_btn.setOnClickListener(this);

        threeKm_btn = view.findViewById(R.id.threeKm_btn);
        threeKm_btn.setOnClickListener(this);

        fiveKm_btn = view.findViewById(R.id.fiveKm_btn);
        fiveKm_btn.setOnClickListener(this);

        allStalls_btn = view.findViewById(R.id.allStalls_btn);
        allStalls_btn.setOnClickListener(this);

        // Get user's location
        getUserLocation();

        // Display list of all Hawker Centres
        getAllHawkerCentres();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hawker, container, false);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.allStalls_btn)
        {
            getAllHawkerCentres();
        }

        else
        {
            if (id == R.id.oneKm_btn)
            {
                distFrom = "1";
            }

            if (id == R.id.threeKm_btn)
            {
                distFrom = "3";
            }

            if (id == R.id.fiveKm_btn)
            {
                distFrom = "5";
            }

            if (lat==null || lon==null)
            {
                Toast.makeText(mContext, "Unable to retrieve user location", Toast.LENGTH_SHORT).show();
            }

            else
            {
                getHawkerCentresByDistance();
            }
        }

    }

    public void getAllHawkerCentres()
    {
        hawkerCentres = new ArrayList<HawkerCentre>();

        getAllHawkerCentresURL = "https://gdipsa-ad-springboot.herokuapp.com/api/getAllHawkerCentres";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getAllHawkerCentresURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                // get JSON object
                                JSONObject hawkerCentreJSONObj = response.getJSONObject(i);

                                // initialise and instantiate a new empty HawkerCentre object
                                HawkerCentre hawkerCentre = new HawkerCentre();

                                // set attributes for new HawkerCentre object from JSON object
                                hawkerCentre.setId(hawkerCentreJSONObj.getString("id"));
                                hawkerCentre.setName(hawkerCentreJSONObj.getString("name"));
                                hawkerCentre.setAddress(hawkerCentreJSONObj.getString("address"));
                                hawkerCentre.setNumOfStalls(hawkerCentreJSONObj.getInt("numOfStalls"));
                                hawkerCentre.setLatitude(hawkerCentreJSONObj.getDouble("latitude"));
                                hawkerCentre.setLongitude(hawkerCentreJSONObj.getDouble("longitude"));
                                hawkerCentre.setImgUrl(hawkerCentreJSONObj.getString("imgUrl"));

                                // add HawkerCentre to list of hawkerCentres
                                hawkerCentres.add(hawkerCentre);

                                if(i == (response.length() - 1))
                                {
                                    createListHawkersView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressBarHawker.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHawker.setVisibility(View.GONE);
                Toast.makeText(mContext, "Error Retrieving Hawker Centres", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void getHawkerCentresByDistance()
    {
        hawkerCentres = new ArrayList<HawkerCentre>();

        getHawkerCentresByDistanceURL  = "https://gdipsa-ad-springboot.herokuapp.com/api/getHawkerCentresByDistance/" + lat + "/" + lon + "/" + distFrom;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getHawkerCentresByDistanceURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if(response.length() == 0)
                        {
                            createListHawkersView();
                            Toast.makeText(mContext, "There are no hawker centres near you.", Toast.LENGTH_SHORT).show();
                        }

                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                // get JSON object
                                JSONObject hawkerCentreJSONObj = response.getJSONObject(i);

                                // initialise and instantiate a new empty HawkerCentre object
                                HawkerCentre hawkerCentre = new HawkerCentre();

                                // set attributes for new HawkerCentre object from JSON object
                                hawkerCentre.setId(hawkerCentreJSONObj.getString("id"));
                                hawkerCentre.setName(hawkerCentreJSONObj.getString("name"));
                                hawkerCentre.setAddress(hawkerCentreJSONObj.getString("address"));
                                hawkerCentre.setNumOfStalls(hawkerCentreJSONObj.getInt("numOfStalls"));
                                hawkerCentre.setLatitude(hawkerCentreJSONObj.getDouble("latitude"));
                                hawkerCentre.setLongitude(hawkerCentreJSONObj.getDouble("longitude"));
                                hawkerCentre.setImgUrl(hawkerCentreJSONObj.getString("imgUrl"));

                                // add HawkerCentre to list of hawkerCentres
                                hawkerCentres.add(hawkerCentre);

                                if(i == (response.length() - 1))
                                {
                                    createListHawkersView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error Retrieving Hawker Centres", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void createListHawkersView()
    {
        ListHawkerCentresAdaptor adaptor = new ListHawkerCentresAdaptor(mContext, hawkerCentres);

        if(listHawkerCentres !=null)
        {
            listHawkerCentres.setAdapter(adaptor);

//             implement onItemClick(...) for listView
            listHawkerCentres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (hawkerCentres.size() != 0)
                    {
                        HawkerCentre hc = hawkerCentres.get(i);
                        String hcId = hc.getId();
                        replaceFragment(hcId, hc);
                    }
                }
            });
        }
    }

    public void replaceFragment(String hcId, HawkerCentre hc)
    {
        Bundle arguments = new Bundle();
        arguments.putString("centreId", hcId);
        arguments.putSerializable("centre",hc);

        Fragment fragment = new Hawkers_HawkerStallsFragment();
        fragment.setArguments(arguments);

        this.getParentFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = String.valueOf(location.getLatitude());
                            lon = String.valueOf(location.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(mContext, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available, request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getUserLocation();
        }
    }


}