package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by piwal on 3/5/2017.
 */

public class TodayWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DETAIL_ACTIVITY = "com.udacity.stockhawk.TodayWidgetProvider.LAUNCH_DETAIL_ACTIVITY";
    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);


        if (intent.getAction() == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            Timber.d("onReceive via Widget");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_item);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

            // does not work this way.
            /*
            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.stock_list_view, clickPendingIntentTemplate);
            */
            Intent intent = new Intent(context,StockWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            //views.setRemoteAdapter(R.id.stock_list_view, new Intent(context, StockWidgetRemoteViewsService.class));
            views.setRemoteAdapter(R.id.stock_list_view, intent);


            appWidgetManager.updateAppWidget(appWidgetId,views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.stock_list_view);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
