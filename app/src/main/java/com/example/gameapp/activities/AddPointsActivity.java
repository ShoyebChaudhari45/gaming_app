package com.example.gameapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;

import com.example.gameapp.models.response.DepositResponse;
import com.example.gameapp.models.response.PriceResponse;
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

public class AddPointsActivity extends AppCompatActivity {

    private static final String TAG = "AddPointsActivity";
    private EditText edtPoints;
    private TextView txtPoints, txtFileName;
    private LinearLayout gridPrices;
    private ImageView imgQr;
    private Uri selectedFileUri = null;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    private static final int PICK_FILE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);

        edtPoints = findViewById(R.id.enterBox)
                .findViewById(android.R.id.edit);

        txtPoints = findViewById(R.id.txtPoints);
        gridPrices = findViewById(R.id.grid);
        imgQr = findViewById(R.id.imgQr);
        txtFileName = findViewById(R.id.txtFileName);

        MaterialButton btnSelectFile = findViewById(R.id.btnSelectFile);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        ImageButton btnBack = findViewById(R.id.btnBack);

        apiService = ApiClient.getClient().create(ApiService.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        // Show balance
        txtPoints.setText(String.valueOf(SessionManager.getBalance(this)));

        // Load QR from Session with better error handling
        String qrUrl = SessionManager.getQrCode(this);
        Log.d(TAG, "QR URL from session: " + qrUrl);

        if (qrUrl != null && qrUrl.startsWith("http")) {
            Glide.with(this)
                    .load(qrUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imgQr);
        } else {
            Log.e(TAG, "Invalid QR URL: " + qrUrl);
        }


        loadQuickPrices();

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSelectFile.setOnClickListener(v -> pickFile());

        btnSubmit.setOnClickListener(v -> {
            String amountStr = edtPoints.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedFileUri == null) {
                Toast.makeText(this, "Upload payment proof", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadDeposit(Integer.parseInt(amountStr));
        });
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Accept images only for payment proof
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            String fileName = FileUtils.getFileName(this, selectedFileUri);
            txtFileName.setText(fileName != null ? fileName : "File selected");
        }
    }

    // ===========================
    // UPLOAD DEPOSIT (amount + file)
    // ===========================
    private void uploadDeposit(int amount) {
        progressDialog.show();

        RequestBody amountBody = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(amount)
        );

        File file = FileUtils.getFileFromUri(this, selectedFileUri);
        if (file == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody fileBody = RequestBody.create(
                MediaType.parse(getContentResolver().getType(selectedFileUri)),
                file
        );

        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("payment_proof", file.getName(), fileBody);

        apiService.depositAmount(
                "Bearer " + SessionManager.getToken(this),
                amountBody,
                filePart
        ).enqueue(new Callback<DepositResponse>() {
            @Override
            public void onResponse(Call<DepositResponse> call, Response<DepositResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null &&
                        response.body().getStatus_code() == 200) {

                    Toast.makeText(AddPointsActivity.this,
                            "Deposit Successful!", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(AddPointsActivity.this, HomeActivity.class));
                    finish();

                } else {
                    Toast.makeText(AddPointsActivity.this, "Deposit Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepositResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddPointsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuickPrices() {
        apiService.getPrices(
                "Bearer " + SessionManager.getToken(this),
                "application/json"
        ).enqueue(new Callback<PriceResponse>() {
            @Override
            public void onResponse(Call<PriceResponse> call, Response<PriceResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    gridPrices.removeAllViews();
                    LinearLayout currentRow = null;
                    int count = 0;

                    for (String price : response.body().data) {
                        if (count % 3 == 0) {
                            currentRow = new LinearLayout(AddPointsActivity.this);
                            currentRow.setOrientation(LinearLayout.HORIZONTAL);
                            currentRow.setWeightSum(3);
                            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            rowParams.bottomMargin = 12;
                            currentRow.setLayoutParams(rowParams);
                            gridPrices.addView(currentRow);
                        }

                        currentRow.addView(createPriceButton(price));
                        count++;
                    }
                } else {
                    Toast.makeText(AddPointsActivity.this, "Price list not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PriceResponse> call, Throwable t) {
                Toast.makeText(AddPointsActivity.this, "Failed to load prices", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MaterialButton createPriceButton(String amount) {
        MaterialButton btn = new MaterialButton(this);
        btn.setText("₹" + amount);
        btn.setAllCaps(false);
        btn.setTextSize(16);
        btn.setCornerRadius(14);
        btn.setStrokeWidth(2);
        btn.setStrokeColorResource(R.color.dark_blue);
        btn.setTextColor(getResources().getColor(R.color.dark_blue));
        btn.setBackgroundTintList(
                getResources().getColorStateList(android.R.color.white)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                );
        params.setMargins(6, 6, 6, 6);
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> edtPoints.setText(amount));

        return btn;
    }

    // =====================================================
    // BACK PRESS → HOME
    // =====================================================
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}