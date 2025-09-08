package com.example.easyledger.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import com.example.easyledger.database.BillType;

/**
 * 账单实体类，对应数据库中的bill表
 */
@Entity(tableName = "bill")
public class Bill {
    @PrimaryKey(autoGenerate = true)
    private int id;            // 账单唯一标识符，自动生成
    private String title;      // 账单标题
    private String subtitle;   // 账单副标题
    private Date date;         // 账单日期
    private double amount;     // 账单金额
    private BillType type;     // 账单类型(支出、收入、转账、还款)
    private String category;   // 账单类别
    private String account;    // 支付账户/源账户
    private String targetAccount; // 目标账户
    // private String remark;     // 账单备注

    // 支出收入类型构造函数
    public Bill(String title, String subtitle, Date date, double amount, BillType type, String category, String account) {
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.account = account;
        this.targetAccount = null;
    }

    // 转账还款类型构造函数
    public Bill(String title, String subtitle, Date date, double amount, BillType type, String category, String fromAccount, String toAccount) {
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.account = fromAccount;
        this.targetAccount = toAccount;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }
}