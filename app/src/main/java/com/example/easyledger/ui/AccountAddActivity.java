package com.example.easyledger.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.easyledger.R;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class AccountAddActivity extends AppCompatActivity {
    public static final String EXTRA_ACCOUNT_ID = "com.example.easyledger.EXTRA_ACCOUNT_ID";
    
    private MaterialToolbar toolbar;
    private EditText nameEditText;
    private Spinner typeSpinner;
    private Spinner categorySpinner;
    private TextView balanceLabel;
    private EditText balanceEditText;
    private EditText descriptionEditText;
    private Button saveButton;
    private AccountViewModel accountViewModel;
    private Account currentAccount;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add);

        // 初始化Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化控件
        nameEditText = findViewById(R.id.account_name);
        typeSpinner = findViewById(R.id.account_type);
        categorySpinner = findViewById(R.id.account_category);
        balanceLabel = findViewById(R.id.balance_label);
        balanceEditText = findViewById(R.id.account_balance);
        descriptionEditText = findViewById(R.id.account_description);
        saveButton = findViewById(R.id.save_account);

        // 初始化账户类型下拉列表
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // 初始化账户类别下拉列表
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.account_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        
        // 设置账户类别变化监听器
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateBalanceLabelAndHint();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 默认设置为正常账户
                updateBalanceLabelAndHint();
            }
        });

        // 初始化ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // 检查是否为编辑模式
        int accountId = getIntent().getIntExtra(EXTRA_ACCOUNT_ID, -1);
        if (accountId != -1) {
            isEditMode = true;
            toolbar.setTitle("编辑账户");
            loadAccountForEdit(accountId);
        } else {
            toolbar.setTitle("添加账户");
        }

        // 保存按钮点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccount();
            }
        });
    }

    private void loadAccountForEdit(int accountId) {
        accountViewModel.getAllAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                for (Account account : accounts) {
                    if (account.getId() == accountId) {
                        currentAccount = account;
                        populateFields(account);
                        break;
                    }
                }
            }
        });
    }

    private void populateFields(Account account) {
        nameEditText.setText(account.getName());
        
        // 根据账户类型显示不同的值
        if (account.isCreditAccount()) {
            balanceEditText.setText(String.valueOf(account.getCreditLimit()));
        } else {
            balanceEditText.setText(String.valueOf(account.getBalance()));
        }
        
        descriptionEditText.setText(account.getDescription());
        
        // 设置账户类型
        ArrayAdapter<CharSequence> typeAdapter = (ArrayAdapter<CharSequence>) typeSpinner.getAdapter();
        for (int i = 0; i < typeAdapter.getCount(); i++) {
            if (typeAdapter.getItem(i).toString().equals(account.getType())) {
                typeSpinner.setSelection(i);
                break;
            }
        }
        
        // 设置账户类别
        String categoryDisplay = account.getCategory().equals("NORMAL") ? "正常账户" : "信贷账户";
        ArrayAdapter<CharSequence> categoryAdapter = (ArrayAdapter<CharSequence>) categorySpinner.getAdapter();
        for (int i = 0; i < categoryAdapter.getCount(); i++) {
            if (categoryAdapter.getItem(i).toString().equals(categoryDisplay)) {
                categorySpinner.setSelection(i);
                break;
            }
        }
        
        // 更新余额标签和提示
        updateBalanceLabelAndHint();
    }

    /**
     * 根据账户类别更新余额标签和提示文本
     */
    private void updateBalanceLabelAndHint() {
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        
        if ("信贷账户".equals(selectedCategory)) {
            balanceLabel.setText("最大额度");
            balanceEditText.setHint("请输入最大额度");
        } else {
            balanceLabel.setText("初始余额");
            balanceEditText.setHint("请输入初始余额");
        }
    }

    private void saveAccount() {
        String name = nameEditText.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        String categoryDisplay = categorySpinner.getSelectedItem().toString();
        String category = categoryDisplay.equals("正常账户") ? "NORMAL" : "CREDIT";
        String balanceStr = balanceEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // 验证输入
        if (name.isEmpty()) {
            Toast.makeText(this, "请输入账户名称", Toast.LENGTH_SHORT).show();
            return;
        }

        double balance = 0.0;
        if (!balanceStr.isEmpty()) {
            try {
                balance = Double.parseDouble(balanceStr);
            } catch (NumberFormatException e) {
                String errorMessage = "信贷账户".equals(categoryDisplay) ? 
                    "请输入有效的最大额度" : "请输入有效的余额";
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (isEditMode && currentAccount != null) {
            // 编辑模式：更新现有账户
            currentAccount.setName(name);
            currentAccount.setType(type);
            currentAccount.setCategory(category);
            currentAccount.setDescription(description);
            
            // 根据账户类型更新不同字段
            if ("CREDIT".equals(category)) {
                // 信贷账户：更新信用额度
                currentAccount.setCreditLimit(balance);
                // 信贷账户的余额字段保持为0
                currentAccount.setBalance(0);
            } else {
                // 正常账户：更新余额
                currentAccount.setBalance(balance);
                // 正常账户的信用额度保持为0
                currentAccount.setCreditLimit(0);
                currentAccount.setUsedCredit(0);
            }
            
            accountViewModel.update(currentAccount);
            Toast.makeText(this, "账户更新成功", Toast.LENGTH_SHORT).show();
        } else {
            // 新增模式：创建新账户
            Account account = new Account(name, type, balance, description, category);
            accountViewModel.insert(account);
            Toast.makeText(this, "账户添加成功", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_add_menu, menu);
        
        // 只有在编辑模式下才显示删除按钮
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (deleteItem != null) {
            deleteItem.setVisible(isEditMode);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_delete) {
            deleteCurrentAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除当前账户
     */
    private void deleteCurrentAccount() {
        if (currentAccount != null) {
            new AlertDialog.Builder(this)
                    .setTitle("删除账户")
                    .setMessage("确定要删除账户 \"" + currentAccount.getName() + "\" 吗？\n\n注意：删除账户将同时删除所有相关的账单记录。")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 执行删除操作
                            accountViewModel.delete(currentAccount);
                            Toast.makeText(AccountAddActivity.this, "账户删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }
}