package com.arcgis.appframework;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.AsyncTask;

import java.lang.UnsatisfiedLinkError;

import com.esri.app8633e1d6d5004af08a2071bc6d82b987.R;

public class NotificationReceiver extends BroadcastReceiver {

    static
    {
        try
        {
            System.loadLibrary("qml_ArcGIS_AppFramework_Notifications_Local_libAppFrameworkNotificationsLocalPlugin");
        } catch (UnsatisfiedLinkError e)
        {
            System.err.println("Failed to load libAppFrameworkNotificationsLocalPlugin.so");
        }
    }

    public static native void triggerInvoked(int identifier, long notification_instance);

    private static int id = 0;
    private static long instance = 0;

    @Override
    public void onReceive (Context context, Intent intent)
    {

        // Get values from the intents
        String message = intent.getStringExtra ("message");
        String title = intent.getStringExtra ("title");
        id = intent.getIntExtra("id", 0);
        instance = intent.getLongExtra("notification_instance", 0);


        // Build the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder =
                            new Notification.Builder(context)
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true);

        String packageName = context.getApplicationContext().getPackageName();
        Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(context, 0,
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        // Trigger the notification
        notificationManager.notify(id, builder.build());

        // callback for trigger
        sendTriggered();
    }

    public static void sendTriggered()
    {
        triggerInvoked(id, instance);
    }

}
