package com.team5.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link stallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class stallFragment extends Fragment {

    Integer stallId;
    HawkerCentre hc;
    HawkerStall hs;
    List<MenuItem> menuItems = new ArrayList<MenuItem>();
    ListView listMenuItems;


    //LSQ
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button fvrt_brn;
    Boolean fvrtCheck = false;

    RequestQueue mQueue;
    RequestQueue mQueue2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public stallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment stallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static stallFragment newInstance(String param1, String param2) {
        stallFragment fragment = new stallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            stallId = bundle.getInt("stallId");
            hc = (HawkerCentre) bundle.getSerializable("centre");
            hs = (HawkerStall) bundle.getSerializable("stall");
        }
        View view = getView();
        TextView HawkerStallName = view.findViewById(R.id.HawkerStallName);
        HawkerStallName.setText(hs.getStallName());

        TextView MarketName = view.findViewById(R.id.MarketName);
        MarketName.setText(hc.getName());

        ImageView StallImage = view.findViewById(R.id.StallImage);
        Picasso.get()
                .load(hs.getStallImgUrl())
                .resize(1000, 600)
                .centerCrop()
                .into(StallImage);

        //LSQ
        //Number[] num = {1,2,4};
        Number[] num = hs.getFvt_list();
        fvrt_brn = view.findViewById(R.id.fvrt_item);
        for (Number i: num) {
            if (i == stallId){
                fvrtCheck = true;
                fvrt_brn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
                break;
            }
            else{
                fvrtCheck = false;
                fvrt_brn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
            }
        }

        fvrt_brn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fvrtCheck == true){
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                    fvrtCheck = false;
                    String uemail = user.getEmail();
                    String url ="http://10.40.1.56:8080//api/favorites/" + uemail + "/" + stallId;

                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    JsonRequest request = new JsonObjectRequest(url,
                            null, //if jsonRequest == null then Method.GET otherwise Method.POST
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //handler the response here
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //handler the error here
                        }
                    });
                    queue.add(request);
                }
                else{
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
                    fvrtCheck = true;
                    String uemail = user.getEmail();
                    String url ="http://10.40.1.56:8080//api/favorites/" + uemail + "/" + stallId;

                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    JsonRequest request = new JsonObjectRequest(url,
                            null, //if jsonRequest == null then Method.GET otherwise Method.POST
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //handler the response here
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //handler the error here
                        }
                    });
                    queue.add(request);
                }
            }
        });

        TextView StallUnitNumber = view.findViewById(R.id.StallUnitNumber);
        StallUnitNumber.setText(getString(R.string.Unit_Number)+ hs.getUnitNumber());

        TextView StallContactNumber = view.findViewById(R.id.StallContactNumber);
        StallContactNumber.setText(getString(R.string.Contact_Number) + hs.getContactNumber());

        listMenuItems = view.findViewById(R.id.listMenuItems);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Display list of menu items
        parseData();

        Button OpenMap = view.findViewById(R.id.OpenMap);
        OpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latLongDirections = "geo:" + hc.getLatitude() + "," + hc.getLongitude() + "q=" + hc.getLatitude() + "," + hc.getLongitude() + "(" + hc.getName() + ")";
                Uri uri = Uri.parse(latLongDirections);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);
            }
        });

        Button ReportProblem = view.findViewById(R.id.ReportProblem);
        ReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Sorry, haven't implement this yet.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void parseData()
    {
        String url = "http://10.40.1.56:8080//api/listMenuItem/" + hs.getId();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                JSONObject menuItemJSONObj = response.getJSONObject(i);

                                MenuItem menuItem = new MenuItem();

                                menuItem.setId(menuItemJSONObj.getInt("id"));
                                menuItem.setName(menuItemJSONObj.getString("name"));
                                menuItem.setDescription(menuItemJSONObj.getString("description"));
                                menuItem.setPrice(menuItemJSONObj.getDouble("price"));
//                              menuItem.setPhoto(menuItemJSONObj.getString("photo"));
                                menuItem.setStatus(menuItemJSONObj.getString("status"));
                                menuItem.setLocalUrl(menuItemJSONObj.getString("localUrl"));
//                              menuItem.setHawker(menuItemJSONObj.getString("hawker"));
//                              menuItem.setPhotoImagePath(menuItemJSONObj.getString("photoImagePath"));

                                menuItems.add(menuItem);

                                if(i == (response.length() - 1))
                                {
                                    createListMenuItemsView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Error Retrieving Menu Items", Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);

    }

    public void createListMenuItemsView() {

        ListMenuItemsAdaptor adaptor = new ListMenuItemsAdaptor(getActivity().getApplicationContext(), menuItems);

        if (listMenuItems != null) {
            listMenuItems.setAdapter(adaptor);

        }
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
        return inflater.inflate(R.layout.fragment_stall, container, false);
    }
}