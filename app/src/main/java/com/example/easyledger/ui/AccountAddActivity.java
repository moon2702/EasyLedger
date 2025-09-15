package com.example.easyledger.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
        balanceEditText = findViewById(R.id.account_balance);
        descriptionEditText = findViewById(R.id.account_description);
        saveButton = findViewById(R.id.save_account);

        // 初始化账户类型下拉列表
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

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
        balanceEditText.setText(String.valueOf(account.getBalance()));
        descriptionEditText.setText(account.getDescription());
        
        // 设置账户类型
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) typeSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(account.getType())) {
                typeSpinner.setSelection(i);
                break;
            }
        }
    }

    private void saveAccount() {
        String name = nameEditText.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
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
                Toast.makeText(this, "请输入有效的余额", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (isEditMode && currentAccount != null) {
            // 编辑模式：更新现有账户
            currentAccount.setName(name);
            currentAccount.setType(type);
            currentAccount.setBalance(balance);
            currentAccount.setDescription(description);
            accountViewModel.update(currentAccount);
            Toast.makeText(this, "账户更新成功", Toast.LENGTH_SHORT).show();
        } else {
            // 新增模式：创建新账户
            Account account = new Account(name, type, balance, description);
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