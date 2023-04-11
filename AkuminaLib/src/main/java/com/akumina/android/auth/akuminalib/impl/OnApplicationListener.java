package com.akumina.android.auth.akuminalib.impl;

import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.akumina.android.auth.akuminalib.msal.MSALUtils;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

public class OnApplicationListener implements ApplicationListener {

    @Override
    public void onCreated(IPublicClientApplication application) {
        MSALUtils.getInstance().setMsalClientApplication(application);
    }

    @Override
    public void onError(MsalException exception) {
        MSALUtils.getInstance().onError(exception);
    }
}
