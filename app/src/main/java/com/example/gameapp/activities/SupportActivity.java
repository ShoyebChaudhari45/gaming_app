package com.example.gameapp.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.SupportResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    private static final String TAG = "SupportActivity";

    private MaterialCardView rowCall, rowWhatsapp, rowEmail, rowTelegram, rowProof;
    private TextView txtCallNumber, txtWhatsappNumber, txtEmail, txtNoData;
    private ProgressBar progressBar;

    private SupportResponse.SupportData supportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initViews();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        fetchSupportData();
    }

    private void initViews() {
        rowCall = findViewById(R.id.rowCall);
        rowWhatsapp = findViewById(R.id.rowWhatsapp);
        rowEmail = findViewById(R.id.rowEmail);
        rowTelegram = findViewById(R.id.rowTelegram);
        rowProof = findViewById(R.id.rowProof);

        txtCallNumber = findViewById(R.id.txtCallNumber);
        txtWhatsappNumber = findViewById(R.id.txtWhatsappNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtNoData = findViewById(R.id.txtNoData);

        progressBar = findViewById(R.id.progressBar);

        Log.d(TAG, "Views initialized successfully");
    }

    private void fetchSupportData() {
        progressBar.setVisibility(View.VISIBLE);
        hideAllRows();

        String token = "Bearer " + SessionManager.getToken(this);

        ApiClient.getClient()
                .create(ApiService.class)
                .getSupport(token)
                .enqueue(new Callback<SupportResponse>() {

                    @Override
                    public void onResponse(Call<SupportResponse> call,
                                           Response<SupportResponse> response) {

                        progressBar.setVisibility(View.GONE);

                        Log.d(TAG, "Response received: " + response.isSuccessful());

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isStatus()
                                && response.body().getData() != null) {

                            supportData = response.body().getData();
                            Log.d(TAG, "Support data received");
                            displaySupportOptions();

                        } else {
                            Log.e(TAG, "Invalid response or no data");
                            showNoDataMessage();
                        }
                    }

                    @Override
                    public void onFailure(Call<SupportResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "API call failed: " + t.getMessage());
                        Toast.makeText(SupportActivity.this,
                                "Failed to load support details",
                                Toast.LENGTH_SHORT).show();
                        showNoDataMessage();
                    }
                });
    }

    // ================= DYNAMIC UI =================

    private void displaySupportOptions() {
        Log.d(TAG, "displaySupportOptions called");
        SessionManager.saveSupportWhatsapp(SupportActivity.this, supportData.getWhatsappNo());


        boolean hasAnyData = false;

        // CALL
        if (supportData.hasValidContact()) {
            Log.d(TAG, "Setting up Call option: " + supportData.getContactNo());
            rowCall.setVisibility(View.VISIBLE);
            txtCallNumber.setText(supportData.getContactNo());

            // Get the LinearLayout inside the card and set click on it
            LinearLayout callLayout = (LinearLayout) rowCall.getChildAt(0);
            callLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Call clicked");
                    dialNumber(supportData.getContactNo());
                }
            });
            hasAnyData = true;
        }

        // WHATSAPP
        if (supportData.hasValidWhatsapp()) {
            Log.d(TAG, "Setting up WhatsApp option: " + supportData.getWhatsappNo());
            rowWhatsapp.setVisibility(View.VISIBLE);
            txtWhatsappNumber.setText(supportData.getWhatsappNo());

            LinearLayout whatsappLayout = (LinearLayout) rowWhatsapp.getChildAt(0);
            whatsappLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "WhatsApp clicked");
                    openWhatsApp(supportData.getWhatsappNo());
                }
            });
            hasAnyData = true;
        }

        // EMAIL
        if (supportData.hasValidEmail()) {
            Log.d(TAG, "Setting up Email option: " + supportData.getEmailId());
            rowEmail.setVisibility(View.VISIBLE);
            txtEmail.setText(supportData.getEmailId());

            LinearLayout emailLayout = (LinearLayout) rowEmail.getChildAt(0);
            emailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Email clicked");
                    sendEmail(supportData.getEmailId());
                }
            });
            hasAnyData = true;
        }

        // TELEGRAM
        if (supportData.hasValidTelegram()) {
            Log.d(TAG, "Setting up Telegram option");
            rowTelegram.setVisibility(View.VISIBLE);

            LinearLayout telegramLayout = (LinearLayout) rowTelegram.getChildAt(0);
            telegramLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Telegram clicked");
                    openLink(supportData.getTelegramLink());
                }
            });
            hasAnyData = true;
        }

        // PROOF
        if (supportData.hasValidProof()) {
            Log.d(TAG, "Setting up Proof option");
            rowProof.setVisibility(View.VISIBLE);

            LinearLayout proofLayout = (LinearLayout) rowProof.getChildAt(0);
            proofLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Proof clicked");
                    openLink(supportData.getProofLink());
                }
            });
            hasAnyData = true;
        }

        if (!hasAnyData) {
            Log.d(TAG, "No valid data found");
            showNoDataMessage();
        } else {
            Log.d(TAG, "Support options displayed successfully");
        }
    }

    private void hideAllRows() {
        rowCall.setVisibility(View.GONE);
        rowWhatsapp.setVisibility(View.GONE);
        rowEmail.setVisibility(View.GONE);
        rowTelegram.setVisibility(View.GONE);
        rowProof.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
    }

    private void showNoDataMessage() {
        hideAllRows();
        txtNoData.setVisibility(View.VISIBLE);
    }

    // ================= ACTIONS =================

    private void dialNumber(String number) {
        Log.d(TAG, "dialNumber called with: " + number);
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + number));
            startActivity(intent);
            Log.d(TAG, "Dialer opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error opening dialer: " + e.getMessage());
            Toast.makeText(this,
                    "Unable to open dialer",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openWhatsApp(String number) {
        Log.d(TAG, "openWhatsApp called with: " + number);
        try {
            // Remove all non-numeric characters except +
            String clean = number.replaceAll("[^0-9+]", "");

            // Remove leading + if present for wa.me link
            if (clean.startsWith("+")) {
                clean = clean.substring(1);
            }

            String url = "https://wa.me/" + clean;
            Log.d(TAG, "WhatsApp URL: " + url);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            Log.d(TAG, "WhatsApp opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error opening WhatsApp: " + e.getMessage());
            Toast.makeText(this,
                    "WhatsApp is not installed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(String email) {
        Log.d(TAG, "sendEmail called with: " + email);
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            intent.putExtra(Intent.EXTRA_TEXT, "");

            Intent chooser = Intent.createChooser(intent, "Send Email");

            if (chooser.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
                Log.d(TAG, "Email app opened successfully");
            } else {
                Log.e(TAG, "No email app found");
                Toast.makeText(this,
                        "No email app found. Please install an email app.",
                        Toast.LENGTH_LONG).show();
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "No email app available: " + e.getMessage());
            Toast.makeText(this,
                    "No email app found. Please install an email app.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error opening email: " + e.getMessage(), e);
            Toast.makeText(this,
                    "Unable to send email: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openLink(String link) {
        Log.d(TAG, "openLink called with: " + link);
        try {
            // Trim whitespace
            link = link.trim();

            // Ensure link has proper schema
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "https://" + link;
            }

            Log.d(TAG, "Final URL: " + link);

            // Create intent to open the link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));

            // Try to start the activity directly without checking resolveActivity
            try {
                startActivity(intent);
                Log.d(TAG, "Link opened successfully");
            } catch (ActivityNotFoundException e) {
                // If no app can handle it, show error
                Log.e(TAG, "No app can handle this link: " + e.getMessage());
                Toast.makeText(this,
                        "No app found to open this link. Please install a browser or the appropriate app.",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening link: " + e.getMessage(), e);
            Toast.makeText(this,
                    "Unable to open link: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Alternative method with chooser for better compatibility
    private void openLinkWithChooser(String link) {
        Log.d(TAG, "openLinkWithChooser called with: " + link);
        try {
            link = link.trim();

            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "https://" + link;
            }

            Log.d(TAG, "Final URL: " + link);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

            // Create chooser to let user select which app to use
            Intent chooser = Intent.createChooser(intent, "Open link with");

            try {
                startActivity(chooser);
                Log.d(TAG, "Link opened with chooser successfully");
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "No app available: " + e.getMessage());
                Toast.makeText(this,
                        "No app found to open this link",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening link: " + e.getMessage(), e);
            Toast.makeText(this,
                    "Unable to open link",
                    Toast.LENGTH_SHORT).show();
        }
    }
}