package com.example.easyledger.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.easyledger.R;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户选择器自定义View
 * 封装了账户选择的所有逻辑，包括数据加载、对话框显示、选择处理等
 */
public class AccountSelectorView extends LinearLayout {
    
    private TextView textViewSelector;
    private AccountSelectorListener listener;
    private List<Account> accounts = new ArrayList<>();
    private Account selectedAccount;
    private AccountViewModel accountViewModel;
    private LifecycleOwner lifecycleOwner;
    
    public AccountSelectorView(Context context) {
        super(context);
        init(context);
    }
    
    public AccountSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public AccountSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    /**
     * 初始化View
     */
    private void init(Context context) {
        // 设置布局方向
        setOrientation(VERTICAL);
        
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.selector_item, this, true);
        
        // 获取TextView控件
        textViewSelector = findViewById(R.id.textViewSelector);
        
        // 设置点击监听器
        textViewSelector.setOnClickListener(v -> {
            if (listener != null && !listener.onAccountSelectorClicked()) {
                return; // 如果监听器返回false，则不显示对话框
            }
            showAccountSelectionDialog();
        });
    }
    
    /**
     * 设置生命周期所有者，用于观察数据变化
     */
    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        loadAccounts();
    }
    
    /**
     * 设置账户选择监听器
     */
    public void setAccountSelectorListener(AccountSelectorListener listener) {
        this.listener = listener;
    }
    
    /**
     * 加载账户数据
     */
    private void loadAccounts() {
        if (lifecycleOwner == null) {
            return;
        }
        
        accountViewModel = new ViewModelProvider((androidx.fragment.app.FragmentActivity) getContext())
                .get(AccountViewModel.class);
        
        accountViewModel.getAllAccounts().observe(lifecycleOwner, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accountsData) {
                accounts.clear();
                if (accountsData != null) {
                    accounts.addAll(accountsData);
                }
            }
        });
    }
    
    /**
     * 显示账户选择对话框
     */
    private void showAccountSelectionDialog() {
        if (accounts.isEmpty()) {
            Toast.makeText(getContext(), "暂无账户数据", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建账户名称列表
        List<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择账户");
        builder.setItems(accountNames.toArray(new String[0]), (dialog, which) -> {
            if (which >= 0 && which < accounts.size()) {
                Account selected = accounts.get(which);
                setSelectedAccount(selected);
            }
        });
        builder.show();
    }
    
    /**
     * 设置选中的账户
     */
    public void setSelectedAccount(Account account) {
        this.selectedAccount = account;
        if (account != null) {
            textViewSelector.setText(account.getName());
        } else {
            textViewSelector.setText(getContext().getString(R.string.selector_hint));
        }
        
        // 通知监听器
        if (listener != null) {
            if (account != null) {
                listener.onAccountSelected(account);
            } else {
                listener.onAccountCleared();
            }
        }
    }
    
    /**
     * 获取当前选中的账户
     */
    public Account getSelectedAccount() {
        return selectedAccount;
    }
    
    /**
     * 获取当前选中的账户名称
     */
    public String getSelectedAccountName() {
        return selectedAccount != null ? selectedAccount.getName() : "";
    }
    
    /**
     * 清除选择
     */
    public void clearSelection() {
        setSelectedAccount(null);
    }
    
    /**
     * 设置提示文本
     */
    public void setHint(String hint) {
        textViewSelector.setHint(hint);
    }
    
    /**
     * 设置提示文本（通过资源ID）
     */
    public void setHint(int hintResId) {
        textViewSelector.setHint(hintResId);
    }
    
    /**
     * 检查是否有选中的账户
     */
    public boolean hasSelection() {
        return selectedAccount != null;
    }
    
    /**
     * 根据账户名称设置选中账户
     */
    public void setSelectedAccountByName(String accountName) {
        if (accountName == null || accountName.isEmpty()) {
            clearSelection();
            return;
        }
        
        for (Account account : accounts) {
            if (account.getName().equals(accountName)) {
                setSelectedAccount(account);
                return;
            }
        }
        
        // 如果没找到对应的账户，清除选择
        clearSelection();
    }
}
