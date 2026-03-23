package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.ForgotPasswordRequest;
import com.example.gameapp.models.response.CommonResponse;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "FORGOT_PASSWORD";

    private EditText edtEmail;
    private MaterialButton btnSendOtp;
    private View progressContainer;
    private final long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        progressContainer = findViewById(R.id.progressContainer);

        btnSendOtp.setOnClickListener(v -> sendOtp());

        findViewById(R.id.txtBackToLogin).setOnClickListener(v -> finish());
        ImageButton btnBack= findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }

    private void sendOtp() {

        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            toast("Enter your email address");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Enter a valid email address");
            return;
        }

        showLoader(true);

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.email = email;

        Log.d(TAG, "Sending OTP to: " + email);

        ApiClient.getClient()
                .create(ApiService.class)
                .forgotPassword(request)
                .enqueue(new Callback<CommonResponse>() {

                    @Override
                    public void onResponse(Call<CommonResponse> call,
                                           Response<CommonResponse> response) {

                        showLoader(false);

                        Log.d(TAG, "HTTP CODE: " + response.code());

                        if (!response.isSuccessful()) {
                            toast("Server error (" + response.code() + ")");
                            return;
                        }

                        CommonResponse res = response.body();

                        if (res != null) {
                            Log.d(TAG, "API MESSAGE: " + res.getMessage());

                            toast(res.getMessage());

                            // ✅ DIRECT REDIRECT ON HTTP 200
                            Intent intent = new Intent(
                                    ForgotPasswordActivity.this,
                                    ResetPasswordActivity.class
                            );
                            intent.putExtra("email", email);
                            startActivity(intent);

                        } else {
                            toast("Unexpected server response");
                        }
                    }


                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        showLoader(false);
                        Log.e(TAG, "Network Error", t);
                        toast("Network error. Please try again");
                    }
                });
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSendOtp.setEnabled(!show);
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
