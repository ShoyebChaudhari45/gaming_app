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
import com.example.gameapp.models.request.UpdateProfileRequest;
import com.example.gameapp.models.response.GenericResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "PROFILE_ACTIVITY";

    // Views
    private TextInputEditText etName, etEmail, etMobile;
    private TextView tvUserType;
    private View progressContainer;
    private ImageButton btnBack;
    private MaterialButton btnEditSubmit, btnLogout;

    // Edit mode state
    private boolean isEditMode = false;
    private long lastBackPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setListeners();
        fetchUserDetails();
    }

    // ================= INITIALIZATION =================

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);

        tvUserType = findViewById(R.id.tvUserType);
        progressContainer = findViewById(R.id.progressContainer);
        btnBack = findViewById(R.id.btnBack);

        btnEditSubmit = findViewById(R.id.btnEditSubmit);
        btnLogout = findViewById(R.id.btnLogout);

        // Initially disabled
        etName.setEnabled(false);
        etEmail.setEnabled(false);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> goToHomePage());
        btnEditSubmit.setOnClickListener(v -> handleEditSubmit());
        btnLogout.setOnClickListener(v -> logout());
    }

    // ================= NAVIGATION =================

    private void goToHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // ================= FETCH PROFILE =================

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
                        Log.d(TAG, "Fetch Profile HTTP: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {

                            UserDetailsResponse.User user = response.body().data;

                            if (user == null) {
                                toast("User data not found");
                                return;
                            }

                            etName.setText(nullSafe(user.name));
                            etEmail.setText(nullSafe(user.email));
                            etMobile.setText(nullSafe(user.mobileNo));
                            tvUserType.setText(capitalize(user.userType));
                            return;
                        }

                        toast("Failed to load profile");
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        showLoader(false);
                        Log.e(TAG, "Fetch Profile Error", t);
                        toast("Network error");
                    }
                });
    }

    // ================= EDIT/SUBMIT HANDLER =================

    private void handleEditSubmit() {
        if (!isEditMode) {
            // Enable edit mode
            enableEditMode();
        } else {
            // Submit the profile update
            submitProfileUpdate();
        }
    }

    private void enableEditMode() {
        isEditMode = true;
        etName.setEnabled(true);
        etEmail.setEnabled(true);

        // Update button UI
        btnEditSubmit.setText("Submit Request");
        btnEditSubmit.setIcon(getDrawable(R.drawable.ic_send));

        toast("Edit your profile");
    }

    private void submitProfileUpdate() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name required");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            return;
        }

        showLoader(true);

        String token = "Bearer " + SessionManager.getToken(this);
        UpdateProfileRequest request = new UpdateProfileRequest(name, email);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.updateProfile(token, "application/json", request)
                .enqueue(new Callback<GenericResponse>() {

                    @Override
                    public void onResponse(Call<GenericResponse> call,
                                           Response<GenericResponse> response) {

                        showLoader(false);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().status) {

                            toast("Profile updated successfully");

                            // Reset to view mode
                            disableEditMode();
                            return;
                        }

                        toast(response.body() != null
                                ? response.body().message
                                : "Update failed");
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        showLoader(false);
                        toast("Network error");
                    }
                });
    }

    private void disableEditMode() {
        isEditMode = false;
        etName.setEnabled(false);
        etEmail.setEnabled(false);

        // Update button UI back to edit mode
        btnEditSubmit.setText("Edit Profile");
        btnEditSubmit.setIcon(getDrawable(R.drawable.ic_edit));
    }

    // ================= HELPERS =================

    private String nullSafe(String value) {
        return value == null ? "" : value;
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