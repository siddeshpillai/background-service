/* Copyright 2017 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.arcgis.appframework;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

// import android.os.Vibrator;

/*
import java.lang.String;
import java.util.Date;
import java.util.ArrayList;
import java.lang.StringBuilder;
import com.arcgis.appframework.NotificationReceiver;
import java.util.Collections;
import java.util.List;
import android.app.AlarmManager;
import android.os.SystemClock;
import android.app.NotificationManager;
import android.app.PendingIntent;
*/

/*
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.content.pm.PackageManager;

import com.arcgis.appframework.FingerprintAuthenticationDialogFragment;
*/

import com.arcgis.appframework.SecureStorageCallback;
import org.qtproject.qt5.android.QtNative;

/*
import java.io.File;
import java.util.ArrayList;
import android.text.Html;
import android.text.Spanned;
*/


import android.app.PendingIntent;
import android.app.AlarmManager;


//------------------------------------------------------------------------------

public class QmlApplicationActivity extends org.qtproject.qt5.android.bindings.QtActivity
{
    public static native void consoleLog(String text);
    public static native void openUrl(String url);

    //--------------------------------------------------------------------------

    private static QmlApplicationActivity m_Instance;

    // private static Vibrator m_Vibrator;

    // private static List<Integer> scheduledIDBuffer = Collections.synchronizedList(new ArrayList<Integer>());

    //--------------------------------------------------------------------------

    /*     
    private static KeyStore keyStore;
    private static Cipher cipher;
    private static FingerprintManager fingerprintManager = null;
    private static final String KEY_NAME = QmlApplicationActivity.class.getSimpleName();
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    */

    //--------------------------------------------------------------------------

    
    private AlarmManager alarmManager;
    private Intent serviceIntent;
    private PendingIntent pendingIntent;
    

    public QmlApplicationActivity()
    {
        m_Instance = this;
    }

    //--------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        
//        alarmManager = (AlarmManager)m_Instance.getSystemService(Context.ALARM_SERVICE);
//        serviceIntent = new Intent(m_Instance, com.arcgis.appframework.LocationWakefulReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(m_Instance, 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            fingerprintManager = (FingerprintManager) m_Instance.getSystemService(Context.FINGERPRINT_SERVICE);
        }
        */

        consoleLog("onCreate");

        Intent intent = getIntent();

        Uri uri = intent.getData();
        if (uri != null)
        {
            openUrl(uri.toString());
        }

        if (isXLargeScreen(getApplicationContext()))
        {
            consoleLog("Tablet");

            if (!true)
            {
                //set tablets to portrait;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                //set tablets to landscape;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
        else
        {
            consoleLog("Phone");

            if (!false)
            {
                //set phones to portrait;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                //set phones to landscape;
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
    }

    //---------------------------------------------------------------------------

    @Override
    protected void onResume()
    {
        
//        alarmManager.cancel(pendingIntent);
        
        super.onResume();

        if (isXLargeScreen(getApplicationContext()))
        {
            // Tablet
            //hideStatusBar();
        }
        else
        {
            // Phone
            //hideStatusBar();
        }
    }

    //--------------------------------------------------------------------------

    @Override
    public void onPause()
    {
        Intent i = new Intent(m_Instance, com.arcgis.appframework.LocationJobIntentService.class);  //is any of that needed?  idk.
        //note, putExtra remembers type and I need this to be an integer.  so get an integer first.
        i.putExtra("times", 30);  //should do error checking here!
        LocationJobIntentService.enqueueWork(getApplicationContext(),i);

//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 20000, pendingIntent);
        
        super.onPause();
    }

    //--------------------------------------------------------------------------

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        consoleLog("onNewIntent");

        Uri uri = intent.getData();
        if (uri != null)
        {
            openUrl(uri.toString());
        }
    }

    //--------------------------------------------------------------------------

    public static boolean isXLargeScreen(Context context)
    {
        int screenLayout = context.getResources().getConfiguration().screenLayout;

        return ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    //--------------------------------------------------------------------------

    public void hideStatusBar()
    {
        try {
            if (Build.VERSION.SDK_INT < 16) {
                //Android 4.0 and Lower
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                               WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                // Android 4.1 and Higher
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------

    /*
    public static void vibrate()
    {
        if (m_Vibrator == null)
        {
            if (m_Instance != null)
            {
                m_Vibrator = (Vibrator) m_Instance.getSystemService(Context.VIBRATOR_SERVICE);
                m_Vibrator.vibrate(1000);
            }
        }
        else m_Vibrator.vibrate(1000);
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static boolean hasVibration()
    {
        if (m_Vibrator == null)
        {
            if (m_Instance != null)
            {
                m_Vibrator = (Vibrator) m_Instance.getSystemService(Context.VIBRATOR_SERVICE);
                return m_Vibrator.hasVibrator();
            }
            return false;
        }
        else return m_Vibrator.hasVibrator();
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static int schedule(String title, String message, int timeinMilliSeconds, long instance)
    {
        int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        // schedule all notifications via manager
        return scheduleLater(timeinMilliSeconds, title, message, id, instance);
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static int scheduleLater(int milliSeconds, String title, String message,
                                                        int identifier, long instance)
    {
        // Added to the notification ids buffer
        scheduledIDBuffer.add(new Integer(identifier));

        Context con = m_Instance;

        Intent alarmIntent = new Intent(con, NotificationReceiver.class);

        alarmIntent.putExtra ("message", message);
        alarmIntent.putExtra ("title", title);
        alarmIntent.putExtra ("id", identifier);
        alarmIntent.putExtra ("notification_instance", instance);

        // This will create unique pending intent for each notification created
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, identifier, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        // created alarmManager when the application is created
        // reassuring the alarm manager is giving the alarm service
        AlarmManager alarmManager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);

        // Sets after x no of seconds.
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milliSeconds, pendingIntent);

        return identifier;
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static boolean clear(int id)
    {
        synchronized (scheduledIDBuffer) {
            if (scheduledIDBuffer.contains(id))
            {
                Context con = m_Instance;

                Intent alarmIntent = new Intent(con, NotificationReceiver.class);

                AlarmManager alarmManager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(con, id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

                alarmManager.cancel(pendingIntent);

                return true;
            }
       }

       return false;
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static void clearAll()
    {
        AlarmManager alarmManager = (AlarmManager) m_Instance.getSystemService(Context.ALARM_SERVICE);

        synchronized (scheduledIDBuffer) {
            //cancel all scheduled notification
            for (int ids = 0 ; ids < scheduledIDBuffer.size(); ids++)
            {
                clear(scheduledIDBuffer.get(ids));
            }
        }

        scheduledIDBuffer.clear();

        NotificationManager notificationManager = (NotificationManager) m_Instance.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static boolean supported()
    {
        // return false if devices are less than version 19
        // since AppOpsManager was introduced in 19
        // no tracking method available below this version

        return NotificationPermissionUtils.isNotificationEnabled(m_Instance);
    }
    */

    //--------------------------------------------------------------------------

    /*     
    public static void authenticate(String Ui_SignIn, String Ui_Cancel, String Ui_Message, 
                String Ui_Hint, String Ui_Fail, String Ui_Success, long instance)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            generateKey();

            if (cipherInit())
            {
                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

                // Add UI String Values to bundles and send as arguments to fragments
                Bundle bundle = new Bundle();
                bundle.putString("SignIn", Ui_SignIn);
                bundle.putString("Cancel", Ui_Cancel);
                bundle.putString("Message", Ui_Message);
                bundle.putString("Hint", Ui_Hint);
                bundle.putString("Fail", Ui_Fail);
                bundle.putString("Success", Ui_Success);

                FingerprintAuthenticationDialogFragment fragment
                        = new FingerprintAuthenticationDialogFragment();

                fragment.setInstance(instance);
                fragment.setArguments(bundle);
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(cipher));

                fragment.show(m_Instance.getFragmentManager(), DIALOG_FRAGMENT_TAG);

            }
        }
    }
    */    

    //--------------------------------------------------------------------------

    /*
    public static boolean fingerprintActivated()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (fingerprintManager.hasEnrolledFingerprints())
            {
                String permission = "android.permission.USE_FINGERPRINT";
                int res = m_Instance.checkCallingOrSelfPermission(permission);
                return (res == PackageManager.PERMISSION_GRANTED);
            }
        }

        return false;
    }
    */    

    //--------------------------------------------------------------------------    

    /*
    public static int getErrorMessage()
    {
        // The values for returning error-codes are
        // NoError - 0 (default)
        // BiometricNotAvailable - 1
        // BiometricNotActvated - 2
        // BiometricPermissionDisabled - 3

        if (fingerprintManager != null)
        {
            if (!fingerprintManager.isHardwareDetected())
            {
                return 1;
            }
            else if (!fingerprintManager.hasEnrolledFingerprints())
            {
                return 2;
            }
            else
            {
                String permission = "android.permission.USE_FINGERPRINT";
                int res = m_Instance.checkCallingOrSelfPermission(permission);
                if (res != PackageManager.PERMISSION_GRANTED)
                {
                    return 3;
                }
            }
        }
        else
        {
            return 1;
        }

        return 0;
    }
    */    

    //--------------------------------------------------------------------------    

    /*
    public static boolean fingerprintSupported()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return fingerprintManager.isHardwareDetected();
        }
        return false;
    }
    */

    //--------------------------------------------------------------------------

    /*
    protected static void generateKey()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            KeyGenerator keyGenerator;

            try
            {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                keyStore.load(null);

                keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                           KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                               KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
                keyGenerator.generateKey();
                Log.d("Key Generated", "Keygen");

            }
            catch (Exception e)
            {
                if (e instanceof KeyPermanentlyInvalidatedException) {}
                e.printStackTrace();
            }
        }
    }
    */

    //--------------------------------------------------------------------------

    /*
    public static boolean cipherInit()
    {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                   null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        }
        catch (Exception e)
        {
            if (e instanceof KeyPermanentlyInvalidatedException) {}
            e.printStackTrace();
        }
        return false;
    }    
    */

    //--------------------------------------------------------------------------

    public static boolean setValue(String alias, String data, long instance)
    {
        return SecureStorageCallback.storeInKeyChain(alias, data, m_Instance, instance);
    }

    //--------------------------------------------------------------------------

    public static String value(String alias, long instance)
    {
        return SecureStorageCallback.retrieveFromKeyChain(alias, m_Instance, instance);
    }

    //--------------------------------------------------------------------------

    public static void share(String data, String messageUi)
    {
        Context context = QtNative.activity();
        Intent textShareIntent = new Intent(Intent.ACTION_SEND);
        textShareIntent.putExtra(Intent.EXTRA_TEXT, data);
        textShareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(textShareIntent, messageUi));
    }

    //--------------------------------------------------------------------------

    /*
    public static void show(String to, String cc, String bcc, String subject, String body, String attachments, boolean html)
    {
        boolean isToValid = false, isCcValid = false, isBccValid = false, isAttachmentsValid = false;

        String[] filePaths = null;
        String[] toArray = null;
        String[] ccArray = null;
        String[] bccArray = null;

        if (attachments != null && !attachments.isEmpty()) {
            isAttachmentsValid = true;
            filePaths = attachments.split(",");
        }

        if (to != null && !to.isEmpty()) {
            isToValid = true;
            toArray = to.split(",");
        }

        if (cc != null && !cc.isEmpty()) {
            isCcValid = true;
            ccArray = cc.split(",");
        }

        if (bcc != null && !bcc.isEmpty()) {
            isBccValid = true;
            bccArray = bcc.split(",");
        }

        Context context = QtNative.activity();

        ArrayList<Uri> uris = new ArrayList<Uri>();

        if (isAttachmentsValid) {
            for (String file : filePaths) {
                File fileIn = new File(file);
                Uri u = Uri.fromFile(fileIn);
                uris.add(u);
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        if (isToValid) intent.putExtra(Intent.EXTRA_EMAIL, toArray);
        if (isCcValid) intent.putExtra(Intent.EXTRA_CC, ccArray);
        if (isBccValid) intent.putExtra(Intent.EXTRA_BCC, bccArray);

        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (html)
        {
            consoleLog("html");
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_TEXT, fromHtml(body));
        }
        else
        {
            consoleLog("text");
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }

        if (isAttachmentsValid) intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        context.startActivity(intent);
    }

    //--------------------------------------------------------------------------

    public static Spanned fromHtml(String html) {
        consoleLog(html);
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            // will replace once we move to API 26
//            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            result = Html.fromHtml(html);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
    */
    //--------------------------------------------------------------------------
}

//------------------------------------------------------------------------------
