package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.GameAdapter;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.GameModel;
import com.example.gameapp.models.response.GameResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private RecyclerView rvGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        rvGames = findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(this));

        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        loadGames();

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
            } else if (id == R.id.nav_game_rates) {
                startActivity(new Intent(this, GameRatesActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                SessionManager.logout(this);
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
            return true;
        });
    }

    private void loadGames() {

        ApiClient.getClient()
                .create(ApiService.class)
                .getGames("Bearer " + SessionManager.getToken(this))
                .enqueue(new Callback<GameResponse>() {

                    @Override
                    public void onResponse(Call<GameResponse> call,
                                           Response<GameResponse> response) {

                        Log.d("GAME_API", "HTTP CODE: " + response.code());

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(HomeActivity.this,
                                    "Failed to load games",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("GAME_API", "SUCCESS = " + response.body().success);
                        Log.d("GAME_API", "DATA SIZE = " +
                                (response.body().data != null ? response.body().data.size() : 0));

                        // ✅ ONLY CHECK DATA
                        if (response.body().data == null || response.body().data.isEmpty()) {
                            Toast.makeText(HomeActivity.this,
                                    "No games available",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<GameModel> list = new ArrayList<>();

                        for (GameResponse.Game g : response.body().data) {
                            list.add(new GameModel(
                                    g.name,
                                    g.result,
                                    g.time
                            ));
                        }

                        rvGames.setAdapter(new GameAdapter(HomeActivity.this, list));

                        Log.d("GAME_API", "DATA BOUND SUCCESSFULLY");
                    }

                    @Override
                    public void onFailure(Call<GameResponse> call, Throwable t) {
                        Log.e("GAME_API", "NETWORK ERROR", t);
                        Toast.makeText(HomeActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
