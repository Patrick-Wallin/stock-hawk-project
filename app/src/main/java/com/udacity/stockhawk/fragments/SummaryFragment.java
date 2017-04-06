package com.udacity.stockhawk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.SummaryAdapter;
import com.udacity.stockhawk.data.StockInfo;
import com.udacity.stockhawk.data.StockParcelable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.ButterKnife;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by piwal on 3/3/2017.
 */

public class SummaryFragment extends Fragment {
    private StockParcelable mStockParcelable;

    private RecyclerView mRecycleView;
    private SummaryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String[]> summaryData = new ArrayList<>();

    public SummaryFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_fragment, container, false);

        mRecycleView = (RecyclerView)rootView.findViewById(R.id.info_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        summaryData.clear();

        Bundle args = getArguments();
        if(args != null) {

            mStockParcelable = args.getParcelable("stock_information");
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_previous_close), mStockParcelable.getPreviousClose()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_open), mStockParcelable.getOpen()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_bid), mStockParcelable.getBid()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_ask), mStockParcelable.getAsk()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_day_range), mStockParcelable.getDayRange()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_52_week_range), mStockParcelable.getYearRange()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_volume), mStockParcelable.getVolume()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_average_volume), mStockParcelable.getAvgVolume()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_market_cap), mStockParcelable.getMarketCap()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_pe), mStockParcelable.getPE()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_eps), mStockParcelable.getEPS()});
            summaryData.add(new String[] { getActivity().getApplicationContext().getString(R.string.summary_dividend), mStockParcelable.getDividend()});
        }

        mAdapter = new SummaryAdapter(summaryData);

        RecyclerView recyclerView = ButterKnife.findById(rootView, R.id.info_recycler_view);
        recyclerView.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
