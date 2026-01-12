package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.GameTapAdapter;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.GameItem;
import com.example.gameapp.models.response.TapsResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private RecyclerView rvGameTaps;
    private long lastBackPressedTime = 0;

    // ✅ NEW – Drawer header views
    private TextView txtPlayerName, txtPlayerMobile,txtViewProfile;

    private static final String TAG = "HomeActivity";

    private final List<GameItem> gameItems = new ArrayList<>();
    private GameTapAdapter gameTapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerView();
        setupDrawer();
        setupActionButtons();

        loadUserDetails();   // ✅ NEW
        loadGameTaps();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        rvGameTaps = findViewById(R.id.rvGameTaps);

        // ✅ INIT HEADER VIEWS
        View headerView = navigationView.getHeaderView(0);
        txtPlayerName = headerView.findViewById(R.id.txtPlayerName);
        txtPlayerMobile = headerView.findViewById(R.id.txtPlayerMobile);
        txtViewProfile = headerView.findViewById(R.id.txtviewprofile);

        txtViewProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });
    }

    private void setupRecyclerView() {
        rvGameTaps.setLayoutManager(new LinearLayoutManager(this));

        gameTapAdapter = new GameTapAdapter(
                this,
                gameItems,
                this::openGameSelection
        );
        rvGameTaps.setAdapter(gameTapAdapter);
    }

    private void setupActionButtons() {
        findViewById(R.id.btnWhatsApp).setOnClickListener(v ->
                toast("Opening WhatsApp...")
        );

        findViewById(R.id.btnStarline).setOnClickListener(v ->
                toast("Opening Starline...")
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

            } else if (id == R.id.nav_logout) {
                SessionManager.logout(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            return true;
        });
    }

    // =====================================================
    // ✅ USER DETAILS API (REUSED MODEL)
    // =====================================================
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

                            // ✅ Save balance globally
                            SessionManager.saveBalance(
                                    HomeActivity.this,
                                    user.balance
                            );
                            //save email
                            SessionManager.saveEmail(HomeActivity.this, user.email);


                            Log.d(TAG, "User loaded: " + user.name);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        Log.e(TAG, "User details failed", t);
                    }
                });
    }

    // =====================================================
    // GAME TAPS (UNCHANGED)
    // =====================================================
    private void loadGameTaps() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getTaps("Bearer " + SessionManager.getToken(this))
                .enqueue(new Callback<TapsResponse>() {

                    @Override
                    public void onResponse(Call<TapsResponse> call,
                                           Response<TapsResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

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

                                gameItems.add(
                                        new GameItem(game.getName(), openTap, closeTap)
                                );
                            }

                            sortGamesByStatus();
                            gameTapAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<TapsResponse> call, Throwable t) {
                        toast("Network error");
                    }
                });
    }

    private void sortGamesByStatus() {
        Collections.sort(gameItems, (a, b) ->
                Integer.compare(getGamePriority(a), getGamePriority(b))
        );
    }

    private int getGamePriority(GameItem item) {
        String o = item.hasOpenTap() ? item.getOpenTap().getStatus() : null;
        String c = item.hasCloseTap() ? item.getCloseTap().getStatus() : null;

        if (isRunning(o) || isRunning(c)) return 1;
        if (isOpenOrUpcoming(o) || isOpenOrUpcoming(c)) return 2;
        return 3;
    }

    private boolean isRunning(String s) {
        return s != null && s.equalsIgnoreCase("running");
    }

    private boolean isOpenOrUpcoming(String s) {
        return s != null && (s.equalsIgnoreCase("open") || s.equalsIgnoreCase("upcoming"));
    }

    private void openGameSelection(TapsResponse.Tap tap, String type) {
        Intent i = new Intent(this, GameTypesActivity.class);
        i.putExtra("tap_id", tap.getId());
        i.putExtra("tap_type", type);
        i.putExtra("game_name", tap.getGameName());
        i.putExtra("end_time", tap.getEndTime());
        i.putExtra("status", tap.getStatus());
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
