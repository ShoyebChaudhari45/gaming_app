package com.example.gameapp.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import com.bumptech.glide.Glide;
import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.LotteryRateRequest;
import com.example.gameapp.models.response.LotteryRateResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidActivity extends AppCompatActivity {

    private static final String TAG = "BidActivity";

    private int openId = -1;
    private int closeId = -1;
    private String openStatus, closeStatus;

    private TextView txtTitle, txtBalance, txtCurrentDate;
    private ImageButton btnBack;
    private ImageView imgGameType;
    private EditText etDigits, etPoints;

    private RadioButton btnOpen, btnClose;
    private MaterialButton btnProceed;
    private MaterialCardView cardOpen, cardClose;

    private final int COLOR_BLUE = R.color.dark_blue;
    private final int COLOR_GRAY = R.color.textSecondary;

    private String gameName, gameType, gameImage;
    private boolean isOpenSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid);

        getIntentData();
        initViews();
        setupUI();
        setupClickListeners();
    }

    private void getIntentData() {
        gameName = getIntent().getStringExtra("game_name");
        gameType = getIntent().getStringExtra("game_type");
        gameImage = getIntent().getStringExtra("game_image");

        openId = getIntent().getIntExtra("open_id", -1);
        closeId = getIntent().getIntExtra("close_id", -1);
        openStatus = getIntent().getStringExtra("open_status");
        closeStatus = getIntent().getStringExtra("close_status");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtTitle = findViewById(R.id.txtTitle);
        txtBalance = findViewById(R.id.txtBalance);
        txtCurrentDate = findViewById(R.id.txtCurrentDate);
        imgGameType = findViewById(R.id.imgGameType);
        btnOpen = findViewById(R.id.btnOpen);
        btnClose = findViewById(R.id.btnClose);
        cardOpen = findViewById(R.id.cardOpen);
        cardClose = findViewById(R.id.cardClose);
        etPoints = findViewById(R.id.etPoints);
        btnProceed = findViewById(R.id.btnProceed);
        etDigits = findViewById(R.id.etDigits);

        etDigits.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(20) // allow everything up to 20 characters
        });

    }

    private void setupUI() {
        txtTitle.setText(gameType);
        txtBalance.setText(String.valueOf(SessionManager.getBalance(this)));
        txtCurrentDate.setText(getCurrentDateFormatted());

        if (gameImage != null)
            Glide.with(this).load(gameImage).placeholder(R.drawable.ic_placeholder).into(imgGameType);

        // ⭐ GET STATUS FROM INTENT
        String openStatus = getIntent().getStringExtra("open_status");
        String closeStatus = getIntent().getStringExtra("close_status");

        // ⭐ CHECK VALID STATUSES
        boolean isOpenAvailable = openStatus != null &&
                (openStatus.equalsIgnoreCase("running") ||
                        openStatus.equalsIgnoreCase("open") ||
                        openStatus.equalsIgnoreCase("upcoming"));

        boolean isCloseAvailable = closeStatus != null &&
                (closeStatus.equalsIgnoreCase("running") ||
                        closeStatus.equalsIgnoreCase("open") ||
                        closeStatus.equalsIgnoreCase("upcoming"));

        // ⭐ DEFAULT SELECTION (open priority)
        if (isOpenAvailable) {
            isOpenSelected = true;
            updateOpenCloseSelection(true);
        } else if (isCloseAvailable) {
            isOpenSelected = false;
            updateOpenCloseSelection(false);
        }

        // ⭐ DISABLE OPEN IF NOT AVAILABLE
        if (!isOpenAvailable) {
            cardOpen.setEnabled(false);
            cardOpen.setAlpha(0.4f);
            btnOpen.setEnabled(false);
        } else {
            cardOpen.setEnabled(true);
            cardOpen.setAlpha(1f);
            btnOpen.setEnabled(true);
        }

        // ⭐ DISABLE CLOSE IF NOT AVAILABLE
        if (!isCloseAvailable) {
            cardClose.setEnabled(false);
            cardClose.setAlpha(0.4f);
            btnClose.setEnabled(false);
        } else {
            cardClose.setEnabled(true);
            cardClose.setAlpha(1f);
            btnClose.setEnabled(true);
        }
    }


    private void setupClickListeners() {

        btnBack.setOnClickListener(v -> finish());

        // Card click
        cardOpen.setOnClickListener(v -> {
            if (openId != -1) {
                isOpenSelected = true;
                updateOpenCloseSelection(true);
            }
        });

        cardClose.setOnClickListener(v -> {
            if (closeId != -1) {
                isOpenSelected = false;
                updateOpenCloseSelection(false);
            }
        });

        // Radio click FIX 🔥
        btnOpen.setOnClickListener(v -> {
            if (openId != -1) {
                isOpenSelected = true;
                updateOpenCloseSelection(true);
            }
        });

        btnClose.setOnClickListener(v -> {
            if (closeId != -1) {
                isOpenSelected = false;
                updateOpenCloseSelection(false);
            }
        });

        btnProceed.setOnClickListener(v -> validateAndConfirmBid());
    }

    private void updateOpenCloseSelection(boolean isOpen) {

        if (isOpen) {
            cardOpen.setCardBackgroundColor(getColor(COLOR_BLUE));
            cardOpen.setCardElevation(4f);
            btnOpen.setChecked(true);
            btnOpen.setTextColor(getColor(android.R.color.white));

            cardClose.setCardBackgroundColor(getColor(android.R.color.white));
            cardClose.setCardElevation(0f);
            btnClose.setChecked(false);
            btnClose.setTextColor(getColor(COLOR_GRAY));

        } else {
            cardClose.setCardBackgroundColor(getColor(COLOR_BLUE));
            cardClose.setCardElevation(4f);
            btnClose.setChecked(true);
            btnClose.setTextColor(getColor(android.R.color.white));

            cardOpen.setCardBackgroundColor(getColor(android.R.color.white));
            cardOpen.setCardElevation(0f);
            btnOpen.setChecked(false);
            btnOpen.setTextColor(getColor(COLOR_GRAY));
        }

        CompoundButtonCompat.setButtonTintList(
                btnOpen, ColorStateList.valueOf(getColor(android.R.color.white)));
        CompoundButtonCompat.setButtonTintList(
                btnClose, ColorStateList.valueOf(getColor(android.R.color.white)));
    }

    private void validateAndConfirmBid() {

        int selectedTapId = isOpenSelected ? openId : closeId;

        if (selectedTapId == -1) {
            toast(isOpenSelected ? "Open session not available" : "Close session not available");
            return;
        }

        String digits = etDigits.getText().toString().trim();
        String pointsStr = etPoints.getText().toString().trim();

        if (digits.isEmpty()) {
            toast("Enter digits");
            return;
        }
        if (pointsStr.isEmpty()) {
            toast("Enter points");
            return;
        }

        int points;
        try {
            points = Integer.parseInt(pointsStr);
        } catch (Exception e) {
            toast("Invalid points");
            return;
        }

        if (points <= 0) {
            toast("Points must be greater than 0");
            return;
        }

        showConfirmationDialog(digits, points, gameType);
    }

    private void showConfirmationDialog(String digits, int points, String type) {

        new AlertDialog.Builder(this)
                .setTitle("Confirm Bid")
                .setMessage(
                        "Game: " + gameName +
                                "\nType: " + type +
                                "\nDigits: " + digits +
                                "\nPoints: " + points +
                                "\nSession: " + (isOpenSelected ? "OPEN" : "CLOSE")
                )
                .setPositiveButton("Confirm",
                        (dialog, which) -> submitBid(digits, points, type))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitBid(String digits, int points, String type) {

        int selectedTapId = isOpenSelected ? openId : closeId;

        LotteryRateRequest request = new LotteryRateRequest(
                selectedTapId,
                type,
                digits,
                points
        );

        ApiClient.getClient()
                .create(ApiService.class)
                .placeBid(
                        "Bearer " + SessionManager.getToken(this),
                        "application/json",
                        request
                )
                .enqueue(new Callback<LotteryRateResponse>() {

                    @Override
                    public void onResponse(Call<LotteryRateResponse> call,
                                           Response<LotteryRateResponse> resp) {

                        if (resp.isSuccessful() && resp.body() != null) {
                            refreshWalletBalance(resp.body().getMessage());
                        } else {
                            try {
                                String error = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : "Unknown error";
                                toast("Bid failed: " + error);
                            } catch (Exception e) {
                                toast("Bid failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LotteryRateResponse> call, Throwable t) {
                        toast("Network error: " + t.getMessage());
                    }
                });
    }

    private void refreshWalletBalance(String successMessage) {

        ApiClient.getClient()
                .create(ApiService.class)
                .getUserDetails(
                        "Bearer " + SessionManager.getToken(this),
                        "application/json"
                )
                .enqueue(new Callback<UserDetailsResponse>() {

                    @Override
                    public void onResponse(Call<UserDetailsResponse> call,
                                           Response<UserDetailsResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().data != null) {

                            int newBalance = response.body().data.balance;
                            SessionManager.saveBalance(BidActivity.this, newBalance);
                            txtBalance.setText(String.valueOf(newBalance));
                        }

                        showSuccessDialog(successMessage);
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        showSuccessDialog(successMessage);
                    }
                });
    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("✅ Bid Success")
                .setMessage(message)
                .setPositiveButton("OK", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    private String getCurrentDateFormatted() {
        return new SimpleDateFormat(
                "EEE dd-MMM-yyyy",
                Locale.ENGLISH
        ).format(Calendar.getInstance().getTime());
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
