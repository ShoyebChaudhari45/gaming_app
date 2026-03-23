package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StarlineRatesResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<StarlineRate> data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<StarlineRate> getData() {
        return data;
    }

    public static class StarlineRate {
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
}