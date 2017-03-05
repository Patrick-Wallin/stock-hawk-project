package com.udacity.stockhawk.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.StockInfo;
import com.udacity.stockhawk.data.StockParcelable;
import com.udacity.stockhawk.fragments.HistoricalFragment;
import com.udacity.stockhawk.fragments.NewsFragment;
import com.udacity.stockhawk.fragments.SummaryFragment;

import java.util.ArrayList;

import yahoofinance.Stock;

/**
 * Created by piwal on 3/3/2017.
 */

public class DetailPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    private final int[] MENUPAGER = {R.string.title_pager_summary,R.string.title_pager_historical,R.string.title_pager_news};

    private SummaryFragment summaryFragment;
    private HistoricalFragment historicalFragment;
    private NewsFragment newsFragment;

    private final Fragment[] fragments = new Fragment[MENUPAGER.length];

    private String mStockSymbol = "";

    public DetailPagerAdapter(FragmentManager fm, Context context, StockParcelable stockParcelable) {
        super(fm);
        mContext = context;
        Bundle bundle = new Bundle();
        bundle.putParcelable("stock_information", stockParcelable);
        summaryFragment = new SummaryFragment();
        summaryFragment.setArguments(bundle);
        historicalFragment = new HistoricalFragment();
        historicalFragment.setArguments(bundle);
        newsFragment = new NewsFragment();
        newsFragment.setArguments(bundle);
        fragments[0] = summaryFragment;
        fragments[1] = historicalFragment;
        fragments[2] = newsFragment;

    }

    @Override
    public Fragment getItem(int position) {
        if(MENUPAGER.length > position) {
            return fragments[position];
        }
        return null;
    }

    @Override
    public int getCount() {
        return MENUPAGER.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(MENUPAGER.length > position) {
            return mContext.getString(MENUPAGER[position]);
        }
        return null;
    }
}
