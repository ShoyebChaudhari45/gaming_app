package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class UserDetailsResponse {

    @SerializedName("status_code")
    public int statusCode;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public User data;

    public static class User {

        @SerializedName("id")
        public int id;

        @SerializedName("panel_user_id")
        public int panelUserId;

        @SerializedName("name")
        public String name;

        @SerializedName("email")
        public String email;

        @SerializedName("mobile_no")
        public String mobileNo;

        @SerializedName("status")
        public String status;

        @SerializedName("user_type")
        public String userType;

        @SerializedName("balance")
        public int balance;

        @SerializedName("qr_code")   // ⭐ NEW
        public String qrCode;

        @SerializedName("created_at")
        public String createdAt;

        @SerializedName("updated_at")
        public String updatedAt;
    }
}
