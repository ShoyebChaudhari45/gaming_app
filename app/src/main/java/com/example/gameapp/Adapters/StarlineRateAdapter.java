package com.example.gameapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;
import com.example.gameapp.models.response.StarlineRatesResponse;

import java.util.List;

public class StarlineRateAdapter extends RecyclerView.Adapter<StarlineRateAdapter.ViewHolder> {

    private final Context context;
    private final List<StarlineRatesResponse.StarlineRate> rates;

    public StarlineRateAdapter(Context context, List<StarlineRatesResponse.StarlineRate> rates) {
        this.context = context;
        this.rates = rates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_starline_rate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StarlineRatesResponse.StarlineRate rate = rates.get(position);

        // Use game name directly from backend
        String gameName = rate.getGame() != null ? rate.getGame() : "Unknown";
        holder.txtGameName.setText(gameName);

        // Set price range (price-digit format like "10-95")
        String price = rate.getPrice() != null ? rate.getPrice() : "0";
        String digit = rate.getDigit() != null ? rate.getDigit() : "0";
        String priceRange = price + "-" + digit;
        holder.txtPriceRange.setText(priceRange);
    }

    @Override
    public int getItemCount() {
        return rates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtGameName, txtPriceRange;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGameName = itemView.findViewById(R.id.txtGameName);
            txtPriceRange = itemView.findViewById(R.id.txtPriceRange);
        }
    }
}