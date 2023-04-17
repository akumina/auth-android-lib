package com.akumina.android.auth.akuminalib.http;

import android.content.Context;

import com.android.volley.VolleyError;

public interface ResponseHandler<T> {

    public void handleResponse(Context context, T t);
    void handleError(VolleyError e);
}
