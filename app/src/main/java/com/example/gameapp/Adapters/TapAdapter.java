package com.example.gameapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.response.TapsResponse;

import java.util.List;

public class TapAdapter extends RecyclerView.Adapter<TapAdapter.Holder> {

    private final Context context;
    private final List<TapsResponse.Tap> list;

    public TapAdapter(Context context, List<TapsResponse.Tap> list) {
        this.context = context;
        this.list = list;
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
        TapsResponse.Tap tap = list.get(position);

        // GAME NAME (from injected value)
        h.txtGameName.setText(
                tap.getGameName() != null
                        ? tap.getGameName().toUpperCase()
                        : "-"
        );

        // TYPE -> show as result/title
        h.txtResult.setText(
                tap.getType() != null
                        ? tap.getType().toUpperCase()
                        : "---"
        );

        // END TIME
        h.txtOpenTime.setText(
                tap.getEndTime() != null
                        ? tap.getEndTime()
                        : "--:--"
        );

        // STATUS
        h.txtStatus.setText(
                tap.getStatus() != null
                        ? tap.getStatus().toUpperCase()
                        : "-"
        );
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    // ================= VIEW HOLDER =================
    static class Holder extends RecyclerView.ViewHolder {

        TextView txtGameName, txtResult, txtOpenTime, txtCloseTime, txtStatus;

        Holder(@NonNull View v) {
            super(v);
            txtGameName = v.findViewById(R.id.txtGameName);
            txtOpenTime = v.findViewById(R.id.txtOpenTime);
            txtCloseTime = v.findViewById(R.id.txtCloseTime); // unused but safe
            txtStatus = v.findViewById(R.id.txtOpenStatus);

        }
    }
}
