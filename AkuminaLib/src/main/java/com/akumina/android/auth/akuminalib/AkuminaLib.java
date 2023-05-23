package com.akumina.android.auth.akuminalib;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.WorkerThread;

import com.akumina.android.auth.akuminalib.beans.ClientDetails;
import com.akumina.android.auth.akuminalib.data.AppAccount;
import com.akumina.android.auth.akuminalib.data.AppSettings;
import com.akumina.android.auth.akuminalib.http.AkuminaAPIClient;
import com.akumina.android.auth.akuminalib.http.ResponseHandler;
import com.akumina.android.auth.akuminalib.impl.AuthenticationHandler;
import com.akumina.android.auth.akuminalib.listener.AkuminaTokenCallback;
import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.akumina.android.auth.akuminalib.listener.ErrorListener;
import com.akumina.android.auth.akuminalib.listener.LoggingHandler;
import com.akumina.android.auth.akuminalib.listener.ResponseListener;
import com.akumina.android.auth.akuminalib.listener.SharePointAuthCallback;
import com.akumina.android.auth.akuminalib.msal.AuthFile;
import com.akumina.android.auth.akuminalib.msal.MSALUtils;
import com.akumina.android.auth.akuminalib.utils.Constants;
import com.akumina.android.auth.akuminalib.utils.HttpUtils;
import com.akumina.android.auth.akuminalib.utils.TokenType;
import com.akumina.android.auth.akuminalib.utils.Utils;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public final class AkuminaLib {

    private static AkuminaLib akuminaLib;

    private static Logger logger = Logger.getLogger(AkuminaLib.class.getName());

    private ClientDetails clientDetails;

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
        this.clientDetails = clientDetails;
        MSALUtils.getInstance().acquireToken(activity, authenticationHandler,false, authFile,clientDetails, applicationListener);
    }

    public void authenticateWithMSALAndMAM(Activity activity,
                                           AuthFile authFile,
                                           ClientDetails clientDetails,
                                           AuthenticationHandler authenticationHandler,  ApplicationListener applicationListener) throws Exception {
        loadVolley(activity);
        this.clientDetails = clientDetails;
        MSALUtils.getInstance().createMAMEnrollmentManager(activity);
        MSALUtils.getInstance().acquireToken(activity,authenticationHandler, true, authFile,clientDetails, applicationListener);
    }
    public  void signOut(Activity activity,String userName) throws Exception {
        MSALUtils.getInstance().signOutAccount(activity, userName);
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

    public void callAkuminaApi(int method, String url, Map<String,String> query, Map<String,String> headers, org.json.JSONObject payload, String token, ResponseListener responseListener,
                                   ErrorListener errorListener, Activity parentActivity) throws Exception {
        AkuminaAPIClient request = new AkuminaAPIClient(method,url,token,query,headers,payload,responseListener, errorListener);
        request.execute(parentActivity);
        ;
    }

    public void saveFCMToken(String token, Activity mActivity, String appId, String versionName, ResponseHandler responseHandler) {
        Context mContext = mActivity.getApplicationContext();

        AppAccount mUserAccount = AppSettings.getAccount(mContext);
        String uuid =  Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String osInfo = "OS : android"
                + ", manufacturer : " + Build.MANUFACTURER
                + ", model : " + Build.MODEL
                + ", version : " + Build.VERSION.SDK_INT
                + ", versionRelease : " + Build.VERSION.RELEASE;

        Map<String, String> params = new Hashtable<>();
        params.put("tenant_id",  mUserAccount.getTenantID());
        params.put("upn", mUserAccount.getUPN());
        params.put("bundle_id", appId);
        params.put("bundle_version", versionName);
        params.put("id", uuid);
        params.put("device_token", token);
        params.put("os", osInfo);
        params.put("domain", Utils.getDomain(clientDetails.getAppManagerURL()));

        Gson gson = new Gson();
        String requestBody = gson.toJson(params);

        Map<String, String> extraHeader = new HashMap<>();
        extraHeader.put("Content-Type", "application/json");
        extraHeader.put("Authorization", "Bearer " + AppSettings.readAccessToken(mContext));

        HttpUtils httpUtils = new HttpUtils(mContext);
        httpUtils.post(Constants.POST_FCM_TOKEN, requestBody, extraHeader, responseHandler);
    }
}
