package com.example.gameapp.api;

import com.example.gameapp.models.request.ChangePasswordRequest;
import com.example.gameapp.models.request.LoginRequest;
import com.example.gameapp.models.request.RegisterRequest;
import com.example.gameapp.models.response.ChangePasswordResponse;
import com.example.gameapp.models.response.LoginResponse;
import com.example.gameapp.models.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("user")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("user/change-password")
    Call<ChangePasswordResponse> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request
    );

}
