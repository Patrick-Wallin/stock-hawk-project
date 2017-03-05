package com.udacity.stockhawk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.StockParcelable;

import java.util.ArrayList;

/**
 * Created by piwal on 3/3/2017.
 */

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {
    private ArrayList<String[]> mData;

    public SummaryAdapter(ArrayList<String[]> data) {
        mData = data;
    }

    @Override
    public SummaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SummaryAdapter.ViewHolder holder, int position) {
        String[] data = mData.get(position);
        holder.tvSummaryDescription.setText(data[0]);
        holder.tvSummaryInformation.setText(data[1]);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSummaryDescription;
        public TextView tvSummaryInformation;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSummaryDescription = (TextView) itemView.findViewById(R.id.summary_description);
            tvSummaryInformation = (TextView) itemView.findViewById(R.id.summary_information);
        }
    }
}
