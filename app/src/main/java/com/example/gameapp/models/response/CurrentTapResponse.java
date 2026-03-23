package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class CurrentTapResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CurrentTapData data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public CurrentTapData getData() {
        return data;
    }

    public static class CurrentTapData {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("tap_time_id")
        private int tapTimeId;

        @SerializedName("type")
        private String type;

        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        @SerializedName("current_date")
        private String currentDate;

        @SerializedName("status")
        private String status;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getTapTimeId() {
            return tapTimeId;
        }

        public String getType() {
            return type;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getCurrentDate() {
            return currentDate;
        }

        public String getStatus() {
            return status;
        }
    }
}