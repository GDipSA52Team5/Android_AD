package com.team5.splash;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listStallsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listStallsFragment extends Fragment {

    RequestQueue mQueue;
    ListView listHawkerStalls;
    String centreId;
    HawkerCentre hc;
    HawkerStall hs;
    List<HawkerStall> hawkerStalls = new ArrayList<HawkerStall>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public listStallsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listStallsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static listStallsFragment newInstance(String param1, String param2) {
        listStallsFragment fragment = new listStallsFragment();
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

        TextView stallsTxt = view.findViewById(R.id.hawkerCentre_info);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            centreId = bundle.getString("centreId");
            hc = (HawkerCentre) bundle.getSerializable("centre");

            String numStallsText = getString(R.string.NumStalls1) + hc.getNumOfStalls() + getString(R.string.NumStalls2) + hc.getName() + getString(R.string.NumStalls3);
            stallsTxt.setText(numStallsText);
        }

        listHawkerStalls = view.findViewById(R.id.listHawkerStalls);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Display list of stalls
        parseData();

    }

    public void parseData()
    {
        String urlLocal = "http://10.40.1.56:8080/api/listHawkers/";

        String urlHeroku  = "https://gdipsa-ad-springboot.herokuapp.com/api/listHawkers/";

        String url = urlHeroku + centreId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                Toast.makeText(getActivity().getApplicationContext(), "Error Retrieving Hawker Stalls", Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);
    }

    public void createListStallsView()
    {

        ListHawkerStallsAdaptor adaptor = new ListHawkerStallsAdaptor(getActivity().getApplicationContext(), hawkerStalls);

        if(listHawkerStalls !=null)
        {
            listHawkerStalls.setAdapter(adaptor);

            // implement onItemClick(...) for listView
            listHawkerStalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    hs = hawkerStalls.get(i);
                    Integer hcId = hs.getId();
//                    String stallName = hs.getStallName();
                    replaceFragment(hcId);
                }
            });
        }
    }

    public void replaceFragment(Integer hcId)
    {
        Bundle arguments = new Bundle();
        arguments.putInt("stallId", hcId);
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
        return inflater.inflate(R.layout.fragment_list_stalls, container, false);
    }
}