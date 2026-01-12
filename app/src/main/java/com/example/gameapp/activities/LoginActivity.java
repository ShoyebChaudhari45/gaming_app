package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_API";

    private EditText edtMobile, edtPassword;
    private View progressContainer;
    private long lastBackPressedTime = 0;


    // 🔐 Password rule:
    // 1 uppercase, 1 lowercase, 1 number, 1 special char, min 8 chars
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ AUTO LOGIN
        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        progressContainer = findViewById(R.id.progressContainer);

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView txtSignup = findViewById(R.id.txtSignup);
        TextView txtForgot = findViewById(R.id.txtForgot);

        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        txtSignup.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        btnLogin.setOnClickListener(v -> loginUser());
    }

    // ================= LOGIN =================
    private void loginUser() {

        String mobile = edtMobile.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // 📱 Mobile validation
        if (mobile.isEmpty()) {
            toast("Please enter mobile number");
            return;
        }

        if (!mobile.matches("^[6-9]\\d{9}$")) {
            toast("Enter a valid 10-digit mobile number");
            return;
        }

        // 🔐 Password validation
        if (password.isEmpty()) {
            toast("Please enter password");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            toast("Password must contain:\n" +
                    "• 1 uppercase letter\n" +
                    "• 1 lowercase letter\n" +
                    "• 1 number\n" +
                    "• 1 special character\n" +
                    "• Minimum 6 characters");
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

                    if (token != null && !token.isEmpty()) {
                        SessionManager.saveLogin(LoginActivity.this, token);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        showLoader(false);
                        toast("Invalid mobile number or password");
                    }

                } else {
                    showLoader(false);
                    toast("Server error. Please try again");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoader(false);
                Log.e(TAG, "Login failed", t);
                toast("Network error. Check your internet connection");
            }
        });
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressedTime < 2000) {
            finish();
        } else {
            lastBackPressedTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
