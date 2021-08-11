package com.team5.splash;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
public class hawkerFragment extends Fragment {

    RequestQueue mQueue;
    List<HawkerCentre> hawkerCentres = new ArrayList<HawkerCentre>();

    AppCompatButton findStallsBtn;
    ListView listHawkerCentres;

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
        findStallsBtn = view.findViewById(R.id.findStallsBtn);
        listHawkerCentres = view.findViewById(R.id.listHawkerCentres);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Display list of hawkers
        parseData();

        findStallsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    replaceFragment("999",null);

            }
        });

    }

    public void parseData()
    {
        String url = "http://10.40.1.56:8080/api/listCentre";

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

}