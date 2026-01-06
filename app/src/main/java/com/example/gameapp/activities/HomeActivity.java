package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.GameAdapter;
import com.example.gameapp.R;
import com.example.gameapp.models.GameModel;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ================= INIT VIEWS =================
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        RecyclerView rvGames = findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(this));

        // ================= OPEN DRAWER ON MENU CLICK =================
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // ================= GAME LIST =================
        List<GameModel> list = new ArrayList<>();
        list.add(new GameModel("KARNATAKA DAY", "169 - 64 - 789", "10:00 AM - 11:00 AM"));
        list.add(new GameModel("MAIN BAZAR", "123 - 45 - 678", "11:00 AM - 12:00 PM"));
        list.add(new GameModel("STARLINE", "789 - 12 - 345", "01:00 PM - 02:00 PM"));

        rvGames.setAdapter(new GameAdapter(list, game -> {
            Intent intent = new Intent(HomeActivity.this, GamePlayActivity.class);
            intent.putExtra("GAME_NAME", game.getName());
            startActivity(intent);
        }));

        // ================= DRAWER MENU HANDLING =================
        navigationView.setNavigationItemSelectedListener(item -> {

            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_profile) {

                startActivity(new Intent(this, ProfileActivity.class));
                finish();

            } else if (id == R.id.nav_add_funds) {

                startActivity(new Intent(this, AddPointsActivity.class));
                finish();

            } else if (id == R.id.nav_withdraw) {

                startActivity(new Intent(this, WithdrawActivity.class));
                finish();

            } else if (id == R.id.nav_wallet) {

                startActivity(new Intent(this, WalletStatementActivity.class));
                finish();

            } else if (id == R.id.nav_bid_history) {

                startActivity(new Intent(this, BidHistoryActivity.class));
                finish();

            } else if (id == R.id.nav_win_history) {

                startActivity(new Intent(this, WinHistoryActivity.class));
                finish();

            } else if (id == R.id.nav_game_rates) {

                startActivity(new Intent(this, GameRatesActivity.class));
                finish();

            } else if (id == R.id.nav_support) {

                startActivity(new Intent(this, SupportActivity.class));
                finish();

            } else if (id == R.id.nav_share) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String message = "Check out this app:\n" +
                        "https://play.google.com/store/apps/details?id=" + getPackageName();

                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(shareIntent, "Share via"));

            } else if (id == R.id.nav_change_password) {

                startActivity(new Intent(this, ChangePasswordActivity.class));

            } else if (id == R.id.nav_logout) {

                SessionManager.logout(this);

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
    }

    // ================= BACK PRESS =================
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
