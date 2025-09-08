package com.example.easyledger.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

/**
 * 账单视图模型类，用于连接UI和数据仓库
 */
public class BillViewModel extends AndroidViewModel {
    private final BillRepository repository;
    private final LiveData<List<Bill>> allBills;

    // 构造函数
    public BillViewModel(Application application) {
        super(application);
        repository = new BillRepository(application);
        allBills = repository.getAllBills();
    }

    // 获取所有账单
    public LiveData<List<Bill>> getAllBills() {
        return allBills;
    }

    // 插入账单
    public void insert(Bill bill) {
        repository.insert(bill);
    }

    // 更新账单
    public void update(Bill bill) {
        repository.update(bill);
    }

    // 删除账单
    public void delete(Bill bill) {
        repository.delete(bill);
    }

    // 删除所有账单
    public void deleteAll() {
        repository.deleteAll();
    }

    // 查询指定日期范围内的账单

    // 根据ID查询账单
    public Bill getBillById(int id) {
        return repository.getBillById(id);
    }
    public LiveData<List<Bill>> getBillsInRange(long startDate, long endDate) {
        return repository.getBillsInRange(startDate, endDate);
    }

    // 查询支出账单
    public LiveData<List<Bill>> getExpenseBills() {
        return repository.getExpenseBills();
    }

    // 查询收入账单
    public LiveData<List<Bill>> getIncomeBills() {
        return repository.getIncomeBills();
    }

    // 查询指定类别的账单
    public LiveData<List<Bill>> getBillsByCategory(String category) {
        return repository.getBillsByCategory(category);
    }

    // 查询总支出
    public LiveData<Double> getTotalExpense() {
        return repository.getTotalExpense();
    }

    // 查询总收入
    public LiveData<Double> getTotalIncome() {
        return repository.getTotalIncome();
    }
}