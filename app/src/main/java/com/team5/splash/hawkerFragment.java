package com.team5.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link hawkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class hawkerFragment extends Fragment implements View.OnClickListener {

    RequestQueue mQueue;
    List<HawkerCentre> hawkerCentres = new ArrayList<HawkerCentre>();

    AppCompatButton findStallsBtn;
    ListView listHawkerCentres;

    private Context mContext;
    int PERMISSION_ID = 44;

    FusedLocationProviderClient mFusedLocationClient;
    TextView latitudeTextView, longitTextView;

    String lat;
    String lon;
    String distFrom;

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

        mContext = getContext();

        View view = getView();
//        findStallsBtn = view.findViewById(R.id.findStallsBtn);
        listHawkerCentres = view.findViewById(R.id.listHawkerCentres);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Display list of hawkers
        parseData();

//        findStallsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    replaceFragment("999",null);
//
//            }
//        });

        latitudeTextView = view.findViewById(R.id.latTextView);
        longitTextView = view.findViewById(R.id.lonTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        // method to get the location
        getLastLocation();

        Button OneKmBtn = view.findViewById(R.id.OneKmBtn);
        OneKmBtn.setOnClickListener(this);

        Button ThreeKmBtn = view.findViewById(R.id.ThreeKmBtn);
        ThreeKmBtn.setOnClickListener(this);

        Button FiveKmBtn = view.findViewById(R.id.FiveKmBtn);
        FiveKmBtn.setOnClickListener(this);

    }

    public void parseData()
    {
        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/listCentre";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                JSONObject hawkerCentreJSONObj = response.getJSONObject(i);

                                HawkerCentre hawkerCentre = new HawkerCentre();
                                hawkerCentre.setId(hawkerCentreJSONObj.getString("id"));
                                hawkerCentre.setName(hawkerCentreJSONObj.getString("name"));
                                hawkerCentre.setAddress(hawkerCentreJSONObj.getString("address"));
                                hawkerCentre.setNumOfStalls(hawkerCentreJSONObj.getInt("numOfStalls"));
                                hawkerCentre.setLatitude(hawkerCentreJSONObj.getDouble("latitude"));
                                hawkerCentre.setLongitude(hawkerCentreJSONObj.getDouble("longitude"));
                                hawkerCentre.setImgUrl(hawkerCentreJSONObj.getString("imgUrl"));

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
//                listHawkerCentres.setText("That didn't work!");
                Toast.makeText(getActivity().getApplicationContext(), "Error Retrieving Hawker Centres", Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);
    }

    public void createListHawkersView()
    {

        ListHawkerCentresAdaptor adaptor = new ListHawkerCentresAdaptor(getActivity().getApplicationContext(), hawkerCentres);

        if(listHawkerCentres !=null)
        {
            listHawkerCentres.setAdapter(adaptor);

            // implement onItemClick(...) for listView
            listHawkerCentres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HawkerCentre hc = hawkerCentres.get(i);
                    String hcId = hc.getId();
                    Toast.makeText(getActivity().getApplicationContext(), "Hello I've been clicked! ", Toast.LENGTH_LONG).show();
                    replaceFragment(hcId,hc);
                }
            });
        }
    }

    public void replaceFragment(String hcId,HawkerCentre hc)
    {
        Bundle arguments = new Bundle();
        arguments.putString("centreId", hcId);
        arguments.putSerializable("centre",hc);

        Fragment fragment = new listStallsFragment();
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
        return inflater.inflate(R.layout.fragment_hawker, container, false);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                            lat = String.valueOf(location.getLatitude());
                            lon = String.valueOf(location.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onClick(View view) {

        Toast.makeText(mContext, "Hi you click me ah?", Toast.LENGTH_SHORT).show();
    }
}