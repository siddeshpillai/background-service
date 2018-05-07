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

import android.app.DialogFragment;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.UnsatisfiedLinkError;

import com.esri.app8633e1d6d5004af08a2071bc6d82b987.R;

//--------------------------------------------------------------------------

/**
 * A dialog which uses fingerprint APIs to authenticate the user. This class
 * is responsible to perform authentication and send triggerers back to JNI
 * which will be handled in the AppFramework authentication module
 *
 * sendTriggered(int) can take in 3 values
 * 0 - success
 * 1 - cancelled by user
 * 2 - invalid credentials
 */
 public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements FingerprintUiHelper.Callback
{
    static
    {
        try
        {
            System.loadLibrary("qml_ArcGIS_AppFramework_Authentication_libAppFrameworkAuthenticationPlugin");
        } catch (UnsatisfiedLinkError e)
        {
            System.err.println("Failed to load libAppFrameworkAuthenticationPlugin.so");
        }
    }

    //--------------------------------------------------------------------------

    public static native void authenticationCompleteInvoked(int result, long instance);

    //--------------------------------------------------------------------------

    private static long m_Instance = 0;

    //--------------------------------------------------------------------------

    private Button mCancelButton;    
    private View mFingerprintContent;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private QmlApplicationActivity mActivity;
    private String uiSignIn = null, uiCancel = null, uiMessage = null, uiHint = null, uiFail = null, uiSuccess = null;

    //--------------------------------------------------------------------------

    public static void setInstance(long instance)
    {
        m_Instance = instance;
    }

    //--------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Avoid recreation of a new Fragment when the Activity
        // is re-created when orientation changes
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
     }

    //--------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState)
    {
        // get ui values from bundle
        uiSignIn = getArguments().getString("SignIn"); 
        uiCancel = getArguments().getString("Cancel"); 
        uiMessage = getArguments().getString("Message");
        uiHint = getArguments().getString("Hint");
        uiFail = getArguments().getString("Fail");
        uiSuccess = getArguments().getString("Success");

        getDialog().setCanceledOnTouchOutside(false);

        if (uiSignIn != null && !uiSignIn.isEmpty())
        {
            getDialog().setTitle(uiSignIn);
        }
        else
        {
            getDialog().setTitle(getString(R.string.sign_in));            
        }

        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
             @Override
             public void onClick(View view) {
                 dismiss();
                 sendTriggered(1);
             }
         });

         mFingerprintContent = v.findViewById(R.id.fingerprint_container);

         mFingerprintUiHelper = new FingerprintUiHelper(
                 mActivity.getSystemService(FingerprintManager.class),
                 (TextView) v.findViewById(R.id.fingerprint_description),
                 (ImageView) v.findViewById(R.id.fingerprint_icon),
                 (TextView) v.findViewById(R.id.fingerprint_status), 
                 uiMessage, uiFail, uiSuccess, uiHint,
                 this);

        if (uiCancel != null && !uiCancel.isEmpty())
        {
            mCancelButton.setText(uiCancel);
        }
        else
        {
            mCancelButton.setText(R.string.cancel);            
        }
        
        mFingerprintContent.setVisibility(View.VISIBLE);

        return v;
    }

    //--------------------------------------------------------------------------

    @Override
    public void onResume()
    {
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject);
    }

    //--------------------------------------------------------------------------

    @Override
    public void onPause()
    {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    //--------------------------------------------------------------------------

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mActivity = (QmlApplicationActivity) getActivity();
    }

    //--------------------------------------------------------------------------

    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject)
    {
        mCryptoObject = cryptoObject;
    }

    //--------------------------------------------------------------------------

    @Override
    public void onAuthenticated()
    {
        dismiss();
        sendTriggered(0);
    }

    //--------------------------------------------------------------------------

    @Override
    public void onError()
    {
        sendTriggered(3);
    }

    //--------------------------------------------------------------------------

    public static void sendTriggered(int result)
    {
        authenticationCompleteInvoked(result, m_Instance);
    }

    //--------------------------------------------------------------------------

}
