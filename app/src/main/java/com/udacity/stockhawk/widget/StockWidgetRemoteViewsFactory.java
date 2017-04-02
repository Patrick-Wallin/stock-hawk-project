package com.udacity.stockhawk.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.StockParcelable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by piwal on 3/6/2017.
 */

public class StockWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor mStockData = null;
    private Context mContext;

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;


    public StockWidgetRemoteViewsFactory(Context context) {
        mContext = context;

        Log.i("SWRVFactory","Constructor");
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onCreate() {
        Log.i("RemoteViewsFactory","onCreate");
    }

    @Override
    public void onDataSetChanged() {
        Log.i("RemoteViewsFactory","onDataSetChanged");
        if (mStockData != null) {
            mStockData.close();
        }

        Log.i("RemoteViewsFactory","onDataSetChanged - 1");
        String[] projection = new String[]{
                Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_BID
                , Contract.Quote.COLUMN_ABSOLUTE_CHANGE, Contract.Quote.COLUMN_PERCENTAGE_CHANGE };

        final long identityToken = Binder.clearCallingIdentity();

        Log.i("RemoteViewsFactory","onDataSetChanged - 2");
        mStockData = mContext.getContentResolver().query(Contract.Quote.getContentUri(),projection,null,null,null);
        Log.i("RemoteViewsFactory","onDataSetChanged - 3");
        Binder.restoreCallingIdentity(identityToken);
        Log.i("RemoteViewsFactory",String.valueOf(mStockData.getCount()));
    }

    @Override
    public void onDestroy() {
        if (mStockData != null) {
            mStockData.close();
            mStockData = null;
        }
    }

    @Override
    public int getCount() {
        return (mStockData == null ? 0 : mStockData.getCount());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.i("RemoteViewsFactory","getViewAt");
        if (position == AdapterView.INVALID_POSITION ||
                mStockData == null || !mStockData.moveToPosition(position)) {
            return null;
        }


        Log.i("RemoteViewsFactory",String.valueOf(position));
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.stock_widget_list_item);

        Log.i("RemoteViewsFactory",String.valueOf(mStockData.getPosition()));

        StockParcelable stockParcelable = new StockParcelable(mStockData);
        Log.i("RemoteViewsFactory",String.valueOf(position));
        // Bind data to the views

        float price = Float.parseFloat(stockParcelable.getBid());
        float absoluteChange = Float.parseFloat(stockParcelable.getAbsoluteChange());
        float percentageChange = Float.parseFloat(stockParcelable.getPercentageChange());

        Log.i("RemoteViewsFactory","step-1");

        //float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        //float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        views.setTextViewText(R.id.symbol, stockParcelable.getSymbol());

        views.setTextViewText(R.id.price, dollarFormat.format(price));

        Log.i("RemoteViewsFactory","step-2");
        Log.i("RemoveViewsFactory", String.format("%.2f",absoluteChange));
        if (absoluteChange > 0) {
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        String change = dollarFormatWithPlus.format(absoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        Log.i("RemoteViewsFactory","step-3");
        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            views.setTextViewText(R.id.change,change);
        } else {
            views.setTextViewText(R.id.change,percentage);
        }



        /*
        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(getResources().getString(R.string.string_symbol), data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        */

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mStockData.moveToPosition(position)) {
            int idColumnIndex = mStockData.getColumnIndex(Contract.Quote._ID);
            //return mStockData.getLong(mStockData.get);
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
