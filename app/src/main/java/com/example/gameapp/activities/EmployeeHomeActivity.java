package com.example.gameapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.SupportResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;
    private ImageButton btnMenu;

    private long lastBackPressedTime = 0;

    private TextView txtPlayerName, txtPlayerMobile, txtViewProfile;
    private TextView txtBalance;

    // Game input fields
    private EditText edtDigit, edtPoint;

    // Game buttons
    private MaterialButton btnJodi, btnOpen, btnCycle, btnPatte;
    private MaterialButton btnTp, btnSp, btnDp;

    // Keypad buttons
    private MaterialButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private MaterialButton btnDelete, btnSubmit;

    // Selected game tracking
    private MaterialButton selectedGameButton = null;
    private String selectedGameType = null;

    // Color states
    private int selectedColor;
    private int unselectedColor;

    // TextWatcher flag to prevent recursive calls
    private boolean isFormatting = false;

    private static final String TAG = "EmployeeHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Employee Only Access
        if (SessionManager.isCustomer(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_employee_home);

        // Initialize colors
        selectedColor = getResources().getColor(R.color.dark_blue);
        unselectedColor = getResources().getColor(R.color.dark_blue);

        initViews();
        setupDrawer();
        setupActionButtons();
        setupGameButtons();
        setupKeypad();
        setupDigitAutoFormat();
        loadEmployeeDetails();
        loadSupportData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SessionManager.isCustomer(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        updateBalanceUI();
    }

    private void initViews() {

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnMenu = findViewById(R.id.btnMenu);
        txtBalance = findViewById(R.id.txtBalance);

        // Header views
        View headerView = navigationView.getHeaderView(0);
        txtPlayerName = headerView.findViewById(R.id.txtPlayerName);
        txtPlayerMobile = headerView.findViewById(R.id.txtPlayerMobile);
        txtViewProfile = headerView.findViewById(R.id.txtviewprofile);

        // Game input fields
        edtDigit = findViewById(R.id.edtDigit);
        edtPoint = findViewById(R.id.edtPoint);

        // Game buttons
        btnJodi = findViewById(R.id.btnJodi);
        btnOpen = findViewById(R.id.btnOpen);
        btnCycle = findViewById(R.id.btnCycle);
        btnPatte = findViewById(R.id.btnPatte);
        btnTp = findViewById(R.id.btnTp);
        btnSp = findViewById(R.id.btnSp);
        btnDp = findViewById(R.id.btnDp);

        // Keypad buttons
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnDelete = findViewById(R.id.btnDelete);
        btnSubmit = findViewById(R.id.btnSubmit);

        swipeRefresh.setOnRefreshListener(() -> {
            loadEmployeeDetails();
            loadSupportData();
            swipeRefresh.setRefreshing(false);
        });

        txtViewProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Make EditTexts non-keyboard input
        edtDigit.setShowSoftInputOnFocus(false);
        edtPoint.setShowSoftInputOnFocus(false);

        // Request focus on digit field initially
        edtDigit.requestFocus();
    }

    private void setupDrawer() {

        btnMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        navigationView.setNavigationItemSelectedListener(item -> {

            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();

            if (id == R.id.nav_home) return true;

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_add_funds) {
                startActivity(new Intent(this, AddPointsActivity.class));
            } else if (id == R.id.nav_withdraw) {
                startActivity(new Intent(this, WithdrawActivity.class));
            } else if (id == R.id.nav_wallet) {
                startActivity(new Intent(this, WalletStatementActivity.class));
            } else if (id == R.id.nav_bid_history) {
                startActivity(new Intent(this, BidHistoryActivity.class));
            } else if (id == R.id.nav_game_rates) {
                startActivity(new Intent(this, GameRatesActivity.class));
            } else if (id == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));
            } else if (id == R.id.nav_change_password) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else if (id == R.id.nav_share) {
                shareApp();
            } else if (id == R.id.nav_panel_access) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://lottery.durwankurgroup.com/panel/login"));
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                SessionManager.logout(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            return true;
        });
    }

    private void setupActionButtons() {

        findViewById(R.id.btnAddPoints).setOnClickListener(v ->
                startActivity(new Intent(this, AddPointsActivity.class))
        );

        findViewById(R.id.btnWithdraw).setOnClickListener(v ->
                startActivity(new Intent(this, WithdrawActivity.class))
        );
    }

    private void setupGameButtons() {

        btnJodi.setOnClickListener(v -> selectGameButton(btnJodi, "Jodi"));
        btnOpen.setOnClickListener(v -> selectGameButton(btnOpen, "Open"));
        btnCycle.setOnClickListener(v -> selectGameButton(btnCycle, "Cycle"));
        btnPatte.setOnClickListener(v -> selectGameButton(btnPatte, "Patte"));
        btnTp.setOnClickListener(v -> selectGameButton(btnTp, "TP"));
        btnSp.setOnClickListener(v -> selectGameButton(btnSp, "SP"));
        btnDp.setOnClickListener(v -> selectGameButton(btnDp, "DP"));
    }

    /**
     * Handles game button selection with visual feedback.
     * Only one game can be selected at a time.
     */
    private void selectGameButton(MaterialButton clickedButton, String gameType) {

        // If clicking the already selected button, do nothing (use Submit button instead)
        if (selectedGameButton == clickedButton) {
            return;
        }

        // Deselect previously selected button
        if (selectedGameButton != null) {
            selectedGameButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(unselectedColor)
            );
            selectedGameButton.setStrokeWidth(0);
        }

        // Select the new button with visual feedback
        clickedButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        getResources().getColor(android.R.color.holo_green_dark)
                )
        );
        clickedButton.setStrokeColor(
                android.content.res.ColorStateList.valueOf(
                        getResources().getColor(android.R.color.white)
                )
        );
        clickedButton.setStrokeWidth(4);

        // Update selection tracking
        selectedGameButton = clickedButton;
        selectedGameType = gameType;

        // Clear inputs when changing game type
        edtDigit.setText("");
        edtPoint.setText("");
        edtDigit.requestFocus();

        toast("Selected: " + gameType);
        Log.d(TAG, "Game type selected: " + gameType);
    }

    /**
     * Process the game entry when a selected game button is clicked again
     */
    private void processGameEntry() {
        if (selectedGameType == null) {
            toast("Please select a game type first");
            return;
        }

        String digit = cleanLastEquals(edtDigit.getText().toString().trim());
        String point = edtPoint.getText().toString().trim();

        if (digit.isEmpty()) {
            toast("Please enter digit");
            edtDigit.requestFocus();
            return;
        }

        if (point.isEmpty()) {
            toast("Please enter point");
            edtPoint.requestFocus();
            return;
        }

        // Validate digit format
        if (!isValidDigitFormat(digit)) {
            toast("Invalid digit pattern for " + selectedGameType);
            return;
        }

        int pointValue;
        try {
            pointValue = Integer.parseInt(point);
        } catch (Exception e) {
            toast("Invalid points");
            return;
        }

        if (pointValue <= 0) {
            toast("Points must be greater than 0");
            return;
        }

        // Show confirmation
        showConfirmationDialog(digit, pointValue, selectedGameType);
    }

    /**
     * Validate digit format based on game type
     */
    private boolean isValidDigitFormat(String digits) {

        // Remove "=" for clean checking
        String clean = digits.replace("=", "");

        // Only digits allowed
        if (!clean.matches("[0-9]+")) {
            return false;
        }

        // JODI → 2-digit blocks
        if (selectedGameType.equalsIgnoreCase("Jodi")) {
            return clean.length() % 2 == 0;
        }

        // OPEN / SP / DP / CYCLE → always valid (1 digit each)
        if (selectedGameType.equalsIgnoreCase("Open") ||
                selectedGameType.equalsIgnoreCase("SP") ||
                selectedGameType.equalsIgnoreCase("DP") ||
                selectedGameType.equalsIgnoreCase("Cycle")) {
            return clean.length() > 0;
        }

        // PATTE / TP → 3-digit blocks
        if (selectedGameType.equalsIgnoreCase("Patte") ||
                selectedGameType.equalsIgnoreCase("TP")) {
            return clean.length() % 3 == 0;
        }

        return true;
    }

    /**
     * Setup auto-formatting for digit input based on game type
     */
    private void setupDigitAutoFormat() {

        edtDigit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {

                if (isFormatting || selectedGameType == null) return;
                isFormatting = true;

                String input = editable.toString();

                // Remove old "=" and spaces and non-digits
                String clean = input.replace("=", "")
                        .replace(" ", "")
                        .replaceAll("[^0-9]", "");

                StringBuilder formatted = new StringBuilder();

                int blockSize;

                if (selectedGameType.equalsIgnoreCase("Jodi")) {
                    blockSize = 2;
                } else if (selectedGameType.equalsIgnoreCase("Open")
                        || selectedGameType.equalsIgnoreCase("SP")
                        || selectedGameType.equalsIgnoreCase("DP")
                        || selectedGameType.equalsIgnoreCase("Cycle")) {
                    blockSize = 1;
                } else if (selectedGameType.equalsIgnoreCase("Patte")
                        || selectedGameType.equalsIgnoreCase("TP")) {
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

                edtDigit.setText(formatted.toString());
                edtDigit.setSelection(edtDigit.getText().length());

                isFormatting = false;
            }

        });
    }

    private void setupKeypad() {

        View.OnClickListener numberClickListener = v -> {
            MaterialButton btn = (MaterialButton) v;
            String number = btn.getText().toString();
            appendToActiveField(number);
        };

        btn0.setOnClickListener(numberClickListener);
        btn1.setOnClickListener(numberClickListener);
        btn2.setOnClickListener(numberClickListener);
        btn3.setOnClickListener(numberClickListener);
        btn4.setOnClickListener(numberClickListener);
        btn5.setOnClickListener(numberClickListener);
        btn6.setOnClickListener(numberClickListener);
        btn7.setOnClickListener(numberClickListener);
        btn8.setOnClickListener(numberClickListener);
        btn9.setOnClickListener(numberClickListener);

        btnDelete.setOnClickListener(v -> deleteFromActiveField());

        btnSubmit.setOnClickListener(v -> processGameEntry());
    }

    private void appendToActiveField(String number) {
        EditText activeField = getCurrentFocus() instanceof EditText ?
                (EditText) getCurrentFocus() : edtDigit;

        if (activeField == null) {
            activeField = edtDigit;
        }

        String current = activeField.getText().toString();
        activeField.setText(current + number);
        activeField.setSelection(activeField.getText().length());
    }

    private void deleteFromActiveField() {
        EditText activeField = getCurrentFocus() instanceof EditText ?
                (EditText) getCurrentFocus() : edtDigit;

        if (activeField == null) {
            activeField = edtDigit;
        }

        String current = activeField.getText().toString();
        if (current.length() > 0) {
            activeField.setText(current.substring(0, current.length() - 1));
            activeField.setSelection(activeField.getText().length());
        }
    }

    private String cleanLastEquals(String digits) {
        if (digits.endsWith("=")) {
            return digits.substring(0, digits.length() - 1);
        }
        return digits;
    }

    private void showConfirmationDialog(String digit, int price, String type) {

        String message =
                "Game Type: " + type +
                        "\nDigit: " + digit +
                        "\nPoint: " + price;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Entry")
                .setMessage(message)
                .setPositiveButton("Confirm",
                        (dialog, which) -> submitEntry(digit, price, type))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitEntry(String digit, int price, String type) {
        // TODO: Implement actual game submission logic
        toast("Entry Submitted: " + type + " - " + digit + " - " + price);

        // Clear fields after submission
        edtDigit.setText("");
        edtPoint.setText("");
        edtDigit.requestFocus();

        // Clear selection
        if (selectedGameButton != null) {
            selectedGameButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(unselectedColor)
            );
            selectedGameButton.setStrokeWidth(0);
        }
        selectedGameButton = null;
        selectedGameType = null;

        Log.d(TAG, "Game Entry - Type: " + type + ", Digit: " + digit + ", Point: " + price);
    }

    private void loadSupportData() {
        String token = "Bearer " + SessionManager.getToken(this);

        ApiClient.getClient()
                .create(ApiService.class)
                .getSupport(token)
                .enqueue(new Callback<SupportResponse>() {

                    @Override
                    public void onResponse(Call<SupportResponse> call,
                                           Response<SupportResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isStatus()
                                && response.body().getData() != null) {

                            SupportResponse.SupportData supportData = response.body().getData();

                            if (supportData.hasValidWhatsapp()) {
                                SessionManager.saveSupportWhatsapp(EmployeeHomeActivity.this,
                                        supportData.getWhatsappNo());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SupportResponse> call, Throwable t) {
                        Log.e(TAG, "Support API failed: " + t.getMessage());
                    }
                });
    }

    private void loadEmployeeDetails() {

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

                            UserDetailsResponse.User user = response.body().data;

                            txtPlayerName.setText(user.name);
                            txtPlayerMobile.setText(user.mobileNo);

                            // Save and display balance
                            SessionManager.saveBalance(EmployeeHomeActivity.this, user.balance);
                            updateBalanceUI();

                            SessionManager.saveUserName(EmployeeHomeActivity.this, user.name);
                            SessionManager.saveUserMobile(EmployeeHomeActivity.this, user.mobileNo);
                            SessionManager.saveEmail(EmployeeHomeActivity.this, user.email);

                            // Save QR code if available
                            String qr = user.qrCode;
                            if (qr != null && !qr.isEmpty()) {
                                if (!qr.startsWith("http")) {
                                    qr = "https://lottery.durwankurgroup.com/" + qr;
                                }
                                SessionManager.saveQrCode(EmployeeHomeActivity.this, qr);
                            }

                            Log.d(TAG, "Employee loaded: "
                                    + new Gson().toJson(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        toast("Failed to load details");
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    private void updateBalanceUI() {
        int balance = SessionManager.getBalance(this);
        txtBalance.setText(String.valueOf(balance));
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,
                "Download the app:\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(i, "Share App"));
    }

    private void openWhatsApp(String number) {
        try {
            String clean = number.replaceAll("[^0-9+]", "");
            if (clean.startsWith("+")) clean = clean.substring(1);

            String url = "https://wa.me/" + clean;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        long time = System.currentTimeMillis();

        if (time - lastBackPressedTime < 2000) {
            finishAffinity();
        } else {
            lastBackPressedTime = time;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}