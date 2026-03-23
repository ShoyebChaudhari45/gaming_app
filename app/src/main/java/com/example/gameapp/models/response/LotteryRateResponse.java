package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class LotteryRateResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
