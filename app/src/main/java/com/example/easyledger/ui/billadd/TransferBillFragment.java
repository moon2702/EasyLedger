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

public class TransferBillFragment extends Fragment implements BillSaveable, AccountSelectorListener {

    // private EditText editTextTitle;
    private EditText editTextAmount;
    private AccountSelectorView editTextFromAccount;
    private AccountSelectorView editTextToAccount;
    private EditText editTextDate;
    private BillViewModel billViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = null;
        try {
            root = inflater.inflate(R.layout.fragment_transfer_bill, container, false);

            // 初始化ViewModel
            if (getActivity() != null) {
                billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
            } else {
                android.util.Log.e("TransferBillFragment", "Activity is null, cannot initialize ViewModel");
                return root;
            }

            // 获取视图控件
            editTextAmount = root.findViewById(R.id.editTextAmount);
            editTextFromAccount = root.findViewById(R.id.editTextFromAccount);
            editTextToAccount = root.findViewById(R.id.editTextToAccount);
            editTextDate = root.findViewById(R.id.editTextDate);
            Button btnSaveBill = root.findViewById(R.id.btnSaveBill);
            
            // 检查关键控件是否成功获取
            if (editTextAmount == null || editTextFromAccount == null || 
                editTextToAccount == null || editTextDate == null) {
                android.util.Log.e("TransferBillFragment", "Some views are null, layout may be corrupted");
                return root;
            }

            editTextFromAccount.setLifecycleOwner(getViewLifecycleOwner());
            editTextFromAccount.setAccountSelectorListener(this);
            editTextFromAccount.setHint("选择转出账户");
            editTextToAccount.setLifecycleOwner(getViewLifecycleOwner());
            editTextToAccount.setAccountSelectorListener(this);
            editTextToAccount.setHint("选择转入账户");

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
                        android.util.Log.e("TransferBillFragment", "Error in save button click", e);
                        Toast.makeText(getContext(), "保存时发生错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            android.util.Log.e("TransferBillFragment", "Error in onCreateView", e);
            // 如果发生异常，返回一个简单的视图
            if (root == null) {
                root = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
            }
        }

        return root;
    }

    @Override
    public boolean saveBill() {
        try {
            // 获取输入内容
            // String title = editTextTitle.getText().toString().trim();
            String title = "转账";
            String amountStr = editTextAmount.getText().toString().trim();
            // String fromAccount = editTextFromAccount.getText().toString().trim();
            // String toAccount = editTextToAccount.getText().toString().trim();
            Account fromAccount = editTextFromAccount.getSelectedAccount();
            Account toAccount = editTextToAccount.getSelectedAccount();
            String dateStr = editTextDate.getText().toString().trim();

            // 验证输入
            if (title.isEmpty() || amountStr.isEmpty() || fromAccount == null || toAccount == null || dateStr.isEmpty()) {
                Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (fromAccount.equals(toAccount)) {
                Toast.makeText(getContext(), "转出账户和转入账户不能相同", Toast.LENGTH_SHORT).show();
                return false;
            }

            double amount = Double.parseDouble(amountStr);

            // TODO: 解析日期字符串为Date对象
            // 这里简化处理，实际应用中需要添加日期解析逻辑
            java.util.Date date = new java.util.Date();

            // 创建转账Bill对象
            Bill bill = new Bill(title, "", date, amount, BillType.TRANSFER, "转账", fromAccount.getName(), toAccount.getName());

            // 保存账单
            billViewModel.insert(bill);

            Toast.makeText(getContext(), "转账保存成功", Toast.LENGTH_SHORT).show();
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
        // editTextFromAccount.setText(bill.getAccount());
        // editTextToAccount.setText(bill.getTargetAccount());
        editTextFromAccount.setSelectedAccountByName(bill.getAccount());
        editTextToAccount.setSelectedAccountByName(bill.getTargetAccount());
        
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