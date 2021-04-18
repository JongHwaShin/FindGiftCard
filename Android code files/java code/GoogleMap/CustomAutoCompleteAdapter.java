package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomAutoCompleteAdapter extends ArrayAdapter<CustomItem> {

    private List<CustomItem> queryListFull;

    public CustomAutoCompleteAdapter(@NonNull Context context, @NonNull List<CustomItem> queryList) {
        super(context, 0, queryList);
        queryListFull = new ArrayList<>(queryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return queryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.custom_autocomplete_layout, parent, false
            );
        }

        TextView textView_query = convertView.findViewById(R.id.customL_query);
        TextView textView_addr = convertView.findViewById(R.id.customL_addr);
        TextView textView_idx = convertView.findViewById(R.id.customL_idx);
        ImageView imageView_icon = convertView.findViewById(R.id.customL_icon);

        CustomItem customItem = getItem(position);

        if(customItem != null){
            textView_query.setText(customItem.getQuery());
            textView_addr.setText(customItem.getAddr());
            textView_idx.setText("" + customItem.getIdx());
            imageView_icon.setImageResource(customItem.getIcon());
        }

        return convertView;
    }

    private Filter queryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CustomItem> suggestions = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                suggestions.addAll(queryListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CustomItem item : queryListFull) {
                    if(item.getQuery().toLowerCase().contains(filterPattern)){
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((CustomItem) resultValue).getQuery();
        }
    };
}


class CustomItem {
    private String query;
    private String addr;
    private int icon;
    private int idx;

    public CustomItem(String query, String addr, int icon, int idx) {
        this.query = query;
        this.addr = addr;
        this.icon = icon;
        this.idx = idx;
    }

    public String getQuery(){
        return query;
    }
    public String getAddr(){
        return addr;
    }
    public int getIcon(){
        return icon;
    }
    public int getIdx(){
        return idx;
    }
}

