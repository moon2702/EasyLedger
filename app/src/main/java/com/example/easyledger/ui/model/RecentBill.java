package com.example.easyledger.ui.model;

public class RecentBill {
    public final String title;
    public final String subtitle;
    public final String dateText;
    public final String amountText;
    public final boolean isExpense;

    public RecentBill(String title, String subtitle, String dateText, String amountText, boolean isExpense) {
        this.title = title;
        this.subtitle = subtitle;
        this.dateText = dateText;
        this.amountText = amountText;
        this.isExpense = isExpense;
    }
}


