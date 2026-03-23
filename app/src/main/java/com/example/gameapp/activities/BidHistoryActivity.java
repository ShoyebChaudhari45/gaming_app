package com.example.gameapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.Adapters.BidHistoryAdapter;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.response.BidHistoryResponse;
import com.example.gameapp.models.response.BidItem;
import com.google.android.material.button.MaterialButton;
import com.example.gameapp.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidHistoryActivity extends AppCompatActivity {

    private static final String TAG = "BidHistoryActivity";

    // UI Components
    private TextView txtFromDate, txtToDate;
    private MaterialButton btnSubmit;
    private RecyclerView recyclerViewBids;
    private ProgressBar progressBar;
    private View emptyState;

    // Data
    private Calendar fromCalendar, toCalendar;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat apiDateFormat;
    private BidHistoryAdapter adapter;
    private List<BidItem> bidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_history);

        initializeViews();
        setupDateFormats();
        setupCalendars();
        setupListeners();
        setupRecyclerView();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        txtFromDate = findViewById(R.id.txtFromDate);
        txtToDate = findViewById(R.id.txtToDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        recyclerViewBids = findViewById(R.id.recyclerViewBids);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);

        btnBack.setOnClickListener(v -> navigateToHome());
    }

    private void setupDateFormats() {
        // Format for display: dd-MM-yyyy
        displayDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Format for API: yyyy-M-d (e.g., 2026-1-1)
        apiDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
    }

    private void setupCalendars() {
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();

        // Set default: last 7 days to today
        fromCalendar.add(Calendar.DAY_OF_MONTH, -7);

        updateDateDisplays();
    }

    private void setupListeners() {
        txtFromDate.setOnClickListener(v -> showDatePicker(true));
        txtToDate.setOnClickListener(v -> showDatePicker(false));
        btnSubmit.setOnClickListener(v -> fetchBidHistory());
    }

    private void setupRecyclerView() {
        bidList = new ArrayList<>();
        adapter = new BidHistoryAdapter(this, bidList);
        recyclerViewBids.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBids.setAdapter(adapter);
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = isFromDate ? fromCalendar : toCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Validate date range
                    if (isFromDate && fromCalendar.after(toCalendar)) {
                        Toast.makeText(this, "From date cannot be after To date", Toast.LENGTH_SHORT).show();
                        fromCalendar.setTime(toCalendar.getTime());
                        fromCalendar.add(Calendar.DAY_OF_MONTH, -1);
                    } else if (!isFromDate && toCalendar.before(fromCalendar)) {
                        Toast.makeText(this, "To date cannot be before From date", Toast.LENGTH_SHORT).show();
                        toCalendar.setTime(fromCalendar.getTime());
                        toCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    updateDateDisplays();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date as today for both pickers
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Styling
        datePickerDialog.show();
    }

    private void updateDateDisplays() {
        txtFromDate.setText(displayDateFormat.format(fromCalendar.getTime()));
        txtToDate.setText(displayDateFormat.format(toCalendar.getTime()));
    }

    private void fetchBidHistory() {

        String startDate = apiDateFormat.format(fromCalendar.getTime());
        String endDate = apiDateFormat.format(toCalendar.getTime());

        Log.d(TAG, "=== BID HISTORY REQUEST ===");
        Log.d(TAG, "Start Date: " + startDate);
        Log.d(TAG, "End Date: " + endDate);

        showLoading(true);

        String token = "Bearer " + SessionManager.getToken(this);



        // Correct Retrofit create call
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Correct API call
        Call<BidHistoryResponse> call = apiService.getBidHistory(
                token,
                startDate,
                endDate
        );

        Log.d(TAG, "Request URL: " + call.request().url().toString());

        call.enqueue(new Callback<BidHistoryResponse>() {
            @Override
            public void onResponse(Call<BidHistoryResponse> call, Response<BidHistoryResponse> response) {
                showLoading(false);

                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Response Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    BidHistoryResponse bidResponse = response.body();

                    if (bidResponse.getStatusCode() == 200 && bidResponse.getData() != null) {
                        List<BidItem> bids = bidResponse.getData();

                        if (bids.isEmpty()) {
                            showEmptyState(true);
                            Toast.makeText(BidHistoryActivity.this,
                                    "No bids found for selected dates", Toast.LENGTH_SHORT).show();
                        } else {
                            bidList.clear();
                            bidList.addAll(bids);
                            adapter.notifyDataSetChanged();
                            showBidList(true);

                            Toast.makeText(BidHistoryActivity.this,
                                    "Found " + bids.size() + " bid(s)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showError("No data available");
                        showEmptyState(true);
                    }
                } else {
                    String msg = "Failed to load data (Error " + response.code() + ")";

                    if (response.code() == 401) msg = "Authentication failed.";
                    if (response.code() == 500) msg = "Server error. Try again later.";

                    showError(msg);
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<BidHistoryResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                showEmptyState(true);
            }
        });
    }


    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBids.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        btnSubmit.setEnabled(!show);
    }

    private void showBidList(boolean show) {
        recyclerViewBids.setVisibility(show ? View.VISIBLE : View.GONE);
        emptyState.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBids.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }

    private void navigateToHome() {
        Intent intent = new Intent(BidHistoryActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateToHome();
    }
}