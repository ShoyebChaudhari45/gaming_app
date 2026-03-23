package com.example.gameapp.models.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("mobile_no")
    public String mobile_no;

    @SerializedName("password")
    public String password;
}
