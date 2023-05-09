package com.akumina.android.auth.akuminalib.http;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AkuminaAPIClient {

    private  int method;
    private String endPoint;
    private Map<String,String> query;
    private JSONObject body;

    private String authToken;
    private Response.Listener<AkuminaResponse> listener;
    private Response.ErrorListener errorListener;

    private Map<String,String> headers ;

    private static final String PROTOCOL_CHARSET = "utf-8";

    public AkuminaAPIClient(int method,
                            String endPoint,
                            String authToken,
                            Map<String,String> query,
                            Map<String,String> headers,
                            JSONObject body,
                            Response.Listener<AkuminaResponse> listener,
                            @Nullable Response.ErrorListener errorListener) {
        this.endPoint = endPoint;
        this.body = body;
        this.errorListener = errorListener;
        this.listener = listener;
        this.method = method;
        this.query = query;
        this.authToken = authToken;
        this.headers = headers;
        // findTypeArguments(getClass());
    }

    public void execute(Activity activity) {
        RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
        Uri uri = Uri.parse(endPoint);
        Uri.Builder builder = uri.buildUpon();
        query.forEach((key,value) -> {
            builder.appendQueryParameter(key,value);
        });

        String url = builder.toString();
        Log.i("Akumina Client", "Calling URL: " + url);

        Request<AkuminaResponse> request = new Request<AkuminaResponse>(method,url, errorListener ) {
            @Override
            protected Response<AkuminaResponse> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString =
                            new String(
                                    response.data,
                                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    AkuminaResponse t = gson.fromJson(jsonString, AkuminaResponse.class);
                    return Response.success(
                            t, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(AkuminaResponse response) {
                listener.onResponse(response);
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String > map =  new HashMap<>();
                map.put("Authorization", "Bearer " + authToken);
                map.put("Content-Type","application/json");
                if(headers !=null ) {
                    map.putAll(headers);
                }
                return map;
            }

            public byte[] getBody() throws AuthFailureError {
                try {
                    return body == null ? null : body.toString().getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf(
                            "Unsupported Encoding while trying to get the bytes of %s using %s",
                            body, PROTOCOL_CHARSET);
                    return null;
                }
            }
        };

        queue.add(request);
    }

}
