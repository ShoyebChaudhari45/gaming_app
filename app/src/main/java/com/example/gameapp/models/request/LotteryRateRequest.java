package com.example.gameapp.models.request;

import com.google.gson.annotations.SerializedName;

public class LotteryRateRequest {

    @SerializedName("time_id")
    private final int timeId;

    @SerializedName("type")
    private final String type;

    @SerializedName("digit")
    private final String digit;

    @SerializedName("price")
    private final int price;

    public LotteryRateRequest(int timeId, String type, String digit, int price) {
        this.timeId = timeId;
        this.type = type;
        this.digit = digit;
        this.price = price;
    }
}
