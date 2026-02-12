package com.example.gameapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.response.BidItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BidHistoryAdapter extends RecyclerView.Adapter<BidHistoryAdapter.BidViewHolder> {

    private final Context context;
    private final List<BidItem> bidList;
    private final SimpleDateFormat inputDateFormat;
    private final SimpleDateFormat outputDateFormat;

    public BidHistoryAdapter(Context context, List<BidItem> bidList) {
        this.context = context;
        this.bidList = bidList;
        // API returns: "2026-02-06T14:50:33.000000Z"
        this.inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        this.outputDateFormat = new SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_bid_history, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        BidItem bid = bidList.get(position);

        // Bid type (e.g. "Jodi", "Open", "SP")
        holder.txtBidType.setText(bid.getType());

        // Tap name (e.g. "MILAN NIGHT")
        if (bid.getTapName() != null && !bid.getTapName().isEmpty()) {
            holder.txtTapName.setText(bid.getTapName());
            holder.txtTapName.setVisibility(View.VISIBLE);
        } else {
            holder.txtTapName.setVisibility(View.GONE);
        }

        // Input value (digit entered)
        holder.txtInputValue.setText(bid.getInputValue());

        // Price (individual bid amount)
        holder.txtPrice.setText(String.valueOf(bid.getPrice()));

        // Total (calculated total)
        holder.txtTotal.setText(String.valueOf(bid.getTotal()));

        // Date
        holder.txtDate.setText(formatDate(bid.getCreatedOn()));
    }

    @Override
    public int getItemCount() {
        return bidList == null ? 0 : bidList.size();
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "-";
        try {
            Date date = inputDateFormat.parse(dateString);
            if (date != null) {
                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    static class BidViewHolder extends RecyclerView.ViewHolder {
        TextView txtBidType, txtTapName, txtInputValue, txtPrice, txtTotal, txtDate;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBidType    = itemView.findViewById(R.id.txtBidType);
            txtTapName    = itemView.findViewById(R.id.txtTapName);
            txtInputValue = itemView.findViewById(R.id.txtInputValue);
            txtPrice      = itemView.findViewById(R.id.txtPrice);
            txtTotal      = itemView.findViewById(R.id.txtTotal);
            txtDate       = itemView.findViewById(R.id.txtDate);
        }
    }
}