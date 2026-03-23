package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class GameRateItem {

    @SerializedName("id")
    private int id;

    @SerializedName("game")
    private String game;

    @SerializedName("digit")
    private String digit;

    @SerializedName("price")
    private String price;

    public int getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getDigit() {
        return digit;
    }

    public String getPrice() {
        return price;
    }
}