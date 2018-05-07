
//package com.arcgis.appframework;

//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.content.WakefulBroadcastReceiver;
//import android.util.Log;

//import com.arcgis.appframework.LocationIntentService;

//public class LocationWakefulReceiver extends WakefulBroadcastReceiver {

//    @Override
//    public void onReceive(Context context, Intent intent) {

//        // Start the service, keeping the device awake while the service is
//        // launching. This is the Intent to deliver to the service.
//        Intent service = new Intent(context, com.arcgis.appframework.LocationIntentService.class);
//        Log.i("LocationWakefulReceiver", "Starting service");
//        startWakefulService(context, service);
//    }
//}

