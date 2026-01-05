package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.RegisterRequest;
import com.example.gameapp.models.response.RegisterResponse;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REGISTER_API";

    EditText edtName, edtMobile, edtEmail, edtPassword;
    RadioGroup rgUserType;
    MaterialButton btnSignup;
    TextView txtLogin;
    View progressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        rgUserType = findViewById(R.id.rgUserType);
        btnSignup = findViewById(R.id.btnSignup);
        txtLogin = findViewById(R.id.txtLogin);
        progressContainer = findViewById(R.id.progressContainer);

        btnSignup.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void registerUser() {

        String name = edtName.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // 🔹 CLIENT-SIDE VALIDATION
        if (name.isEmpty()) {
            toast("Enter your name");
            return;
        }

        if (mobile.length() != 10) {
            toast("Mobile number must be 10 digits");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            toast("Password must be at least 6 characters");
            return;
        }

        showLoader(true);

        RegisterRequest request = new RegisterRequest();
        request.name = name;
        request.mobile_no = mobile;
        request.email = email;
        request.password = password;
        request.status = "active";

        int selectedId = rgUserType.getCheckedRadioButtonId();
        request.user_type = (selectedId == R.id.rbEmployee) ? "employee" : "customer";

        Log.d(TAG, "Request -> " + email + " | " + mobile);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.register(request).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                showLoader(false);
                Log.d(TAG, "HTTP Code: " + response.code());

                // ✅ SUCCESS = HTTP 200
                if (response.isSuccessful()) {

                    String msg = "Registration successful";
                    if (response.body() != null && response.body().message != null) {
                        msg = response.body().message;
                    }

                    toast(msg);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                // ❌ VALIDATION ERROR
                if (response.code() == 422 && response.errorBody() != null) {
                    try {
                        String error = response.errorBody().string();
                        Log.e(TAG, error);

                        if (error.contains("email")) {
                            toast("Email already exists");
                        } else if (error.contains("mobile")) {
                            toast("Mobile number already exists");
                        } else {
                            toast("Invalid input");
                        }
                    } catch (Exception e) {
                        toast("Validation error");
                    }
                    return;
                }

                toast("Registration failed");
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoader(false);
                Log.e(TAG, "API Failure", t);
                toast("Network error. Please try again");
            }
        });
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignup.setEnabled(!show);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
