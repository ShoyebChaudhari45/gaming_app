package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TapsResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<GameData> data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<GameData> getData() {
        return data;
    }

    // ================= GAME =================
    public static class GameData {

        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("times")
        private List<Tap> times;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<Tap> getTimes() {
            return times;
        }
    }

    // ================= TAP =================
    public static class Tap {

        @SerializedName("type")
        private String type;

        @SerializedName("id")
        private int id;

        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        @SerializedName("status")
        private String status;

        // ⭐ NEW: Result field
        @SerializedName("result")
        private String result;

        // Runtime fields
        private String gameName;

        public int getId() {
            return id;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getStatus() {
            return status;
        }

        // ⭐ NEW: Result getter
        public String getResult() {
            return result;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}