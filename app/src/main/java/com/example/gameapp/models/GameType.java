package com.example.gameapp.models;

import com.google.gson.annotations.SerializedName;

public class GameType {

    @SerializedName("name")
    private String name;
    @SerializedName("api_type")
    private String apiType;
    @SerializedName("code")
    private String code;
    @SerializedName("image")
    private String image;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
    public String getCode() { return code;}public String getApiType() {
        return apiType;
    }
}
