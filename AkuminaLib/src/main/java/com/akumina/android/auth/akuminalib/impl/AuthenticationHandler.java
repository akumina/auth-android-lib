package com.akumina.android.auth.akuminalib.impl;

import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

public interface AuthenticationHandler {

   public void onCancel();

   public void onSuccess(final IAuthenticationResult authenticationResult);

   public void onError(final MsalException exception);

}
