package com.example.gameapp.Adapters;

import android.content.Context;
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

public class GameTapAdapter extends RecyclerView.Adapter<GameTapAdapter.ViewHolder> {

    private Context context;
    private List<GameItem> gameItems;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(TapsResponse.Tap tap, String gameType);
    }

    public GameTapAdapter(Context context, List<GameItem> gameItems, OnGameClickListener listener) {
        this.context = context;
        this.gameItems = gameItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tap, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameItem item = gameItems.get(position);

        holder.txtGameName.setText(item.getGameName());

        // Determine if we need the middle divider
        boolean showDivider = item.hasOpenTap() && item.hasCloseTap();
        holder.dividerMiddle.setVisibility(showDivider ? View.VISIBLE : View.GONE);

        // Setup OPEN section
        if (item.hasOpenTap()) {
            holder.layoutOpen.setVisibility(View.VISIBLE);
            TapsResponse.Tap openTap = item.getOpenTap();

            holder.txtOpenTime.setText(openTap.getEndTime());
            setupStatus(holder.txtOpenStatus, holder.cardOpenStatus, openTap.getStatus());
            setupPlayButton(holder.btnPlayOpen, holder.imgPlayIconOpen, openTap.getStatus());

            // Set click listener only if not closed
            if (isClickable(openTap.getStatus())) {
                holder.btnPlayOpen.setOnClickListener(v ->
                        listener.onGameClick(openTap, "open")
                );
            } else {
                holder.btnPlayOpen.setOnClickListener(null);
            }
        } else {
            holder.layoutOpen.setVisibility(View.GONE);
        }

        // Setup CLOSE section
        if (item.hasCloseTap()) {
            holder.layoutClose.setVisibility(View.VISIBLE);
            TapsResponse.Tap closeTap = item.getCloseTap();

            holder.txtCloseTime.setText(closeTap.getEndTime());
            setupStatus(holder.txtCloseStatus, holder.cardCloseStatus, closeTap.getStatus());
            setupPlayButton(holder.btnPlayClose, holder.imgPlayIconClose, closeTap.getStatus());

            // Set click listener only if not closed
            if (isClickable(closeTap.getStatus())) {
                holder.btnPlayClose.setOnClickListener(v ->
                        listener.onGameClick(closeTap, "close")
                );
            } else {
                holder.btnPlayClose.setOnClickListener(null);
            }
        } else {
            holder.layoutClose.setVisibility(View.GONE);
        }
    }

    private boolean isClickable(String status) {
        if (status == null) return false;
        String statusLower = status.toLowerCase();
        return statusLower.equals("open") ||
                statusLower.equals("upcoming") ||
                statusLower.equals("running");
    }

    private void setupStatus(TextView txtStatus, MaterialCardView cardStatus, String status) {
        if (status == null) status = "closed";

        switch (status.toLowerCase()) {
            case "open":
            case "upcoming":
                txtStatus.setText("OPEN");
                txtStatus.setTextColor(0xFF4CAF50);
                cardStatus.setCardBackgroundColor(0xFFE8F5E9);
                break;

            case "running":
                txtStatus.setText("RUNNING");
                txtStatus.setTextColor(0xFFFF9800);
                cardStatus.setCardBackgroundColor(0xFFFFF3E0);
                break;

            case "closed":
            default:
                txtStatus.setText("CLOSED");
                txtStatus.setTextColor(0xFFDC143C);
                cardStatus.setCardBackgroundColor(0xFFFFEBEE);
                break;
        }
    }

    private void setupPlayButton(MaterialCardView btnPlay, ImageView imgIcon, String status) {
        boolean clickable = isClickable(status);

        btnPlay.setClickable(clickable);
        btnPlay.setFocusable(clickable);

        if (clickable) {
            // Active state
            btnPlay.setCardBackgroundColor(context.getResources().getColor(R.color.dark_blue));
            btnPlay.setCardElevation(4f);
            btnPlay.setAlpha(1.0f);
            imgIcon.setAlpha(1.0f);
        } else {
            // Disabled state
            btnPlay.setCardBackgroundColor(0xFFBDBDBD); // Gray color
            btnPlay.setCardElevation(0f);
            btnPlay.setAlpha(0.5f);
            imgIcon.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() {
        return gameItems.size();
    }

    public void updateData(List<GameItem> newGameItems) {
        this.gameItems = newGameItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtGameName, txtOpenTime, txtCloseTime;
        TextView txtOpenStatus, txtCloseStatus;
        MaterialCardView cardOpenStatus, cardCloseStatus;
        MaterialCardView btnPlayOpen, btnPlayClose;
        ImageView imgPlayIconOpen, imgPlayIconClose;
        View layoutOpen, layoutClose, dividerMiddle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtGameName = itemView.findViewById(R.id.txtGameName);

            layoutOpen = itemView.findViewById(R.id.layoutOpen);
            txtOpenTime = itemView.findViewById(R.id.txtOpenTime);
            txtOpenStatus = itemView.findViewById(R.id.txtOpenStatus);
            cardOpenStatus = itemView.findViewById(R.id.cardOpenStatus);
            btnPlayOpen = itemView.findViewById(R.id.btnPlayOpen);
            imgPlayIconOpen = itemView.findViewById(R.id.imgPlayIconOpen);

            layoutClose = itemView.findViewById(R.id.layoutClose);
            txtCloseTime = itemView.findViewById(R.id.txtCloseTime);
            txtCloseStatus = itemView.findViewById(R.id.txtCloseStatus);
            cardCloseStatus = itemView.findViewById(R.id.cardCloseStatus);
            btnPlayClose = itemView.findViewById(R.id.btnPlayClose);
            imgPlayIconClose = itemView.findViewById(R.id.imgPlayIconClose);

            dividerMiddle = itemView.findViewById(R.id.dividerMiddle);
        }
    }
}