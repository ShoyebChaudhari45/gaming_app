package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

    EditText edtMobile, edtPassword;
    View progressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ ONE-TIME AUTO LOGIN
        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView txtSignup = findViewById(R.id.txtSignup);
        progressContainer = findViewById(R.id.progressContainer);

        btnLogin.setOnClickListener(v -> loginUser());

        txtSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {

        String mobile = edtMobile.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // 🔐 VALIDATIONS
        if (mobile.length() != 10) {
            toast("Enter valid 10 digit mobile number");
            return;
        }

        if (password.isEmpty()) {
            toast("Enter password");
            return;
        }

        showLoader(true);

        LoginRequest request = new LoginRequest();
        request.mobile_no = mobile;
        request.password = password;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                showLoader(false);
                Log.d(TAG, "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getToken();

                    if (token != null && !token.isEmpty()) {
                        // ✅ LOGIN SUCCESS
                        SessionManager.saveLogin(LoginActivity.this, token);
                        showSuccessDialog(response.body().getMessage());
                    } else {
                        // ❌ INVALID CREDENTIALS
                        toast("Invalid mobile number or password");
                    }

                } else {
                    toast("Server error. Please try again");
                }
            }




            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoader(false);
                Log.e(TAG, "Login failed", t);
                toast("Network error. Try again");
            }
        });
    }

    // ✅ SUCCESS DIALOG AFTER LOGIN
    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Successful")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Continue", (dialog, which) -> {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                })
                .show();
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
