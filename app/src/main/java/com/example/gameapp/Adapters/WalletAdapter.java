package com.example.gameapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gameapp.R;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.VH> {


    List<String> list;

    public WalletAdapter(List<String> list) {
        this.list = list;
    }

    static class VH extends RecyclerView.ViewHolder {
        VH(View v) {
            super(v);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_transaction, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
