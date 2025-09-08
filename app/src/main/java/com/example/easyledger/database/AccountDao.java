package com.example.easyledger.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Account数据访问对象，定义对account表的操作
 */
@Dao
public interface AccountDao {
    // 插入一条账户
    @Insert
    void insert(Account account);

    // 插入多条账户
    @Insert
    void insertAll(Account... accounts);

    // 更新账户
    @Update
    void update(Account account);

    // 删除账户
    @Delete
    void delete(Account account);

    // 删除所有账户
    @Query("DELETE FROM account")
    void deleteAll();

    // 查询所有账户
    @Query("SELECT * FROM account ORDER BY name ASC")
    List<Account> getAllAccounts();

    // 查询所有账户（返回LiveData）
    @Query("SELECT * FROM account ORDER BY name ASC")
    LiveData<List<Account>> getAllAccountsLiveData();

    // 根据ID查询账户
    @Query("SELECT * FROM account WHERE id = :id")
    Account getAccountById(int id);

    // 根据名称查询账户
    @Query("SELECT * FROM account WHERE name = :name")
    Account getAccountByName(String name);

    // 查询指定类型的账户
    @Query("SELECT * FROM account WHERE type = :type ORDER BY name ASC")
    List<Account> getAccountsByType(String type);

    // 查询指定类型的账户（返回LiveData）
    @Query("SELECT * FROM account WHERE type = :type ORDER BY name ASC")
    LiveData<List<Account>> getAccountsByTypeLiveData(String type);

    // 查询账户总数
    @Query("SELECT COUNT(*) FROM account")
    int getAccountCount();

    // 查询账户总数（返回LiveData）
    @Query("SELECT COUNT(*) FROM account")
    LiveData<Integer> getAccountCountLiveData();

    // 查询所有账户的总余额
    @Query("SELECT SUM(balance) FROM account")
    double getTotalBalance();

    // 查询所有账户的总余额（返回LiveData）
    @Query("SELECT SUM(balance) FROM account")
    LiveData<Double> getTotalBalanceLiveData();
}