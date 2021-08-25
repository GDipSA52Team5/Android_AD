package com.team5.HawkeRise.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.team5.HawkeRise.utilities.ListMenuItemsAdaptor;
import com.team5.HawkeRise.utilities.MySingleton;
import com.team5.HawkeRise.R;
import com.team5.HawkeRise.models.HawkerCentre;
import com.team5.HawkeRise.models.HawkerStall;
import com.team5.HawkeRise.models.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Hawkers_StallInfoFragment extends Fragment {

    // initialise fragment variables
    private Context mContext;
    private View view;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // initialise views
    private ListView menuItems_lv;
    private ImageView back_btn;
    private RatingBar stall_rb;
    private TextView stallName_txt, hawkerCentreName_txt, unitNumber_txt, contactNumber_txt;
    private ImageView stall_img;
    private Button getDirections_btn, reportProblem_btn, favourite_btn;

    // initialise variables
    private Integer stallId;
    private HawkerCentre hc;
    private HawkerStall hs;
    private MenuItem menuItem;
    private List<MenuItem> menuItems = new ArrayList<MenuItem>();
    private int currentRating, likeOrNot;
    private Boolean favouriteFlag = false;
    private String getMenuItemsURL, getFavouriteStatusURL, findRatingURL, setRatingURL, email;

    public Hawkers_StallInfoFragment() {
        // Required empty public constructor
    }

    public static Hawkers_StallInfoFragment newInstance(String param1, String param2) {
        Hawkers_StallInfoFragment fragment = new Hawkers_StallInfoFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // instantiate fragment variables
        mContext = getContext();
        view = getView();

        // get bundle arguments from previous fragment
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            stallId = bundle.getInt("stallId");
            hc = (HawkerCentre) bundle.getSerializable("centre");
            hs = (HawkerStall) bundle.getSerializable("stall");
        }

        // instantiate fragment views
        stallName_txt = view.findViewById(R.id.stallName_txt);
        stallName_txt.setText(hs.getStallName());

        hawkerCentreName_txt = view.findViewById(R.id.hawkerCentreName_txt);
        hawkerCentreName_txt.setText(hc.getName());

        stall_img = view.findViewById(R.id.stall_img);
        Picasso.get()
                .load(hs.getStallImgUrl())
                .resize(1000, 600)
                .centerCrop()
                .into(stall_img);

        unitNumber_txt = view.findViewById(R.id.unitNumber_txt);
        unitNumber_txt.setText(getString(R.string.Unit_Number)+ hs.getUnitNumber());

        contactNumber_txt = view.findViewById(R.id.contactNumber_txt);
        contactNumber_txt.setText(getString(R.string.Contact_Number) + hs.getContactNumber());

        menuItems_lv = view.findViewById(R.id.menuItems_lv);
        if (menuItems.size() == 0)
        {
            getMenuItems();
        }

        getDirections_btn = view.findViewById(R.id.getDirections_btn);
        getDirections_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latLongDirections = "geo:" + hc.getLatitude() + "," + hc.getLongitude() + "?q=" + hc.getLatitude() + "," + hc.getLongitude() + "(" + hc.getName() + ")";
                Uri uri = Uri.parse(latLongDirections);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);
            }
        });

        reportProblem_btn = view.findViewById(R.id.reportProblem_btn);
        reportProblem_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "This function will only be implemented in the next update.", Toast.LENGTH_SHORT).show();
            }
        });

        back_btn = view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        // rating bar
        stall_rb = view.findViewById(R.id.stall_rb);
        if (user == null)
        {
            stall_rb.setVisibility(View.INVISIBLE);
        }
        else
        {
            stall_rb.setStepSize(1);

            // fetch current rating
            email = user.getEmail();
            getCurrentRating();

            stall_rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                    float newRating = v;

                    if(v < 1)
                    {
                        ratingBar.setRating(1);
                        newRating = 1;
                    }

                    setRating(Math.round(newRating));
                    getCurrentRating();
                }
            });
        }

        // favourite button
        favourite_btn = view.findViewById(R.id.favourite_btn);
        if (user == null)
        {
            favourite_btn.setVisibility(View.INVISIBLE);
        }
        else
        {
            getFavouriteStatus(user.getEmail(), hs.getId());

            favourite_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (favouriteFlag == true){
                        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                        favouriteFlag = false;
                        email = user.getEmail();
                        String url ="https://gdipsa-ad-springboot.herokuapp.com/api/favorites/" + email + "/" + stallId;

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
                                Toast.makeText(mContext, "Error Fav", Toast.LENGTH_SHORT).show();
                            }
                        });
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        MySingleton.getInstance(mContext).addToRequestQueue(request);
                    }
                    else{
                        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
                        favouriteFlag = true;
                        email = user.getEmail();
                        String url ="https://gdipsa-ad-springboot.herokuapp.com/api/favorites/" + email + "/" + stallId;

                        JsonRequest request = new JsonObjectRequest(url,
                                null, //if jsonRequest == null then Method.GET otherwise Method.POST
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(mContext, "Error Fav", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //handler the error here
                            }
                        });
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        MySingleton.getInstance(mContext).addToRequestQueue(request);
                    }
                }
            });
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stall, container, false);
    }

    public void getMenuItems()
    {
        getMenuItemsURL = "https://gdipsa-ad-springboot.herokuapp.com/api/getMenuItems/" + hs.getId();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getMenuItemsURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i < response.length(); i++)
                        {
                            try {
                                // get JSON object
                                JSONObject menuItemJSONObj = response.getJSONObject(i);

                                // initialise and instantiate a new empty MenuItem object
                                MenuItem menuItem = new MenuItem();

                                // set attributes for new MenuItem object from JSON object
                                menuItem.setId(menuItemJSONObj.getInt("id"));
                                menuItem.setName(menuItemJSONObj.getString("name"));
                                menuItem.setDescription(menuItemJSONObj.getString("description"));
                                menuItem.setPrice(menuItemJSONObj.getDouble("price"));
                                menuItem.setStatus(menuItemJSONObj.getString("status"));
                                menuItem.setLocalUrl(menuItemJSONObj.getString("localUrl"));

                                // add MenuItem to list of menuItems
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
                Toast.makeText(mContext, "Error Retrieving Menu Items", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);

    }

    public void getFavouriteStatus(String email, int stallId)
    {
        getFavouriteStatusURL = "https://gdipsa-ad-springboot.herokuapp.com/api/getFavouriteStatus/" + email + "/" + stallId;

        StringRequest request = new StringRequest(Request.Method.GET, getFavouriteStatusURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        likeOrNot = Integer.parseInt(response);

                        if (likeOrNot ==0)
                        {
                            // do nothing as 0 means not like
                        }
                        else
                        {
                            favouriteFlag = true;
                            favourite_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error Retrieving Favourite Status", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void getCurrentRating()
    {
        findRatingURL = "https://gdipsa-ad-springboot.herokuapp.com/api/findRating/" + email + "/" + stallId;

        StringRequest request = new StringRequest(Request.Method.GET, findRatingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            currentRating = Integer.parseInt(response);

                            if (currentRating == 9)
                            {
                                // do nothing as 9 means not found
                            }
                            else
                            {
                                stall_rb.setRating(currentRating);
                            }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error Retrieving Ratings", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void setRating(int newRating)
    {
        setRatingURL = "https://gdipsa-ad-springboot.herokuapp.com/api/setRating/" + email + "/" + stallId + "/" + newRating;

        StringRequest request = new StringRequest(Request.Method.GET, setRatingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error Setting Ratings", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void createListMenuItemsView() {

        ListMenuItemsAdaptor adaptor = new ListMenuItemsAdaptor(mContext, menuItems);

        if(menuItems_lv !=null) {
            menuItems_lv.setAdapter(adaptor);

            menuItems_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    menuItem = menuItems.get(i);
                    Integer menuItemId = menuItem.getId();
                    replaceFragment(menuItemId);
                }
            });
        }
    }

    private void replaceFragment(Integer menuItemId) {

        Bundle arguments = new Bundle();
        arguments.putInt("menuItemId", menuItemId);
        arguments.putSerializable("centre", hc);
        arguments.putSerializable("stall", hs);
        arguments.putSerializable("menuItem", menuItem);

        Fragment fragment = new Hawkers_MenuFragment();
        fragment.setArguments(arguments);

        this.getParentFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), fragment, null)
                .addToBackStack(null)
                .commit();

    }

    public void removeFragment() {
        this.getParentFragmentManager().popBackStack();
    }

}