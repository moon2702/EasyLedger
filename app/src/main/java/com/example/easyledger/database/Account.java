package com.example.easyledger.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 账户实体类，对应数据库中的account表
 */
@Entity(tableName = "account")
public class Account {
    @PrimaryKey(autoGenerate = true)
    private int id;            // 账户唯一标识符，自动生成
    private String name;       // 账户名称
    private String type;       // 账户类型(银行卡、现金、支付宝等)
    private double balance;    // 账户余额
    private double creditLimit; // 信贷账户的信用额度
    private double usedCredit;  // 已使用的信用额度
    private String description; // 账户描述
    private String category;   // 账户类别(NORMAL-正常账户, CREDIT-信贷账户)

    // 带类别参数的构造函数（Room使用这个）
    public Account(String name, String type, double balance, String description, String category) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
        
        if ("CREDIT".equals(category)) {
            // 信贷账户：balance作为信用额度，usedCredit初始为0
            this.creditLimit = balance;
            this.usedCredit = 0;
            this.balance = 0; // 信贷账户的balance字段设为0
        } else {
            // 正常账户
            this.balance = balance;
            this.creditLimit = 0;
            this.usedCredit = 0;
        }
    }

    // 简化构造函数（忽略，用于向后兼容）
    @Ignore
    public Account(String name, String type, double balance, String description) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.description = description;
        this.category = "NORMAL"; // 默认为正常账户
        this.creditLimit = 0;
        this.usedCredit = 0;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public double getUsedCredit() {
        return usedCredit;
    }

    public void setUsedCredit(double usedCredit) {
        this.usedCredit = usedCredit;
    }

    // 信贷账户专用方法
    /**
     * 判断是否为信贷账户
     */
    public boolean isCreditAccount() {
        return "CREDIT".equals(category);
    }

    /**
     * 获取可用信用额度
     */
    public double getAvailableCredit() {
        if (!isCreditAccount()) {
            return 0;
        }
        return creditLimit - usedCredit;
    }

    /**
     * 获取信贷账户的显示余额（负数表示欠款）
     */
    public double getCreditDisplayBalance() {
        if (!isCreditAccount()) {
            return balance;
        }
        return -usedCredit; // 负数表示欠款
    }

    /**
     * 检查是否超出信用额度
     */
    public boolean isOverCreditLimit(double amount) {
        if (!isCreditAccount()) {
            return false;
        }
        return (usedCredit + amount) > creditLimit;
    }

    /**
     * 更新信贷账户使用额度
     */
    public boolean updateCreditUsage(double amount, boolean isExpense) {
        if (!isCreditAccount()) {
            return false;
        }
        
        if (isExpense) {
            // 支出：增加使用额度
            if (isOverCreditLimit(amount)) {
                return false; // 超出信用额度
            }
            this.usedCredit += amount;
        } else {
            // 还款：减少使用额度
            this.usedCredit = Math.max(0, this.usedCredit - amount);
        }
        return true;
    }

    /**
     * 获取格式化的余额显示
     */
    public String getFormattedBalance() {
        if (isCreditAccount()) {
            if (usedCredit == 0) {
                return String.format("总额度: %.2f", creditLimit);
            } else {
                return String.format("已用: %.2f / %.2f", usedCredit, creditLimit);
            }
        } else {
            return String.format("%.2f", balance);
        }
    }
}