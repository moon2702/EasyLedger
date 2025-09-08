package com.example.easyledger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.example.easyledger.R;
import com.example.easyledger.database.Bill;
import com.example.easyledger.database.BillType;
import com.example.easyledger.database.BillViewModel;
import com.example.easyledger.ui.adapter.RecentBillsAdapter;
import com.example.easyledger.ui.model.RecentBill;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// 日志
import android.util.Log;

public class HomeFragment extends Fragment {
    // 日志标签
    public static String TAG = "HomeFragment";

    private BillViewModel billViewModel;
    private RecentBillsAdapter adapter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private List<Bill> bills; // 保存账单数据
    private TextView summaryValue; // 收支总计
    private TextView summaryDesc; // 收入支出详情

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        View fab = root.findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v -> Snackbar.make(v, R.string.snack_quick_add_placeholder, Snackbar.LENGTH_SHORT).show());
        }
        Button btnAddNewBill = root.findViewById(R.id.btnAddNewBill);
        Button btnSwitchLedger = root.findViewById(R.id.btnSwitchLedger);
        Button btnSearchBills = root.findViewById(R.id.btnSearchBills);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerRecentBills);
        if (btnAddNewBill != null) {
            btnAddNewBill.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.example.easyledger.BillAddActivity.class);
                startActivity(intent);
            });
        }
        if (btnSwitchLedger != null) {
            btnSwitchLedger.setOnClickListener(v -> {
                Snackbar.make(v, "切换账本", Snackbar.LENGTH_SHORT).show();

            });
        }
        if (btnSearchBills != null) {
            btnSearchBills.setOnClickListener(v -> {
                Snackbar.make(v, "搜索账单", Snackbar.LENGTH_SHORT).show();
            });
        }

        // 初始化适配器
        adapter = new RecentBillsAdapter(new ArrayList<>());

        // 设置列表项点击事件监听器
        adapter.setOnItemClickListener(bill -> {
            // 这里处理点击事件，例如显示详情或编辑界面
            // Snackbar.make(getView(), "点击了: " + bill.title, Snackbar.LENGTH_SHORT).show();
            // 获取对应的Bill对象
            if (bills != null && !bills.isEmpty()) {
                // 查找对应的Bill对象（这里简化处理，实际应用中可能需要更好的方式）
                for (Bill b : bills) {
                    if (b.getId() == bill.id) {
                        // 启动编辑界面
                        Intent intent = new Intent(getActivity(), com.example.easyledger.BillAddActivity.class);
                        intent.putExtra(com.example.easyledger.BillAddActivity.EXTRA_BILL_ID, b.getId());
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        // 初始化summary相关控件
        summaryValue = root.findViewById(R.id.summaryValue);
        summaryDesc = root.findViewById(R.id.summaryDesc);

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(this).get(BillViewModel.class);

        // 观察账单数据变化
        billViewModel.getAllBills().observe(getViewLifecycleOwner(), bills -> {
            this.bills = bills; // 保存到类成员变量
            // 将Bill对象转换为RecentBill对象
            List<RecentBill> recentBills = convertToRecentBills(bills);
            adapter.updateData(recentBills);
            // 更新summaryCard
            updateSummaryCard();
        });

        return root;
    }

    /**
     * 将Bill对象列表转换为RecentBill对象列表
     */
    private List<RecentBill> convertToRecentBills(List<Bill> bills) {
        List<RecentBill> recentBills = new ArrayList<>();
        for (Bill bill : bills) {
            boolean isRepaymentOrTransfer = bill.getType() == BillType.REPAYMENT || bill.getType() == BillType.TRANSFER;
            boolean isExpense = bill.getType() == BillType.EXPENSE;

            String dateText = dateFormat.format(bill.getDate());
            String amountText = "";
            String subtitle = "";


            if (isRepaymentOrTransfer) {
                amountText = String.valueOf(bill.getAmount());
                subtitle = bill.getCategory() + " · " + bill.getAccount() + " -> " + bill.getTargetAccount();

            } else {
                if (isExpense) {
                    amountText = "-" + String.valueOf(bill.getAmount());
                } else {
                    amountText = "+" + String.valueOf(bill.getAmount());
                }
                subtitle = bill.getCategory() + " · " + bill.getAccount();
            }

            recentBills.add(new RecentBill(bill.getId(), bill.getTitle(), subtitle, dateText, amountText, isRepaymentOrTransfer, isExpense));
        }
        return recentBills;
    }

    /**
     * 更新summaryCard中的收支数据
     */
    private void updateSummaryCard() {
        if (summaryValue == null || summaryDesc == null || bills == null) {
            return;
        }

        // 获取当前月份
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        double income = 0.0;
        double expense = 0.0;

        // 计算本月的收入和支出
        for (Bill bill : bills) {
            calendar.setTime(bill.getDate());
            int billMonth = calendar.get(Calendar.MONTH);
            int billYear = calendar.get(Calendar.YEAR);

            // 只计算本月的账单
            if (billMonth == currentMonth && billYear == currentYear) {
                if (bill.getType() == BillType.INCOME) {
                    income += bill.getAmount();
                } else if (bill.getType() == BillType.EXPENSE) {
                    expense += bill.getAmount();
                }
                // 转账和还款不影响总收入支出
            }
        }

        // 计算总计
        double total = income - expense;

        // 更新UI
        summaryValue.setText(String.format("%s%.2f", total >= 0 ? "+" : "", total));
        summaryDesc.setText(String.format("收入 %.2f · 支出 %.2f", income, expense));
    }
}


