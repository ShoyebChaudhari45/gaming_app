package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;

public class GamePlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        TextView txtTitle = findViewById(R.id.txtGameTitle);

        String gameName = getIntent().getStringExtra("GAME_NAME");
        txtTitle.setText(gameName);
        ImageButton btnBack= findViewById(R.id.btnMenu);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GamePlayActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
