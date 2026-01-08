package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GameResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("data")
    public List<Game> data;

    public static class Game {

        @SerializedName("name")
        public String name;

        @SerializedName("result")
        public String result;

        @SerializedName("time")
        public String time;
    }
}
