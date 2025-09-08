package com.example.easyledger.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

/**
 * 账单仓库类，封装对账单数据的访问
 */
public class BillRepository {
    private final BillDao billDao;
    private final LiveData<List<Bill>> allBills;

    // 构造函数
    public BillRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        billDao = db.billDao();
        allBills = billDao.getAllBillsLiveData();
    }

    // 获取所有账单（LiveData）
    public LiveData<List<Bill>> getAllBills() {
        return allBills;
    }

    // 插入账单
    public void insert(Bill bill) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            billDao.insert(bill);
        });
    }

    // 更新账单
    public void update(Bill bill) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            billDao.update(bill);
        });
    }

    // 删除账单
    public void delete(Bill bill) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            billDao.delete(bill);
        });
    }

    // 删除所有账单
    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            billDao.deleteAll();
        });
    }

    // 根据ID查询账单
    public Bill getBillById(int id) {
        // 注意：这是同步操作，应该在后台线程调用
        return billDao.getBillById(id);
    }

    // 查询指定日期范围内的账单
    public LiveData<List<Bill>> getBillsInRange(long startDate, long endDate) {
        return billDao.getBillsInRangeLiveData(startDate, endDate);
    }

    // 查询支出账单
    public LiveData<List<Bill>> getExpenseBills() {
        return billDao.getExpenseBillsLiveData();
    }

    // 查询收入账单
    public LiveData<List<Bill>> getIncomeBills() {
        return billDao.getIncomeBillsLiveData();
    }

    // 查询指定类别的账单
    public LiveData<List<Bill>> getBillsByCategory(String category) {
        return billDao.getBillsByCategoryLiveData(category);
    }

    // 查询总支出
    public LiveData<Double> getTotalExpense() {
        return billDao.getTotalExpenseLiveData();
    }

    // 查询总收入
    public LiveData<Double> getTotalIncome() {
        return billDao.getTotalIncomeLiveData();
    }
}