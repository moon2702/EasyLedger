package com.example.easyledger.database;

import androidx.annotation.NonNull;

/**
 * 账单类型枚举
 */
public enum BillType {
    EXPENSE("支出"),       // 支出
    INCOME("收入"),        // 收入
    TRANSFER("转账"),      // 普通账户之间转移资产
    REPAYMENT("还款");     // 普通账户向信贷账户的转账

    private final String displayName;

    BillType(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}