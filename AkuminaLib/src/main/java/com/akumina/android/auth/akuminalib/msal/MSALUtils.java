package com.akumina.android.auth.akuminalib.msal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.akumina.android.auth.akuminalib.beans.ClientDetails;
import com.akumina.android.auth.akuminalib.data.AppAccount;
import com.akumina.android.auth.akuminalib.data.AppSettings;
import com.akumina.android.auth.akuminalib.http.SaveTokenResponseHandler;
import com.akumina.android.auth.akuminalib.impl.AuthenticationHandler;
import com.akumina.android.auth.akuminalib.listener.AkuminaTokenCallback;
import com.akumina.android.auth.akuminalib.listener.ApplicationListener;
import com.akumina.android.auth.akuminalib.listener.LoggingHandler;
import com.akumina.android.auth.akuminalib.listener.SharePointAuthCallback;
import com.akumina.android.auth.akuminalib.utils.Constants;
import com.akumina.android.auth.akuminalib.utils.HttpUtils;
import com.akumina.android.auth.akuminalib.utils.TokenType;
import com.akumina.android.auth.akuminalib.utils.Utils;
import com.akumina.android.auth.akuminalib.utils.ValidationUtils;
import com.google.gson.Gson;
import com.microsoft.identity.client.AcquireTokenParameters;
import com.microsoft.identity.client.AcquireTokenSilentParameters;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MSALUtils {
    private static final Logger LOGGER = Logger.getLogger(MSALUtils.class.getName());

    private IPublicClientApplication mMsalClientApplication;

    private ISingleAccountPublicClientApplication mSingleAccountApp;

    private Context appContext;

    private MAMEnrollmentManager mamEnrollmentManager;

    private static MSALUtils instance;

    private final List<Map> authData = new ArrayList<>();

    private ClientDetails clientDetails;

    private AppAccount mUserAccount;

    private SharePointAuthCallback sharePointAuthCallback;

    private AkuminaTokenCallback akuminaTokenCallback;

    private boolean mamEnrollment;

    private ApplicationListener applicationListener;

    private String graphToken;

    private String sharePointToken;

    private AuthFile configFile;

    private LoggingHandler loggingHandler;

    private MSALUtils() {
        mamEnrollmentManager = MAMComponents.get(MAMEnrollmentManager.class);
    }

    public static MSALUtils getInstance() {
        if (instance == null) {
            instance = new MSALUtils();
        }
        return instance;
    }

    public MAMEnrollmentManager createMAMEnrollmentManager(Activity activity) {
        if (ValidationUtils.isNull(appContext)) {
            this.setAppContext(activity.getApplicationContext());
        }
        MAMEnrollmentManager manager = MAMComponents.get(MAMEnrollmentManager.class);

        mamEnrollmentManager = manager;

        return mamEnrollmentManager;
    }

    private void acquireTokenSilent(String aadId, @NonNull final AuthenticationHandler handler)
            throws MsalException, InterruptedException {

        final IAccount account = getAccount(aadId);
        if (account == null) {
            LOGGER.severe("Failed to acquire token: no account found for " + aadId);
            handler.onError(
                    new MsalUiRequiredException(MsalUiRequiredException.NO_ACCOUNT_FOUND, "no account found for " + aadId));

            return;
        }

        AcquireTokenSilentParameters params = new AcquireTokenSilentParameters.Builder()
                .forAccount(account)
                .fromAuthority(account.getAuthority())
                .withScopes(Arrays.asList(clientDetails.getScopes()))
                .withCallback(new SilentAuthenticationCallback() {
                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        handleAuthSuccess(authenticationResult);
                        handler.onSuccess(authenticationResult);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        handler.onError(exception);
                    }
                })
                .build();

        mMsalClientApplication.acquireTokenSilentAsync(params);
    }

    private void initForToken( final AuthenticationHandler handler, Activity activity) {
        AppAccount appAccount = AppSettings.getAccount(appContext);

        if (appAccount == null) {
            AcquireTokenParameters params = new AcquireTokenParameters.Builder()
                    .withScopes(Arrays.asList(clientDetails.getScopes()))
                    .withCallback(new AuthenticationCallback() {
                        @Override
                        public void onCancel() {
                            handler.onCancel();
                        }

                        @Override
                        public void onSuccess(IAuthenticationResult authenticationResult) {
                            handleAuthSuccess(authenticationResult);
                            handler.onSuccess(authenticationResult);
                        }

                        @Override
                        public void onError(MsalException exception) {
                            handler.onError(exception);
                        }
                    })
                    .startAuthorizationFromActivity(activity)
                    .withLoginHint(clientDetails.getUserName())
                    .build();
            mMsalClientApplication.acquireToken(params);
        } else {
            try {
                acquireTokenSilent(appAccount.getAADID(), handler);
            } catch (MsalException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @WorkerThread
    public void acquireToken(Activity activity, @NonNull final AuthenticationHandler handler, boolean mamEnrollment,
                             AuthFile configFile, ClientDetails clientDetails, ApplicationListener applicationListener)
            throws MsalException, InterruptedException {
        this.mamEnrollment = mamEnrollment;
        this.configFile = configFile;
        this.clientDetails = clientDetails;
        setAppContext(activity.getApplicationContext());

        initializeMsalClientApplication(applicationListener,handler ,activity);


    }

    private void handleAuthSuccess(IAuthenticationResult result) {
        IAccount account = result.getAccount();
        final String upn = account.getUsername();
        final String aadId = account.getId();
        final String tenantId = account.getTenantId();
        final String authorityURL = account.getAuthority();
        String message = "Authentication succeeded for user " + upn + " token =" + result.getAccessToken();
        updateLog(message, false);
        this.graphToken = result.getAccessToken();
        Map<String, String> graphParams = new Hashtable<>();
        graphParams.put("resource", clientDetails.getScopes()[0].replace(".default", ""));
        graphParams.put("id_token", result.getAccount().getIdToken());
        graphParams.put("access_token", result.getAccessToken());
        graphParams.put("expires_on", String.valueOf(result.getExpiresOn().getTime() / 1000L));
        String mScope = Utils.getFormattedScope(result.getScope(), clientDetails.getScopes()[0].replace("/.default", ""));
        graphParams.put("scope", mScope);
        authData.add(graphParams);

        // Save the user account in the settings, since the user is now "signed in".
        mUserAccount = new AppAccount(upn, aadId, tenantId, authorityURL);
        AppSettings.saveAccount(getAppContext(), mUserAccount);
        AppSettings.saveAccessToken(result.getAccessToken(), getAppContext());

        if (mamEnrollment) {
            // Register the account for MAM.
            getMamEnrollmentManager().registerAccountForMAM(upn, aadId, tenantId, authorityURL);
        }
        getSharePointAccessTokenAsync();
    }

    private void handleSharePointAuthSuccess(IAuthenticationResult authenticationResult) {
        Map<String, String> spParams = new Hashtable<>();
        spParams.put("resource", clientDetails.getSharePointScope());
        spParams.put("id_token", authenticationResult.getAccount().getIdToken());
        spParams.put("access_token", authenticationResult.getAccessToken());
        spParams.put("expires_on", String.valueOf(authenticationResult.getExpiresOn().getTime() / 1000L));
        String SHAREPOINT_SCOPE_REPLACE = clientDetails.getSharePointScope().replace(".default", "");
        String mScope = Utils.getFormattedScope(authenticationResult.getScope(), SHAREPOINT_SCOPE_REPLACE);
        this.sharePointToken = authenticationResult.getAccessToken();
        spParams.put("scope", mScope);
        authData.add(spParams);
        Gson gson = new Gson();
        String jsonCartList = gson.toJson(authData);
        LOGGER.log(Level.INFO, "DATA:" + jsonCartList);
        try {
            sendTokenToServer(jsonCartList);
        } catch (Exception e) {

        }
    }

    private void sendTokenToServer(String requestBody) {
        String token = AppSettings.getToken(getAppContext());

        Map<String, String> extraHeader = new HashMap<>();
        extraHeader.put("Content-Type", "application/json");
        extraHeader.put("x-akumina-auth-id", token);

        HttpUtils httpUtils = new HttpUtils(getAppContext());
        SaveTokenResponseHandler handler = new SaveTokenResponseHandler();
        handler.setChild(akuminaTokenCallback);
        httpUtils.post(clientDetails.getAppManagerURL(), requestBody, extraHeader, handler);

    }

    private void getSharePointAccessTokenAsync() {
        getSingleAccountApp().acquireTokenSilentAsync(
                clientDetails.getSharePointScope().split(" "),
                clientDetails.getAuthority(),
                new SilentAuthenticationCallback() {
                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        if (sharePointAuthCallback != null) {
                            sharePointAuthCallback.onSuccess(authenticationResult);
                        }
                        LOGGER.log(Level.FINE, "Successfully authenticated " +
                                authenticationResult.getAccessToken() + " ID TOKEN " + authenticationResult.getAccount().getIdToken());
                        handleSharePointAuthSuccess(authenticationResult);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        if (sharePointAuthCallback != null) {
                            sharePointAuthCallback.onError(exception);
                        }
                        LOGGER.log(Level.SEVERE, "Error While get Sharepoint token ", exception);
                    }
                });
    }

    private synchronized void initializeMsalClientApplication(ApplicationListener applicationListener, AuthenticationHandler handler, Activity activity)
            throws MsalException, InterruptedException {
        if (ValidationUtils.isNull(appContext)) {
            throw new IllegalStateException("Context is null");
        }
        this.applicationListener = applicationListener;

        if (mMsalClientApplication == null) {
            com.microsoft.identity.client.Logger msalLogger
                    = com.microsoft.identity.client.Logger.getInstance();
            msalLogger.setEnableLogcatLog(true);
            msalLogger.setLogLevel(com.microsoft.identity.client.Logger.LogLevel.VERBOSE);
            msalLogger.setEnablePII(true);

            if (!configFile.isFileBased()) {
                this.mMsalClientApplication = PublicClientApplication.create(appContext, configFile.getFileId());
                applicationListener.onCreated(this.mMsalClientApplication);
                mSingleAccountApp = PublicClientApplication.createSingleAccountPublicClientApplication(appContext, configFile.getFileId());
                LOGGER.log(Level.INFO, "mSingleAccountApp .. Init " + mSingleAccountApp);
                updateLog("mSingleAccountApp .. Init " + mSingleAccountApp, false);
                if(handler != null) {
                    initForToken(handler, activity);
                }
            } else {

                Thread t = new Thread(() -> {

                    PublicClientApplication.create(appContext, configFile.getFile(), new IPublicClientApplication.ApplicationCreatedListener() {
                        @Override
                        public void onCreated(IPublicClientApplication application) {
                            mMsalClientApplication = application;
                            applicationListener.onCreated(application);
                            try {
                                mSingleAccountApp = PublicClientApplication.createSingleAccountPublicClientApplication(appContext, configFile.getFile());
                                if(handler != null) {
                                    initForToken(handler, activity);
                                }
                            } catch (InterruptedException e) {
                                LOGGER.log(Level.SEVERE, "createSingleAccountPublicClientApplication InterruptedException", e);
                                onError(new MsalClientException(e.getLocalizedMessage()));
                                return;
                            } catch (MsalException e) {
                                LOGGER.log(Level.SEVERE, "createSingleAccountPublicClientApplication ", e);
                                onError(e);
                                return;
                            }
                        }

                        @Override
                        public void onError(MsalException exception) {
                            Log.e("PublicClientApplication", "onError: ", exception);
                            applicationListener.onError(exception);
                        }
                    });

                });
                t.start();
            }
        }
    }

    public ISingleAccountPublicClientApplication getSingleAccountApp() {
        return mSingleAccountApp;
    }

    public void setMsalClientApplication(IPublicClientApplication mMsalClientApplicationTemp) {
        mMsalClientApplication = mMsalClientApplicationTemp;
    }

    public void onError(MsalException exception) {
        LOGGER.log(Level.SEVERE, "Error", exception);
    }

    public MAMEnrollmentManager getMamEnrollmentManager() {
        return mamEnrollmentManager;
    }

//    public void setMamEnrollmentManager(MAMEnrollmentManager mamEnrollmentManager) {
//        this.mamEnrollmentManager = mamEnrollmentManager;
//    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
        Constants.packageName = this.appContext.getPackageName();
    }

    public void addAuthenticationCallback(MAMServiceAuthenticationCallback callback) {
        if (this.mamEnrollmentManager != null)
            this.mamEnrollmentManager.registerAuthenticationCallback(callback);
        else {
            updateLog("MAM Enrollment Manager  not initialized", true);
            throw new IllegalStateException("MAM Enrollment Manager  not initialized");
        }
    }

    private IAccount getAccount(String userId) throws InterruptedException, MsalException {
        IAccount account = null;

        if (mMsalClientApplication instanceof IMultipleAccountPublicClientApplication) {
            IMultipleAccountPublicClientApplication multiAccountPCA =
                    (IMultipleAccountPublicClientApplication) mMsalClientApplication;

            account = multiAccountPCA.getAccount(userId);
        } else {
            ISingleAccountPublicClientApplication singleAccountPCA =
                    (ISingleAccountPublicClientApplication) mMsalClientApplication;

            ICurrentAccountResult accountResult = singleAccountPCA.getCurrentAccount();
            if (accountResult != null) {
                account = accountResult.getCurrentAccount();
                // make sure this is the correct user
                if (account != null && !account.getId().equals(userId))
                    account = null;
            }
        }
        return account;
    }


    public void setSharePointAuthCallback(SharePointAuthCallback sharePointAuthCallback) {
        this.sharePointAuthCallback = sharePointAuthCallback;
    }

    public void setAkuminaTokenCallback(AkuminaTokenCallback akuminaTokenCallback) {
        this.akuminaTokenCallback = akuminaTokenCallback;
    }

    public void signOutAccount(Activity activity) throws Exception {

        if (appContext == null) {
            setAppContext(activity.getApplicationContext());
        }
//       if(ValidationUtils.isNull(clientDetails)) {
//           throw new IllegalAccessError("Client Details is null");
//        }
//        if(ValidationUtils.isNull(applicationListener)) {
//            throw new Exception("Application is null");
//        }

        initializeMsalClientApplication(new ApplicationListener() {
            @Override
            public void onCreated(IPublicClientApplication application) {

            }

            @Override
            public void onError(MsalException exception) {

            }
        }, null, activity);

        final IAccount account = getAccount(clientDetails.getUserName());

        if (account == null) {
            String error = "Failed to sign out account: No account found for " + clientDetails.getUserName();
            updateLog(error, true);
            LOGGER.warning(error);
            return;
        }

        if (mMsalClientApplication instanceof IMultipleAccountPublicClientApplication) {
            IMultipleAccountPublicClientApplication multiAccountPCA =
                    (IMultipleAccountPublicClientApplication) mMsalClientApplication;

            multiAccountPCA.removeAccount(account);
        } else {
            ISingleAccountPublicClientApplication singleAccountPCA =
                    (ISingleAccountPublicClientApplication) mMsalClientApplication;

            singleAccountPCA.signOut();
        }
    }

    public String getToken(TokenType tokenType) {

        if (tokenType.equals(TokenType.GRAPH)) {
            ValidationUtils.isEmpty(graphToken, "Graph Token is empty", loggingHandler);
            return graphToken;
        } else if (tokenType.equals(TokenType.AKUMINA)) {
            String token = AppSettings.getToken(appContext);
            ValidationUtils.isEmpty(token, "Akumina Token is empty", loggingHandler);
            return token;
        } else {
            ValidationUtils.isEmpty(sharePointToken, "Sharepoint Token is empty", loggingHandler);
            return sharePointToken;
        }
    }

    private void updateLog(String message, boolean error) {
        if (this.loggingHandler != null) {
            this.loggingHandler.handleMessage(message, error);
        }
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        this.loggingHandler = loggingHandler;
    }
}
