package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class DeleteAccountResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Object data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return statusCode == 200;
    }
}
