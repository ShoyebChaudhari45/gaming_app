package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.ResetPasswordRequest;
import com.example.gameapp.models.request.ResendOtpRequest;
import com.example.gameapp.models.response.CommonResponse;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "RESET_PASSWORD";

    private EditText edtOtp, edtNewPassword, edtConfirmPassword;
    private MaterialButton btnResetPassword;
    private TextView txtResendOtp;
    private View progressContainer;
    private String userEmail;
    private final long lastBackPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        userEmail = getIntent().getStringExtra("email");

        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        txtResendOtp = findViewById(R.id.txtResendOtp);
        progressContainer = findViewById(R.id.progressContainer);

        btnResetPassword.setOnClickListener(v -> resetPassword());
        txtResendOtp.setOnClickListener(v -> resendOtp());
        ImageButton btnBack= findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }

    private void resetPassword() {

        String otp = edtOtp.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (otp.length() != 6) {
            toast("Enter valid 6 digit OTP");
            return;
        }

        if (newPass.length() < 6) {
            toast("Password must be at least 6 characters");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            toast("Passwords do not match");
            return;
        }

        showLoader(true);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.email = userEmail;
        request.otp = otp;
        request.new_password = newPass;
        request.new_password_confirmation = confirmPass;

        ApiClient.getClient()
                .create(ApiService.class)
                .resetPassword(request)
                .enqueue(new Callback<CommonResponse>() {

                    @Override
                    public void onResponse(Call<CommonResponse> call,
                                           Response<CommonResponse> response) {

                        showLoader(false);

                        Log.d(TAG, "HTTP CODE: " + response.code());

                        if (!response.isSuccessful()) {
                            toast("Failed to reset password (" + response.code() + ")");
                            return;
                        }

                        CommonResponse res = response.body();

                        if (res != null) {
                            toast(res.getMessage());
                        } else {
                            toast("Password updated successfully");
                        }

                        // ✅ ALWAYS REDIRECT ON HTTP 200
                        Intent intent = new Intent(
                                ResetPasswordActivity.this,
                                LoginActivity.class
                        );
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        showLoader(false);
                        Log.e(TAG, "Network Error", t);
                        toast("Network error. Please try again");
                    }
                });
    }

    private void resendOtp() {

        if (userEmail == null || userEmail.isEmpty()) {
            toast("Email not found. Please try again");
            finish();
            return;
        }

        showLoader(true);

        ResendOtpRequest request = new ResendOtpRequest();
        request.email = userEmail;

        ApiClient.getClient()
                .create(ApiService.class)
                .resendOtp(request)
                .enqueue(new Callback<CommonResponse>() {

                    @Override
                    public void onResponse(Call<CommonResponse> call,
                                           Response<CommonResponse> response) {

                        showLoader(false);

                        if (response.isSuccessful() && response.body() != null) {
                            toast(response.body().getMessage());
                        } else {
                            toast("Failed to resend OTP");
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        showLoader(false);
                        toast("Network error");
                    }
                });
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        btnResetPassword.setEnabled(!show);
        txtResendOtp.setEnabled(!show);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    // =====================================================
    // BACK PRESS (DOUBLE TAP EXIT)
    // =====================================================
    @Override
    public void onBackPressed() {
        // Go back to Home Activity
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
