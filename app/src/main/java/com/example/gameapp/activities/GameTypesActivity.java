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
import com.example.gameapp.models.GameType;
import com.example.gameapp.models.response.GamesResponse;
import com.example.gameapp.session.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameTypesActivity extends AppCompatActivity {

    private static final String TAG = "GameTypesActivity";

    private RecyclerView rvGameTypes;
    private TextView txtTitle;
    private ImageButton btnBack;

    private final List<GameType> gameTypes = new ArrayList<>();
    private GameTypeAdapter adapter;

    private int openId = -1;
    private int closeId = -1;
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_types);

        getIntentData();
        initViews();
        setupRecyclerView();
        loadGameTypes();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GameTypesActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void getIntentData() {
        openId = getIntent().getIntExtra("open_id", -1);
        closeId = getIntent().getIntExtra("close_id", -1);
        gameName = getIntent().getStringExtra("game_name");

        Log.d(TAG, "Received - Game: " + gameName + ", Open ID: " + openId + ", Close ID: " + closeId);
    }

    private void initViews() {
        rvGameTypes = findViewById(R.id.rvGameTypes);
        txtTitle = findViewById(R.id.txtTitle);
        btnBack = findViewById(R.id.btnBack);

        txtTitle.setText(gameName);
    }

    private void setupRecyclerView() {
        rvGameTypes.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GameTypeAdapter(this, gameTypes, this::onGameTypeClick);
        rvGameTypes.setAdapter(adapter);
    }

    private void loadGameTypes() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getGames("Bearer " + SessionManager.getToken(this))
                .enqueue(new Callback<GamesResponse>() {

                    @Override
                    public void onResponse(Call<GamesResponse> call,
                                           Response<GamesResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            gameTypes.clear();
                            gameTypes.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();

                        } else {
                            toast("Failed to load game types");
                        }
                    }

                    @Override
                    public void onFailure(Call<GamesResponse> call, Throwable t) {
                        Log.e(TAG, "API Error", t);
                        toast("Error: " + t.getMessage());
                    }
                });
    }

    private void onGameTypeClick(GameType gameType) {

        Intent intent = new Intent(this, BidActivity.class);

        intent.putExtra("open_id", openId);
        intent.putExtra("close_id", closeId);
        intent.putExtra("game_name", gameName);
        intent.putExtra("game_image", gameType.getImage());

// pass open/close statuses
        intent.putExtra("open_status", getIntent().getStringExtra("open_status"));
        intent.putExtra("close_status", getIntent().getStringExtra("close_status"));

// dynamic type
        String backendType = gameType.getName().trim();
        intent.putExtra("game_type", backendType);

        startActivity(intent);


    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
