package com.team5.HawkeRise.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team5.HawkeRise.R;
import com.team5.HawkeRise.models.MenuItem;

import java.util.List;

public class ListMenuItemsAdaptor extends ArrayAdapter {

    private final Context context;
    List<MenuItem> menuItems;

    public ListMenuItemsAdaptor(Context context, List<MenuItem> menuItems) {
        super(context, R.layout.menu_items_list);

        this.context = context;
        this.menuItems = menuItems;

        for(int i=0; i <menuItems.size(); i++)
        {
            add(null);
        }
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.menu_items_list, parent, false);
        }

        TextView hawkerStall_name = view.findViewById(R.id.itemName);
        hawkerStall_name.setText(menuItems.get(pos).getName());

        TextView itemPrice = view.findViewById(R.id.itemPrice);
        itemPrice.setText(String.valueOf(menuItems.get(pos).getPrice()));

        TextView itemStatus = view.findViewById(R.id.itemStatus);
        itemStatus.setText(menuItems.get(pos).getStatus());

        return view;
    }

}
