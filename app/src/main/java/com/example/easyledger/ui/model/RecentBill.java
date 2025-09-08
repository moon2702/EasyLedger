package com.example.easyledger.ui.model;

public class RecentBill {
    public final int id;
    public final String title;
    public final String subtitle;
    public final String dateText;
    public final String amountText;
    public final boolean isRepaymentOrTransfer;
    public final boolean isExpense;

    public RecentBill(int id, String title, String subtitle, String dateText, String amountText, boolean isRepaymentOrTransfer, boolean isExpense) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.dateText = dateText;
        this.amountText = amountText;
        this.isRepaymentOrTransfer = isRepaymentOrTransfer;
        this.isExpense = isExpense;
    }
}


