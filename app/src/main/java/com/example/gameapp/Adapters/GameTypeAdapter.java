package com.example.gameapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gameapp.R;
import com.example.gameapp.models.GameType;

import java.util.List;

public class GameTypeAdapter
        extends RecyclerView.Adapter<GameTypeAdapter.Holder> {

    public interface OnGameTypeClickListener {
        void onGameTypeClick(GameType gameType);
    }

    private final Context context;
    private final List<GameType> list;
    private final OnGameTypeClickListener listener;

    public GameTypeAdapter(Context context,
                           List<GameType> list,
                           OnGameTypeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_game_type, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        GameType gameType = list.get(position);

        h.txtGameType.setText(gameType.getName());

        Glide.with(context)
                .load(gameType.getImage())
                .placeholder(R.drawable.ic_placeholder)
                .into(h.imgGameType);

        h.itemView.setOnClickListener(v ->
                listener.onGameTypeClick(gameType));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView txtGameType;
        ImageView imgGameType;

        Holder(@NonNull View itemView) {
            super(itemView);
            txtGameType = itemView.findViewById(R.id.txtGameTypeName);
            imgGameType = itemView.findViewById(R.id.imgGameType);
        }
    }
}
