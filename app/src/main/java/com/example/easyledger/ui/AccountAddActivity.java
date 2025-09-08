package com.example.easyledger.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyledger.R;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;

public class AccountAddActivity extends AppCompatActivity {
    private EditText nameEditText;
    private Spinner typeSpinner;
    private EditText balanceEditText;
    private EditText descriptionEditText;
    private Button saveButton;
    private AccountViewModel accountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add);

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
        accountViewModel = new AccountViewModel(getApplication());

        // 保存按钮点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccount();
            }
        });
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

        // 创建账户对象
        Account account = new Account(name, type, balance, description);

        // 保存账户
        accountViewModel.insert(account);

        // 显示保存成功消息并关闭Activity
        Toast.makeText(this, "账户添加成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}