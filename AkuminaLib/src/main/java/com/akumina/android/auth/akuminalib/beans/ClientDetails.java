package com.akumina.android.auth.akuminalib.beans;

public class ClientDetails {

    private  String authority,clientId,redirectUri,sharePointScope,appManagerURL,tenantId;
    private  String[] scopes;

    public  ClientDetails() {

    }
    public ClientDetails(String authority, String clientId, String redirectUri, String sharePointScope, String appManagerURL, String tenantId, String[] scopes) {
        this.authority = authority;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.sharePointScope = sharePointScope;
        this.appManagerURL = appManagerURL;
        this.tenantId = tenantId;
        this.scopes = scopes;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getSharePointScope() {
        return sharePointScope;
    }

    public void setSharePointScope(String sharePointScope) {
        this.sharePointScope = sharePointScope;
    }

    public String getAppManagerURL() {
        return appManagerURL;
    }

    public void setAppManagerURL(String appManagerURL) {
        this.appManagerURL = appManagerURL;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

}
