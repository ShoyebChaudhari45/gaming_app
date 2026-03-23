package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StarlineTimesResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<StarlineTime> data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<StarlineTime> getData() {
        return data;
    }

    public static class StarlineTime {
        @SerializedName("id")
        private int id;

        @SerializedName("time")
        private String time;

        @SerializedName("result_code")
        private String resultCode;

        @SerializedName("status")
        private String status;

        @SerializedName("end_time")
        private String endTime;

        public int getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public String getResultCode() {
            return resultCode;
        }

        public String getStatus() {
            return status;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }
    }
}