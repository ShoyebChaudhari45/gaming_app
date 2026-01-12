package com.example.gameapp.api;

import com.example.gameapp.models.request.ChangePasswordRequest;
import com.example.gameapp.models.request.DepositRequest;
import com.example.gameapp.models.request.ForgotPasswordRequest;
import com.example.gameapp.models.request.LoginRequest;
import com.example.gameapp.models.request.RegisterRequest;
import com.example.gameapp.models.request.ResendOtpRequest;
import com.example.gameapp.models.request.ResetPasswordRequest;
import com.example.gameapp.models.request.UpdateProfileRequest;
import com.example.gameapp.models.request.WithdrawRequest;
import com.example.gameapp.models.response.ChangePasswordResponse;
import com.example.gameapp.models.response.CommonResponse;
import com.example.gameapp.models.response.DepositResponse;
import com.example.gameapp.models.response.GamesResponse;
import com.example.gameapp.models.response.GenericResponse;
import com.example.gameapp.models.response.LoginResponse;
import com.example.gameapp.models.response.PriceResponse;
import com.example.gameapp.models.response.RegisterResponse;
import com.example.gameapp.models.response.TapsResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.models.response.WalletStatementResponse;
import com.example.gameapp.models.response.WithdrawResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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


    @GET("user/details")
    Call<UserDetailsResponse> getUserDetails(
            @Header("Authorization") String token,
            @Header("Accept") String accept
    );

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("user/forgot-password")
    Call<CommonResponse> forgotPassword(@Body ForgotPasswordRequest request);


    @POST("user/reset-password")
    Call<CommonResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("user/resend-otp")
    Call<CommonResponse> resendOtp(@Body ResendOtpRequest request);

    @GET("prices")
    Call<PriceResponse> getPrices(
            @Header("Authorization") String token,
            @Header("Accept") String accept
    );


    @GET("games")
    Call<GamesResponse> getGames(@Header("Authorization") String authorization);


    @GET("taps")
    Call<TapsResponse> getTaps(@Header("Authorization") String authorization);

    @POST("profile")
    Call<GenericResponse> updateProfile(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Body UpdateProfileRequest request
    );

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("wallet/deposit")
    Call<DepositResponse> depositAmount(
            @Header("Authorization") String token,
            @Body DepositRequest request
    );
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("wallet/withdraw")
    Call<WithdrawResponse> withdrawAmount(
            @Header("Authorization") String token,
            @Body WithdrawRequest request
    );

    @GET("wallet/statement")
    Call<WalletStatementResponse> getWalletStatement(
            @Header("Authorization") String token
    );



}
//46|Q3mgxI7zB62rDYzmpZrxmlp8Achw43lyDL0TEpPda`c7a5f73