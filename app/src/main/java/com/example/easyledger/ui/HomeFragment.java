package com.example.easyledger.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.example.easyledger.R;
import com.example.easyledger.ui.adapter.RecentBillsAdapter;
import com.example.easyledger.ui.model.RecentBill;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        View fab = root.findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v -> Snackbar.make(v, R.string.snack_quick_add_placeholder, Snackbar.LENGTH_SHORT).show());
        }
        Button btnExpense = root.findViewById(R.id.btnQuickExpense);
        Button btnIncome = root.findViewById(R.id.btnQuickIncome);
        Button btnSwitchLedger = root.findViewById(R.id.btnSwitchLedger);
        Button btnSearchBills = root.findViewById(R.id.btnSearchBills);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerRecentBills);
        if (btnExpense != null) {
            btnExpense.setOnClickListener(v -> Snackbar.make(v, R.string.home_action_expense, Snackbar.LENGTH_SHORT).show());
        }
        if (btnIncome != null) {
            btnIncome.setOnClickListener(v -> Snackbar.make(v, R.string.home_action_income, Snackbar.LENGTH_SHORT).show());
        }
        if (btnSwitchLedger != null) {
            btnSwitchLedger.setOnClickListener(v -> Snackbar.make(v, R.string.home_switch_ledger, Snackbar.LENGTH_SHORT).show());
        }
        if (btnSearchBills != null) {
            btnSearchBills.setOnClickListener(v -> Snackbar.make(v, R.string.home_search_bills, Snackbar.LENGTH_SHORT).show());
        }

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new RecentBillsAdapter(createMockBills()));
        }
        return root;
    }

    private List<RecentBill> createMockBills() {
        List<RecentBill> list = new ArrayList<>();
        list.add(new RecentBill("早餐", "餐饮 · 现金", "2025-08-20 08:12", "-12.50", true));
        list.add(new RecentBill("打车", "出行 · 微信", "2025-08-20 09:05", "-23.00", true));
        list.add(new RecentBill("工资", "收入 · 工资卡", "2025-08-15 10:00", "+8500.00", false));
        list.add(new RecentBill("咖啡", "餐饮 · 信用卡", "2025-08-19 14:20", "-18.00", true));
        list.add(new RecentBill("退款", "其他 · 支付宝", "2025-08-18 12:30", "+35.00", false));
        list.add(new RecentBill("晚餐", "餐饮 · 信用卡", "2025-08-20 19:30", "-89.00", true));
        list.add(new RecentBill("电影票", "娱乐 · 支付宝", "2025-08-19 21:15", "-55.00", true));
        list.add(new RecentBill("房租", "生活 · 银行记账", "2025-08-01 11:00", "-2500.00", true));
        list.add(new RecentBill("理财收入", "收入 · 银行记账", "2025-08-15 14:45", "+250.00", false));
        list.add(new RecentBill("超市购物", "购物 · 微信", "2025-08-17 17:50", "-156.80", true));
        list.add(new RecentBill("电话费", "生活 · 微信", "2025-08-05 16:30", "-50.00", true));
        list.add(new RecentBill("兼职收入", "收入 · 支付宝", "2025-08-10 18:00", "+300.00", false));
        list.add(new RecentBill("买书", "学习 · 信用卡", "2025-08-18 10:20", "-99.00", true));
        list.add(new RecentBill("会员费", "运动 · 信用卡", "2025-08-08 09:30", "-360.00", true));
        return list;
    }
}


