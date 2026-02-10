package com.example.gameapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
            Log.d(TAG, "Opening WhatsApp with number: " + number);
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
            }
            else if (id == R.id.nav_panel_access) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://lottery.durwankurgroup.com/panel/login"));
                startActivity(intent);
            }
            else if (id == R.id.nav_logout) {
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
                                String whatsappNumber = supportData.getWhatsappNo();
                                SessionManager.saveSupportWhatsapp(HomeActivity.this, whatsappNumber);
                                Log.d(TAG, "WhatsApp number saved: " + whatsappNumber);
                            } else {
                                Log.w(TAG, "No valid WhatsApp number in support data");
                            }
                        } else {
                            Log.e(TAG, "Failed to load support data");
                        }
                    }

                    @Override
                    public void onFailure(Call<SupportResponse> call, Throwable t) {
                        Log.e(TAG, "Support API call failed: " + t.getMessage());
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

                            // Set basic UI
                            txtPlayerName.setText(user.name);
                            txtPlayerMobile.setText(user.mobileNo);

                            // 🔥 Save balance
                            SessionManager.saveBalance(HomeActivity.this, user.balance);
                            updateBalanceUI();

                            // 🔥 Save email
                            SessionManager.saveEmail(HomeActivity.this, user.email);

                            // 🔥 NEW: Save QR code
                            String qr = user.qrCode;

                            if (qr != null && !qr.isEmpty()) {

                                // If backend gives path only like /qr_codes/QR.jpeg
                                if (!qr.startsWith("http")) {
                                    qr = "https://lottery.durwankurgroup.com/" + qr;
                                }

                                SessionManager.saveQrCode(HomeActivity.this, qr);
                                Log.d(TAG, "Final QR saved: " + qr);
                            }
                            try {
                                String raw = new Gson().toJson(response.body());
                                Log.e("RAW_ANDROID_RESPONSE", raw);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            Log.d(TAG, "User loaded: " + user.name + " | QR saved: " + user.qrCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        Log.e(TAG, "User details failed", t);
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
                            Log.e(TAG, "Invalid API response");
                            return;
                        }

                        gameItems.clear();

                        for (TapsResponse.GameData game : response.body().getData()) {
                            Log.d(TAG, "Processing game: " + game.getName());

                            TapsResponse.Tap openTap = null;
                            TapsResponse.Tap closeTap = null;

                            List<TapsResponse.Tap> times = game.getTimes();

                            // ⭐ FIX: Use "type" field to identify open/close taps
                            for (TapsResponse.Tap tap : times) {
                                tap.setGameName(game.getName());

                                String type = tap.getType();
                                String status = tap.getStatus();

                                Log.d(TAG, game.getName() + " - Type: " + type + ", Status: " + status);

                                if ("open".equalsIgnoreCase(type)) {
                                    openTap = tap;
                                } else if ("close".equalsIgnoreCase(type)) {
                                    closeTap = tap;
                                }
                            }

                            // Fallback if type is missing
                            if (openTap == null && times.size() > 0) {
                                openTap = times.get(0);
                                openTap.setGameName(game.getName());
                            }
                            if (closeTap == null && times.size() > 1) {
                                closeTap = times.get(1);
                                closeTap.setGameName(game.getName());
                            }

                            gameItems.add(new GameItem(
                                    game.getName(),
                                    openTap,
                                    closeTap
                            ));
                        }

                        sortGamesByStatus();
                        gameTapAdapter.notifyDataSetChanged();

                        Log.d(TAG, "Total games loaded: " + gameItems.size());
                    }

                    @Override
                    public void onFailure(Call<TapsResponse> call, Throwable t) {
                        Log.e(TAG, "Network error loading games", t);
                        toast("Network error");
                    }
                });
    }

    // ⭐ FIX: Proper priority based on actual status
    private int getGamePriority(GameItem item) {
        TapsResponse.Tap openTap = item.getOpenTap();
        TapsResponse.Tap closeTap = item.getCloseTap();

        // Check both taps for "running" or "open" status
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
        return 3; // closed
    }

    private void sortGamesByStatus() {
        Collections.sort(gameItems, (a, b) ->
                Integer.compare(getGamePriority(a), getGamePriority(b))
        );
    }

    private void openGameSelection(TapsResponse.Tap openTap,
                                   TapsResponse.Tap closeTap) {

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
        Log.d(TAG, "openWhatsApp called with: " + number);
        try {
            String clean = number.replaceAll("[^0-9+]", "");
            if (clean.startsWith("+")) {
                clean = clean.substring(1);
            }

            String url = "https://wa.me/" + clean;
            Log.d(TAG, "WhatsApp URL: " + url);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            Log.d(TAG, "WhatsApp opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error opening WhatsApp: " + e.getMessage());
            Toast.makeText(this,
                    "WhatsApp is not installed",
                    Toast.LENGTH_SHORT).show();
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
        if (txtBalance != null) {
            txtBalance.setText(String.valueOf(balance));
        }
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
            System.exit(0);
        } else {
            lastBackPressedTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}