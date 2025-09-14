package com.example.easyledger.ui.billadd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.easyledger.R;
import com.example.easyledger.database.Bill;
import com.example.easyledger.database.BillType;
import com.example.easyledger.database.BillViewModel;
import com.example.easyledger.BillAddActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import com.example.easyledger.ui.AccountSelectorView;
import com.example.easyledger.ui.AccountSelectorListener;
import com.example.easyledger.ui.AccountBalanceManager;

public class RepaymentBillFragment extends Fragment implements BillSaveable, AccountSelectorListener {

    // private EditText editTextTitle;
    private EditText editTextAmount;
    // private EditText editTextCreditor;
    private AccountSelectorView editTextDebtorAccount;
    private AccountSelectorView editTextCreditorAccount;
    private EditText editTextDate;
    private BillViewModel billViewModel;
    private AccountBalanceManager balanceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = null;
        try {
            root = inflater.inflate(R.layout.fragment_repayment_bill, container, false);

            // 初始化ViewModel
            if (getActivity() != null) {
                billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
            } else {
                android.util.Log.e("RepaymentBillFragment", "Activity is null, cannot initialize ViewModel");
                return root;
            }

            // 初始化余额管理器
            balanceManager = new AccountBalanceManager(requireContext());

            // 获取视图控件
            editTextAmount = root.findViewById(R.id.editTextAmount);
            editTextDebtorAccount = root.findViewById(R.id.editTextDebtorAccount);
            editTextCreditorAccount = root.findViewById(R.id.editTextCreditorAccount);
            editTextDate = root.findViewById(R.id.editTextDate);
            Button btnSaveBill = root.findViewById(R.id.btnSaveBill);
            
            // 检查关键控件是否成功获取
            if (editTextAmount == null || editTextDebtorAccount == null || 
                editTextCreditorAccount == null || editTextDate == null) {
                android.util.Log.e("RepaymentBillFragment", "Some views are null, layout may be corrupted");
                return root;
            }

            editTextDebtorAccount.setLifecycleOwner(getViewLifecycleOwner());
            editTextDebtorAccount.setAccountSelectorListener(this);
            editTextDebtorAccount.setHint("选择支出账户");
            editTextCreditorAccount.setLifecycleOwner(getViewLifecycleOwner());
            editTextCreditorAccount.setAccountSelectorListener(this);
            editTextCreditorAccount.setHint("选择信贷账户");

            // 设置保存按钮点击事件
            if (btnSaveBill != null) {
                btnSaveBill.setOnClickListener(v -> {
                    try {
                        // 调用Activity中的保存方法
                        if (getActivity() instanceof BillAddActivity) {
                            ((BillAddActivity) getActivity()).saveCurrentBill();
                        } else {
                            saveBill();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("RepaymentBillFragment", "Error in save button click", e);
                        Toast.makeText(getContext(), "保存时发生错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            android.util.Log.e("RepaymentBillFragment", "Error in onCreateView", e);
            // 如果发生异常，返回一个简单的视图
            if (root == null) {
                root = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
            }
        }

        return root;
    }

    private boolean updateAccountBalance(Account debtorAccount, Account creditorAccount, double amount) {
        if (balanceManager == null) {
            Toast.makeText(getContext(), "余额管理器未初始化", Toast.LENGTH_SHORT).show();
            return false;
        }

        AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleRepaymentBill(debtorAccount, creditorAccount, amount);
        
        if (!result.isSuccess()) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean saveBill() {
        try {
            // 获取输入内容
            // String title = editTextTitle.getText().toString().trim();
            String title = "还款";
            String amountStr = editTextAmount.getText().toString().trim();
            // String creditor = editTextCreditorAccount.getText().toString().trim();
            // String account = editTextDebtorAccount.getText().toString().trim();
            String dateStr = editTextDate.getText().toString().trim();
            Account creditor = editTextCreditorAccount.getSelectedAccount();
            Account debtor = editTextDebtorAccount.getSelectedAccount();

            // 验证输入
            if (title.isEmpty() || amountStr.isEmpty() || creditor == null || debtor == null || dateStr.isEmpty()) {
                Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
                return false;
            }

            double amount = Double.parseDouble(amountStr);

            // TODO: 解析日期字符串为Date对象
            // 这里简化处理，实际应用中需要添加日期解析逻辑
            java.util.Date date = new java.util.Date();

            // 创建还款Bill对象
            Bill bill = new Bill(title, "", date, amount, BillType.REPAYMENT, "还款", debtor.getName(), creditor.getName());

            // 先更新账户余额
            if (!updateAccountBalance(debtor, creditor, amount)) {
                return false; // 余额更新失败，不保存账单
            }

            // 保存账单
            billViewModel.insert(bill);

            Toast.makeText(getContext(), "还款保存成功", Toast.LENGTH_SHORT).show();
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "金额格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        } catch (Exception e) {
            Toast.makeText(getContext(), "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean updateBill(Bill bill) {
        if (bill == null) {
            return false;
        }

        // 填充UI控件
        editTextAmount.setText(String.valueOf(bill.getAmount()));
        // editTextDebtorAccount.setText(bill.getAccount());
        // editTextCreditorAccount.setText(bill.getTargetAccount());
        editTextDebtorAccount.setSelectedAccountByName(bill.getAccount());
        editTextCreditorAccount.setSelectedAccountByName(bill.getTargetAccount());

        
        // 设置日期
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        editTextDate.setText(dateFormat.format(bill.getDate()));

        return true;
    }
    // AccountSelectorListener接口实现
    @Override
    public void onAccountSelected(Account selectedAccount) {
        // 账户被选择时的处理逻辑
        // 可以在这里添加额外的业务逻辑，比如验证账户余额等
    }

    @Override
    public void onAccountCleared() {
        // 账户选择被清除时的处理逻辑
    }

    @Override
    public boolean onAccountSelectorClicked() {
        // 返回true允许显示选择对话框
        return true;
    }
}