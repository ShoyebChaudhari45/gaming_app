package com.example.gameapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;
    private ImageButton btnMenu;

    private long lastBackPressedTime = 0;

    private TextView txtEmployeeName, txtEmployeeMobile;
    private TextView txtEmployeeId, txtEmployeeEmail;

    private static final String TAG = "EmployeeHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Employee Only Access
        if (SessionManager.isCustomer(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_employee_home);

        initViews();
        setupDrawer();
        setupActionButtons();
        loadEmployeeDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SessionManager.isCustomer(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        updateUI();
    }

    private void initViews() {

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnMenu = findViewById(R.id.btnMenu);

        txtEmployeeName = findViewById(R.id.txtEmployeeName);
        txtEmployeeMobile = findViewById(R.id.txtEmployeeMobile);
        txtEmployeeId = findViewById(R.id.txtEmployeeId);
        txtEmployeeEmail = findViewById(R.id.txtEmployeeEmail);

        swipeRefresh.setOnRefreshListener(() -> {
            loadEmployeeDetails();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void setupDrawer() {

        btnMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        navigationView.setNavigationItemSelectedListener(item -> {

            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();

            if (id == R.id.nav_home) return true;

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));

            } else if (id == R.id.nav_add_funds) {
                toast("Employees cannot add funds");

            } else if (id == R.id.nav_withdraw) {
                toast("Employees cannot withdraw");

            } else if (id == R.id.nav_wallet) {
                toast("Wallet not available for employees");

            } else if (id == R.id.nav_bid_history) {
                toast("Bid history not available");

            } else if (id == R.id.nav_game_rates) {
                toast("Game rates not available");

            } else if (id == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));

            } else if (id == R.id.nav_change_password) {
                startActivity(new Intent(this, ChangePasswordActivity.class));

            } else if (id == R.id.nav_share) {
                shareApp();

            } else if (id == R.id.nav_panel_access) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://lottery.durwankurgroup.com/panel/login"));
                startActivity(i);

            } else if (id == R.id.nav_logout) {
                SessionManager.logout(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            return true;
        });
    }

    private void setupActionButtons() {

        findViewById(R.id.btnWhatsApp).setOnClickListener(v -> {
            String number = SessionManager.getSupportWhatsapp(this);
            if (number == null || number.isEmpty()) {
                toast("Support WhatsApp number not available");
                return;
            }
            openWhatsApp(number);
        });

        findViewById(R.id.btnViewReports).setOnClickListener(v ->
                toast("Reports feature coming soon"));

        findViewById(R.id.btnManageUsers).setOnClickListener(v ->
                toast("User management coming soon"));

        findViewById(R.id.btnPanelAccess).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://lottery.durwankurgroup.com/panel/login"));
            startActivity(i);
        });

        findViewById(R.id.btnChangePassword).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            SessionManager.logout(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadEmployeeDetails() {

        ApiClient.getClient()
                .create(ApiService.class)
                .getUserDetails(
                        "Bearer " + SessionManager.getToken(this),
                        "application/json"
                )
                .enqueue(new Callback<UserDetailsResponse>() {

                    @Override
                    public void onResponse(Call<UserDetailsResponse> call,
                                           Response<UserDetailsResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().data != null) {

                            UserDetailsResponse.User user = response.body().data;

                            txtEmployeeName.setText(user.name);
                            txtEmployeeMobile.setText(user.mobileNo);
                            txtEmployeeId.setText("ID: " + user.id);
                            txtEmployeeEmail.setText(user.email);

                            SessionManager.saveUserName(EmployeeHomeActivity.this, user.name);
                            SessionManager.saveUserMobile(EmployeeHomeActivity.this, user.mobileNo);
                            SessionManager.saveEmail(EmployeeHomeActivity.this, user.email);

                            Log.d(TAG, "Employee loaded: "
                                    + new Gson().toJson(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        toast("Failed to load details");
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    private void updateUI() {
        txtEmployeeName.setText(SessionManager.getUserName(this));
        txtEmployeeMobile.setText(SessionManager.getUserMobile(this));
        txtEmployeeEmail.setText(SessionManager.getEmail(this));
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,
                "Download the app:\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(i, "Share App"));
    }

    private void openWhatsApp(String number) {
        try {
            String clean = number.replaceAll("[^0-9+]", "");
            if (clean.startsWith("+")) clean = clean.substring(1);

            String url = "https://wa.me/" + clean;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        long time = System.currentTimeMillis();

        if (time - lastBackPressedTime < 2000) {
            finishAffinity();
        } else {
            lastBackPressedTime = time;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
