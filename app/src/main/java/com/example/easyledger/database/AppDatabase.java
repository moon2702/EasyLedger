package com.example.easyledger.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 应用数据库类，定义数据库配置和版本
 */
@Database(entities = {Bill.class, Account.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, BillTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    // 获取BillDao实例
    public abstract BillDao billDao();

    // 获取AccountDao实例
    public abstract AccountDao accountDao();

    // 单例模式
    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // 获取数据库实例
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "easy_ledger_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // 数据库创建时的回调
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // 如果需要在数据库创建时插入初始数据，可以在这里实现
            databaseWriteExecutor.execute(() -> {
                BillDao dao = INSTANCE.billDao();
                // 清空表（可选）
                dao.deleteAll();

                // 插入示例数据
                /*
                Date date = new Date();
                Bill bill = new Bill("购物", "超市购物", date, 120.5, true, "日常消费", "银行卡");
                dao.insert(bill);
                bill = new Bill("工资", "月工资", date, 5000.0, false, "收入", "银行卡");
                dao.insert(bill);
                
                // 插入示例账户数据
                Account account = new Account("银行卡", "储蓄账户", 10000.0, "主要银行账户");
                INSTANCE.accountDao().insert(account);
                account = new Account("现金", "现金账户", 500.0, "随身携带的现金");
                INSTANCE.accountDao().insert(account);
                */
            });
        }
    };
}