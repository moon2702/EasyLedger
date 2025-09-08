package com.example.easyledger.database;

import androidx.room.Entity;
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
    private String description; // 账户描述

    // 构造函数
    public Account(String name, String type, double balance, String description) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.description = description;
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
}