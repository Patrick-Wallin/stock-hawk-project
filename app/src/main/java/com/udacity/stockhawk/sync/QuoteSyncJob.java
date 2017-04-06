package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    public static final String ACTION_SYMBOL_VALIDATED = "com.udacity.stockhawk.ACTION_SYMBOL_VALIDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {}

    static void isSymbolValidated(Context context, String symbol) {
        boolean isValidated = false;

        Stock stock = null;
        try {
            stock = YahooFinance.get(symbol);
            isValidated = (stock.getName() != null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent symbolValidatedIntent = new Intent(ACTION_SYMBOL_VALIDATED);
        symbolValidatedIntent.putExtra("symbolvalidated",isValidated);
        symbolValidatedIntent.putExtra("symbol",symbol);
        context.sendBroadcast(symbolValidatedIntent);
    }

    static void getQuotes(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {
            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            if (stockArray.length == 0) {
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                String symbol = iterator.next();

                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();

                if(stock.getName() != null) {
                    float price = 0.0f;
                    float change = 0.0f;
                    float percentChange = 0.0f;
                    float previousClose = 0.0f;
                    float open = 0.0f;
                    float bid = 0.0f;
                    float ask = 0.0f;
                    float daylow = 0.0f;
                    float dayhigh = 0.0f;
                    float yearlow = 0.0f;
                    float yearhigh = 0.0f;
                    float marketCap = 0.0f;
                    float pe = 0.0f;
                    float eps = 0.0f;
                    float dividend = 0.0f;
                    long volume = 0l;
                    long avgVolume = 0l;

                    try {
                        price = quote.getPrice().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        change = quote.getChange().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        percentChange = quote.getChangeInPercent().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        previousClose = quote.getPreviousClose().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        open = quote.getOpen().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        bid = quote.getBid().floatValue();
                    } catch (Exception e) {
                    }

                    //Log.i("Bid",String.format("%.2f",bid));
                    try {
                        ask = quote.getAsk().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        daylow = quote.getDayLow().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        dayhigh = quote.getDayHigh().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        yearlow = quote.getYearLow().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        yearhigh = quote.getYearHigh().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        volume = quote.getVolume();
                    } catch (Exception e) {
                    }

                    try {
                        avgVolume = quote.getAvgVolume();
                    } catch (Exception e) {
                    }

                    try {
                        marketCap = stock.getStats().getMarketCap().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        pe = stock.getStats().getShortRatio().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        eps = stock.getStats().getEps().floatValue();
                    } catch (Exception e) {
                    }

                    try {
                        dividend = stock.getDividend().getAnnualYield().floatValue();
                    } catch (Exception e) {
                    }


                    // WARNING! Don't request historical data for a stock that doesn't exist!
                    // The request will hang forever X_x
                    List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                    StringBuilder historyBuilder = new StringBuilder();

                    for (HistoricalQuote it : history) {
                        historyBuilder.append(it.getDate().getTimeInMillis());
                        historyBuilder.append(", ");
                        historyBuilder.append(it.getClose());
                        historyBuilder.append("\n");
                    }

                    ContentValues quoteCV = new ContentValues();
                    quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                    quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                    quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                    quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

                    quoteCV.put(Contract.Quote.COLUMN_PREVIOUS_CLOSE, previousClose);
                    quoteCV.put(Contract.Quote.COLUMN_OPEN, open);
                    quoteCV.put(Contract.Quote.COLUMN_BID, bid);
                    quoteCV.put(Contract.Quote.COLUMN_ASK, ask);
                    quoteCV.put(Contract.Quote.COLUMN_DAY_LOW, daylow);
                    quoteCV.put(Contract.Quote.COLUMN_DAY_HIGH, dayhigh);
                    quoteCV.put(Contract.Quote.COLUMN_YEAR_LOW, yearlow);
                    quoteCV.put(Contract.Quote.COLUMN_YEAR_HIGH, yearhigh);
                    quoteCV.put(Contract.Quote.COLUMN_VOLUME, volume);
                    quoteCV.put(Contract.Quote.COLUMN_AVG_VOLUME, avgVolume);
                    quoteCV.put(Contract.Quote.COLUMN_MARKET_CAP, marketCap);
                    quoteCV.put(Contract.Quote.COLUMN_PE, pe);
                    quoteCV.put(Contract.Quote.COLUMN_EPS, eps);
                    quoteCV.put(Contract.Quote.COLUMN_DIVIDEND, dividend);

                    quoteCVs.add(quoteCV);
                }
            }

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {
        //Timber.d("initialize");
        //ConnectivityManager cm =
        //        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Timber.d("get in!");
            schedulePeriodic(context);
            syncImmediately(context, "");
        //}
    }

    public static synchronized void syncImmediately(Context context, String symbol) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            if(symbol != null && !symbol.isEmpty())
                nowIntent.putExtra("symbol",symbol);
            context.startService(nowIntent);
        } else {
            Timber.d("Jobbuilder");
            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }

}
