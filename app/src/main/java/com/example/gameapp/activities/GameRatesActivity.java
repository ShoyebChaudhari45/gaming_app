package com.example.gameapp.activities;

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
import com.example.gameapp.models.response.PriceResponse;
import com.example.gameapp.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRatesActivity extends AppCompatActivity {

    private RecyclerView rv;
    private long lastBackPressedTime = 0;
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

        loadRates();
    }

    private void loadRates() {

        String token = "Bearer " + SessionManager.getToken(this);

        ApiClient.getClient()
                .create(ApiService.class)
                .getPrices(token, "application/json")
                .enqueue(new Callback<PriceResponse>() {

                    @Override
                    public void onResponse(Call<PriceResponse> call,
                                           Response<PriceResponse> response) {

                        Log.d("PRICE_API", "HTTP CODE: " + response.code());

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(GameRatesActivity.this,
                                    "Failed to load rates",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("PRICE_API", "DATA = " + response.body().data);

                        if (response.body().data == null || response.body().data.isEmpty()) {
                            Toast.makeText(GameRatesActivity.this,
                                    "No rates available",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<GameRateModel> list = new ArrayList<>();

                        for (String amount : response.body().data) {

                            String rate;

                            switch (amount) {
                                case "500":
                                    rate = "10 - 95";
                                    break;
                                case "1000":
                                    rate = "10 - 950";
                                    break;
                                case "2000":
                                    rate = "10 - 3000";
                                    break;
                                case "5000":
                                    rate = "10 - 7000";
                                    break;
                                case "10000":
                                    rate = "10 - 10000";
                                    break;
                                default:
                                    rate = amount;
                            }

                            list.add(new GameRateModel("Amount " + amount, rate));
                        }

                        rv.setAdapter(new GameRateAdapter(list));
                    }

                    @Override
                    public void onFailure(Call<PriceResponse> call, Throwable t) {
                        Log.e("PRICE_API", "NETWORK ERROR", t);
                        Toast.makeText(GameRatesActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
