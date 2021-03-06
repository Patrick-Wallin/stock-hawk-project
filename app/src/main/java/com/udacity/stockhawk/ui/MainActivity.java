package com.udacity.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.StockParcelable;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.widget.TodayWidgetProvider;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        StockAdapter.StockAdapterOnClickHandler {

    private static final int STOCK_LOADER = 0;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView error;
    private StockAdapter adapter;

    private String symbolFromWidget = "";

    private SymbolValidatedBroadcastReceiver mySymbolValidatedBroadcastReceiver;

    @Override
    public void onClick(String symbol, Cursor cursor) {
        symbolFromWidget = "";
        StockParcelable sp = new StockParcelable(cursor);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("stock_information",sp);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        System.setProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");

        ButterKnife.bind(this);

        adapter = new StockAdapter(this, this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        IntentFilter intentFilter = new IntentFilter(QuoteSyncJob.ACTION_SYMBOL_VALIDATED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        mySymbolValidatedBroadcastReceiver = new SymbolValidatedBroadcastReceiver();
        registerReceiver(mySymbolValidatedBroadcastReceiver,intentFilter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        QuoteSyncJob.initialize(this);
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(MainActivity.this, symbol);
                getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);

                refreshWidget();

            }
        }).attachToRecyclerView(stockRecyclerView);

        symbolFromWidget = "";
        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(getString(R.string.header_symbol))) {
                symbolFromWidget = intent.getStringExtra(getString(R.string.header_symbol));
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mySymbolValidatedBroadcastReceiver);
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRefresh() {

        QuoteSyncJob.syncImmediately(this,"");
        if (!networkUp() && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
        } else if (!networkUp()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(this).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    public void button(@SuppressWarnings("UnusedParameters") View view) {
        if(networkUp()) {
            new AddStockDialog().show(getFragmentManager(), "StockDialogFragment");
        }else {
            String message = getString(R.string.error_no_network_new_stock);
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        }
    }

    void addStock(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {
            if(networkUp()) {
                QuoteSyncJob.syncImmediately(this, symbol);
            }else {
                String message = getString(R.string.error_no_network_new_stock);
                Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            }
        }
    }

    public void refreshWidget() {
        ComponentName name = new ComponentName(this, TodayWidgetProvider.class);
        int [] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(name);

        Intent intent = new Intent(this,TodayWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data.getCount() != 0) {
            error.setVisibility(View.GONE);
        }
        adapter.setCursor(data);

        if(data.getCount() != 0 && !symbolFromWidget.isEmpty()) {
            onClick(symbolFromWidget, adapter.getCurrentCursorBasedOnSymbol(symbolFromWidget));
        }

        refreshWidget();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
    }


    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
            item.setTitle(R.string.content_description_dollar);
        } else {
            item.setIcon(R.drawable.ic_dollar);
            item.setTitle(R.string.content_description_percentage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SymbolValidatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSymbolValidated = intent.getBooleanExtra("symbolvalidated",false);
            String symbol = intent.getStringExtra("symbol");
            if(isSymbolValidated) {
                if (networkUp()) {
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                PrefUtils.addStock(getApplicationContext(), symbol);
                QuoteSyncJob.syncImmediately(getApplicationContext(), "");
            }else {
                String message = getString(R.string.toast_stock_symbol_invalidated, symbol);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

        }
    }
}
