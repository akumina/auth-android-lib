package com.akumina.android.auth.akuminalib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.akumina.android.auth.akuminalib.http.ResponseHandler;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public final class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();
    private static final Logger LOGGER = Logger.getLogger(TAG);

    private final Context mContext;

    public HttpUtils(Context context)  {
        this.mContext = context;
    }
    public void post(String URL, String requestBody, Map<String, String> extraHeader, ResponseHandler handler) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            try
            {
                if(!TextUtils.isEmpty(response)) {
                    String data = new JSONObject(response).getString("Data");
                    if(!TextUtils.isEmpty(data)) {
                        handler.handleResponse(mContext,data);
                    }
                    LOGGER.info("json " + data);
                } else {
                    handler.handleError(new VolleyError("Empty Response"));
                }
            } catch (Exception exception) {
                LOGGER.info("Volley Exception " + exception.getMessage());
                handler.handleError(new VolleyError(exception.getMessage()));
            }

            Log.i("VOLLEY", response);
        }, error -> {
            LOGGER.info("VolleyError " + error.getMessage());
            handler.handleError(error);
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                if(!extraHeader.isEmpty()) {
                    return extraHeader;
                } else {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    return params;
                }
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    String responseString = new String(response.data);
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
                return null;
            }
        };
        requestQueue.add(stringRequest);
    }
}
