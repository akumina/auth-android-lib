package com.akumina.android.auth.akuminalib.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;

import com.akumina.android.auth.akuminalib.utils.Constants;

public final  class AppSettings {

    private AppSettings() { }

    public static void saveAccount(@NonNull final Context appContext, @NonNull final AppAccount account) {
        final SharedPreferences prefs = getPrefs(appContext);
        account.saveToSettings(prefs);
    }

    public static AppAccount getAccount(@NonNull final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        return AppAccount.readFromSettings(prefs);
    }

    /**
     * Delete the saved account from the settings.
     *
     * @param appContext
     *         application Context.
     */
    public static void clearAccount(final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        AppAccount.clearFromSettings(prefs);
    }

    private static SharedPreferences getPrefs(final Context appContext) {
        return appContext.getSharedPreferences(Constants.packageName, Context.MODE_PRIVATE);
    }

    public  static void saveToken(String token, final Context appContext) {
        final SharedPreferences sharedPref = getPrefs(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("appToken", token);
        editor.apply();
    }

    public  static void saveOldToken(String token, final Context appContext) {
        final SharedPreferences sharedPref = getPrefs(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("appOldToken", token);
        editor.apply();
    }

    public static String getToken(final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        return prefs.getString("appToken","");
    }

    public static String getOldToken(final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        return prefs.getString("appOldToken","");
    }

    public static void clearNewAndOldToken(final Context appContext) {
        final SharedPreferences sharedPref = getPrefs(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("appToken", "");
        editor.putString("appOldToken", "");
        editor.apply();
    }

    public static void savePushNotificationToken(String token, final Context appContext) {
        final SharedPreferences sharedPref = getPrefs(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("pushToken", token);
        editor.apply();
    }

    public static String getPushNotificationToken(final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        return prefs.getString("pushToken", "");
    }

    public static void clearAllSavedObject(final Context appContext) {
        final SharedPreferences sharedPref = getPrefs(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("appToken", "");
        editor.putString("appOldToken", "");
        editor.putString("publicAppData", "");
        editor.putString("username", "");
        editor.apply();
    }

    public static void saveAccessToken(String graphToken, final Context appContext) {
        final SharedPreferences sharedPref = getPrefs(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("access_token", graphToken);
        editor.apply();
    }

    public static String readAccessToken(final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        return prefs.getString("access_token", "");
    }
}
