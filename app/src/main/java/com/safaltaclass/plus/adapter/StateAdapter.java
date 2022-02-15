package com.safaltaclass.plus.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.safaltaclass.plus.R;
import com.safaltaclass.plus.model.CentreData;

import java.util.ArrayList;
import java.util.List;

public class StateAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    private List<CentreData> listDataGroup;
    private List<CentreData> listDataGroupFiltered;


    public StateAdapter(Context context, List<CentreData> listDataGroupValues,List<CentreData> listDataGroupFilterValues) {
        this.context = context;
        this.listDataGroup = listDataGroupValues;
        this.listDataGroupFiltered = listDataGroupFilterValues;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_city, null);
        }
        TextView phone = (TextView) convertView.findViewById(R.id.tv_phone_value);
        TextView address = (TextView) convertView.findViewById(R.id.tv_address);
        TextView state = (TextView) convertView.findViewById(R.id.tv_city_state);


        CentreData data = listDataGroupFiltered.get(groupPosition);
        address.setText(String.valueOf(data.getAddress()));
        phone.setText(String.valueOf(data.getPhone()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            state.setText(Html.fromHtml("<font><b>" + data.getCity() + "</b></font>" + ", " + "<font><b>" + data.getState() + "</b></font>" + " " + data.getPin(), 1));
        } else {
            state.setText(Html.fromHtml("<font><b>" + data.getCity() + "</b></font>" + ", " + "<font><b>" + data.getState() + "</b></font>" + " " + data.getPin()));
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return listDataGroupFiltered.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_state, null);
        }

        ImageView ivGroupIndicator = (ImageView) convertView.findViewById(R.id.iv_group_indicator);
        TextView heading = (TextView) convertView.findViewById(R.id.tv_state);
        heading.setText(listDataGroupFiltered.get(groupPosition).getCentername());
        if (isExpanded) {
            ivGroupIndicator.setImageResource(android.R.drawable.arrow_up_float);
        } else {
            ivGroupIndicator.setImageResource(android.R.drawable.arrow_down_float);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                //   listDataGroupFiltered.clear();
                if (charString.isEmpty()) {
                    listDataGroupFiltered = listDataGroup;
                } else {
                    List<CentreData> filteredList = new ArrayList<>();
                    for (CentreData row : listDataGroup) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        if (row.getCentername().toLowerCase().contains(charString.toLowerCase()) || row.getCity().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listDataGroupFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listDataGroupFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listDataGroupFiltered = (ArrayList<CentreData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
