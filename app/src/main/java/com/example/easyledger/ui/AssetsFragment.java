package com.example.easyledger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyledger.R;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import com.example.easyledger.ui.adapter.AccountAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.text.DecimalFormat;
import java.util.List;

public class AssetsFragment extends Fragment {
    private AccountViewModel accountViewModel;
    private AccountAdapter adapter;
    private TextView netAssetValueTextView;
    private TextView totalAssetValueTextView;
    private TextView liabilityValueTextView;
    private TextView reimbursableValueTextView;
    private TextView reimbursedValueTextView;
    private TextView payableValueTextView;
    private TextView receivableValueTextView;
    private TextView investmentTotalValueTextView;
    private TextView investmentProfitValueTextView;
    private DecimalFormat decimalFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assets, container, false);

        // 初始化格式化器
        decimalFormat = new DecimalFormat("0.00");

        // 获取资产卡片TextView
        netAssetValueTextView = view.findViewById(R.id.net_asset_value);
        totalAssetValueTextView = view.findViewById(R.id.total_asset_value);
        liabilityValueTextView = view.findViewById(R.id.liability_value);

        // 获取分类卡片TextView
        LinearLayout categoryLayout = view.findViewById(R.id.category_layout);
        MaterialCardView reimbursementCard = (MaterialCardView) categoryLayout.getChildAt(0);
        MaterialCardView debtCard = (MaterialCardView) categoryLayout.getChildAt(1);
        MaterialCardView investmentCard = (MaterialCardView) categoryLayout.getChildAt(2);

        reimbursableValueTextView = reimbursementCard.findViewById(R.id.reimbursable_value);
        reimbursedValueTextView = reimbursementCard.findViewById(R.id.reimbursed_value);
        payableValueTextView = debtCard.findViewById(R.id.payable_value);
        receivableValueTextView = debtCard.findViewById(R.id.receivable_value);
        investmentTotalValueTextView = investmentCard.findViewById(R.id.investment_total_value);
        investmentProfitValueTextView = investmentCard.findViewById(R.id.investment_profit_value);

        // 初始化RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.accounts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // 初始化Adapter
        adapter = new AccountAdapter();
        recyclerView.setAdapter(adapter);

        // 设置列表项点击事件
        adapter.setOnItemClickListener(account -> {
            // 在这里处理账户点击事件
            Snackbar.make(view, "查看账户: " + account.getName(), Snackbar.LENGTH_SHORT).show();
        });

        // 初始化ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // 观察账户数据
        accountViewModel.getAllAccounts().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                adapter.setAccounts(accounts);
                updateAssetValues(accounts);
            }
        });

        // 顶部资产卡片点击事件
        MaterialCardView assetCard = view.findViewById(R.id.asset_card);
        assetCard.setOnClickListener(v -> {
            Snackbar.make(v, "查看资产详情", Snackbar.LENGTH_SHORT).show();
        });

        // 资产分类卡片点击事件
        // LinearLayout categoryLayout = view.findViewById(R.id.category_layout);
        for (int i = 0; i < categoryLayout.getChildCount(); i++) {
            View child = categoryLayout.getChildAt(i);
            if (child instanceof MaterialCardView) {
                MaterialCardView card = (MaterialCardView) child;
                int position = i;
                card.setOnClickListener(v -> {
                    String[] categories = {"报销", "债务", "理财"};
                    Snackbar.make(v, "查看" + categories[position] + "详情", Snackbar.LENGTH_SHORT).show();
                });
            }
        }

        // 添加按钮点击事件
        FloatingActionButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountAddActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void updateAssetValues(List<Account> accounts) {
        // 计算总资产、总负债和净资产
        double totalAsset = 0;
        double totalLiability = 0;
        double reimbursementTotal = 0;
        double reimbursedTotal = 0;
        double payableTotal = 0;
        double receivableTotal = 0;
        double investmentTotal = 0;
        double investmentProfit = 0;

        for (Account account : accounts) {
            // 这里假设Account类有getType()和getBalance()方法
            // 根据账户类型分类计算
            switch (account.getType()) {
                case "现金":
                case "银行卡":
                    totalAsset += account.getBalance();
                    break;
                case "负债":
                    totalLiability += account.getBalance();
                    break;
                case "报销":
                    reimbursementTotal += account.getBalance();
                    break;
                case "已报销":
                    reimbursedTotal += account.getBalance();
                    break;
                case "应付":
                    payableTotal += account.getBalance();
                    break;
                case "应收":
                    receivableTotal += account.getBalance();
                    break;
                case "理财":
                    investmentTotal += account.getBalance();
                    // 假设理财有10%的收益
                    investmentProfit += account.getBalance() * 0.1;
                    break;
            }
        }

        double netAsset = totalAsset - totalLiability;

        // 更新UI
        netAssetValueTextView.setText(decimalFormat.format(netAsset));
        totalAssetValueTextView.setText(decimalFormat.format(totalAsset));
        liabilityValueTextView.setText(decimalFormat.format(totalLiability));
        reimbursableValueTextView.setText("可报: " + decimalFormat.format(reimbursementTotal));
        reimbursedValueTextView.setText("已报: " + decimalFormat.format(reimbursedTotal));
        payableValueTextView.setText("应付: " + decimalFormat.format(payableTotal));
        receivableValueTextView.setText("应收: " + decimalFormat.format(receivableTotal));
        investmentTotalValueTextView.setText("总额: " + decimalFormat.format(investmentTotal));
        investmentProfitValueTextView.setText("盈亏: " + decimalFormat.format(investmentProfit));
    }
}


