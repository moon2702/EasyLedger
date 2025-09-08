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

public class RepaymentBillFragment extends Fragment implements BillSaveable {

    // private EditText editTextTitle;
    private EditText editTextAmount;
    // private EditText editTextCreditor;
    private EditText editTextDebtorAccount;
    private EditText editTextCreditorAccount;
    private EditText editTextDate;
    private BillViewModel billViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repayment_bill, container, false);

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);

        // 获取视图控件
        // editTextTitle = root.findViewById(R.id.editTextTitle);
        editTextAmount = root.findViewById(R.id.editTextAmount);
        // editTextCreditor = root.findViewById(R.id.editTextCreditor);
        editTextDebtorAccount = root.findViewById(R.id.editTextDebtorAccount);
        editTextCreditorAccount = root.findViewById(R.id.editTextCreditorAccount);
        editTextDate = root.findViewById(R.id.editTextDate);
        Button btnSaveBill = root.findViewById(R.id.btnSaveBill);

        // 设置保存按钮点击事件
        // if (btnSaveBill != null) {
        //     btnSaveBill.setOnClickListener(v -> saveBill());
        // }

        // 设置保存按钮点击事件
        if (btnSaveBill != null) {
            btnSaveBill.setOnClickListener(v -> {
                // 调用Activity中的保存方法
                if (getActivity() instanceof BillAddActivity) {
                    ((BillAddActivity) getActivity()).saveCurrentBill();
                } else {
                    saveBill();
                }
            });
        }

        return root;
    }

    @Override
    public boolean saveBill() {
        try {
            // 获取输入内容
            // String title = editTextTitle.getText().toString().trim();
            String title = "还款";
            String amountStr = editTextAmount.getText().toString().trim();
            String creditor = editTextCreditorAccount.getText().toString().trim();
            String account = editTextDebtorAccount.getText().toString().trim();
            String dateStr = editTextDate.getText().toString().trim();

            // 验证输入
            if (title.isEmpty() || amountStr.isEmpty() || creditor.isEmpty() || account.isEmpty() || dateStr.isEmpty()) {
                Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
                return false;
            }

            double amount = Double.parseDouble(amountStr);

            // TODO: 解析日期字符串为Date对象
            // 这里简化处理，实际应用中需要添加日期解析逻辑
            java.util.Date date = new java.util.Date();

            // 创建还款Bill对象
            Bill bill = new Bill(title, "", date, amount, BillType.REPAYMENT, "还款", account, creditor);

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
        editTextDebtorAccount.setText(bill.getAccount());
        editTextCreditorAccount.setText(bill.getTargetAccount());
        
        // 设置日期
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        editTextDate.setText(dateFormat.format(bill.getDate()));

        return true;
    }
}