package com.example.gameapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.WithdrawRequest;
import com.example.gameapp.models.response.WithdrawResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    private static final String TAG = "WithdrawActivity";

    private TextView txtPoints;
    private EditText etAmount;
    private RadioButton rbUpi, rbBank, rbPaytm;
    private MaterialButton btnSubmit;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    private long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        ImageButton btnBack = findViewById(R.id.btnBack);
        txtPoints = findViewById(R.id.txtBalance);
        etAmount = findViewById(R.id.etAmount);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        apiService = ApiClient.getClient().create(ApiService.class);

        // SHOW BALANCE
        int balance = SessionManager.getBalance(this);
        txtPoints.setText(String.valueOf(balance));

        // 🔙 HEADER BACK → IMMEDIATE FINISH
        btnBack.setOnClickListener(v -> finish());

        // SUBMIT WITHDRAW
        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Enter withdrawal amount", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);

            if (amount < 100) {
                Toast.makeText(this, "Minimum withdrawal ₹100", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMode = "upi";
            if (rbBank.isChecked()) paymentMode = "bank";
            else if (rbPaytm.isChecked()) paymentMode = "paytm";

            withdrawAmount(amount, paymentMode);
        });
    }

    // ================= WITHDRAW API =================
    private void withdrawAmount(int amount, String paymentMode) {

        int currentBalance = SessionManager.getBalance(this);

        if (amount > currentBalance) {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        WithdrawRequest request = new WithdrawRequest(amount, paymentMode);

        apiService.withdrawAmount(
                "Bearer " + SessionManager.getToken(this),
                request
        ).enqueue(new Callback<WithdrawResponse>() {

            @Override
            public void onResponse(Call<WithdrawResponse> call, Response<WithdrawResponse> response) {

                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {

                    int newBalance = currentBalance - amount;
                    SessionManager.saveBalance(WithdrawActivity.this, newBalance);
                    txtPoints.setText(String.valueOf(newBalance));
                    etAmount.setText("");

                    Toast.makeText(
                            WithdrawActivity.this,
                            response.body().getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                } else {
                    Toast.makeText(
                            WithdrawActivity.this,
                            "Withdraw failed",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<WithdrawResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(
                        WithdrawActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // 🔙 SYSTEM BACK → DOUBLE PRESS EXIT
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
