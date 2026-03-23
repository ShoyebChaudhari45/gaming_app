package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {

    @SerializedName("status")
    public boolean status;

    @SerializedName("message")
    public String message;
}
