package com.udacity.stockhawk.widget;

import android.content.Context;
import android.database.Cursor;
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

    }

    @Override
    public void onDataSetChanged() {
        if (mStockData != null) {
            mStockData.close();
        }

        String[] projection = new String[]{
                Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE
                , Contract.Quote.COLUMN_ABSOLUTE_CHANGE, Contract.Quote.COLUMN_PERCENTAGE_CHANGE };

        mStockData = mContext.getContentResolver().query(Contract.Quote.getContentUri(),projection,null,null,null);

        /*
        final long identityToken = Binder.clearCallingIdentity();

        data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);


        Binder.restoreCallingIdentity(identityToken);
        */
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

        // Bind data to the views
        float price = Float.parseFloat(stockParcelable.getBid());
        float absoluteChange = Float.parseFloat(stockParcelable.getAbsoluteChange());
        float percentageChange = Float.parseFloat(stockParcelable.getPercentageChange());

        //float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        //float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        views.setTextViewText(R.id.symbol, stockParcelable.getSymbol());
        views.setTextViewText(R.id.price, dollarFormat.format(price));

        if (absoluteChange > 0) {
            views.setInt(R.id.change, "setBackground", R.drawable.percent_change_pill_green);
        } else {
            views.setInt(R.id.change, "setBackground", R.drawable.percent_change_pill_red);
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
