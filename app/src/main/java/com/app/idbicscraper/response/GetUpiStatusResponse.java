package com.app.idbicscraper.response;

import com.google.gson.annotations.SerializedName;

public class GetUpiStatusResponse {

    @SerializedName("Result")
    private String result;

    @SerializedName("ErrorMessage")
    private String errorMessage;

    @SerializedName("ErrorCode")
    private String errorCode;

    @SerializedName("Id")
    private String id;

    public String getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getId() {
        return id;
    }
}
