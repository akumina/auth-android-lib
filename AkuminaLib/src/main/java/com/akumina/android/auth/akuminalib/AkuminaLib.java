package com.akumina.android.auth.akuminalib;

import android.app.Activity;
import android.content.Context;

import com.akumina.android.auth.akuminalib.beans.ClientDetails;
import com.akumina.android.auth.akuminalib.impl.AuthenticationHandler;
import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.akumina.android.auth.akuminalib.msal.MSALUtils;
import com.microsoft.identity.client.IPublicClientApplication;

import java.util.logging.Logger;

public final class AkuminaLib {

    private static AkuminaLib akuminaLib;

    private static Logger logger = Logger.getLogger(AkuminaLib.class.getName());

    private AkuminaLib() {
        logger.info("Loading Akumina Lib");
    }

    public static AkuminaLib getInstance() {
        if (akuminaLib == null) {
            akuminaLib = new AkuminaLib();
        }
        return akuminaLib;
    }

    public void authenticateWithMSAL(Activity activity, ClientDetails clientDetails,
                                     AuthenticationHandler authenticationHandler, ApplicationListener applicationListener)
            throws Exception {
        MSALUtils.getInstance().createMAMEnrollmentManager();
        MSALUtils.getInstance().acquireToken(activity, clientDetails, authenticationHandler, applicationListener,false);
    }

    public void authenticateWithMSALAndMAM(Activity activity,
                                           ClientDetails clientDetails,
                                           AuthenticationHandler authenticationHandler,
                                           ApplicationListener applicationListener) throws Exception {
        MSALUtils.getInstance().createMAMEnrollmentManager();
        MSALUtils.getInstance().acquireToken(activity, clientDetails, authenticationHandler, applicationListener,true);
    }
}
