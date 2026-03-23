package com.example.gameapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.StarlineRateAdapter;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.StarlineRatesResponse;
import com.example.gameapp.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StarlineActivity extends AppCompatActivity {

    private static final String TAG = "StarlineActivity";

    private ImageButton btnBack;
    private RecyclerView rvStarlineRates;

    private final List<StarlineRatesResponse.StarlineRate> ratesList = new ArrayList<>();
    private StarlineRateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starline);

        initViews();
        setupRecyclerView();
        setupClickListeners();

        loadStarlineRates();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvStarlineRates = findViewById(R.id.rvStarlineRates);
    }

    private void setupRecyclerView() {
        rvStarlineRates.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StarlineRateAdapter(this, ratesList);
        rvStarlineRates.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadStarlineRates() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getStarlineRates(
                        "Bearer " + SessionManager.getToken(this),
                        "application/json"
                )
                .enqueue(new Callback<StarlineRatesResponse>() {
                    @Override
                    public void onResponse(Call<StarlineRatesResponse> call,
                                           Response<StarlineRatesResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            ratesList.clear();
                            ratesList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "Rates loaded: " + ratesList.size());
                        } else {
                            toast("Failed to load rates");
                        }
                    }

                    @Override
                    public void onFailure(Call<StarlineRatesResponse> call, Throwable t) {
                        Log.e(TAG, "Failed to load rates", t);
                        toast("Network error");
                    }
                });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}