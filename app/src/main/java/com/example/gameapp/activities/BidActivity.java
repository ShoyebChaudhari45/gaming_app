package com.example.gameapp.activities;

import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;

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
    }

    private void setupUI() {
        txtTitle.setText(gameType);
        txtBalance.setText(String.valueOf(SessionManager.getBalance(this)));
        txtCurrentDate.setText(getCurrentDateFormatted());

        if (gameImage != null)
            Glide.with(this).load(gameImage).placeholder(R.drawable.ic_placeholder).into(imgGameType);

        setupDigitAutoFormat();

        String openStatus = getIntent().getStringExtra("open_status");
        String closeStatus = getIntent().getStringExtra("close_status");

        boolean isOpenAvailable = openStatus != null &&
                (openStatus.equalsIgnoreCase("running")
                        || openStatus.equalsIgnoreCase("open")
                        || openStatus.equalsIgnoreCase("upcoming"));

        boolean isCloseAvailable = closeStatus != null &&
                (closeStatus.equalsIgnoreCase("running")
                        || closeStatus.equalsIgnoreCase("open")
                        || closeStatus.equalsIgnoreCase("upcoming"));

        if (isOpenAvailable) {
            isOpenSelected = true;
            updateOpenCloseSelection(true);
        } else if (isCloseAvailable) {
            isOpenSelected = false;
            updateOpenCloseSelection(false);
        }

        if (!isOpenAvailable) {
            cardOpen.setEnabled(false);
            cardOpen.setAlpha(0.4f);
            btnOpen.setEnabled(false);
        } else {
            cardOpen.setEnabled(true);
            cardOpen.setAlpha(1f);
            btnOpen.setEnabled(true);
        }

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

    // =====================================================
    //               🔥 VALIDATION ADDED HERE 🔥
    // =====================================================

    private boolean isValidDigitFormat(String digits) {

        // Remove "=" for clean checking
        String clean = digits.replace("=", "");

        // ❌ Only digits allowed
        if (!clean.matches("[0-9]+")) {
            return false;
        }

        // JODI → 2-digit blocks
        if (gameType.equalsIgnoreCase("Jodi")) {
            return clean.length() % 2 == 0;
        }

        // OPEN / SP / DP / CYCLE → always valid (1 digit each)
        if (gameType.equalsIgnoreCase("Open") ||
                gameType.equalsIgnoreCase("SP") ||
                gameType.equalsIgnoreCase("DP") ||
                gameType.equalsIgnoreCase("Cycle")) {
            return clean.length() > 0; // atleast 1
        }

        // PATTE / TRIPPLE PANNA → 3-digit blocks
        if (gameType.equalsIgnoreCase("Patte") ||
                gameType.equalsIgnoreCase("TP") ||
                gameType.equalsIgnoreCase("Tripple Panna") ||
                gameType.equalsIgnoreCase("Triple panna")) {
            return clean.length() % 3 == 0;
        }

        return true;
    }

    private void validateAndConfirmBid() {

        int selectedTapId = isOpenSelected ? openId : closeId;

        if (selectedTapId == -1) {
            toast(isOpenSelected ? "Open session not available" : "Close session not available");
            return;
        }

        String digits = cleanLastEquals(etDigits.getText().toString().trim());
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

        // 🔥 NEW VALIDATION — strictly checks the digit pattern
        if (!isValidDigitFormat(digits)) {
            toast("Invalid digit pattern for " + gameType);
            return;
        }

        String apiType = toTitleCase(gameType);

        showConfirmationDialog(digits, points, apiType);
    }


    // =====================================================
    //               🔥 AUTO-FORMAT LOGIC SAME 🔥
    // =====================================================

    private void setupDigitAutoFormat() {

        etDigits.addTextChangedListener(new TextWatcher() {

            boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {

                if (isEditing) return;
                isEditing = true;

                String input = editable.toString();

                // remove old "=" and SPACES and non-digits
                String clean = input.replace("=", "")
                        .replace(" ", "")
                        .replaceAll("[^0-9]", "");

                StringBuilder formatted = new StringBuilder();

                int blockSize;

                if (gameType.equalsIgnoreCase("Jodi")) {
                    blockSize = 2;
                } else if (gameType.equalsIgnoreCase("Open")
                        || gameType.equalsIgnoreCase("SP")
                        || gameType.equalsIgnoreCase("DP")
                        || gameType.equalsIgnoreCase("Cycle")) {
                    blockSize = 1;
                } else if (gameType.equalsIgnoreCase("Patte")
                        || gameType.equalsIgnoreCase("TP")
                        || gameType.equalsIgnoreCase("Tripple Panna")
                        || gameType.equalsIgnoreCase("Triple panna")) {
                    blockSize = 3;
                } else {
                    blockSize = clean.length();
                }

                // Build formatted text
                for (int i = 0; i < clean.length(); i++) {

                    formatted.append(clean.charAt(i));

                    boolean shouldInsert = (i + 1) % blockSize == 0;

                    if (shouldInsert && (i + 1) < clean.length()) {
                        formatted.append("=");
                    }
                }

                etDigits.setText(formatted.toString());
                etDigits.setSelection(etDigits.getText().length());

                isEditing = false;
            }

        });
    }



    // =====================================================
    //          🔥 REMAINING CODE UNTOUCHED 🔥
    // =====================================================

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        if (input.equalsIgnoreCase("SP") || input.equalsIgnoreCase("DP"))
            return input.toUpperCase();

        return input.substring(0, 1).toUpperCase() +
                input.substring(1).toLowerCase();
    }

    private void showConfirmationDialog(String digit, int price, String type) {

        int timeId = isOpenSelected ? openId : closeId;

        String message =
                "time_id: " + timeId +
                        "\ntype: " + type +
                        "\ndigit: " + digit +
                        "\nprice: " + price;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Bid")
                .setMessage(message)
                .setPositiveButton("Confirm",
                        (dialog, which) -> submitBid(digit, price, type))
                .setNegativeButton("Cancel", null)
                .show();
    }
    private String cleanLastEquals(String digits) {
        if (digits.endsWith("=")) {
            return digits.substring(0, digits.length() - 1);
        }
        return digits;
    }

    private void submitBid(String digit, int price, String type) {

        int timeId = isOpenSelected ? openId : closeId;

        LotteryRateRequest request = new LotteryRateRequest(
                timeId, type, digit, price
        );

        Log.e("FINAL_REQUEST", new Gson().toJson(request));

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
                                String errorJson = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : "Unknown error";

                                String cleanMsg = "Bid failed";

                                if (errorJson.contains("\"message\"")) {
                                    int start = errorJson.indexOf("\"message\"") + 11;
                                    int end = errorJson.indexOf("\"", start);
                                    if (end > start) {
                                        cleanMsg = errorJson.substring(start, end);
                                    }
                                }

                                toast(cleanMsg);

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

                        showSuccessDialogWithSound(successMessage);
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        showSuccessDialogWithSound(successMessage);
                    }
                });
    }

    private void showSuccessDialogWithSound(String message) {
        try {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.success_sound);
            mp.start();
            mp.setOnCompletionListener(MediaPlayer::release);
        } catch (Exception e) {
            Log.e(TAG, "Sound error: " + e.getMessage());
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        TextView txtMessage = dialogView.findViewById(R.id.txtSuccessMessage);
        txtMessage.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                finish();
            }
        }, 2000);
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
