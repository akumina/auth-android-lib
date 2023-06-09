package com.akumina.android.auth.akuminalib.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Map;

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

    public   static String toString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String toString(Map<String,String> map) {

        StringBuilder stringBuilder = new StringBuilder();

        map.forEach((s, s2) -> {
            stringBuilder.append(s);
            stringBuilder.append("=");
            stringBuilder.append(s2);
        });
        return stringBuilder.toString();
    }
}
