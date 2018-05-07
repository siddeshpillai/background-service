package com.arcgis.appframework;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.*;
//import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

public class LocationJobIntentService extends JobIntentService {

    static final int JOB_ID = 1000;
    final String TAG = "LocationJobIntentService";

    private final Random mGenerator = new Random();

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LocationJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.

        int top = intent.getIntExtra("times", 5);  //default is 5.
        int num;

        Log.wtf(TAG, "top " + top);

        for (int i = 0; i < top; i++) {
            Log.wtf(TAG, "i value is " + i);
            num = mGenerator.nextInt(100);
            toast("Random number is " + num);
            Log.wtf(TAG, "Random number is " + num);
            try {
                Thread.sleep(60000);  // 1000 is one second, ten seconds would be 10000
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.wtf(TAG, "All work complete");
        toast("All work complete");
    }

    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                System.out.println(text);
//                Toast.makeText(LocationJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
