package com.example.easyledger.ui;

import com.example.easyledger.database.Account;

/**
 * 账户选择器监听器接口
 * 用于处理账户选择相关的事件回调
 */
public interface AccountSelectorListener {
    
    /**
     * 当账户被选择时调用
     * @param selectedAccount 被选择的账户对象
     */
    void onAccountSelected(Account selectedAccount);
    
    /**
     * 当账户选择被清除时调用
     */
    void onAccountCleared();
    
    /**
     * 当账户选择器被点击时调用（在显示选择对话框之前）
     * @return 是否允许显示选择对话框，返回false可以阻止对话框显示
     */
    boolean onAccountSelectorClicked();
}
