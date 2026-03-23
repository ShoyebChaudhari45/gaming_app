package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.Adapters.WalletStatementAdapter;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.WalletStatementResponse;
import com.example.gameapp.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletStatementActivity extends AppCompatActivity {

    RecyclerView rvWallet;
    TextView txtPoints;
    ImageButton btnBack;
    LinearLayout btnWithdraw, btnAddFunds;

    ApiService apiService;

    private long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_statement);

        rvWallet = findViewById(R.id.rvWallet);
        txtPoints = findViewById(R.id.txtPoints);
        btnBack = findViewById(R.id.btnBack);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnAddFunds = findViewById(R.id.btnAddFunds);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Show balance
        txtPoints.setText(String.valueOf(SessionManager.getBalance(this)));

        rvWallet.setLayoutManager(new LinearLayoutManager(this));

        // 🔙 HEADER BACK → IMMEDIATE BACK
        btnBack.setOnClickListener(v -> finish());

        // 💰 WITHDRAW
        btnWithdraw.setOnClickListener(v -> {
            startActivity(new Intent(
                    WalletStatementActivity.this,
                    WithdrawActivity.class
            ));
        });

        // ➕ ADD FUNDS
        btnAddFunds.setOnClickListener(v -> {
            startActivity(new Intent(
                    WalletStatementActivity.this,
                    AddPointsActivity.class
            ));
        });

        loadWalletStatement();
    }

    private void loadWalletStatement() {
        apiService.getWalletStatement(
                "Bearer " + SessionManager.getToken(this)
        ).enqueue(new Callback<WalletStatementResponse>() {

            @Override
            public void onResponse(
                    Call<WalletStatementResponse> call,
                    Response<WalletStatementResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    WalletStatementAdapter adapter =
                            new WalletStatementAdapter(response.body().getData());
                    rvWallet.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(
                    Call<WalletStatementResponse> call,
                    Throwable t) {

                Toast.makeText(
                        WalletStatementActivity.this,
                        "Failed to load wallet statement",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // 🔙 SYSTEM BACK LOGIC
    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();

        if (lastBackPressedTime == 0) {
            // 1️⃣ FIRST PRESS → HOME
            lastBackPressedTime = currentTime;

            Intent intent = new Intent(
                    WalletStatementActivity.this,
                    HomeActivity.class
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        } else if (currentTime - lastBackPressedTime < 2000) {
            // 2️⃣ DOUBLE PRESS → EXIT APP
            finishAffinity();
        } else {
            lastBackPressedTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
