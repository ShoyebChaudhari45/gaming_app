package com.example.gameapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.WithdrawResponse;
import com.example.gameapp.session.SessionManager;
import com.example.gameapp.utils.FileUtils;
import com.google.android.material.button.MaterialButton;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WithdrawActivity extends AppCompatActivity {

    private TextView txtBalance;
    private EditText etAmount;
    private ImageView imgProof;
    private Uri proofUri = null;

    private ProgressDialog dialog;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        txtBalance = findViewById(R.id.txtBalance);
        etAmount = findViewById(R.id.etAmount);
        imgProof = findViewById(R.id.imgProof);

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnUpload = findViewById(R.id.btnUploadProof);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);

        apiService = ApiClient.getClient().create(ApiService.class);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Processing...");
        dialog.setCancelable(false);

        // Load balance
        txtBalance.setText(String.valueOf(SessionManager.getBalance(this)));

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Upload proof
        btnUpload.setOnClickListener(v -> pickImage());

        // Submit withdraw
        btnSubmit.setOnClickListener(v -> sendWithdrawRequest());
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == 101 && resultCode == RESULT_OK && data != null) {
            proofUri = data.getData();
            imgProof.setImageURI(proofUri);
        }
    }

    private void sendWithdrawRequest() {

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

        int balance = SessionManager.getBalance(this);
        if (amount > balance) {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            return;
        }

        if (proofUri == null) {
            Toast.makeText(this, "Upload UPI payment proof!", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();

        // Create file from Uri
        File file = FileUtils.getFileFromUri(this, proofUri);

        if (file == null) {
            Toast.makeText(this, "Failed to process file!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        // ------------------------- NEW FIX 🔥 -------------------------
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part proof =
                MultipartBody.Part.createFormData("payment_proof", file.getName(), reqFile);
        // ---------------------------------------------------------------

        RequestBody amt = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(amount));
        RequestBody mode = RequestBody.create(MediaType.parse("text/plain"), "upi");

        apiService.withdrawAmount(
                "Bearer " + SessionManager.getToken(this),
                amt,
                mode,
                proof
        ).enqueue(new Callback<WithdrawResponse>() {

            @Override
            public void onResponse(Call<WithdrawResponse> call, Response<WithdrawResponse> resp) {
                dialog.dismiss();

                if (resp.isSuccessful() && resp.body() != null) {

                    int newBalance = balance - amount;
                    SessionManager.saveBalance(WithdrawActivity.this, newBalance);
                    txtBalance.setText(String.valueOf(newBalance));

                    Toast.makeText(
                            WithdrawActivity.this,
                            resp.body().getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    finish();

                } else {
                    Toast.makeText(WithdrawActivity.this, "Withdraw failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WithdrawResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(WithdrawActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
