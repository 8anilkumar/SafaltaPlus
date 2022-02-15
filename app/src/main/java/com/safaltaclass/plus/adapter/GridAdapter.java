package com.safaltaclass.plus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.safaltaclass.plus.R;
import com.safaltaclass.plus.model.GridItemMenu;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends ArrayAdapter {

    private Context context;
    private List<GridItemMenu> menuList = new ArrayList<>();

    public GridAdapter(Context context, int textViewResourceId, List<GridItemMenu> menuList) {
        super(context, textViewResourceId, menuList);
        this.context = context;
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.layout_grid_item, null);
        //TextView textView = (TextView) v.findViewById(R.id.tv_menu);
        ImageView imageView = (ImageView) v.findViewById(R.id.iv_menu);
        // textView.setText(menuList.get(position).getMenuName());
        imageView.setImageResource(menuList.get(position).getMenuImage());
        return v;

    }
}

