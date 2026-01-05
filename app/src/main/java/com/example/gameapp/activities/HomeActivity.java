package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.GameAdapter;
import com.example.gameapp.R;
import com.example.gameapp.models.GameModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import com.example.gameapp.session.SessionManager;


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ================= INIT VIEWS =================
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        RecyclerView rvGames = findViewById(R.id.rvGames);
        LinearLayout walletContainer = findViewById(R.id.walletContainer);
        rvGames.setLayoutManager(new LinearLayoutManager(this));

        List<GameModel> list = new ArrayList<>();
        list.add(new GameModel("KARNATAKA DAY", "169 - 64 - 789", "10:00 AM - 11:00 AM"));
        list.add(new GameModel("MAIN BAZAR", "123 - 45 - 678", "11:00 AM - 12:00 PM"));
        list.add(new GameModel("STARLINE", "789 - 12 - 345", "01:00 PM - 02:00 PM"));

        rvGames.setAdapter(new GameAdapter(list, game -> {

            // 👉 OPEN GAME PAGE
            Intent intent = new Intent(HomeActivity.this, GamePlayActivity.class);
            intent.putExtra("GAME_NAME", game.getName());
            startActivity(intent);

        }));



        // ================= TOOLBAR =================
        setSupportActionBar(toolbar);


        // ================= DRAWER TOGGLE =================
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ================= MENU CLICK HANDLING =================
        navigationView.setNavigationItemSelectedListener(item -> {

            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // already on home
                Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show();


            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                finish();
                // startActivity(new Intent(this, ProfileActivity.class));

                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_add_funds) {
                // startActivity(new Intent(this, AddFundsActivity.class));
                Intent intent = new Intent(this, AddPointsActivity.class);
                startActivity(intent);
                finish();


                Toast.makeText(this, "Add Funds", Toast.LENGTH_SHORT).show();


            } else if (id == R.id.nav_withdraw) {
                // startActivity(new Intent(this, WithdrawActivity.class));
                Intent intent = new Intent(this, WithdrawActivity.class);
                startActivity(intent);
                finish();


                Toast.makeText(this, "Withdraw", Toast.LENGTH_SHORT).show();


            } else if (id == R.id.nav_wallet) {
                // wallet screen
                Intent intent = new Intent(this, WalletStatementActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show();


            } else if (id == R.id.nav_bid_history) {

                // bid history
                Intent intent = new Intent(this, BidHistoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Bid History", Toast.LENGTH_SHORT).show();


            } else if (id == R.id.nav_win_history) {
                // win history
                Intent intent = new Intent(this, WinHistoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Win History", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_game_rates) {
                // game rates
                Intent intent = new Intent(this, GameRatesActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Game Rates", Toast.LENGTH_SHORT).show();


            } else if (id == R.id.nav_support) {
                // support
                Intent intent = new Intent(this, SupportActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Support", Toast.LENGTH_SHORT).show();


            }else if (id == R.id.nav_share) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                String shareMessage = "Check out this app:\n";
                shareMessage += "https://play.google.com/store/apps/details?id="
                        + getPackageName();

                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                startActivity(Intent.createChooser(intent, "Share via"));

                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_change_password) {

                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);

            }
            else if (id == R.id.nav_logout) {

                // ✅ CLEAR SESSION
                SessionManager.logout(this);

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }


            return true;
        });


//        walletContainer.setOnClickListener(v ->
//                Toast.makeText(this, "Wallet Clicked", Toast.LENGTH_SHORT).show()
//        );
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
