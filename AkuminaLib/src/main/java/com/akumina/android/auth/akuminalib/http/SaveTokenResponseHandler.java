package com.akumina.android.auth.akuminalib.http;

import android.content.Context;
import android.util.Log;

import com.akumina.android.auth.akuminalib.data.AppSettings;
import com.akumina.android.auth.akuminalib.listener.AkuminaTokenCallback;
import com.android.volley.VolleyError;

public class SaveTokenResponseHandler implements ResponseHandler<String> {

    private AkuminaTokenCallback child;

    @Override
    public void handleResponse(Context context, String token) {
        AppSettings.saveToken(token, context);
        if (child !=null) {
            child.onSuccess (token);
        }
    }
    @Override
    public void handleError(VolleyError e) {
        Log.e("VolleyError","Error while ", e);
        if (child !=null) {
            child.onError(e);
        }
    }

    public void setChild(AkuminaTokenCallback child) {
        this.child = child;
    }
}
