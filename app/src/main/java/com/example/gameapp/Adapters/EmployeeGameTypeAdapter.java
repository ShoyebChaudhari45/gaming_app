package com.example.gameapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.GameType;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class EmployeeGameTypeAdapter
        extends RecyclerView.Adapter<EmployeeGameTypeAdapter.Holder> {

    public interface OnGameTypeClickListener {
        void onGameTypeClick(GameType gameType, int position);
    }

    private final Context context;
    private final List<GameType> list;
    private final OnGameTypeClickListener listener;
    private int selectedPosition = -1;

    // Different colors for game types
    private static final int[] COLORS = {
            0xFFE3F2FD, // Light Blue
            0xFFFCE4EC, // Light Pink
            0xFFF3E5F5, // Light Purple
            0xFFE8F5E9, // Light Green
            0xFFFFF3E0, // Light Orange
            0xFFE0F2F1, // Light Teal
            0xFFFFF9C4, // Light Yellow
            0xFFFFECB3  // Light Amber
    };

    private static final int COLOR_SELECTED = 0xFF4CAF50; // Green

    public EmployeeGameTypeAdapter(Context context,
                                   List<GameType> list,
                                   OnGameTypeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;

        // Notify changes for both old and new positions
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_game_type_employee, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        GameType gameType = list.get(position);

        h.txtGameType.setText(gameType.getName());

        // Set background color
        if (position == selectedPosition) {
            // Selected state - green
            h.cardGameType.setCardBackgroundColor(COLOR_SELECTED);
            h.txtGameType.setTextColor(0xFFFFFFFF); // White text
        } else {
            // Normal state - different colors
            int colorIndex = position % COLORS.length;
            h.cardGameType.setCardBackgroundColor(COLORS[colorIndex]);
            h.txtGameType.setTextColor(0xFF212121); // Dark gray text
        }

        h.itemView.setOnClickListener(v ->
                listener.onGameTypeClick(gameType, position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        MaterialCardView cardGameType;
        TextView txtGameType;

        Holder(@NonNull View itemView) {
            super(itemView);
            cardGameType = itemView.findViewById(R.id.cardGameType);
            txtGameType = itemView.findViewById(R.id.txtGameTypeName);
        }
    }
}