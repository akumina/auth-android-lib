package com.akumina.android.auth.akuminalib.impl;

import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.akumina.android.auth.akuminalib.msal.MSALUtils;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

public class OnApplicationListener implements ApplicationListener {

    private ApplicationListener childListener;

    public  OnApplicationListener(ApplicationListener childListener) {
        this.childListener = childListener;
    }
    @Override
    public void onCreated(IPublicClientApplication application) {
        MSALUtils.getInstance().setMsalClientApplication(application);
        childListener.onCreated(application);
    }

    @Override
    public void onError(MsalException exception) {
        MSALUtils.getInstance().onError(exception);
        childListener.onError(exception);
    }
}
