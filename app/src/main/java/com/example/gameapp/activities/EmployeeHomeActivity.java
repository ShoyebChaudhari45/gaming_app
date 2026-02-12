package com.example.gameapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.Adapters.EmployeeGameTypeAdapter;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.GameType;
import com.example.gameapp.models.response.GamesResponse;
import com.example.gameapp.models.response.SupportResponse;
import com.example.gameapp.models.response.UserDetailsResponse;
import com.example.gameapp.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private ScrollView mainScrollView;

    private long lastBackPressedTime = 0;

    private TextView txtPlayerName, txtPlayerMobile, txtViewProfile;
    private TextView txtBalance;

    // Game types
    private RecyclerView rvGameTypes;
    private List<GameType> gameTypesList = new ArrayList<>();
    private EmployeeGameTypeAdapter gameTypeAdapter;

    // Game input fields
    private EditText edtDigit, edtPoint;

    // Keypad buttons
    private MaterialButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private MaterialButton btnDelete, btnClear, btnSubmit;

    // Selected game tracking
    private GameType selectedGameType = null;
    private int selectedPosition = -1;

    // Focus tracking
    private EditText focusedField = null;

    // TextWatcher to prevent memory leak
    private TextWatcher currentDigitWatcher = null;

    // TextWatcher flag to prevent recursive calls
    private boolean isFormatting = false;

    private static final String TAG = "EmployeeHomeActivity";

    // State preservation keys
    private static final String KEY_SELECTED_POSITION = "selected_position";
    private static final String KEY_DIGIT_TEXT = "digit_text";
    private static final String KEY_POINT_TEXT = "point_text";
    private static final String KEY_SELECTED_GAME_NAME = "selected_game_name";
    private static final String KEY_SCROLL_Y = "scroll_y";

    // Flag to track if we need to restore state
    private boolean needsStateRestoration = false;
    private Bundle pendingState = null;

    // Flag to prevent multiple restorations
    private boolean isRestoring = false;

    // Handler for delayed restoration
    private Handler handler = new Handler();

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

        initViews();
        setupDrawer();
        setupActionButtons();
        setupGameTypesRecyclerView();
        setupKeypad();
        setupAutoSave();
        loadEmployeeDetails();
        loadSupportData();
        loadGameTypes();

        // Restore state if available
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        // Force UI refresh
        refreshUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current state
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition);
        outState.putString(KEY_DIGIT_TEXT, edtDigit.getText().toString());
        outState.putString(KEY_POINT_TEXT, edtPoint.getText().toString());

        // Save scroll position
        if (mainScrollView != null) {
            outState.putInt(KEY_SCROLL_Y, mainScrollView.getScrollY());
        }

        // Also save the game type name for verification
        if (selectedGameType != null) {
            outState.putString(KEY_SELECTED_GAME_NAME, selectedGameType.getName());
        }

        Log.d(TAG, "State saved: position=" + selectedPosition +
                ", digit=" + edtDigit.getText() + ", point=" + edtPoint.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the scroll position and selected state immediately
        if (rvGameTypes != null && gameTypeAdapter != null) {
            GridLayoutManager layoutManager = (GridLayoutManager) rvGameTypes.getLayoutManager();
            if (layoutManager != null) {
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                SessionManager.saveGameTypesScrollPosition(this, firstVisiblePosition);
            }
            SessionManager.saveSelectedGamePosition(this, selectedPosition);
        }

        // Save input fields
        SessionManager.saveBidInputs(this,
                edtDigit.getText().toString(),
                edtPoint.getText().toString());

        // Save scroll position
        if (mainScrollView != null) {
            SessionManager.saveScrollPosition(this, mainScrollView.getScrollY());
        }
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

        // CRITICAL FIX: Delay restoration to ensure views are ready
        handler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                performFullStateRestoration();
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handlers
        handler.removeCallbacksAndMessages(null);

        // Clean up TextWatcher to prevent memory leak
        if (currentDigitWatcher != null) {
            edtDigit.removeTextChangedListener(currentDigitWatcher);
            currentDigitWatcher = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Force immediate redraw with delay
        handler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                refreshUI();
            }
        }, 50);
    }

    private void refreshUI() {
        if (rvGameTypes != null && gameTypeAdapter != null) {
            rvGameTypes.post(() -> {
                gameTypeAdapter.notifyDataSetChanged();
                if (selectedPosition != -1) {
                    gameTypeAdapter.setSelectedPosition(selectedPosition);
                }
                // Force layout refresh
                rvGameTypes.invalidate();
                rvGameTypes.requestLayout();
            });
        }

        // Refresh balance
        updateBalanceUI();
    }

    private void performFullStateRestoration() {
        if (isRestoring) return;
        isRestoring = true;

        try {
            // Restore from SharedPreferences
            if (gameTypeAdapter != null && !gameTypesList.isEmpty()) {
                int savedPosition = SessionManager.getSelectedGamePosition(this, -1);
                if (savedPosition >= 0 && savedPosition < gameTypesList.size()) {
                    selectedPosition = savedPosition;
                    selectedGameType = gameTypesList.get(savedPosition);
                    gameTypeAdapter.setSelectedPosition(savedPosition);

                    // Enable inputs
                    edtDigit.setEnabled(true);
                    edtPoint.setEnabled(true);

                    // Restore text fields
                    String savedDigit = SessionManager.getSavedDigit(this);
                    String savedPoint = SessionManager.getSavedPoint(this);
                    if (!savedDigit.isEmpty()) {
                        edtDigit.setText(savedDigit);
                    }
                    if (!savedPoint.isEmpty()) {
                        edtPoint.setText(savedPoint);
                    }

                    setupDigitAutoFormat();
                }

                // Restore scroll position
                int scrollPosition = SessionManager.getGameTypesScrollPosition(this, 0);
                rvGameTypes.scrollToPosition(scrollPosition);
            }

            // Restore main scroll position
            int savedScrollY = SessionManager.getScrollPosition(this, 0);
            if (savedScrollY > 0 && mainScrollView != null) {
                mainScrollView.post(() -> mainScrollView.scrollTo(0, savedScrollY));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error restoring state: " + e.getMessage());
        }

        isRestoring = false;
    }

    private void restoreInstanceState(Bundle savedState) {
        // Store the state for later restoration after API loads
        pendingState = savedState;
        needsStateRestoration = true;

        Log.d(TAG, "Marking state for restoration after data loads");
    }

    private void performStateRestoration() {
        if (!needsStateRestoration || pendingState == null || isRestoring) {
            return;
        }
        isRestoring = true;

        try {
            final int savedPosition = pendingState.getInt(KEY_SELECTED_POSITION, -1);
            final String digitText = pendingState.getString(KEY_DIGIT_TEXT, "");
            final String pointText = pendingState.getString(KEY_POINT_TEXT, "");
            final String gameName = pendingState.getString(KEY_SELECTED_GAME_NAME, "");
            final int savedScrollY = pendingState.getInt(KEY_SCROLL_Y, 0);

            Log.d(TAG, "Performing state restoration: position=" + savedPosition +
                    ", game=" + gameName + ", digit=" + digitText + ", point=" + pointText);

            if (savedPosition >= 0 && savedPosition < gameTypesList.size()) {
                // Verify the game type matches
                GameType gameType = gameTypesList.get(savedPosition);
                if (gameName.isEmpty() || gameType.getName().equals(gameName)) {
                    // Restore selection
                    selectedPosition = savedPosition;
                    selectedGameType = gameType;
                    gameTypeAdapter.setSelectedPosition(savedPosition);

                    // Enable inputs
                    edtDigit.setEnabled(true);
                    edtPoint.setEnabled(true);

                    // Restore text
                    if (!digitText.isEmpty()) {
                        edtDigit.setText(digitText);
                    }
                    if (!pointText.isEmpty()) {
                        edtPoint.setText(pointText);
                    }

                    // Setup auto-format
                    setupDigitAutoFormat();

                    Log.d(TAG, "State restoration complete");
                }
            } else if (!digitText.isEmpty() || !pointText.isEmpty()) {
                // Just restore text without selection
                edtDigit.setText(digitText);
                edtPoint.setText(pointText);
            }

            // Restore scroll position
            if (savedScrollY > 0 && mainScrollView != null) {
                mainScrollView.post(() -> mainScrollView.scrollTo(0, savedScrollY));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in state restoration: " + e.getMessage());
        }

        // Clear the pending state
        needsStateRestoration = false;
        pendingState = null;
        isRestoring = false;
    }

    private void initViews() {

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        txtBalance = findViewById(R.id.txtBalance);
        mainScrollView = findViewById(R.id.mainScrollView);

        // Header views
        View headerView = navigationView.getHeaderView(0);
        txtPlayerName = headerView.findViewById(R.id.txtPlayerName);
        txtPlayerMobile = headerView.findViewById(R.id.txtPlayerMobile);
        txtViewProfile = headerView.findViewById(R.id.txtviewprofile);

        // Game types RecyclerView
        rvGameTypes = findViewById(R.id.rvGameTypes);

        // Game input fields
        edtDigit = findViewById(R.id.edtDigit);
        edtPoint = findViewById(R.id.edtPoint);

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
        btnClear = findViewById(R.id.btnClear);
        btnSubmit = findViewById(R.id.btnSubmit);

        txtViewProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Disable soft keyboard but allow number input
        edtDigit.setShowSoftInputOnFocus(false);
        edtPoint.setShowSoftInputOnFocus(false);

        // Setup focus tracking
        edtDigit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusedField = edtDigit;
            }
        });

        edtPoint.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                focusedField = edtPoint;
            }
        });

        // Set default focus
        focusedField = edtDigit;

        // Ensure RecyclerView is properly initialized
        if (rvGameTypes != null) {
            rvGameTypes.setSaveEnabled(true);
            rvGameTypes.setHasFixedSize(true);
        }
    }

    private void setupAutoSave() {
        edtDigit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SessionManager.saveBidInputs(EmployeeHomeActivity.this,
                        s.toString(),
                        edtPoint.getText().toString());
            }
        });

        edtPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SessionManager.saveBidInputs(EmployeeHomeActivity.this,
                        edtDigit.getText().toString(),
                        s.toString());
            }
        });
    }

    private void setupGameTypesRecyclerView() {

        int spanCount = calculateSpanCount();

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        rvGameTypes.setLayoutManager(layoutManager);

        rvGameTypes.setHasFixedSize(true);
        rvGameTypes.setNestedScrollingEnabled(false);
        rvGameTypes.setItemViewCacheSize(20);
        rvGameTypes.setDrawingCacheEnabled(true);
        rvGameTypes.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        gameTypeAdapter = new EmployeeGameTypeAdapter(
                this,
                gameTypesList,
                this::onGameTypeClick
        );

        rvGameTypes.setAdapter(gameTypeAdapter);
        rvGameTypes.setVisibility(View.VISIBLE);
    }

    private int calculateSpanCount() {
        // Always use exactly 3 columns for game types
        return 3;
    }

    private void loadGameTypes() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getGames("Bearer " + SessionManager.getToken(this))
                .enqueue(new Callback<GamesResponse>() {

                    @Override
                    public void onResponse(Call<GamesResponse> call,
                                           Response<GamesResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            gameTypesList.clear();
                            gameTypesList.addAll(response.body().getData());
                            gameTypeAdapter.notifyDataSetChanged();

                            Log.d(TAG, "Game types loaded: " + gameTypesList.size());

                            // Perform state restoration after data is loaded
                            rvGameTypes.post(() -> {
                                performStateRestoration();
                                performFullStateRestoration();
                            });
                        } else {
                            toast("Failed to load game types");
                        }
                    }

                    @Override
                    public void onFailure(Call<GamesResponse> call, Throwable t) {
                        Log.e(TAG, "API Error", t);
                        toast("Error: " + t.getMessage());
                    }
                });
    }

    private void onGameTypeClick(GameType gameType, int position) {

        // If clicking the same game type, deselect it
        if (selectedPosition == position) {
            selectedGameType = null;
            selectedPosition = -1;
            gameTypeAdapter.setSelectedPosition(-1);
            SessionManager.saveSelectedGamePosition(this, -1);

            // Disable inputs
            edtDigit.setEnabled(false);
            edtPoint.setEnabled(false);
            edtDigit.setText("");
            edtPoint.setText("");

            // Clear saved inputs
            SessionManager.saveBidInputs(this, "", "");

            toast("Game type deselected");
            return;
        }

        // Select new game type
        selectedGameType = gameType;
        selectedPosition = position;
        gameTypeAdapter.setSelectedPosition(position);
        SessionManager.saveSelectedGamePosition(this, position);

        // Enable inputs
        edtDigit.setEnabled(true);
        edtPoint.setEnabled(true);
        edtDigit.setText("");
        edtPoint.setText("");
        edtDigit.requestFocus();

        // Clear saved inputs
        SessionManager.saveBidInputs(this, "", "");

        // Setup auto-formatting based on game type
        setupDigitAutoFormat();

        toast("Selected: " + gameType.getName());
        Log.d(TAG, "Game type selected: " + gameType.getName());
    }

    private void setupDigitAutoFormat() {

        // Remove existing TextWatcher to prevent memory leak
        if (currentDigitWatcher != null) {
            edtDigit.removeTextChangedListener(currentDigitWatcher);
        }

        currentDigitWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {

                if (isFormatting || selectedGameType == null) return;
                isFormatting = true;

                String input = editable.toString();
                String clean = input.replace("=", "")
                        .replace(" ", "")
                        .replaceAll("[^0-9]", "");

                StringBuilder formatted = new StringBuilder();

                int blockSize = getBlockSize(selectedGameType.getName());

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
        };

        edtDigit.addTextChangedListener(currentDigitWatcher);
    }

    private int getBlockSize(String gameTypeName) {
        String name = gameTypeName.toUpperCase();

        // JODI and CYCLE both use 2-digit blocks
        if (name.equals("JODI") || name.equals("CYCLE")) {
            return 2;
        } else if (name.equals("OPEN") || name.equals("SP") || name.equals("DP")) {
            return 1;
        } else if (name.equals("PATTE") || name.equals("TP")) {
            return 3;
        }

        return 1; // Default
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

        findViewById(R.id.btnWhatsAppContainer).setOnClickListener(v -> {
            String number = SessionManager.getSupportWhatsapp(this);
            if (number == null || number.isEmpty()) {
                toast("Support WhatsApp number not available");
                return;
            }
            openWhatsApp(number);
        });

        findViewById(R.id.btnStarlineContainer).setOnClickListener(v ->
                startActivity(new Intent(this, StarlineActivity.class))
        );
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

        btnClear.setOnClickListener(v -> clearActiveField());

        btnSubmit.setOnClickListener(v -> processGameEntry());
    }

    private void appendToActiveField(String number) {
        if (selectedGameType == null) {
            toast("Please select a game type first");
            return;
        }

        EditText activeField = (focusedField != null && focusedField.isEnabled())
                ? focusedField
                : edtDigit;

        String current = activeField.getText().toString();
        activeField.setText(current + number);
        activeField.setSelection(activeField.getText().length());
    }

    private void deleteFromActiveField() {
        EditText activeField = (focusedField != null && focusedField.isEnabled())
                ? focusedField
                : edtDigit;

        String current = activeField.getText().toString();
        if (current.length() > 0) {
            activeField.setText(current.substring(0, current.length() - 1));
            activeField.setSelection(activeField.getText().length());
        }
    }

    private void clearActiveField() {
        EditText activeField = (focusedField != null && focusedField.isEnabled())
                ? focusedField
                : edtDigit;

        activeField.setText("");
    }

    private String cleanLastEquals(String digits) {
        if (digits.endsWith("=")) {
            return digits.substring(0, digits.length() - 1);
        }
        return digits;
    }

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
            toast("Invalid digit pattern for " + selectedGameType.getName());
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
        showConfirmationDialog(digit, pointValue, selectedGameType.getName());
    }

    private boolean isValidDigitFormat(String digits) {

        String clean = digits.replace("=", "");

        if (!clean.matches("[0-9]+")) {
            return false;
        }

        String gameType = selectedGameType.getName().toUpperCase();

        // JODI and CYCLE both require even number of digits (2-digit blocks)
        if (gameType.equals("JODI") || gameType.equals("CYCLE")) {
            return clean.length() % 2 == 0;
        }

        if (gameType.equals("OPEN") || gameType.equals("SP") || gameType.equals("DP")) {
            return clean.length() > 0;
        }

        if (gameType.equals("PATTE") || gameType.equals("TP")) {
            return clean.length() % 3 == 0;
        }

        return true;
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

        // Deselect game type
        selectedGameType = null;
        selectedPosition = -1;
        gameTypeAdapter.setSelectedPosition(-1);
        SessionManager.saveSelectedGamePosition(this, -1);

        // Disable inputs again
        edtDigit.setEnabled(false);
        edtPoint.setEnabled(false);

        // Clear saved inputs
        SessionManager.saveBidInputs(this, "", "");

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

                            SessionManager.saveBalance(EmployeeHomeActivity.this, user.balance);
                            updateBalanceUI();

                            SessionManager.saveUserName(EmployeeHomeActivity.this, user.name);
                            SessionManager.saveUserMobile(EmployeeHomeActivity.this, user.mobileNo);
                            SessionManager.saveEmail(EmployeeHomeActivity.this, user.email);

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
        if (txtBalance != null) {
            int balance = SessionManager.getBalance(this);
            txtBalance.setText(String.valueOf(balance));
        }
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
            super.onBackPressed();
            finishAffinity();
        } else {
            lastBackPressedTime = time;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}