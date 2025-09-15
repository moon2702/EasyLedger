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
    private AccountAdapter normalAdapter;
    private AccountAdapter creditAdapter;
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

        // 初始化正常账户RecyclerView
        RecyclerView normalRecyclerView = view.findViewById(R.id.accounts_recycler_view);
        normalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        normalRecyclerView.setHasFixedSize(true);

        // 初始化信贷账户RecyclerView
        RecyclerView creditRecyclerView = view.findViewById(R.id.credit_accounts_recycler_view);
        creditRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        creditRecyclerView.setHasFixedSize(true);

        // 初始化Adapters
        normalAdapter = new AccountAdapter();
        creditAdapter = new AccountAdapter();
        
        normalRecyclerView.setAdapter(normalAdapter);
        creditRecyclerView.setAdapter(creditAdapter);

        // 设置正常账户列表项点击事件
        normalAdapter.setOnItemClickListener(account -> {
            // 跳转到账户编辑界面
            Intent intent = new Intent(getActivity(), AccountAddActivity.class);
            intent.putExtra(AccountAddActivity.EXTRA_ACCOUNT_ID, account.getId());
            startActivity(intent);
        });

        // 设置信贷账户列表项点击事件
        creditAdapter.setOnItemClickListener(account -> {
            // 跳转到账户编辑界面
            Intent intent = new Intent(getActivity(), AccountAddActivity.class);
            intent.putExtra(AccountAddActivity.EXTRA_ACCOUNT_ID, account.getId());
            startActivity(intent);
        });

        // 初始化ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // 观察正常账户数据
        accountViewModel.getNormalAccounts().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                normalAdapter.setAccounts(accounts);
            }
        });

        // 观察信贷账户数据
        accountViewModel.getCreditAccounts().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                creditAdapter.setAccounts(accounts);
            }
        });

        // 观察所有账户数据（用于计算资产总值）
        accountViewModel.getAllAccounts().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
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
        double creditUsedTotal = 0; // 信贷账户已用额度总计

        for (Account account : accounts) {
            // 处理信贷账户
            if (account.isCreditAccount()) {
                // 信贷账户的已用额度计入应付和总负债
                double usedCredit = account.getUsedCredit();
                creditUsedTotal += usedCredit;
                payableTotal += usedCredit;
                totalLiability += usedCredit;
                continue; // 信贷账户不参与其他类型计算
            }
            
            // 根据账户类型分类计算（仅正常账户）
            switch (account.getType()) {
                case "现金":
                case "银行卡":
                case "微信":
                case "支付宝":
                case "其他":
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
        
        // 应付金额显示：包含信贷账户已用额度
        // if (creditUsedTotal > 0) {
        //     double otherPayable = payableTotal - creditUsedTotal;
        //     if (otherPayable > 0) {
        //         payableValueTextView.setText("应付: " + decimalFormat.format(payableTotal) + 
        //             " (信贷: " + decimalFormat.format(creditUsedTotal) + ")");
        //     } else {
        //         payableValueTextView.setText("应付: " + decimalFormat.format(creditUsedTotal) + " (信贷)");
        //     }
        // } else {
        payableValueTextView.setText("应付: " + decimalFormat.format(payableTotal));
        // }
        
        receivableValueTextView.setText("应收: " + decimalFormat.format(receivableTotal));
        investmentTotalValueTextView.setText("总额: " + decimalFormat.format(investmentTotal));
        investmentProfitValueTextView.setText("盈亏: " + decimalFormat.format(investmentProfit));
    }
}


