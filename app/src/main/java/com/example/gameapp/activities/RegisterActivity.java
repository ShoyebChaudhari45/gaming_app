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
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.text.InputType;

public class RegisterActivity extends AppCompatActivity {

    private final long lastBackPressedTime = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = "REGISTER_API";

    private EditText edtName, edtMobile, edtEmail, edtPassword, edtReferralCode;
    private RadioGroup rgUserType;
    private MaterialButton btnSignup;
    private TextView txtLogin;
    private View progressContainer;

    private TextInputLayout tilName, tilMobile, tilEmail, tilPassword, tilReferralCode;

    private static final int MIN_PASSWORD_LENGTH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtReferralCode = findViewById(R.id.edtReferralCode);
        rgUserType = findViewById(R.id.rgUserType);
        btnSignup = findViewById(R.id.btnSignup);
        txtLogin = findViewById(R.id.txtLogin);
        progressContainer = findViewById(R.id.progressContainer);

        tilName = (TextInputLayout) edtName.getParent().getParent();
        tilMobile = (TextInputLayout) edtMobile.getParent().getParent();
        tilEmail = (TextInputLayout) edtEmail.getParent().getParent();
        tilPassword = findViewById(R.id.tilPassword);
        tilReferralCode = (TextInputLayout) edtReferralCode.getParent().getParent();

        btnSignup.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {

        clearErrors();

        String name = edtName.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String referralCode = edtReferralCode.getText().toString().trim();

        if (name.isEmpty()) {
            tilName.setError("Please enter your name");
            edtName.requestFocus();
            return;
        }

        if (!mobile.matches("^[6-9]\\d{9}$")) {
            tilMobile.setError("Enter valid 10-digit mobile number");
            edtMobile.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            edtEmail.requestFocus();
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            edtPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            edtPassword.setSelection(edtPassword.length());

            tilPassword.setError("Password must be at least 4 characters");
            edtPassword.requestFocus();
            return;

        } else {
            edtPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
            edtPassword.setSelection(edtPassword.length());
            tilPassword.setError(null);
        }

        if (rgUserType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoader(true);

        RegisterRequest request = new RegisterRequest();
        request.name = name;
        request.mobile_no = mobile;
        request.email = email;
        request.password = password;
        request.status = "active";

        if (!referralCode.isEmpty()) {
            request.referral_code = referralCode;
        }

        int selectedId = rgUserType.getCheckedRadioButtonId();
        request.user_type = (selectedId == R.id.rbEmployee) ? "employee" : "customer";

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.register(request).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call,
                                   Response<RegisterResponse> response) {

                showLoader(false);
                Log.d(TAG, "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    toast("Registration successful");
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                // ==================================
                // 🔥 FIXED BACKEND VALIDATION CHECK
                // ==================================
                if (response.errorBody() != null) {
                    try {
                        String errorJson = response.errorBody().string().toLowerCase();
                        Log.d(TAG, "Error Response: " + errorJson);

                        if (errorJson.contains("email")) {
                            tilEmail.setError("Email already exists");
                            edtEmail.requestFocus();
                            return;
                        }

                        if (errorJson.contains("mobile") || errorJson.contains("phone")) {
                            tilMobile.setError("Mobile number already exists");
                            edtMobile.requestFocus();
                            return;
                        }

                        toast("Registration failed");

                    } catch (Exception e) {
                        e.printStackTrace();
                        toast("Registration failed");
                    }
                    return;
                }

                toast("Registration failed");
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoader(false);
                toast("Network error. Please try again");
            }
        });
    }

    private void clearErrors() {
        tilName.setError(null);
        tilMobile.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilReferralCode.setError(null);
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignup.setEnabled(!show);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
            return;
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() ->
                doubleBackToExitPressedOnce = false, 2000);
    }
}
