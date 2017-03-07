package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";

        public static final String COLUMN_PREVIOUS_CLOSE = "previous_close";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_BID = "bid";
        public static final String COLUMN_ASK = "ask";
        public static final String COLUMN_DAY_LOW = "day_low";
        public static final String COLUMN_DAY_HIGH = "day_high";
        public static final String COLUMN_YEAR_LOW = "year_low";
        public static final String COLUMN_YEAR_HIGH = "year_high";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_AVG_VOLUME = "avg_volume";
        public static final String COLUMN_MARKET_CAP = "market_cap";
        public static final String COLUMN_PE = "pe";
        public static final String COLUMN_EPS = "eps";
        public static final String COLUMN_DIVIDEND = "dividend";

        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final int POSITION_HISTORY = 5;

        public static final int POSITION_PREVIOUS_CLOSE = 6;
        public static final int POSITION_OPEN = 7;
        public static final int POSITION_BID = 8;
        public static final int POSITION_ASK = 9;
        public static final int POSITION_DAY_LOW = 10;
        public static final int POSITION_DAY_HIGH = 11;
        public static final int POSITION_YEAR_LOW = 12;
        public static final int POSITION_YEAR_HIGH = 13;
        public static final int POSITION_VOLUME = 14;
        public static final int POSITION_AVG_VOLUME = 15;
        public static final int POSITION_MARKET_CAP = 16;
        public static final int POSITION_PE = 17;
        public static final int POSITION_EPS = 18;
        public static final int POSITION_DIVIDEND = 19;

        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY,
                COLUMN_PREVIOUS_CLOSE,
                COLUMN_OPEN,
                COLUMN_BID,
                COLUMN_ASK,
                COLUMN_DAY_LOW,
                COLUMN_DAY_HIGH,
                COLUMN_YEAR_LOW,
                COLUMN_YEAR_HIGH,
                COLUMN_VOLUME,
                COLUMN_AVG_VOLUME,
                COLUMN_MARKET_CAP,
                COLUMN_EPS,
                COLUMN_DIVIDEND
        );
        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri getContentUri() {
            return BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
