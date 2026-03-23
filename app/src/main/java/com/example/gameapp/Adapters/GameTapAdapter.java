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
                            TapsResponse.Tap closeTap,
                            View clickedView); // ⭐ ADDED VIEW PARAMETER
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

        // ⭐ PASS THE CARD VIEW TO THE LISTENER
        h.cardGame.setOnClickListener(v ->
                listener.onGameTapClick(openTap, closeTap, h.cardGame));

        h.btnPlay.setOnClickListener(v ->
                listener.onGameTapClick(openTap, closeTap, h.cardGame));
    }

    private String getResultText(TapsResponse.Tap openTap, TapsResponse.Tap closeTap) {
        String openResult = (openTap != null && openTap.getResult() != null && !openTap.getResult().isEmpty())
                ? openTap.getResult() : null;

        String closeResult = (closeTap != null && closeTap.getResult() != null && !closeTap.getResult().isEmpty())
                ? closeTap.getResult() : null;

        if (openResult != null && closeResult != null) {
            // open  = "180-9"   → parts: ["180", "9"]
            // close = "9-360"   → parts: ["9",   "360"]
            // merged → "180-" + "9" + "9" + "-360" = "180-99-360"

            String[] openParts  = openResult.split("-");
            String[] closeParts = closeResult.split("-");

            String left   = openParts.length  > 0 ? openParts[0]  : "";   // "180"
            String midL   = openParts.length  > 1 ? openParts[1]  : "";   // "9"
            String midR   = closeParts.length > 0 ? closeParts[0] : "";   // "9"
            String right  = closeParts.length > 1 ? closeParts[1] : "";   // "360"

            return left + "-" + midL + midR + "-" + right;  // "180-99-360"

        } else if (openResult != null) {
            return openResult;   // e.g. "478-9"

        } else if (closeResult != null) {
            return closeResult;  // e.g. "9-360"

        } else {
            return "***-**-***";
        }
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