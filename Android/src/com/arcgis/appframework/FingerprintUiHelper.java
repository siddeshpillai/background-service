package com.arcgis.appframework;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.Toast;

import android.widget.ImageView;
import android.widget.TextView;

import com.esri.app8633e1d6d5004af08a2071bc6d82b987.R;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
public class FingerprintUiHelper extends FingerprintManager.AuthenticationCallback
{

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    //--------------------------------------------------------------------------

    private final FingerprintManager mFingerprintManager;
    private final ImageView mIcon;
    private final TextView mMessageTextView, mErrorTextView;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;
    private String mDescription, mFailMessage, mSuccessMessage, mHintMessage;

    //--------------------------------------------------------------------------

    private boolean mSelfCancelled;

    //--------------------------------------------------------------------------

    FingerprintUiHelper(FingerprintManager fingerprintManager, TextView messageTextView,
            ImageView icon, TextView errorTextView, String description, 
            String failMessage, String successMessage, String hintMessage, Callback callback)
    {
        mFingerprintManager = fingerprintManager;
        mMessageTextView = messageTextView;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mDescription = description;
        mFailMessage = failMessage;
        mSuccessMessage = successMessage;
        mHintMessage = hintMessage;
        mCallback = callback;

        // set the description
        mMessageTextView.setText(mDescription); 
    }

    //--------------------------------------------------------------------------

    public boolean isFingerprintAuthAvailable()
    {
        return mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    //--------------------------------------------------------------------------

    public void startListening(FingerprintManager.CryptoObject cryptoObject)
    {
        if (!isFingerprintAuthAvailable())
        {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager
                .authenticate(cryptoObject, mCancellationSignal, 0 /* flags */, this, null);
        mIcon.setImageResource(R.drawable.ic_fingerprint);
    }

    //--------------------------------------------------------------------------

    public void stopListening()
    {
        if (mCancellationSignal != null)
        {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    //--------------------------------------------------------------------------

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString)
    {
        if (!mSelfCancelled)
        {
            showError(errString);
            mIcon.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mCallback.onError();
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
    }

    //--------------------------------------------------------------------------

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
    {
        showError(helpString);
    }

    //--------------------------------------------------------------------------

    @Override
    public void onAuthenticationFailed()
    {
        if (mFailMessage != null && mFailMessage.isEmpty())
        {
            showError(mFailMessage);            
        }
        else
        {
            showError(mIcon.getResources().getString(
                R.string.fingerprint_not_recognized));
        }
    }

    //--------------------------------------------------------------------------

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
    {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.success_color, null));

        if (mSuccessMessage != null && mSuccessMessage.isEmpty())
        {
            mErrorTextView.setText(mSuccessMessage);
        }
        else
        {
            mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_success));
        }

        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    //--------------------------------------------------------------------------

    private void showError(CharSequence error)
    {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.warning_color, null));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    //--------------------------------------------------------------------------

    private Runnable mResetErrorTextRunnable = new Runnable()
    {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.hint_color, null));

            if (mHintMessage != null && mHintMessage.isEmpty())
            {
                mErrorTextView.setText(mHintMessage);
            }
            else
            {
                mErrorTextView.setText(
                    mErrorTextView.getResources().getString(R.string.fingerprint_hint));
            }

            mIcon.setImageResource(R.drawable.ic_fingerprint);
        }
    };

    //--------------------------------------------------------------------------

    public interface Callback
    {
        void onAuthenticated();
        void onError();
    }

    //--------------------------------------------------------------------------

}
