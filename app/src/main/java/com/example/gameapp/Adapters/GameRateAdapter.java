package com.example.gameapp.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.GameRateModel;

import java.util.List;

public class GameRateAdapter extends RecyclerView.Adapter<GameRateAdapter.VH> {

    List<GameRateModel> list;

    public GameRateAdapter(List<GameRateModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_rate, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        GameRateModel m = list.get(position);
        h.name.setText(m.name);
        h.rate.setText(m.rate);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, rate;

        VH(View v) {
            super(v);
            name = v.findViewById(R.id.txtName);
            rate = v.findViewById(R.id.txtRate);
        }
    }
}
