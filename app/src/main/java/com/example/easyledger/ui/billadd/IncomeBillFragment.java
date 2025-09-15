package com.example.easyledger.ui.billadd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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

public class IncomeBillFragment extends Fragment implements BillSaveable, AccountSelectorListener {

    private EditText editTextTitle;
    private EditText editTextAmount;
    private EditText editTextCategory;
    private AccountSelectorView editTextAccount;
    private EditText editTextDate;
    private GridView gridSubCategories;
    private ArrayAdapter<String> subCategoryAdapter;
    private BillViewModel billViewModel;
    private String selectedSubCategory = "";
    private AccountBalanceManager balanceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = null;
        try {
            root = inflater.inflate(R.layout.fragment_income_bill, container, false);
            
            // 初始化ViewModel
            if (getActivity() != null) {
                billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
            } else {
                android.util.Log.e("IncomeBillFragment", "Activity is null, cannot initialize ViewModel");
                return root;
            }

            // 初始化余额管理器
            balanceManager = new AccountBalanceManager(requireActivity());

            // 获取视图控件
            editTextTitle = root.findViewById(R.id.editTextTitle);
            editTextAmount = root.findViewById(R.id.editTextAmount);
            editTextCategory = root.findViewById(R.id.editTextCategory);
            editTextAccount = root.findViewById(R.id.editTextAccount);
            editTextDate = root.findViewById(R.id.editTextDate);
            gridSubCategories = root.findViewById(R.id.gridSubCategories);
            Button btnSaveBill = root.findViewById(R.id.btnSaveBill);
            
            // 检查关键控件是否成功获取
            if (editTextTitle == null || editTextAmount == null || editTextCategory == null || 
                editTextAccount == null || editTextDate == null || gridSubCategories == null) {
                android.util.Log.e("IncomeBillFragment", "Some views are null, layout may be corrupted");
                return root;
            }

            // 设置账户选择器
            editTextAccount.setLifecycleOwner(getViewLifecycleOwner());
            editTextAccount.setAccountSelectorListener(this);
            editTextAccount.setHint("选择账户");



            // 初始化收入子分类数据
            String[] incomeSubCategories = {
                "中奖", "理财", "奖金", "工资",
                "借入", "兼职", "二手", "报销",
                "消债"
            };

            // 设置GridView适配器
            try {
                subCategoryAdapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.grid_item_subcategory,
                    incomeSubCategories
                );
                gridSubCategories.setAdapter(subCategoryAdapter);

                // 设置GridView项点击监听器
                gridSubCategories.setOnItemClickListener((parent, view, position, id) -> {
                    try {
                        if (position >= 0 && position < incomeSubCategories.length) {
                            selectedSubCategory = incomeSubCategories[position];
                            if (editTextCategory != null) {
                                editTextCategory.setText("收入" + " · " + selectedSubCategory);
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.e("IncomeBillFragment", "Error in grid item click", e);
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("IncomeBillFragment", "Error setting up GridView", e);
            }

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
                        android.util.Log.e("IncomeBillFragment", "Error in save button click", e);
                        Toast.makeText(getContext(), "保存时发生错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            android.util.Log.e("IncomeBillFragment", "Error in onCreateView", e);
            // 如果发生异常，返回一个简单的视图
            if (root == null) {
                root = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
            }
        }

        return root;
    }

    private boolean updateAccountBalance(Account account, double amount) {
        if (balanceManager == null) {
            Toast.makeText(getContext(), "余额管理器未初始化", Toast.LENGTH_SHORT).show();
            return false;
        }

        AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleIncomeBill(account, amount);
        
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
            String title = editTextTitle.getText().toString().trim();
            String amountStr = editTextAmount.getText().toString().trim();
            String category = editTextCategory.getText().toString().trim();
            String dateStr = editTextDate.getText().toString().trim();
            Account selectedAccount = editTextAccount.getSelectedAccount();

            // 验证输入
            if (title.isEmpty() || amountStr.isEmpty() || category.isEmpty() || selectedAccount == null || dateStr.isEmpty()) {
                Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
                return false;
            }

            double amount = Double.parseDouble(amountStr);

            // TODO: 解析日期字符串为Date对象
            // 这里简化处理，实际应用中需要添加日期解析逻辑
            java.util.Date date = new java.util.Date();

            // 主分类默认为收入
            String mainCategory = "收入";
            // 使用选择的子分类
            String subCategory = selectedSubCategory.isEmpty() ? category : selectedSubCategory;

            // 创建Bill对象
            Bill bill = new Bill(title, "", date, amount, BillType.INCOME, subCategory, selectedAccount.getName());

            // 先更新账户余额
            if (!updateAccountBalance(selectedAccount, amount)) {
                return false; // 余额更新失败，不保存账单
            }

            // 保存账单
            billViewModel.insert(bill);

            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
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
        editTextTitle.setText(bill.getTitle());
        editTextAmount.setText(String.valueOf(bill.getAmount()));
        editTextAccount.setSelectedAccountByName(bill.getAccount());
        
        // 设置日期
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        editTextDate.setText(dateFormat.format(bill.getDate()));

        // 设置类别
        String subCategory = bill.getCategory();
        selectedSubCategory = subCategory;
        editTextCategory.setText("收入 · " + subCategory);

        // 初始化收入子分类数据
        String[] incomeSubCategories = {
            "中奖", "理财", "奖金", "工资",
            "借入", "兼职", "二手", "报销",
            "消债"
        };

        // 选中对应的子类别
        for (int i = 0; i < incomeSubCategories.length; i++) {
            if (incomeSubCategories[i].equals(subCategory)) {
                // 这里需要延迟执行，等待gridView绘制完成
                final int position = i;
                gridSubCategories.post(() -> {
                    View view = gridSubCategories.getChildAt(position);
                    if (view != null) {
                        view.setActivated(true);
                    }
                });
                break;
            }
        }

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