package com.example.gameapp.activities;

import android.content.Intent;
import android.net.Uri;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_API";

    private EditText edtMobile, edtPassword;
    private View progressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto login
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
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
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