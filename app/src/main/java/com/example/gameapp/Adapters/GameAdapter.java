package com.example.gameapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.activities.GamePlayActivity;
import com.example.gameapp.models.GameModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private final Context context;
    private final List<GameModel> list;

    public GameAdapter(Context context, List<GameModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {

        GameModel model = list.get(position);

        // ✅ NULL SAFE
        if (holder.txtGameName != null)
            holder.txtGameName.setText(model.getName());

        if (holder.txtGameTime != null)
            holder.txtGameTime.setText(model.getTime());

        if (holder.txtGameResult != null)
            holder.txtGameResult.setText(
                    model.getResult() != null ? model.getResult() : "-"
            );

        holder.btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(context, GamePlayActivity.class);
            intent.putExtra("GAME_NAME", model.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView txtGameName, txtGameTime, txtGameResult;
        MaterialButton btnPlay;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);

            // ✅ MUST MATCH XML IDS
            txtGameName = itemView.findViewById(R.id.txtGameName);
            txtGameTime = itemView.findViewById(R.id.txtGameTime);
            txtGameResult = itemView.findViewById(R.id.txtGameResult);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}
