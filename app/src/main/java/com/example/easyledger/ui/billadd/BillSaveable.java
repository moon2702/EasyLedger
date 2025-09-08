package com.example.easyledger.ui.billadd;

import com.example.easyledger.database.Bill;

public interface BillSaveable {
    /**
     * 保存当前账单
     * @return 是否保存成功
     */
    boolean saveBill();

    /**
     * 更新当前账单
     * @param bill 要更新的账单对象
     * @return 是否更新成功
     */
    boolean updateBill(Bill bill);
}