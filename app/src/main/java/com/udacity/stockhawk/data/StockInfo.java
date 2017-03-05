package com.udacity.stockhawk.data;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by piwal on 3/3/2017.
 */

public class StockInfo {
    private Stock mStock;
    private StockQuote mStockQuote;

    public StockInfo(String stockSymbol) {
        try {
            mStock = YahooFinance.get(stockSymbol);
            mStockQuote = mStock.getQuote();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPreviousClose() {
        if(mStockQuote != null)
            return mStockQuote.getPreviousClose().toString();
        else
            return "";
    }

    public String getOpen() {
        if(mStockQuote != null) {
            return mStockQuote.getOpen().toString();
        }else
            return "";
    }
}
