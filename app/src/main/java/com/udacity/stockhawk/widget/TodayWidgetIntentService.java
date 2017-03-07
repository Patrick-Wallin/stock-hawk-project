package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.udacity.stockhawk.sync.QuoteSyncJob;


/**
 * Created by piwal on 3/5/2017.
 */

public class TodayWidgetIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,TodayWidgetProvider.class));



        //QuoteSyncJob.getQuotes(getApplicationContext());

        /*
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")){
            args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
*/
        /*
        String location = Utility.getPreferredLocation(this);
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location,
                System.currentTimeMillis());
        Cursor data = getContentResolver().query(weatherForLocationUri,FORECAST_COLUMNS,null,null,
                WeatherContract.WeatherEntry.COLUMN_DATE + "ASC");
        if(data == null)
            return;
        if(!data.moveToFirst()) {
            data.close();
            return;
        }

        int weatherId = data.getInt(INDEX_WEATHER_ID);
        int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
        String description = data.getString(INDEX_SHORT_DESC);
        double maxTemp = data.getDouble(INDEX_MAX_TEMP);
        String formattedMaxTemperature = Utility.formatTemperature(this,maxTemp);
        data.close();

        for(int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_today_small;
            RemoteViews views = new RemoteViews(getPackageName(),layoutId);

            views.setImageViewResource(R.id.widget_icon,weatherArtResourceId);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views,description);
            }
            views.setTextViewText(R.id.widget_high_temperature,formattedMaxTemperature);

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,launchIntent,0);
            views.setOnClickPendingIntent(R.id.widget,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId,views);


        }
        */
    }
}
