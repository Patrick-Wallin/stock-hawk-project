package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by piwal on 3/6/2017.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i("RemoteViewsFactory","test");
        return new StockWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}
