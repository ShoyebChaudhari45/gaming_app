package com.example.gameapp.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.google.android.material.button.MaterialButton;

public class WinHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_history);

        // ✅ BACK BUTTON (CUSTOM TOOLBAR)
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> finish());

        // ✅ VIEWS
        TextView fromDate = findViewById(R.id.txtFromDate);
        TextView toDate = findViewById(R.id.txtToDate);
        MaterialButton submit = findViewById(R.id.btnSubmit);

        fromDate.setOnClickListener(v ->
                Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show());

        toDate.setOnClickListener(v ->
                Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show());

        submit.setOnClickListener(v ->
                Toast.makeText(this, "Submit clicked", Toast.LENGTH_SHORT).show());
    }
}
