package com.example.gameapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.response.GameItem;
import com.example.gameapp.models.response.TapsResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class GameTapAdapter
        extends RecyclerView.Adapter<GameTapAdapter.Holder> {

    private static final String TAG = "GameTapAdapter";

    public interface OnGameTapClickListener {
        void onGameTapClick(TapsResponse.Tap openTap,
                            TapsResponse.Tap closeTap);
    }

    private final Context context;
    private final List<GameItem> list;
    private final OnGameTapClickListener listener;

    public GameTapAdapter(Context context,
                          List<GameItem> list,
                          OnGameTapClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_tap, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {

        GameItem item = list.get(position);

        TapsResponse.Tap openTap = item.getOpenTap();
        TapsResponse.Tap closeTap = item.getCloseTap();

        h.txtGameName.setText(item.getGameName().toUpperCase());

        String resultText = getResultText(openTap, closeTap);
        h.txtResultCode.setText(resultText);

        h.txtOpenTime.setText(
                openTap != null ? openTap.getEndTime() : "--:--"
        );
        h.txtCloseTime.setText(
                closeTap != null ? closeTap.getEndTime() : "--:--"
        );

        String status = determineGameStatus(openTap, closeTap);

        Log.d(TAG, item.getGameName() + " - Final Status: " + status);

        setupStatus(h.txtStatus, h.cardStatus, h.txtPlayGame, status);
        setupPlayButton(h.btnPlay, h.imgPlayIcon, status);

        // ⭐ ALWAYS ALLOW CLICK → HomeActivity will decide open/closed logic
        h.cardGame.setOnClickListener(v ->
                listener.onGameTapClick(openTap, closeTap));

        h.btnPlay.setOnClickListener(v ->
                listener.onGameTapClick(openTap, closeTap));
    }

    private String getResultText(TapsResponse.Tap openTap, TapsResponse.Tap closeTap) {
        if (openTap != null && openTap.getResult() != null && !openTap.getResult().isEmpty()) {
            return openTap.getResult();
        }
        if (closeTap != null && closeTap.getResult() != null && !closeTap.getResult().isEmpty()) {
            return closeTap.getResult();
        }
        return "***-**-***";
    }

    private String determineGameStatus(TapsResponse.Tap openTap, TapsResponse.Tap closeTap) {
        String openStatus = openTap != null ? openTap.getStatus() : null;
        String closeStatus = closeTap != null ? closeTap.getStatus() : null;

        Log.d(TAG, "Open Status: " + openStatus + ", Close Status: " + closeStatus);

        if ("running".equalsIgnoreCase(openStatus) || "open".equalsIgnoreCase(openStatus)) {
            return "open";
        }
        if ("running".equalsIgnoreCase(closeStatus) || "open".equalsIgnoreCase(closeStatus)) {
            return "open";
        }

        if ("upcoming".equalsIgnoreCase(openStatus) || "upcoming".equalsIgnoreCase(closeStatus)) {
            return "upcoming";
        }

        return "closed";
    }

    private void setupStatus(TextView txtStatus,
                             MaterialCardView cardStatus,
                             TextView txtPlayGame,
                             String status) {

        switch (status.toLowerCase()) {
            case "open":
            case "running":
                txtStatus.setText("OPEN");
                cardStatus.setCardBackgroundColor(0xFF4CAF50);
                txtPlayGame.setTextColor(0xFFFFFFFF);
                break;

            case "upcoming":
                txtStatus.setText("UPCOMING");
                cardStatus.setCardBackgroundColor(0xFFFF9800);
                txtPlayGame.setTextColor(0xFFFF9800);
                break;

            default:
                txtStatus.setText("CLOSED");
                txtStatus.setTextColor(0xFFFFFFFF);
                cardStatus.setCardBackgroundColor(0xFFE53935);
                txtPlayGame.setTextColor(0xFF757575);
                break;
        }
    }

    private void setupPlayButton(MaterialCardView btnPlay,
                                 ImageView imgIcon,
                                 String status) {

        switch (status.toLowerCase()) {
            case "open":
            case "running":
                btnPlay.setCardBackgroundColor(0xFF4CAF50);
                imgIcon.setImageResource(R.drawable.ic_play);
                break;

            case "upcoming":
                btnPlay.setCardBackgroundColor(0xFFFF9800);
                imgIcon.setImageResource(R.drawable.ic_clock);
                break;

            default:
                btnPlay.setCardBackgroundColor(0xFFD32F2F);
                imgIcon.setImageResource(R.drawable.ic_pause);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        MaterialCardView cardGame, cardStatus, btnPlay;
        TextView txtGameName, txtResultCode,
                txtOpenTime, txtCloseTime,
                txtStatus, txtPlayGame;
        ImageView imgPlayIcon;

        Holder(@NonNull View v) {
            super(v);
            cardGame = v.findViewById(R.id.cardGame);
            txtGameName = v.findViewById(R.id.txtGameName);
            txtResultCode = v.findViewById(R.id.txtResultCode);
            txtOpenTime = v.findViewById(R.id.txtOpenTime);
            txtCloseTime = v.findViewById(R.id.txtCloseTime);
            txtStatus = v.findViewById(R.id.txtStatus);
            cardStatus = v.findViewById(R.id.cardStatus);
            btnPlay = v.findViewById(R.id.btnPlay);
            imgPlayIcon = v.findViewById(R.id.imgPlayIcon);
            txtPlayGame = v.findViewById(R.id.txtPlayGame);
        }
    }
}
