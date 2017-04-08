package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static android.R.attr.data;

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

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        if (mStockData != null) {
            mStockData.close();
        }

        String[] projection = new String[]{
                Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_BID
                , Contract.Quote.COLUMN_ABSOLUTE_CHANGE, Contract.Quote.COLUMN_PERCENTAGE_CHANGE };

        final long identityToken = Binder.clearCallingIdentity();

        mStockData = mContext.getContentResolver().query(Contract.Quote.getContentUri(),projection,null,null,null);

        Binder.restoreCallingIdentity(identityToken);

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
        if (position == AdapterView.INVALID_POSITION ||
                mStockData == null || !mStockData.moveToPosition(position)) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.stock_widget_list_item);

        StockParcelable stockParcelable = new StockParcelable(mStockData);

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

        float price = 0.0f;
        float absoluteChange = 0.0f;
        float percentageChange = 0.0f;

        price = stockParcelable.getBidInFloat();
        absoluteChange = stockParcelable.getAbsoluteChangeInFloat();
        percentageChange = stockParcelable.getPercentageChangeInFloat();

        //String price = stockParcelable.getBid();
        //String absoluteChange = stockParcelable.getAbsoluteChange();
        //String percentageChange = stockParcelable.getPercentageChange();


        /*
        try {
            Number parsePrice = nf.parse(stockParcelable.getBid());
            //price = Float.parseFloat(stockParcelable.getBid());
            price = parsePrice.floatValue();
        }catch(Exception e) {}

        try {
            Number parseAbsolutePrice = nf.parse(stockParcelable.getAbsoluteChange());
            //absoluteChange = Float.parseFloat(stockParcelable.getAbsoluteChange());
            absoluteChange = parseAbsolutePrice.floatValue();
        }catch(Exception e) {}

        try {
            Number parsePercentageChange = nf.parse(stockParcelable.getPercentageChange());
            //percentageChange = Float.parseFloat(stockParcelable.getPercentageChange());
            percentageChange = parsePercentageChange.floatValue();
        }catch(Exception e) {}
        */

        //float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        //float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        views.setTextViewText(R.id.symbol, stockParcelable.getSymbol());

        views.setTextViewText(R.id.price, dollarFormat.format(price));
        //views.setTextViewText(R.id.price,price);


        if (absoluteChange > 0) {
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }


        String change = dollarFormatWithPlus.format(absoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            views.setTextViewText(R.id.change,change);
        } else {
            views.setTextViewText(R.id.change,percentage);
        }

        /*
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(mContext.getString(R.string.header_symbol), stockParcelable.getSymbol());
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_list_item,pendingIntent);
        */


        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(mContext.getString(R.string.header_symbol), stockParcelable.getSymbol());

        views.setOnClickFillInIntent(R.id.widget_list_item, intent);


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
