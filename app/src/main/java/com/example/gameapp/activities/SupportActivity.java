package com.example.gameapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;

public class SupportActivity extends AppCompatActivity {
    private long lastBackPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ImageButton btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SupportActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });



        setupSupportRows();
    }

    private void setupSupportRows() {

        findViewById(R.id.rowCall).setOnClickListener(v ->
                Toast.makeText(this, "Call clicked", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.rowWhatsapp).setOnClickListener(v ->
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/919000000000")
                ))
        );

        findViewById(R.id.rowEmail).setOnClickListener(v ->
                startActivity(new Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("mailto:support@example.com")
                ))
        );


        findViewById(R.id.rowProof).setOnClickListener(v ->
                Toast.makeText(this, "Withdraw Proof clicked", Toast.LENGTH_SHORT).show()
        );
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
