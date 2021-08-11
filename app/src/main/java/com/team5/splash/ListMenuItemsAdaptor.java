package com.team5.splash;

import android.content.Context;
import android.widget.ArrayAdapter;

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

}
