package com.akumina.android.auth.akuminalib.listener;

public interface AkuminaTokenCallback {

    public void onSuccess(String token);

    public void onError(Exception e);
}
