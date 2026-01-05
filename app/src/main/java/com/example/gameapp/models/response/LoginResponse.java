package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    // ✅ TOKEN ACCESS (IMPORTANT)
    public String getToken() {
        return data != null ? data.token : null;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    // ================= DATA =================
    public static class Data {

        @SerializedName("token")
        private String token;

        @SerializedName("id")
        private int id;

        @SerializedName("panel_user_id")
        private int panelUserId;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("mobile_no")
        private String mobileNo;

        @SerializedName("status")
        private String status;

        @SerializedName("user_type")
        private String userType;

        @Override
        public String toString() {
            return "Data{" +
                    "token='" + token + '\'' +
                    ", id=" + id +
                    ", panelUserId=" + panelUserId +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", mobileNo='" + mobileNo + '\'' +
                    ", status='" + status + '\'' +
                    ", userType='" + userType + '\'' +
                    '}';
        }
    }
}
