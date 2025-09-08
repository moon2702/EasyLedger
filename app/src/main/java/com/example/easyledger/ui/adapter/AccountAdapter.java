package com.example.easyledger.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyledger.R;
import com.example.easyledger.database.Account;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private List<Account> accounts;
    private OnItemClickListener listener;

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        if (accounts != null) {
            Account current = accounts.get(position);
            holder.nameTextView.setText(current.getName());
            holder.typeTextView.setText(current.getType());
            holder.balanceTextView.setText(String.valueOf(current.getBalance()));
        }
    }

    @Override
    public int getItemCount() {
        return accounts != null ? accounts.size() : 0;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    public Account getAccountAt(int position) {
        return accounts.get(position);
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView typeTextView;
        private final TextView balanceTextView;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.account_name);
            typeTextView = itemView.findViewById(R.id.account_type);
            balanceTextView = itemView.findViewById(R.id.account_balance);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(accounts.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Account account);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}