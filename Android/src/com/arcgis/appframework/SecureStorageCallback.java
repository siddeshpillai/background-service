package com.arcgis.appframework;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 *  This class is responsible to store and retrieve data from
 *  android keychain and make callbacks with error code
 **/
public class SecureStorageCallback {

    private static SharedPreferences.Editor editor;
    private static SharedPreferences preferences;
    private static String publicKeyBytesBase64 = null, privateKeyBytesBase64 = null;

    // Security Constants
    public static final String ALGORITHM_TYPE = "RSA";
    public static final String PUBLIC_KEY = "public-key";
    public static final String PRIVATE_KEY = "private-key";
    public static final String PADDING = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";

    static
    {
        try
        {
            System.loadLibrary("qml_ArcGIS_AppFramework_SecureStorage_libAppFrameworkSecureStoragePlugin");
        } catch (UnsatisfiedLinkError e)
        {
            System.err.println("Failed to load libAppFrameworkSecureStoragePlugin.so");
        }
    }

    public static native void errorHandler(int identifier, long instance, String message);

    public static void sendErrorHandler(int id, long instance, String message)
    {
        errorHandler(id, instance, message);
    }

    public static void init(Context context) {

        if (!areKeysAvailable(context))
        {
            KeyPair kp = getKeyPair();
            PublicKey publicKey = kp.getPublic();
            byte[] publicKeyBytes = publicKey.getEncoded();
            publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

            PrivateKey privateKey = kp.getPrivate();
            byte[] privateKeyBytes = privateKey.getEncoded();
            privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));

            preferences = context.getSharedPreferences(PUBLIC_KEY, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.putString(PUBLIC_KEY, publicKeyBytesBase64);
            editor.apply();
            editor.commit();

            preferences = context.getSharedPreferences(PRIVATE_KEY, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.putString(PRIVATE_KEY, privateKeyBytesBase64);
            editor.apply();
            editor.commit();
        }
        else
        {
            preferences = context.getSharedPreferences(PUBLIC_KEY, Context.MODE_PRIVATE);
            publicKeyBytesBase64 = preferences.getString(PUBLIC_KEY, "");

            preferences = context.getSharedPreferences(PRIVATE_KEY, Context.MODE_PRIVATE);
            privateKeyBytesBase64 = preferences.getString(PRIVATE_KEY, "");
        }
    }

    public static boolean areKeysAvailable(Context context) {
        preferences = context.getSharedPreferences(PUBLIC_KEY, Context.MODE_PRIVATE);
        publicKeyBytesBase64 = preferences.getString(PUBLIC_KEY, "");

        preferences = context.getSharedPreferences(PRIVATE_KEY, Context.MODE_PRIVATE);
        privateKeyBytesBase64 = preferences.getString(PRIVATE_KEY, "");

        return publicKeyBytesBase64 != null && !publicKeyBytesBase64.isEmpty() &&
                privateKeyBytesBase64 != null && !privateKeyBytesBase64.isEmpty();
    }

    public static KeyPair getKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM_TYPE);
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kp;
    }

    public static boolean storeInKeyChain(String key, String value, Context context, long instance)
    {
        String encryptedBase64 = null;

        try
        {
            if (key == null || key.isEmpty())
            {
                throw new Exception("Key cannot be null or empty to encrypt data.");
            }

            // delete from preferences
            if (!key.isEmpty() && value.isEmpty())
            {
                preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.remove(key);
                editor.apply();
                editor.commit();
                return true;
            }

            init(context);

            // Encrypt data
            KeyFactory keyFac = KeyFactory.getInstance(ALGORITHM_TYPE);
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKeyBytesBase64.trim().getBytes(), Base64.DEFAULT));
            Key publicKey = keyFac.generatePublic(keySpec);

            final Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

            // Store data in preference
            preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.putString(key, encryptedBase64);
            editor.apply();
            editor.commit();
            return true;
        }
        catch (Exception e)
        {
            sendErrorHandler(1, instance, e.getMessage());
        }

        return false;
    }

    public static String retrieveFromKeyChain(String key, Context context, long instance)
    {
        String encryptedBase64 = null;

        try
        {
            if (key != null && !key.isEmpty())
            {
                preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
                encryptedBase64 = preferences.getString(key, "");

                init(context);

                if (encryptedBase64 != null && !encryptedBase64.isEmpty())
                {
                    KeyFactory keyFac = KeyFactory.getInstance(ALGORITHM_TYPE);
                    KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKeyBytesBase64.trim().getBytes(), Base64.DEFAULT));
                    Key privateKey = keyFac.generatePrivate(keySpec);

                    Cipher cipher1 = Cipher.getInstance(PADDING);
                    cipher1.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] decryptedBytes = cipher1.doFinal(Base64.decode(encryptedBase64, Base64.DEFAULT));
                    return new String(decryptedBytes);
                }
                else
                {
                    throw new Exception("Key not found");
                }
            }
            else
            {
                throw new Exception("Key cannot be null or empty to decrypt data.");
            }
        }
        catch (Exception e)
        {
            sendErrorHandler(1, instance, e.getMessage());
        }
        return null;
    }
}
