package com.example.easyledger.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Bill数据访问对象，定义对bill表的操作
 */
@Dao
public interface BillDao {
    // 插入一条账单
    @Insert
    void insert(Bill bill);

    // 插入多条账单
    @Insert
    void insertAll(Bill... bills);

    // 更新账单
    @Update
    void update(Bill bill);

    // 删除账单
    @Delete
    void delete(Bill bill);

    // 删除所有账单
    @Query("DELETE FROM bill")
    void deleteAll();

    // 查询所有账单，按日期降序排列
    @Query("SELECT * FROM bill ORDER BY date DESC")
    List<Bill> getAllBills();

    // 查询所有账单（返回LiveData）
    @Query("SELECT * FROM bill ORDER BY date DESC")
    LiveData<List<Bill>> getAllBillsLiveData();

    // 根据ID查询账单
    @Query("SELECT * FROM bill WHERE id = :id")
    Bill getBillById(int id);

    // 查询指定日期范围内的账单
    @Query("SELECT * FROM bill WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Bill> getBillsInRange(long startDate, long endDate);

    // 查询指定日期范围内的账单（返回LiveData）
    @Query("SELECT * FROM bill WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Bill>> getBillsInRangeLiveData(long startDate, long endDate);

    // 查询支出账单
    @Query("SELECT * FROM bill WHERE type = 'EXPENSE' ORDER BY date DESC")
    List<Bill> getExpenseBills();

    // 查询支出账单（返回LiveData）
    @Query("SELECT * FROM bill WHERE type = 'EXPENSE' ORDER BY date DESC")
    LiveData<List<Bill>> getExpenseBillsLiveData();

    // 查询收入账单
    @Query("SELECT * FROM bill WHERE type = 'INCOME' ORDER BY date DESC")
    List<Bill> getIncomeBills();

    // 查询收入账单（返回LiveData）
    @Query("SELECT * FROM bill WHERE type = 'INCOME' ORDER BY date DESC")
    LiveData<List<Bill>> getIncomeBillsLiveData();

    // 查询转账账单
    @Query("SELECT * FROM bill WHERE type = 'TRANSFER' ORDER BY date DESC")
    List<Bill> getTransferBills();

    // 查询转账账单（返回LiveData）
    @Query("SELECT * FROM bill WHERE type = 'TRANSFER' ORDER BY date DESC")
    LiveData<List<Bill>> getTransferBillsLiveData();

    // 查询还款账单
    @Query("SELECT * FROM bill WHERE type = 'REPAYMENT' ORDER BY date DESC")
    List<Bill> getRepaymentBills();

    // 查询还款账单（返回LiveData）
    @Query("SELECT * FROM bill WHERE type = 'REPAYMENT' ORDER BY date DESC")
    LiveData<List<Bill>> getRepaymentBillsLiveData();

    // 查询指定类别的账单
    @Query("SELECT * FROM bill WHERE category = :category ORDER BY date DESC")
    List<Bill> getBillsByCategory(String category);

    // 查询指定类别的账单（返回LiveData）
    @Query("SELECT * FROM bill WHERE category = :category ORDER BY date DESC")
    LiveData<List<Bill>> getBillsByCategoryLiveData(String category);

    // 查询总支出
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'EXPENSE'")
    double getTotalExpense();

    // 查询总支出（返回LiveData）
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'EXPENSE'")
    LiveData<Double> getTotalExpenseLiveData();

    // 查询总收入
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'INCOME'")
    double getTotalIncome();

    // 查询总收入（返回LiveData）
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'INCOME'")
    LiveData<Double> getTotalIncomeLiveData();

    // 查询总转账金额
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'TRANSFER'")
    double getTotalTransfer();

    // 查询总转账金额（返回LiveData）
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'TRANSFER'")
    LiveData<Double> getTotalTransferLiveData();

    // 查询总还款金额
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'REPAYMENT'")
    double getTotalRepayment();

    // 查询总还款金额（返回LiveData）
    @Query("SELECT SUM(amount) FROM bill WHERE type = 'REPAYMENT'")
    LiveData<Double> getTotalRepaymentLiveData();
}