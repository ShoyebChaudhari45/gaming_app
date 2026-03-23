package com.example.gameapp.models.response;

import com.example.gameapp.models.GameType;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GamesResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<GameType> data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<GameType> getData() {
        return data;
    }
}
