package com.s22010213.wasteless;

import android.widget.Filter;

import com.s22010213.wasteless.adapters.AdapterAd;
import com.s22010213.wasteless.models.ModelAd;

import java.util.ArrayList;

public class FilterAd extends Filter {

    private AdapterAd adapter;
    private ArrayList<ModelAd> filterList;

    public FilterAd(AdapterAd adapter, ArrayList<ModelAd> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelAd> filteredModels = new ArrayList<>();
            for (int i=0; i< filterList.size(); i++){

                if (filterList.get(i).getTitle().toUpperCase().contains(constraint) ||
                filterList.get(i).getFood_type().toUpperCase().contains(constraint)) {
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;

        }else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.adArrayList = (ArrayList<ModelAd>) results.values;
        adapter.notifyDataSetChanged();
    }
}
