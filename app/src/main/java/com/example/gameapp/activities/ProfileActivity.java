package com.example.gameapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button edit = findViewById(R.id.btnEdit);
        Button submit = findViewById(R.id.btnSubmit);

        edit.setOnClickListener(v ->
                Toast.makeText(this, "Edit Clicked", Toast.LENGTH_SHORT).show()
        );

        submit.setOnClickListener(v ->
                Toast.makeText(this, "Profile Submitted (Dummy)", Toast.LENGTH_SHORT).show()
        );
    }
}
