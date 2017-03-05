package com.udacity.stockhawk.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by piwal on 3/3/2017.
 */

public class StockParcelable implements Parcelable {
    private String mPreviousClose;
    private String mOpen;
    private String mBid;
    private String mAsk;
    private String mDayRange;
    private String mYearRange;
    private String mVolume;
    private String mAvgVolume;
    private String mMarketCap;
    private String mPE;
    private String mEPS;
    private String mDividend;
    private HashMap<String,String> mHistory;

    public StockParcelable(Cursor cursor) {
        int previousCloseColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_PREVIOUS_CLOSE);
        int openColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_OPEN);
        int bidColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_BID);
        int askColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_ASK);
        int dayLowColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_DAY_LOW);
        int dayHighColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_DAY_HIGH);
        int yearLowColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_YEAR_LOW);
        int yearHighColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_YEAR_HIGH);
        int volumeColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_VOLUME);
        int avgVolumeColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_AVG_VOLUME);
        int marketCapColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_MARKET_CAP);
        int peColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_PE);
        int epsColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_EPS);
        int dividendColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_DIVIDEND);
        int historyColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY);

        mPreviousClose = String.format("%.2f",cursor.getFloat(previousCloseColumn));
        mOpen = String.format("%.2f",cursor.getFloat(openColumn));
        mBid = String.format("%.2f",cursor.getFloat(bidColumn));
        mAsk = String.format("%.2f",cursor.getFloat(askColumn));
        mDayRange = String.format("%.2f -  %.2f",cursor.getFloat(dayLowColumn),cursor.getFloat(dayHighColumn));
        mYearRange = String.format("%.2f -  %.2f",cursor.getFloat(yearLowColumn),cursor.getFloat(yearHighColumn));
        mVolume = String.format("%d",cursor.getInt(volumeColumn));
        mAvgVolume = String.format("%d",cursor.getInt(avgVolumeColumn));
        mMarketCap = String.format("%.2f",cursor.getFloat(marketCapColumn));
//        mPE = String.format("%.2f",cursor.getFloat(peColumn));
        mEPS = String.format("%.2f",cursor.getFloat(epsColumn));
        mDividend = String.format("%.2f",cursor.getFloat(dividendColumn));

        mHistory = new HashMap<String,String>();

        String history = cursor.getString(historyColumn).trim();
        if(!history.isEmpty()) {
            String[] historyData = history.split("\\n");
            if(historyData.length > 0) {
                for(int i = 0; i < historyData.length; i++) {
                    String[] lineData = historyData[i].split(",");
                    if(!mHistory.containsKey(lineData[0].trim())) {
                        Log.i("Date",lineData[0].trim());
                        mHistory.put(lineData[0].trim(),lineData[1].trim());
                    }
                }
            }
        }
    }

    protected StockParcelable(Parcel in) {
        mPreviousClose = in.readString();
        mOpen = in.readString();
        mBid = in.readString();
        mAsk = in.readString();
        mDayRange = in.readString();
        mYearRange = in.readString();
        mVolume = in.readString();
        mAvgVolume = in.readString();
        mMarketCap = in.readString();
        mPE = in.readString();
        mEPS = in.readString();
        mDividend = in.readString();
        mHistory = in.readHashMap(Float.class.getClassLoader());
    }

    public String getPreviousClose() {
        return mPreviousClose;
    }
    public String getOpen() {
        return mOpen;
    }
    public String getBid() { return mBid; }
    public String getAsk() { return mAsk; }
    public String getDayRange() { return mDayRange; }
    public String getYearRange() { return mYearRange; }
    public String getVolume() { return mVolume; }
    public String getAvgVolume() { return mAvgVolume; }
    public String getMarketCap() { return mMarketCap; }
    public String getPE() { return mPE; }
    public String getEPS() { return mEPS; }
    public String getDividend() { return mDividend; }
    public HashMap<String,String> getHistory() { return mHistory; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPreviousClose);
        dest.writeString(mOpen);
        dest.writeString(mBid);
        dest.writeString(mAsk);
        dest.writeString(mDayRange);
        dest.writeString(mYearRange);
        dest.writeString(mVolume);
        dest.writeString(mAvgVolume);
        dest.writeString(mMarketCap);
        dest.writeString(mPE);
        dest.writeString(mEPS);
        dest.writeString(mDividend);
        dest.writeMap(mHistory);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<StockParcelable> CREATOR = new Creator<StockParcelable>() {
        @Override
        public StockParcelable createFromParcel(Parcel in) {
            return new StockParcelable(in);
        }

        @Override
        public StockParcelable[] newArray(int size) {
            return new StockParcelable[size];
        }
    };
}