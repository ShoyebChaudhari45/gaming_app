package com.example.gameapp.models.response;

import com.example.gameapp.models.response.BidItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BidHistoryResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<BidItem> data;

    // Constructors
    public BidHistoryResponse() {}

    public BidHistoryResponse(int statusCode, String message, List<BidItem> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    // Getters
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<BidItem> getData() {
        return data;
    }

    // Setters
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<BidItem> data) {
        this.data = data;
    }
}