package com.example.easyledger.ui.billadd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.easyledger.R;
import com.example.easyledger.database.Bill;
import com.example.easyledger.database.BillType;
import com.example.easyledger.database.BillViewModel;
import com.example.easyledger.BillAddActivity;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import com.example.easyledger.ui.AccountSelectorView;
import com.example.easyledger.ui.AccountSelectorListener;
import androidx.lifecycle.ViewModelProvider;
import java.util.*;
import android.widget.AdapterView.OnItemClickListener;
import androidx.lifecycle.Observer;
import java.util.List;

import android.util.Log;
import android.app.AlertDialog;

public class ExpenseBillFragment extends Fragment implements BillSaveable, AccountSelectorListener {

    private EditText editTextTitle;
    private EditText editTextAmount;
    private AccountSelectorView accountSelector;
    private EditText editTextDate;
    private EditText editTextCategory;
    private Button btnCategoryAll;
    private Button btnCategoryShopping;
    private Button btnCategoryFood;
    private Button btnCategoryTransport;
    private Button btnCategoryEntertainment;
    private Button btnCategoryHome;
    private Button btnCategoryEducation;
    private Button btnCategoryHealth;
    private TextView tvSubCategoryTitle;
    private GridView gridSubCategories;
    private Button btnSaveBill;
    private BillViewModel billViewModel;

    // 类别和子类别数据
    private Map<String, List<String>> categoryMap;
    private String selectedCategory = ""; // 当前选择的类别
    private String selectedSubCategory = ""; // 当前选择的子类别
    private ArrayAdapter<String> subCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense_bill, container, false);

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);

        // 初始化类别数据
        initCategoryData();

        // 获取视图控件
        editTextTitle = root.findViewById(R.id.editTextTitle);
        editTextAmount = root.findViewById(R.id.editTextAmount);
        accountSelector = root.findViewById(R.id.accountSelector);
        editTextDate = root.findViewById(R.id.editTextDate);
        editTextCategory = root.findViewById(R.id.editTextCategory);

        // 设置账户选择器
        accountSelector.setLifecycleOwner(getViewLifecycleOwner());
        accountSelector.setAccountSelectorListener(this);
        accountSelector.setHint("选择账户");
        btnCategoryAll = root.findViewById(R.id.btnCategoryAll);
        btnCategoryShopping = root.findViewById(R.id.btnCategoryShopping);
        btnCategoryFood = root.findViewById(R.id.btnCategoryFood);
        btnCategoryTransport = root.findViewById(R.id.btnCategoryTransport);
        btnCategoryEntertainment = root.findViewById(R.id.btnCategoryEntertainment);
        btnCategoryHome = root.findViewById(R.id.btnCategoryHome);
        btnCategoryEducation = root.findViewById(R.id.btnCategoryEducation);
        btnCategoryHealth = root.findViewById(R.id.btnCategoryHealth);
        tvSubCategoryTitle = root.findViewById(R.id.tvSubCategoryTitle);
        gridSubCategories = root.findViewById(R.id.gridSubCategories);
        btnSaveBill = root.findViewById(R.id.btnSaveBill);

        // 设置类别按钮点击事件
        setCategoryButtonListeners();

        // 初始化子类别适配器
        subCategoryAdapter = new ArrayAdapter<>(
            requireContext(),
            R.layout.grid_item_subcategory,
            new ArrayList<>()
        );
        gridSubCategories.setAdapter(subCategoryAdapter);

        // 设置子类别选择事件
        // gridSubCategories.setOnItemClickListener((parent, view, position, id) -> {
        //     selectedSubCategory = subCategoryAdapter.getItem(position);
        //     // 高亮选中的子类别
        //     for (int i = 0; i < parent.getChildCount(); i++) {
        //         parent.getChildAt(i).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        //     }

        //     view.setBackgroundColor(getResources().getColor(R.color.teal_200));
        // });

        gridSubCategories.setOnItemClickListener((parent, view, position, id) -> {
            selectedSubCategory = subCategoryAdapter.getItem(position);
            // 更新已选择分类文本
            editTextCategory.setText(selectedCategory + " · " + selectedSubCategory);
            // 高亮选中的子类别
            for (int i = 0; i < parent.getChildCount(); i++) {
                parent.getChildAt(i).setActivated(false);
            }
            view.setActivated(true);
        });

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

    // 更新账户余额
    private void updateAccountBalance(Account account, double amount) {
        if (account == null) {
            return;
        }
        
        // 获取AccountViewModel实例
        AccountViewModel accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        // 支出账单，减少账户余额
        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        // 更新账户
        accountViewModel.update(account);
    }

    // 初始化类别数据
    private void initCategoryData() {
        categoryMap = new HashMap<>();

        // 其他类别
        List<String> otherSubCategories = new ArrayList<>();
        otherSubCategories.add("罚款");
        otherSubCategories.add("慈善");
        otherSubCategories.add("理财");
        otherSubCategories.add("借出");
        otherSubCategories.add("还债");
        categoryMap.put("其他类型", otherSubCategories);

        // 购物消费类别
        List<String> shoppingSubCategories = new ArrayList<>();
        shoppingSubCategories.add("日常");
        shoppingSubCategories.add("电子数码");
        shoppingSubCategories.add("生活电器");
        shoppingSubCategories.add("服饰配饰");
        shoppingSubCategories.add("宠物");
        shoppingSubCategories.add("办公");
        categoryMap.put("购物消费", shoppingSubCategories);

        // 食品餐饮类别
        List<String> foodSubCategories = new ArrayList<>();
        foodSubCategories.add("三餐");
        foodSubCategories.add("零食饮料");
        foodSubCategories.add("生鲜蔬菜");
        foodSubCategories.add("粮油调味");
        categoryMap.put("食品餐饮", foodSubCategories);

        // 出行交通类别
        List<String> transportSubCategories = new ArrayList<>();
        transportSubCategories.add("公共交通");
        transportSubCategories.add("打车");
        transportSubCategories.add("停车加油");
        transportSubCategories.add("养护保险");
        categoryMap.put("出行交通", transportSubCategories);

        // 休闲娱乐类别
        List<String> entertainmentSubCategories = new ArrayList<>();
        entertainmentSubCategories.add("旅行度假");
        entertainmentSubCategories.add("电影唱歌");
        entertainmentSubCategories.add("运动健身");
        entertainmentSubCategories.add("棋牌桌游");
        entertainmentSubCategories.add("虚拟充值");
        categoryMap.put("休闲娱乐", entertainmentSubCategories);

        // 居家生活类别
        List<String> homeSubCategories = new ArrayList<>();
        homeSubCategories.add("水电燃气");
        homeSubCategories.add("电话宽带");
        homeSubCategories.add("房租还贷");
        homeSubCategories.add("家政清洁");
        categoryMap.put("居家生活", homeSubCategories);

        // 文化教育类别
        List<String> educationSubCategories = new ArrayList<>();
        educationSubCategories.add("学费资料");
        educationSubCategories.add("培训考试");
        categoryMap.put("文化教育", educationSubCategories);

        // 健康医疗类别
        List<String> healthSubCategories = new ArrayList<>();
        healthSubCategories.add("买药看病");
        healthSubCategories.add("问诊手术");
        categoryMap.put("健康医疗", healthSubCategories);
    }

    // 设置类别按钮监听器
    private void setCategoryButtonListeners() {
        View.OnClickListener categoryClickListener = v -> {
            // 重置所有按钮样式
            resetCategoryButtons();

            // 设置当前按钮样式
            v.setBackgroundColor(getResources().getColor(R.color.teal_200));

            // 获取选择的类别
            Button button = (Button) v;
            selectedCategory = button.getText().toString();

            // 显示对应的子类别
            List<String> subCategories = categoryMap.get(selectedCategory);
            if (subCategories != null && !subCategories.isEmpty()) {
                tvSubCategoryTitle.setVisibility(View.VISIBLE);
                gridSubCategories.setVisibility(View.VISIBLE);
                subCategoryAdapter.clear();
                subCategoryAdapter.addAll(subCategories);
                subCategoryAdapter.notifyDataSetChanged();
                selectedSubCategory = "";
                // 更新已选择分类文本为当前主分类
                editTextCategory.setText(selectedCategory);
            }
        };

        // 为所有类别按钮设置监听器
        btnCategoryAll.setOnClickListener(categoryClickListener);
        btnCategoryShopping.setOnClickListener(categoryClickListener);
        btnCategoryFood.setOnClickListener(categoryClickListener);
        btnCategoryTransport.setOnClickListener(categoryClickListener);
        btnCategoryEntertainment.setOnClickListener(categoryClickListener);
        btnCategoryHome.setOnClickListener(categoryClickListener);
        btnCategoryEducation.setOnClickListener(categoryClickListener);
        btnCategoryHealth.setOnClickListener(categoryClickListener);
    }

    // 选择类别按钮
    private void selectCategoryButton(String category) {
        resetCategoryButtons();
        
        if (category.equals("其他类型")) {
            btnCategoryAll.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("购物消费")) {
            btnCategoryShopping.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("食品餐饮")) {
            btnCategoryFood.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("出行交通")) {
            btnCategoryTransport.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("休闲娱乐")) {
            btnCategoryEntertainment.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("居家生活")) {
            btnCategoryHome.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("文化教育")) {
            btnCategoryEducation.setBackgroundColor(getResources().getColor(R.color.teal_200));
        } else if (category.equals("健康医疗")) {
            btnCategoryHealth.setBackgroundColor(getResources().getColor(R.color.teal_200));
        }
    }

    // 更新子类别列表
    private void updateSubCategories(String category) {
        List<String> subCategories = categoryMap.get(category);
        if (subCategories != null && !subCategories.isEmpty()) {
            tvSubCategoryTitle.setVisibility(View.VISIBLE);
            gridSubCategories.setVisibility(View.VISIBLE);
            subCategoryAdapter.clear();
            subCategoryAdapter.addAll(subCategories);
            subCategoryAdapter.notifyDataSetChanged();
        }
    }

    // 选择子类别
    private void selectSubCategory(String subCategory) {
        for (int i = 0; i < gridSubCategories.getChildCount(); i++) {
            View child = gridSubCategories.getChildAt(i);
            TextView textView = (TextView) child;
            if (textView.getText().toString().equals(subCategory)) {
                child.setActivated(true);
                editTextCategory.setText(selectedCategory + " · " + subCategory);
                break;
            }
        }
    }

    // 重置类别按钮样式
    private void resetCategoryButtons() {
        btnCategoryAll.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryShopping.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryFood.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryTransport.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryEntertainment.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryHome.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryEducation.setBackgroundResource(R.drawable.category_button_bg);
        btnCategoryHealth.setBackgroundResource(R.drawable.category_button_bg);
    }

    @Override
    public boolean saveBill() {
        try {
            // 获取输入内容
            String title = editTextTitle.getText().toString().trim();
            String amountStr = editTextAmount.getText().toString().trim();
            String dateStr = editTextDate.getText().toString().trim();
            Account selectedAccount = accountSelector.getSelectedAccount();

            // 验证输入
            if (title.isEmpty() || amountStr.isEmpty() || selectedAccount == null || dateStr.isEmpty()) {
                Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
                return false;
            }

            // 验证类别选择
            if (selectedCategory.isEmpty()) {
                Toast.makeText(getContext(), "请选择类别", Toast.LENGTH_SHORT).show();
                return false;
            }

            // 如果有子类别，使用子类别；否则使用主类别
            String finalCategory = selectedSubCategory.isEmpty() ? selectedCategory : selectedCategory + "/" + selectedSubCategory;

            double amount = Double.parseDouble(amountStr);

            // TODO: 解析日期字符串为Date对象
            // 这里简化处理，实际应用中需要添加日期解析逻辑
            java.util.Date date = new java.util.Date();

            // 创建Bill对象
            Bill bill = new Bill(title, "", date, amount, BillType.EXPENSE, finalCategory, selectedAccount.getName());

            // 保存账单
            billViewModel.insert(bill);

            // 更新账户余额
            updateAccountBalance(selectedAccount, amount);

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
        accountSelector.setSelectedAccountByName(bill.getAccount());
        
        // 设置日期（简化处理，实际应用中应使用SimpleDateFormat）
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        editTextDate.setText(dateFormat.format(bill.getDate()));

        // 设置类别
        String category = bill.getCategory();
        if (category != null && !category.isEmpty()) {
            // 处理主类别和子类别
            String[] categoryParts = category.split("/");
            if (categoryParts.length > 0) {
                selectedCategory = categoryParts[0];
                // 选择对应的类别按钮
                selectCategoryButton(selectedCategory);
                
                if (categoryParts.length > 1) {
                    selectedSubCategory = categoryParts[1];
                    // 更新子类别列表并选择
                    updateSubCategories(selectedCategory);
                    selectSubCategory(selectedSubCategory);
                } else {
                    // 只有主类别
                    updateSubCategories(selectedCategory);
                    editTextCategory.setText(selectedCategory);
                }
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