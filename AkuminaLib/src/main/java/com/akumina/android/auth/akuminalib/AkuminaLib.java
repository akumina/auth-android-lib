package com.akumina.android.auth.akuminalib;

import com.akumina.android.auth.akuminalib.beans.ClientDetails;
import com.microsoft.identity.client.IPublicClientApplication;

import java.util.logging.Logger;

public final class AkuminaLib {

    private  static  AkuminaLib akuminaLib;

    private  static  Logger logger = Logger.getLogger(AkuminaLib.class.getName());

    private  AkuminaLib () {
        logger.info("Loading Akumina Lib");
    }

    public static  AkuminaLib getInstance() {
        if (akuminaLib == null) {
            akuminaLib = new AkuminaLib();
        }
        return  akuminaLib;
    }

    public void authenticateWithMSAL(ClientDetails clientDetails, IPublicClientApplication.ApplicationCreatedListener applicationCreatedListener) {

    }

    public void authenticateWithMSALAndMAM(ClientDetails clientDetails,IPublicClientApplication.ApplicationCreatedListener applicationCreatedListener) {

    }
}
