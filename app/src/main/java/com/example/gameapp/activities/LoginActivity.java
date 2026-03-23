package com.example.gameapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.LoginRequest;
import com.example.gameapp.models.response.LoginResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_API";

    private EditText edtMobile, edtPassword;
    private View progressContainer;

    // Retry mechanism
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;
    private static final long[] RETRY_DELAYS = {500, 1500, 3000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto login
        if (SessionManager.isLoggedIn(this)) {
            navigateToHome();
            return;
        }

        setContentView(R.layout.activity_login);

        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        progressContainer = findViewById(R.id.progressContainer);

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnPanelAccess = findViewById(R.id.btnPanelAccess);
        TextView txtSignup = findViewById(R.id.txtSignup);
        TextView txtForgot = findViewById(R.id.txtForgot);

        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        txtSignup.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        btnLogin.setOnClickListener(v -> loginUser());

        btnPanelAccess.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lottery.durwankurgroup.com/panel/login"));
            startActivity(browserIntent);
        });
    }

    // ================= LOGIN =================
    private void loginUser() {

        String mobile = edtMobile.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Mobile validation
        if (mobile.isEmpty()) {
            toast("Please enter mobile number");
            return;
        }

        if (!mobile.matches("^[6-9]\\d{9}$")) {
            toast("Enter a valid 10-digit mobile number");
            return;
        }

        // Password validation (ONLY empty check)
        if (password.isEmpty()) {
            toast("Please enter password");
            return;
        }

        showLoader(true);

        LoginRequest request = new LoginRequest();
        request.mobile_no = mobile;
        request.password = password;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                Log.d(TAG, "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getToken();

                    // If backend returns token → login success
                    if (token != null && !token.isEmpty()) {
                        SessionManager.saveLogin(LoginActivity.this, token);

                        // ⭐ Save basic user data from login response
                        LoginResponse.Data loginData = response.body().getData();
                        if (loginData != null) {
                            SessionManager.saveUserName(LoginActivity.this, loginData.getName());
                            SessionManager.saveUserMobile(LoginActivity.this, loginData.getMobileNo());
                            SessionManager.saveEmail(LoginActivity.this, loginData.getEmail());
                            SessionManager.saveUserType(LoginActivity.this, loginData.getUserType());
                        }

                        // Fetch full user details (balance, QR)
                        fetchUserDetailsAndNavigate(token);
                        return;
                    }
                }

                // ❌ ANY failure = wrong password or mobile
                showLoader(false);
                toast("Invalid mobile number or password");
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoader(false);
                Log.e(TAG, "Login failed", t);

                // ❌ Network fail = still show wrong pass for security
                toast("Invalid mobile number or password");
            }
        });
    }

    // Fetch user details with retry mechanism
    private void fetchUserDetailsAndNavigate(String token) {
        ApiClient.getClient()
                .create(ApiService.class)
                .getUserDetails(
                        "Bearer " + token,
                        "application/json"
                )
                .enqueue(new Callback<UserDetailsResponse>() {

                    @Override
                    public void onResponse(Call<UserDetailsResponse> call,
                                           Response<UserDetailsResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().data != null) {

                            retryCount = 0;
                            UserDetailsResponse.User user = response.body().data;

                            // Save full user details
                            SessionManager.saveUserType(LoginActivity.this, user.userType);
                            SessionManager.saveUserName(LoginActivity.this, user.name);
                            SessionManager.saveUserMobile(LoginActivity.this, user.mobileNo);
                            SessionManager.saveEmail(LoginActivity.this, user.email);
                            SessionManager.saveBalance(LoginActivity.this, user.balance);

                            // Save QR Code
                            String qr = user.qrCode;
                            if (qr != null && !qr.isEmpty()) {
                                if (!qr.startsWith("http")) {
                                    qr = "https://lottery.durwankurgroup.com/" + qr;
                                }
                                SessionManager.saveQrCode(LoginActivity.this, qr);
                            }

                            showLoader(false);
                            navigateToHome();

                        } else {
                            retryOrNavigate(token);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        Log.e(TAG, "User details failed: " + t.getMessage());
                        retryOrNavigate(token);
                    }
                });
    }

    private void retryOrNavigate(String token) {
        if (retryCount < MAX_RETRIES) {
            long delay = RETRY_DELAYS[retryCount];
            retryCount++;
            Log.w(TAG, "Retry #" + retryCount + " after " + delay + "ms");
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> fetchUserDetailsAndNavigate(token), delay
            );
        } else {
            retryCount = 0;
            showLoader(false);
            // Navigate anyway — basic data saved from login response
            navigateToHome();
        }
    }

    // ⭐ Navigate based on user type
    private void navigateToHome() {
        if (SessionManager.isEmployee(this)) {
            startActivity(new Intent(this, EmployeeHomeActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    // ================= HELPERS =================
    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}