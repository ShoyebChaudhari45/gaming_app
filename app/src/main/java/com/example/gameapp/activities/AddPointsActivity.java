package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;

public class AddPointsActivity extends AppCompatActivity {

    private long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);

        // ================= VIEWS =================
        EditText edtPoints = findViewById(R.id.enterBox)
                .findViewById(android.R.id.edit);

        Button btnAddPoints = findViewById(R.id.btnAddPoints);
        GridLayout grid = findViewById(R.id.grid);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // ================= BACK BUTTON =================
        btnBack.setOnClickListener(v -> handleBackPress());

        // ================= QUICK AMOUNT BUTTONS =================
        for (int i = 0; i < grid.getChildCount(); i++) {
            View view = grid.getChildAt(i);

            if (view instanceof Button) {
                Button button = (Button) view;

                button.setOnClickListener(v -> {
                    String amount = button.getText().toString();
                    edtPoints.setText(amount);
                    Toast.makeText(
                            this,
                            "Selected: " + amount,
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        }

        // ================= ADD POINTS BUTTON =================
        btnAddPoints.setOnClickListener(v -> {
            String amount = edtPoints.getText().toString().trim();

            if (amount.isEmpty()) {
                Toast.makeText(this, "Please enter points", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(
                    this,
                    "Add Points clicked: " + amount,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // ================= SYSTEM BACK =================
    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    // ================= BACK LOGIC =================
    private void handleBackPress() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBackPressedTime < 2000) {
            // ✅ DOUBLE TAP → EXIT ACTIVITY
            finish();
        } else {
            // ✅ SINGLE TAP → GO TO HOME
            lastBackPressedTime = currentTime;

            Toast.makeText(
                    this,
                    "Press back again to exit",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(AddPointsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }
}
