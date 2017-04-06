package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        if(intent.hasExtra("symbol")) {
            QuoteSyncJob.isSymbolValidated(getApplicationContext(),intent.getStringExtra("symbol"));
        }else {
            QuoteSyncJob.getQuotes(getApplicationContext());
        }
    }
}
