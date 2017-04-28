package com.udacity.stockhawk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.StockParcelable;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by piwal on 3/3/2017.
 */

public class HistoricalFragment extends Fragment {
    private StockParcelable mStockParcelable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.historical_fragment, container, false);

        Bundle args = getArguments();
        if(args != null) {
            LineChart lineChart = (LineChart) rootView.findViewById(R.id.historical_line_chart);
            Description desc = new Description();

            Locale locale = Locale.getDefault();
            Currency currency = Currency.getInstance(locale);
            String symbol = currency.getSymbol();
            desc.setText(symbol);
            lineChart.setDescription(desc);


            List<Entry> values = new ArrayList<>();


            mStockParcelable = args.getParcelable("stock_information");
            HashMap<String,String> historyHashMap = mStockParcelable.getHistory();
            if(historyHashMap != null && !historyHashMap.isEmpty()) {
                Iterator it = historyHashMap.entrySet().iterator();
                int xAxisLineNumber = 0;
                int ix = 0;
                while (it.hasNext()) {
                    if((xAxisLineNumber%4) == 0) {
                        Map.Entry pair = (Map.Entry) it.next();


                        Date date = new Date(Long.valueOf(pair.getKey().toString()).longValue());
                        float price = Float.valueOf(pair.getValue().toString()).floatValue();

                        Entry stockData = new Entry(ix++, price);
                        values.add(stockData);
                    }
                    xAxisLineNumber++;
                    if(ix > 12)
                        break;
                }
            }


            LineDataSet setStock = new LineDataSet(values, mStockParcelable.getSymbol());

            LineData data = new LineData(setStock);

            lineChart.setData(data);
            lineChart.invalidate();
        }

        return rootView;
    }


}
