package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.ChangePasswordRequest;
import com.example.gameapp.models.response.ChangePasswordResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "CHANGE_PASSWORD";

    EditText edtEmail, edtNewPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtEmail = findViewById(R.id.edtEmail);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        MaterialButton btnChange = findViewById(R.id.btnChangePassword);

        btnChange.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {

        String email = edtEmail.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        // ✅ VALIDATIONS
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Enter valid email");
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

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.email = email;
        request.new_password = newPass;
        request.new_password_confirmation = confirmPass;

        String token = "Bearer " + SessionManager.getToken(this);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.changePassword(token, request).enqueue(new Callback<ChangePasswordResponse>() {

            @Override
            public void onResponse(Call<ChangePasswordResponse> call,
                                   Response<ChangePasswordResponse> response) {

                Log.d(TAG, "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    showSuccessDialog(response.body().message);
                    return;
                }

                toast("Failed to update password");
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.e(TAG, "Error", t);
                toast("Network error");
            }
        });
    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Login Again", (d, w) -> {
                    SessionManager.logout(this);
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
