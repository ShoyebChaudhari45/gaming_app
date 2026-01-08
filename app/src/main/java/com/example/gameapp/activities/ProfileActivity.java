// ProfileActivity.java
package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "USER_DETAILS";

    TextInputEditText etName, etEmail, etMobile;
    TextView tvUserType, tvMemberSince;
    View progressContainer, viewHeader;
    ImageButton btnBack;
    MaterialCardView profileCard;

    MaterialButton btnEdit, btnSubmit, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        tvUserType = findViewById(R.id.tvUserType);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        progressContainer = findViewById(R.id.progressContainer);
        viewHeader = findViewById(R.id.viewHeader);
        btnBack = findViewById(R.id.btnBack);
        profileCard = findViewById(R.id.profileCard);

        btnEdit = findViewById(R.id.btnEdit);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);

        // Back button functionality
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Header click to go to homepage
        viewHeader.setOnClickListener(v -> goToHomePage());

        // Profile card click to go to homepage (optional)
        profileCard.setOnClickListener(v -> goToHomePage());

        btnEdit.setOnClickListener(v -> {
            etName.setEnabled(true);
            etEmail.setEnabled(true);
            btnSubmit.setVisibility(View.VISIBLE);
        });

        btnLogout.setOnClickListener(v -> logout());

        fetchUserDetails();
    }

    // ================= NAVIGATION =================

    private void goToHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Go back to previous activity or homepage
        super.onBackPressed();
    }

    // ================= API CALL =================
    private void fetchUserDetails() {

        showLoader(true);

        String token = "Bearer " + SessionManager.getToken(this);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getUserDetails(token, "application/json")
                .enqueue(new Callback<UserDetailsResponse>() {

                    @Override
                    public void onResponse(Call<UserDetailsResponse> call,
                                           Response<UserDetailsResponse> response) {

                        showLoader(false);
                        Log.d(TAG, "HTTP Code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {

                            UserDetailsResponse.User user = response.body().data;

                            if (user == null) {
                                toast("User data missing");
                                return;
                            }

                            etName.setText(nullSafe(user.name));
                            etEmail.setText(nullSafe(user.email));
                            etMobile.setText(nullSafe(user.mobileNo));
                            tvUserType.setText(capitalize(user.userType));
                            tvMemberSince.setText(formatDate(user.createdAt));

                            return;
                        }

                        toast("Failed to load profile");
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        showLoader(false);
                        Log.e(TAG, "API Error", t);
                        toast("Network error");
                    }
                });
    }

    // ================= SAFE HELPERS =================

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(String iso) {

        if (iso == null || iso.length() < 7) {
            return "N/A";
        }

        try {
            return iso.substring(0, 7).replace("-", " ");
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // ================= LOGOUT =================
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (d, w) -> {
                    SessionManager.logout(this);
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}