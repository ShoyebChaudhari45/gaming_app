package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.GameTypeAdapter;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.GamesResponse;
import com.example.gameapp.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameTypesActivity extends AppCompatActivity {

    private static final String TAG = "GameTypesActivity";
    private long lastBackPressedTime = 0;

    private RecyclerView rvGameTypes;
    private TextView txtTitle;
    private ImageButton btnBack;

    private final List<GamesResponse.Game> gameTypes = new ArrayList<>();
    private GameTypeAdapter gameTypeAdapter;

    private String tapId;
    private String tapType;
    private String gameName;
    private String endTime;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_types);

        getIntentData();
        initViews();
        setupRecyclerView();
        loadGameTypes();
    }

    private void getIntentData() {
        tapId = getIntent().getStringExtra("tap_id");
        tapType = getIntent().getStringExtra("tap_type");
        gameName = getIntent().getStringExtra("game_name");
        endTime = getIntent().getStringExtra("end_time");
        status = getIntent().getStringExtra("status");

        Log.d(TAG, "Intent data: tapId=" + tapId + ", tapType=" + tapType
                + ", gameName=" + gameName + ", status=" + status);
    }

    private void initViews() {
        rvGameTypes = findViewById(R.id.rvGameTypes);
        txtTitle = findViewById(R.id.txtTitle);
        btnBack = findViewById(R.id.btnBack);

        txtTitle.setText(gameName + " - " + tapType);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvGameTypes.setLayoutManager(gridLayoutManager);

        gameTypeAdapter = new GameTypeAdapter(this, gameTypes, this::onGameTypeClick);
        rvGameTypes.setAdapter(gameTypeAdapter);
    }

    private void loadGameTypes() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getGames("Bearer " + SessionManager.getToken(this))
                .enqueue(new Callback<GamesResponse>() {
                    @Override
                    public void onResponse(Call<GamesResponse> call, Response<GamesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Games loaded: " + response.body().getData().size());

                            gameTypes.clear();
                            gameTypes.addAll(response.body().getData());
                            gameTypeAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "Failed to load games: " + response.message());
                            toast("Failed to load game types");
                        }
                    }

                    @Override
                    public void onFailure(Call<GamesResponse> call, Throwable t) {
                        Log.e(TAG, "Error loading games", t);
                        toast("Network error");
                    }
                });
    }

    private void onGameTypeClick(GamesResponse.Game game) {
        Log.d(TAG, "Game type clicked: " + game.getName());

        Intent intent = new Intent(this, BidActivity.class);
        intent.putExtra("tap_id", tapId);
        intent.putExtra("tap_type", tapType);
        intent.putExtra("game_name", gameName);
        intent.putExtra("game_type", game.getName());
        intent.putExtra("game_image", game.getImage());
        intent.putExtra("end_time", endTime);
        intent.putExtra("status", status);
        startActivity(intent);
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
