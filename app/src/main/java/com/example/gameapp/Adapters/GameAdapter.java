package com.example.gameapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.GameModel;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameVH> {

    private List<GameModel> list;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(GameModel game);
    }

    public GameAdapter(List<GameModel> list, OnGameClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false); // ✅ FIX
        return new GameVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameVH holder, int position) {
        GameModel model = list.get(position);

        holder.txtName.setText(model.getName());
        holder.txtResult.setText(model.getResult());
        holder.txtTime.setText(model.getTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class GameVH extends RecyclerView.ViewHolder {

        TextView txtName, txtResult, txtTime;

        public GameVH(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtGameName);
            txtResult = itemView.findViewById(R.id.txtGameResult);
            txtTime = itemView.findViewById(R.id.txtGameTime);
        }
    }
}
