package com.akumina.android.auth.akuminalib.utils;

import android.util.Log;

import java.net.URI;

public final class Utils {

    public static String getFormattedScope(String[] scopes, String SCOPE_TO_REPLACE) {
        String formattedValue = "";
        if (null != scopes && scopes.length > 0) {
            formattedValue = String.join(" ", scopes).replace(SCOPE_TO_REPLACE, "");
        }
        return formattedValue;
    }

    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return  uri.getHost();
        }catch (Exception e) {
            Log.e(Utils.class.getName(), "getDomain: ",e );
            return  "";
        }

    }
}
