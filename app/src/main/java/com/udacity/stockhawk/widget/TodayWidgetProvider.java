package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by piwal on 3/5/2017.
 */

public class TodayWidgetProvider extends AppWidgetProvider {
    /*
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context,QuoteIntentService.class));
        }
    }
    */

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("onupdate","todaywidgetprovider");
        for(int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            views.setRemoteAdapter(R.id.stock_list_view, new Intent(context, StockWidgetRemoteViewsService.class));

            /*
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            views.setOnClickPendingIntent(R.id.stock_list_view, pendingIntent);
            */
            //appWidgetManager.updateAppWidget(appWidgetId,views);


           // views.setRemoteAdapter(R.id.widget_list,
             //       new Intent(context, StockWidgetRemoteViewsService.class));

            /*
            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
            views.setOnClickPendingIntent(R.id.stock_list_view, appPendingIntent);
            */
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
