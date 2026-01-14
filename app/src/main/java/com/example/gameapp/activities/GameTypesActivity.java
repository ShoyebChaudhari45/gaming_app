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

    private String tapId, tapType, gameName, endTime, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_types);

        getIntentData();
        initViews();
        setupRecyclerView();
        loadGameTypes();
        ImageButton btnBack= findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GameTypesActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }

    private void getIntentData() {
        tapId = getIntent().getStringExtra("tap_id");
        tapType = getIntent().getStringExtra("tap_type");
        gameName = getIntent().getStringExtra("game_name");
        endTime = getIntent().getStringExtra("end_time");
        status = getIntent().getStringExtra("status");
    }

    private void initViews() {
        rvGameTypes = findViewById(R.id.rvGameTypes);
        txtTitle = findViewById(R.id.txtTitle);
        btnBack = findViewById(R.id.btnBack);

        txtTitle.setText(gameName + " - " + tapType);
        btnBack.setOnClickListener(v -> finish());
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
                        toast(t.getMessage());
                    }
                });
    }

    private void onGameTypeClick(GameType gameType) {

        Intent intent = new Intent(this, BidActivity.class);
        intent.putExtra("tap_id", tapId);
        intent.putExtra("tap_type", tapType);
        intent.putExtra("game_name", gameName);
        intent.putExtra("game_type", gameType.getName());
        intent.putExtra("game_image", gameType.getImage()); // ✅ Pass game image
        intent.putExtra("end_time", endTime);
        intent.putExtra("status", status);
        startActivity(intent);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}