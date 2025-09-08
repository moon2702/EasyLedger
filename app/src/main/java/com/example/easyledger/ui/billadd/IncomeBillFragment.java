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

public class IncomeBillFragment extends Fragment implements BillSaveable {

    private EditText editTextTitle;
    private EditText editTextAmount;
    private EditText editTextCategory;
    private EditText editTextAccount;
    private EditText editTextDate;
    private GridView gridSubCategories;
    private ArrayAdapter<String> subCategoryAdapter;
    private BillViewModel billViewModel;
    private String selectedSubCategory = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_income_bill, container, false);

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);

        // 获取视图控件
        editTextTitle = root.findViewById(R.id.editTextTitle);
        editTextAmount = root.findViewById(R.id.editTextAmount);
        editTextCategory = root.findViewById(R.id.editTextCategory);
        editTextAccount = root.findViewById(R.id.editTextAccount);
        editTextDate = root.findViewById(R.id.editTextDate);
        gridSubCategories = root.findViewById(R.id.gridSubCategories);
        Button btnSaveBill = root.findViewById(R.id.btnSaveBill);

        // 初始化收入子分类数据
        String[] incomeSubCategories = {
            "中奖", "理财", "奖金", "工资",
            "借入", "兼职", "二手", "报销",
            "消债"
        };

        // 设置GridView适配器
        subCategoryAdapter = new ArrayAdapter<>(
            requireContext(),
            R.layout.grid_item_subcategory,
            incomeSubCategories
        );
        gridSubCategories.setAdapter(subCategoryAdapter);

        // 设置GridView项点击监听器
        gridSubCategories.setOnItemClickListener((parent, view, position, id) -> {
            selectedSubCategory = incomeSubCategories[position];
            editTextCategory.setText("收入" + " · " + selectedSubCategory);
        });

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
            String title = editTextTitle.getText().toString().trim();
            String amountStr = editTextAmount.getText().toString().trim();
            String category = editTextCategory.getText().toString().trim();
            String account = editTextAccount.getText().toString().trim();
            String dateStr = editTextDate.getText().toString().trim();

            // 验证输入
            if (title.isEmpty() || amountStr.isEmpty() || category.isEmpty() || account.isEmpty() || dateStr.isEmpty()) {
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
            Bill bill = new Bill(title, "", date, amount, BillType.INCOME, subCategory, account);

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
        editTextAccount.setText(bill.getAccount());
        
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
}