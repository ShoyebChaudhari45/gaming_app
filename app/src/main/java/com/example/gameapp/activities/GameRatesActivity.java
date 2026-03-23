package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.Adapters.GameRateAdapter;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.GameRateModel;
import com.example.gameapp.models.response.GameRateItem;
import com.example.gameapp.models.response.GameRateResponse;
import com.example.gameapp.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRatesActivity extends AppCompatActivity {

    private RecyclerView rv;
    private final long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rates);

        ImageButton btnBack = findViewById(R.id.btnBack);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rv = findViewById(R.id.rvRates);
        rv.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GameRatesActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadRates();
    }

    private void loadRates() {
        String token = "Bearer " + SessionManager.getToken(this);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getGameRates(token)
                .enqueue(new Callback<GameRateResponse>() {

                    @Override
                    public void onResponse(Call<GameRateResponse> call,
                                           Response<GameRateResponse> response) {

                        // 🔥 RAW RESPONSE PRINT
                        try {
                            String raw = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "No Error Body";

                            Log.e("GAME_RATES_RAW", "RAW RESPONSE: " + raw);

                        } catch (Exception e) {
                            Log.e("GAME_RATES_RAW", "Error reading raw body", e);
                        }

                        // 🔥 PRINT FULL RESPONSE USING GSON
                        try {
                            Log.d("GAME_RATES_JSON", "FULL JSON: " +
                                    new com.google.gson.Gson().toJson(response.body()));
                        } catch (Exception ignored) {}

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getData() == null) {

                            Toast.makeText(GameRatesActivity.this,
                                    "Failed to load rates",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<GameRateModel> list = new ArrayList<>();

                        for (GameRateItem item : response.body().getData()) {

                            String name = item.getGame() != null ? item.getGame() : "Unknown";

                            String price = item.getPrice() != null ? item.getPrice() : "0";
                            String digit = item.getDigit() != null ? item.getDigit() : "0";

                            String rate = price + "-" + digit;

                            // 🔥 Log each game item
                            Log.d("GAME_RATE_ITEM", "Name: " + name + " | Rate: " + rate);

                            list.add(new GameRateModel(name, rate));
                        }

                        rv.setAdapter(new GameRateAdapter(list));
                    }

                    @Override
                    public void onFailure(Call<GameRateResponse> call, Throwable t) {
                        Log.e("GAME_RATES_ERROR", "Network error", t);

                        Toast.makeText(GameRatesActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}