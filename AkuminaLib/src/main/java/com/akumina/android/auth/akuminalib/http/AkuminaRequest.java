package com.akumina.android.auth.akuminalib.http;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

public class AkuminaRequest extends JsonObjectRequest {

    private  final Map<String,String> query;
    private final Map<String,String> headers;
    public AkuminaRequest(int method, Map<String,String> query, Map<String,String> headers, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.query = query;
        this.headers = headers;
    }

    @Nullable
    @Override
    protected Map<String, String> getParams()  {
        return query;
    }

    @Override
    public Map<String, String> getHeaders()  {
        return headers;
    }
}
