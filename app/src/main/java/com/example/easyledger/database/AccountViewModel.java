package com.example.easyledger.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 账户视图模型类，用于连接UI和数据仓库
 */
public class AccountViewModel extends AndroidViewModel {
    private final AccountRepository repository;
    private final LiveData<List<Account>> allAccounts;

    // 构造函数
    public AccountViewModel(Application application) {
        super(application);
        repository = new AccountRepository(application);
        allAccounts = repository.getAllAccounts();
    }

    // 获取所有账户
    public LiveData<List<Account>> getAllAccounts() {
        return allAccounts;
    }

    // 插入账户
    public void insert(Account account) {
        repository.insert(account);
    }

    // 更新账户
    public void update(Account account) {
        repository.update(account);
    }

    // 删除账户
    public void delete(Account account) {
        repository.delete(account);
    }

    // 删除所有账户
    public void deleteAll() {
        repository.deleteAll();
    }

    // 根据ID查询账户
    public Account getAccountById(int id) {
        return repository.getAccountById(id);
    }

    // 根据名称查询账户
    public Account getAccountByName(String name) {
        return repository.getAccountByName(name);
    }

    // 查询指定类型的账户
    public LiveData<List<Account>> getAccountsByType(String type) {
        return repository.getAccountsByType(type);
    }

    // 查询账户总数
    public LiveData<Integer> getAccountCount() {
        return repository.getAccountCount();
    }

    // 查询所有账户的总余额
    public LiveData<Double> getTotalBalance() {
        return repository.getTotalBalance();
    }

    // 根据类别查询账户
    public LiveData<List<Account>> getAccountsByCategory(String category) {
        return repository.getAccountsByCategory(category);
    }

    // 查询正常账户
    public LiveData<List<Account>> getNormalAccounts() {
        return repository.getNormalAccounts();
    }

    // 查询信贷账户
    public LiveData<List<Account>> getCreditAccounts() {
        return repository.getCreditAccounts();
    }
}