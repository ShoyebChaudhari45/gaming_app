package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PriceResponse {

    @SerializedName("status_code")
    public int statusCode;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<String> data;
}
