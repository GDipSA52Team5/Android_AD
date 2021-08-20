package com.team5.splash;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link searchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class favouriteFragment extends Fragment {

    RequestQueue queue;
    private Context mContext;

    List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ListView listFavouriteStalls;
    HawkerCentre hc;
    HawkerStall hs;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public favouriteFragment() {
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
    public static favouriteFragment newInstance(String param1, String param2) {
        favouriteFragment fragment = new favouriteFragment();
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

        listFavouriteStalls = view.findViewById(R.id.listFavourites);


        if (user != null)
        {
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
        String url = "http://10.40.1.56:8080/api/listFavourites/" + email;
//        String url = "https://gdipsa-ad-springboot.herokuapp.com/api/listFavourites/" + email;
        hawkerStalls = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 0)
                        {
                            createListFavouriteStallsView();
                            Toast.makeText(mContext, "No favourite stalls!", Toast.LENGTH_SHORT).show();
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

        String url = "http://10.40.1.56:8080/api/findBelongCentre/" + stallId;
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

        MySingleton.getInstance(mContext).addToRequestQueue(request);

        return hawkerCentre;
    }

    public void replaceFragment(Integer hsId)
    {
        Bundle arguments = new Bundle();
        arguments.putInt("stallId", hsId);
        arguments.putSerializable("centre", hc);
        arguments.putSerializable("stall", hs);

        Fragment fragment = new stallFragment();
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
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }
}