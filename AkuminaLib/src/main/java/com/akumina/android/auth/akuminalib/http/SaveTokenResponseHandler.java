package com.akumina.android.auth.akuminalib.http;

import android.content.Context;
import android.util.Log;

import com.akumina.android.auth.akuminalib.data.AppAccount;
import com.akumina.android.auth.akuminalib.data.AppSettings;
import com.android.volley.VolleyError;

public class SaveTokenResponseHandler implements ResponseHandler<String> {

    private  ResponseHandler<String> child;

    @Override
    public void handleResponse(Context context, String token) {
        AppSettings.saveToken(token, context);
        if (child !=null) {
            child.handleResponse(context,token);
        }
    }
    @Override
    public void handleError(VolleyError e) {
        Log.e("VolleyError","Error while ", e);
        if (child !=null) {
            child.handleError(e);
        }
    }

    public void setChild(ResponseHandler<String> child) {
        this.child = child;
    }
}
