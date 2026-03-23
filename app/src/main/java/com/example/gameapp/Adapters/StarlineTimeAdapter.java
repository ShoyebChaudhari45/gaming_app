package com.example.gameapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.response.StarlineTimesResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class StarlineTimeAdapter extends RecyclerView.Adapter<StarlineTimeAdapter.ViewHolder> {

    private final Context context;
    private final List<StarlineTimesResponse.StarlineTime> times;
    private final OnTimeClickListener listener;

    public interface OnTimeClickListener {
        void onTimeClick(StarlineTimesResponse.StarlineTime time);
    }

    public StarlineTimeAdapter(Context context,
                               List<StarlineTimesResponse.StarlineTime> times,
                               OnTimeClickListener listener) {
        this.context = context;
        this.times = times;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_starline_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StarlineTimesResponse.StarlineTime time = times.get(position);

        holder.txtTime.setText(time.getTime());

        // Display result code or placeholder
        if (time.getResultCode() != null && !time.getResultCode().isEmpty()) {
            holder.txtResultCode.setText(time.getResultCode());
        } else {
            holder.txtResultCode.setText("***-*");
        }

        holder.btnPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimeClick(time);
            }
        });
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtResultCode;
        MaterialCardView btnPlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtResultCode = itemView.findViewById(R.id.txtResultCode);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}