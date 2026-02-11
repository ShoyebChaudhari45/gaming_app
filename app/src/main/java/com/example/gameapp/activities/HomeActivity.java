package com.example.gameapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gameapp.Adapters.GameTapAdapter;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.GameItem;
import com.example.gameapp.models.response.SupportResponse;
import com.example.gameapp.models.response.TapsResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;

    private ImageButton btnMenu;
    private RecyclerView rvGameTaps;
    private long lastBackPressedTime = 0;

    private TextView txtPlayerName, txtPlayerMobile, txtViewProfile;
    private TextView txtBalance;

    private static final String TAG = "HomeActivity";

    private final List<GameItem> gameItems = new ArrayList<>();
    private GameTapAdapter gameTapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerView();
        setupDrawer();
        setupActionButtons();

        loadUserDetails();
        loadGameTaps();
        loadSupportData();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        rvGameTaps = findViewById(R.id.rvGameTaps);
        txtBalance = findViewById(R.id.txtBalance);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        View headerView = navigationView.getHeaderView(0);
        txtPlayerName = headerView.findViewById(R.id.txtPlayerName);
        txtPlayerMobile = headerView.findViewById(R.id.txtPlayerMobile);
        txtViewProfile = headerView.findViewById(R.id.txtviewprofile);

        swipeRefresh.setOnRefreshListener(() -> {
            loadUserDetails();
            loadGameTaps();
            swipeRefresh.setRefreshing(false);
        });

        txtViewProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });
    }

    private void setupRecyclerView() {
        rvGameTaps.setLayoutManager(new LinearLayoutManager(this));
        // ⭐ UPDATED LAMBDA TO ACCEPT VIEW PARAMETER
        gameTapAdapter = new GameTapAdapter(this, gameItems, this::openGameSelection);
        rvGameTaps.setAdapter(gameTapAdapter);
    }

    private void setupActionButtons() {
        findViewById(R.id.btnWhatsApp).setOnClickListener(v -> {
            String number = SessionManager.getSupportWhatsapp(this);
            if (number == null || number.isEmpty()) {
                toast("Support WhatsApp number not available");
                Log.w(TAG, "WhatsApp number not found in SessionManager");
                return;
            }
            openWhatsApp(number);
        });

        findViewById(R.id.btnStarline).setOnClickListener(v ->
                startActivity(new Intent(this, StarlineActivity.class))
        );

        findViewById(R.id.btnAddPoints).setOnClickListener(v ->
                startActivity(new Intent(this, AddPointsActivity.class))
        );

        findViewById(R.id.btnWithdraw).setOnClickListener(v ->
                startActivity(new Intent(this, WithdrawActivity.class))
        );
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
                startActivity(new Intent(this, AddPointsActivity.class));
            } else if (id == R.id.nav_withdraw) {
                startActivity(new Intent(this, WithdrawActivity.class));
            } else if (id == R.id.nav_wallet) {
                startActivity(new Intent(this, WalletStatementActivity.class));
            } else if (id == R.id.nav_bid_history) {
                startActivity(new Intent(this, BidHistoryActivity.class));
            } else if (id == R.id.nav_game_rates) {
                startActivity(new Intent(this, GameRatesActivity.class));
            } else if (id == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));
            } else if (id == R.id.nav_change_password) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else if (id == R.id.nav_share) {
                shareApp();
            } else if (id == R.id.nav_panel_access) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://lottery.durwankurgroup.com/panel/login"));
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                SessionManager.logout(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            return true;
        });
    }

    private void loadSupportData() {
        String token = "Bearer " + SessionManager.getToken(this);

        ApiClient.getClient()
                .create(ApiService.class)
                .getSupport(token)
                .enqueue(new Callback<SupportResponse>() {

                    @Override
                    public void onResponse(Call<SupportResponse> call,
                                           Response<SupportResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isStatus()
                                && response.body().getData() != null) {

                            SupportResponse.SupportData supportData = response.body().getData();

                            if (supportData.hasValidWhatsapp()) {
                                SessionManager.saveSupportWhatsapp(HomeActivity.this,
                                        supportData.getWhatsappNo());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SupportResponse> call, Throwable t) {
                        Log.e(TAG, "Support API failed: " + t.getMessage());
                    }
                });
    }

    private void loadUserDetails() {
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

                            txtPlayerName.setText(user.name);
                            txtPlayerMobile.setText(user.mobileNo);

                            SessionManager.saveBalance(HomeActivity.this, user.balance);
                            updateBalanceUI();

                            SessionManager.saveEmail(HomeActivity.this, user.email);

                            String qr = user.qrCode;
                            if (qr != null && !qr.isEmpty()) {
                                if (!qr.startsWith("http")) {
                                    qr = "https://lottery.durwankurgroup.com/" + qr;
                                }
                                SessionManager.saveQrCode(HomeActivity.this, qr);
                            }

                            Log.e("RAW_ANDROID_RESPONSE", new Gson().toJson(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        Log.e(TAG, "User details failed: " + t.getMessage());
                    }
                });
    }

    private void loadGameTaps() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getTaps("Bearer " + SessionManager.getToken(this))
                .enqueue(new Callback<TapsResponse>() {

                    @Override
                    public void onResponse(Call<TapsResponse> call,
                                           Response<TapsResponse> response) {

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {
                            return;
                        }

                        gameItems.clear();

                        for (TapsResponse.GameData game : response.body().getData()) {

                            TapsResponse.Tap openTap = null;
                            TapsResponse.Tap closeTap = null;

                            for (TapsResponse.Tap tap : game.getTimes()) {
                                tap.setGameName(game.getName());

                                if ("open".equalsIgnoreCase(tap.getType())) {
                                    openTap = tap;
                                } else if ("close".equalsIgnoreCase(tap.getType())) {
                                    closeTap = tap;
                                }
                            }

                            gameItems.add(new GameItem(
                                    game.getName(),
                                    openTap,
                                    closeTap
                            ));
                        }

                        sortGamesByStatus();
                        gameTapAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<TapsResponse> call, Throwable t) {
                        toast("Network error");
                    }
                });
    }

    private int getGamePriority(GameItem item) {
        TapsResponse.Tap openTap = item.getOpenTap();
        TapsResponse.Tap closeTap = item.getCloseTap();

        boolean hasOpen = false;
        boolean hasUpcoming = false;

        if (openTap != null) {
            String status = openTap.getStatus();
            if ("running".equalsIgnoreCase(status) || "open".equalsIgnoreCase(status)) {
                hasOpen = true;
            } else if ("upcoming".equalsIgnoreCase(status)) {
                hasUpcoming = true;
            }
        }

        if (closeTap != null) {
            String status = closeTap.getStatus();
            if ("running".equalsIgnoreCase(status) || "open".equalsIgnoreCase(status)) {
                hasOpen = true;
            } else if ("upcoming".equalsIgnoreCase(status)) {
                hasUpcoming = true;
            }
        }

        if (hasOpen) return 1;
        if (hasUpcoming) return 2;
        return 3;
    }

    private void sortGamesByStatus() {
        Collections.sort(gameItems, (a, b) ->
                Integer.compare(getGamePriority(a), getGamePriority(b))
        );
    }

    // 🔥 CLEAN CLOSED STATUS CHECKER
    private boolean isClosed(String status) {
        if (status == null) return true;
        status = status.trim().toLowerCase();
        return status.contains("closed");
    }

    // ⭐ UPDATED METHOD SIGNATURE TO ACCEPT VIEW
    private void openGameSelection(TapsResponse.Tap openTap,
                                   TapsResponse.Tap closeTap,
                                   View clickedView) {

        boolean bothClosed = true;

        if (openTap != null && !isClosed(openTap.getStatus())) {
            bothClosed = false;
        }

        if (closeTap != null && !isClosed(closeTap.getStatus())) {
            bothClosed = false;
        }

        if (bothClosed) {

            // 💥 SHAKE EFFECT on clicked card
            if (clickedView != null) {
                clickedView.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.shake_view)
                );
            }

            // 💥 VIBRATION
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(
                            200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
            }

            toast("Market is closed");
            return;
        }

        Intent i = new Intent(this, GameTypesActivity.class);

        if (openTap != null) {
            i.putExtra("open_id", openTap.getId());
            i.putExtra("open_status", openTap.getStatus());
            i.putExtra("game_name", openTap.getGameName());
        }

        if (closeTap != null) {
            i.putExtra("close_id", closeTap.getId());
            i.putExtra("close_status", closeTap.getStatus());

            if (!i.hasExtra("game_name")) {
                i.putExtra("game_name", closeTap.getGameName());
            }
        }

        startActivity(i);
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,
                "Download the app:\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(i, "Share App"));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            updateBalanceUI();
        }
    }

    private void updateBalanceUI() {
        int balance = SessionManager.getBalance(this);
        txtBalance.setText(String.valueOf(balance));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalanceUI();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBackPressedTime < 2000) {
            finishAffinity();
        } else {
            lastBackPressedTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}