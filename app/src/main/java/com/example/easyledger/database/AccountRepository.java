package com.example.easyledger.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 账户仓库类，封装对账户数据的访问
 */
public class AccountRepository {
    private final AccountDao accountDao;
    private final LiveData<List<Account>> allAccounts;

    // 构造函数
    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        accountDao = db.accountDao();
        allAccounts = accountDao.getAllAccountsLiveData();
    }

    // 获取所有账户（LiveData）
    public LiveData<List<Account>> getAllAccounts() {
        return allAccounts;
    }

    // 插入账户
    public void insert(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.insert(account);
        });
    }

    // 更新账户
    public void update(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.update(account);
        });
    }

    // 删除账户
    public void delete(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.delete(account);
        });
    }

    // 删除所有账户
    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.deleteAll();
        });
    }

    // 根据ID查询账户
    public Account getAccountById(int id) {
        // 注意：这是同步操作，应该在后台线程调用
        return accountDao.getAccountById(id);
    }

    // 根据名称查询账户
    public Account getAccountByName(String name) {
        // 注意：这是同步操作，应该在后台线程调用
        return accountDao.getAccountByName(name);
    }

    // 查询指定类型的账户
    public LiveData<List<Account>> getAccountsByType(String type) {
        return accountDao.getAccountsByTypeLiveData(type);
    }

    // 查询账户总数
    public LiveData<Integer> getAccountCount() {
        return accountDao.getAccountCountLiveData();
    }

    // 查询所有账户的总余额
    public LiveData<Double> getTotalBalance() {
        return accountDao.getTotalBalanceLiveData();
    }
}