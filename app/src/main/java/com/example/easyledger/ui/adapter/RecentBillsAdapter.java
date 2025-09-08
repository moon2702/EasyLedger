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

    // 点击事件监听器接口
    public interface OnItemClickListener {
        void onItemClick(RecentBill bill);
    }

    private OnItemClickListener listener;

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
        int color = 0xFF000000; // 黑色
        if (!bill.isRepaymentOrTransfer) {
            color = bill.isExpense ? 0xFFB00020 : 0xFF018786; // Material 红/绿近似
        }
        holder.tvAmount.setTextColor((int) color);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * 设置点击事件监听器
     * @param listener 监听器实例
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 更新适配器数据
     * @param newItems 新的账单列表
     */
    public void updateData(List<RecentBill> newItems) {
        // 由于items是final变量，不能直接赋值，只能修改其内容
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    class RecentBillViewHolder extends RecyclerView.ViewHolder {
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

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(position));
                }
            });
        }
    }
}


