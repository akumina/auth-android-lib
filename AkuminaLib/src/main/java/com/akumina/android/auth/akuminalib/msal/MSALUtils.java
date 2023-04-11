package com.akumina.android.auth.akuminalib.msal;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.akumina.android.auth.akuminalib.R;
import com.akumina.android.auth.akuminalib.beans.ClientDetails;
import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

import java.util.logging.Logger;

public class MSALUtils {

    private static final Logger LOGGER = Logger.getLogger(MSALUtils.class.getName());

    private  IPublicClientApplication mMsalClientApplication;

    private  ISingleAccountPublicClientApplication mSingleAccountApp;

    private  Context appContext;

    private static MSALUtils instance;

    private MSALUtils() {

    }

    public  static  MSALUtils getInstance() {
        if(instance == null) {
            instance = new MSALUtils();
        }
        return  instance;
    }
    @WorkerThread
    public static void acquireToken(final Context appContext, ClientDetails clientDetails, @NonNull final AuthenticationCallback callback)
            throws MsalException, InterruptedException {



    }

    private  synchronized void initializeMsalClientApplication(final Context appContext,
                                                                    ClientDetails clientDetails,
                                                                    ApplicationListener applicationListener)
            throws MsalException, InterruptedException {
        if (mMsalClientApplication == null) {
            com.microsoft.identity.client.Logger msalLogger
                    = com.microsoft.identity.client.Logger.getInstance();
            msalLogger.setEnableLogcatLog(true);
            msalLogger.setLogLevel(com.microsoft.identity.client.Logger.LogLevel.VERBOSE);
            msalLogger.setEnablePII(true);
            PublicClientApplication.create(appContext,clientDetails.getClientId(),
                    clientDetails.getAuthority(), clientDetails.getRedirectUri(), applicationListener);
        }
    }

    public  ISingleAccountPublicClientApplication getSingleAccountApp() {
        return mSingleAccountApp;
    }

    public  void setMsalClientApplication(IPublicClientApplication mMsalClientApplicationTemp) {
        mMsalClientApplication = mMsalClientApplicationTemp;
    }

    public  void onError(MsalException exception) {

    }
}
