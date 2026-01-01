package com.example.gameapp.activities;

import android.os.Bundle;
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
    }
}
