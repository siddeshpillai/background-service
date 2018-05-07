
//package com.arcgis.appframework;

//import android.content.Intent;
//import android.app.IntentService;
//import android.util.Log;

//import com.arcgis.appframework.LocationWakefulReceiver;

//public class LocationIntentService extends IntentService {

//    public LocationIntentService() {
//        super("LocationIntentService");
//    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.i("LocationIntentService", "Completed service ");

//        // Release the wake lock provided by the WakefulBroadcastReceiver.
//        LocationWakefulReceiver.completeWakefulIntent(intent);
//    }
//}

