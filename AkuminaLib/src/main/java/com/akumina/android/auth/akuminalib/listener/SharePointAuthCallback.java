package com.akumina.android.auth.akuminalib.listener;

import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

public interface SharePointAuthCallback {

    void onSuccess(final IAuthenticationResult authenticationResult);


    void onError(final MsalException exception);
}
