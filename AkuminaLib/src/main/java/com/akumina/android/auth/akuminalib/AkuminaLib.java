package com.akumina.android.auth.akuminalib;

import android.app.Activity;
import android.content.Context;

import com.akumina.android.auth.akuminalib.beans.ClientDetails;
import com.akumina.android.auth.akuminalib.impl.AuthenticationHandler;
import com.akumina.android.auth.akuminalib.listener.AkuminaTokenCallback;
import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.akumina.android.auth.akuminalib.listener.ErrorListener;
import com.akumina.android.auth.akuminalib.listener.LoggingHandler;
import com.akumina.android.auth.akuminalib.listener.ResponseListener;
import com.akumina.android.auth.akuminalib.listener.SharePointAuthCallback;
import com.akumina.android.auth.akuminalib.msal.AuthFile;
import com.akumina.android.auth.akuminalib.msal.MSALUtils;
import com.akumina.android.auth.akuminalib.utils.TokenType;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.logging.Logger;

public final class AkuminaLib {

    private static AkuminaLib akuminaLib;

    private static Logger logger = Logger.getLogger(AkuminaLib.class.getName());

    private AkuminaLib() {
        logger.info("Loading Akumina Lib");
    }

    private RequestQueue queue = null;
    public static AkuminaLib getInstance() {
        if (akuminaLib == null) {
            akuminaLib = new AkuminaLib();
        }
        return akuminaLib;
    }

    public void authenticateWithMSAL(Activity activity, AuthFile authFile, ClientDetails clientDetails,
                                     AuthenticationHandler authenticationHandler, ApplicationListener applicationListener)
            throws Exception {
        loadVolley(activity);
        MSALUtils.getInstance().acquireToken(activity, authenticationHandler,false, authFile,clientDetails, applicationListener);
    }

    public void authenticateWithMSALAndMAM(Activity activity,
                                           AuthFile authFile,
                                           ClientDetails clientDetails,
                                           AuthenticationHandler authenticationHandler,  ApplicationListener applicationListener) throws Exception {
        loadVolley(activity);
        MSALUtils.getInstance().createMAMEnrollmentManager(activity);
        MSALUtils.getInstance().acquireToken(activity,authenticationHandler, true, authFile,clientDetails, applicationListener);
    }
    public  void signOut() throws Exception {
        MSALUtils.getInstance().signOutAccount();
    }
    public String getToken(TokenType tokenType){
        return MSALUtils.getInstance().getToken(tokenType);
    }

    public void addSharePointAuthCallback(SharePointAuthCallback sharePointAuthCallback) {
        MSALUtils.getInstance().setSharePointAuthCallback(sharePointAuthCallback);
    }

    public void addAkuminaTokenCallback(AkuminaTokenCallback akuminaTokenCallback) {
       MSALUtils.getInstance().setAkuminaTokenCallback(akuminaTokenCallback);
    }

    public void addLoggingHandler(LoggingHandler loggingHandler) {
        MSALUtils.getInstance().setLoggingHandler(loggingHandler);
    }

    private void loadVolley(Context context) {
        this.queue =  Volley.newRequestQueue(context.getApplicationContext());
    }

    public void callAkuminaApi(int method, String url, org.json.JSONObject payload, String token, ResponseListener responseListener,
                               ErrorListener errorListener) throws Exception {
        JsonObjectRequest request = new JsonObjectRequest(method,url,payload,responseListener, errorListener);

        request.getHeaders().put("Authorization", "Bearer " + token);

        request.getHeaders().put("Content-Type","application/json");

        this.queue.add(request);
    }
}
