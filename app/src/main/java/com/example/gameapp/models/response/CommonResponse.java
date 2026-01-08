package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class CommonResponse {

    // Laravel may send "status" or "success"
    @SerializedName(value = "success", alternate = {"status"})
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
