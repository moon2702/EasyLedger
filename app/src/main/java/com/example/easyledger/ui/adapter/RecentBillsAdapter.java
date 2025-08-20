package com.example.easyledger.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyledger.R;
import com.example.easyledger.ui.model.RecentBill;

import java.util.List;

public class RecentBillsAdapter extends RecyclerView.Adapter<RecentBillsAdapter.RecentBillViewHolder> {
    private final List<RecentBill> items;

    public RecentBillsAdapter(List<RecentBill> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecentBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_bill, parent, false);
        return new RecentBillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentBillViewHolder holder, int position) {
        RecentBill bill = items.get(position);
        holder.tvTitle.setText(bill.title);
        holder.tvSub.setText(bill.subtitle);
        holder.tvDate.setText(bill.dateText);
        holder.tvAmount.setText(bill.amountText);
        int color = bill.isExpense ? 0xFFB00020 : 0xFF018786; // Material 红/绿近似
        holder.tvAmount.setTextColor((int) color);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecentBillViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvSub;
        final TextView tvAmount;
        final TextView tvDate;

        RecentBillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSub = itemView.findViewById(R.id.tvSub);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}


