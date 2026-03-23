package com.example.gameapp.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.response.WalletStatementResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class WalletStatementAdapter
        extends RecyclerView.Adapter<WalletStatementAdapter.ViewHolder> {

    private final List<WalletStatementResponse.Data> list;

    public WalletStatementAdapter(List<WalletStatementResponse.Data> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        WalletStatementResponse.Data item = list.get(position);

        // TITLE
        if ("withdraw".equalsIgnoreCase(item.getType())) {
            holder.tvTransactionTitle.setText("Withdrawal");
        } else {
            holder.tvTransactionTitle.setText("Deposit");
        }

        // DATE
        holder.tvTransactionDate.setText(item.getDate());

        // STATUS
        holder.tvTransactionStatus.setText(item.getStatus().toUpperCase());

        if ("success".equalsIgnoreCase(item.getStatus())) {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvTransactionStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FDECEA"));
            holder.tvTransactionStatus.setTextColor(Color.parseColor("#C62828"));
        }

        // AMOUNT
        if ("withdraw".equalsIgnoreCase(item.getType())) {
            holder.tvTransactionAmount.setText("- ₹" + item.getAmount());
            holder.tvTransactionAmount.setTextColor(Color.parseColor("#C62828"));
        } else {
            holder.tvTransactionAmount.setText("+ ₹" + item.getAmount());
            holder.tvTransactionAmount.setTextColor(Color.parseColor("#2E7D32"));
        }

        // ICON
        if ("withdraw".equalsIgnoreCase(item.getType())) {
            holder.imgTransactionIcon.setImageResource(R.drawable.ic_wallet);
            holder.iconCard.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.imgTransactionIcon.setColorFilter(Color.parseColor("#FF9800"));
        } else {
            holder.imgTransactionIcon.setImageResource(R.drawable.ic_diamond);
            holder.iconCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.imgTransactionIcon.setColorFilter(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTransactionIcon;
        TextView tvTransactionTitle, tvTransactionDate,
                tvTransactionStatus, tvTransactionAmount;
        MaterialCardView statusCard, iconCard;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTransactionIcon = itemView.findViewById(R.id.imgTransactionIcon);
            tvTransactionTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionStatus = itemView.findViewById(R.id.tvTransactionStatus);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);

            statusCard = (MaterialCardView) tvTransactionStatus.getParent();
            iconCard = (MaterialCardView) imgTransactionIcon.getParent();
        }
    }
}
