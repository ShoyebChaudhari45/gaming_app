package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.Adapters.WalletAdapter;

import java.util.ArrayList;
import java.util.List;

public class WalletStatementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_statement);

        ImageButton btnBack = findViewById(R.id.btnBack); // ya btnMenu
        btnBack.setOnClickListener(v -> finish());



        Button btnWithdraw = findViewById(R.id.btnWithdraw);
        Button btnAddFunds = findViewById(R.id.btnAddFunds);



        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(WalletStatementActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnWithdraw.setOnClickListener(v ->
                Toast.makeText(this, "Withdraw clicked", Toast.LENGTH_SHORT).show());

        btnAddFunds.setOnClickListener(v ->
                Toast.makeText(this, "Add Funds clicked", Toast.LENGTH_SHORT).show());

        RecyclerView rv = findViewById(R.id.rvWallet);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<String> dummy = new ArrayList<>();
        for (int i = 0; i < 10; i++) dummy.add("tx");

        rv.setAdapter(new WalletAdapter(dummy));
    }
}
