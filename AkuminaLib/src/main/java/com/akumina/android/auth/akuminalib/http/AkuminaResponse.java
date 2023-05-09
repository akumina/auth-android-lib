package com.akumina.android.auth.akuminalib.http;

import com.google.gson.annotations.SerializedName;

public class AkuminaResponse {

    @SerializedName(value = "RawData")
    private String rawData;

    @SerializedName(value = "Success")
    private Boolean success;

    @SerializedName(value = "Message")
    private String message;

    @SerializedName(value = "StatusCode")
    private int statusCode;

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
