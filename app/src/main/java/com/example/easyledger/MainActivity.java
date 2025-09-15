package com.example.easyledger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.easyledger.database.Bill;
import com.example.easyledger.database.BillType;
import com.example.easyledger.database.BillViewModel;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import com.example.easyledger.ui.HomeFragment;
import com.example.easyledger.ui.CalendarFragment;
import com.example.easyledger.ui.AssetsFragment;
import com.example.easyledger.ui.StatsFragment;
import com.example.easyledger.ui.SettingsFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private BillViewModel billViewModel;
    private AccountViewModel accountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(this).get(BillViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // 初始化测试数据
        initTestData();

        // FAB 迁移到 HomeFragment 中

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            // 初始化并缓存 Fragments
            HomeFragment homeFragment = new HomeFragment();
            CalendarFragment calendarFragment = new CalendarFragment();
            AssetsFragment assetsFragment = new AssetsFragment();
            // StatsFragment statsFragment = new StatsFragment();
            // SettingsFragment settingsFragment = new SettingsFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    // .add(R.id.contentContainer, settingsFragment, "settings").hide(settingsFragment)
                    // .add(R.id.contentContainer, statsFragment, "stats").hide(statsFragment)
                    .add(R.id.contentContainer, assetsFragment, "assets").hide(assetsFragment)
                    .add(R.id.contentContainer, calendarFragment, "calendar").hide(calendarFragment)
                    .add(R.id.contentContainer, homeFragment, "home")
                    .commit();

            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    showOnly("home");
                    return true;
                } else if (id == R.id.nav_calendar) {
                    showOnly("calendar");
                    return true;
                } else if (id == R.id.nav_assets) {
                    showOnly("assets");
                    return true;
                }
                //  else if (id == R.id.nav_stats) {
                //     showOnly("stats");
                //     return true;
                // } else if (id == R.id.nav_settings) {
                //     showOnly("settings");
                //     return true;
                // }
                return false;
            });
        }
    }

    /**
     * 初始化测试数据
     */
    private void initTestData() {
        // 使用Handler确保在UI线程执行
        new Handler(Looper.getMainLooper()).post(() -> {
            // 初始化账户数据
            accountViewModel.getAllAccounts().observe(this, accounts -> {
                if (accounts.isEmpty()) {
                    // 创建预置账户数据
                    createDefaultAccounts();
                }
                // 只观察一次
                accountViewModel.getAllAccounts().removeObservers(this);
            });
            
            // 检查是否已有账单数据
            billViewModel.getAllBills().observe(this, bills -> {
                if (bills.isEmpty()) {
                    // 创建一些使用预置账户的测试账单数据
                    createDefaultBills();
                }
                // 只观察一次
                billViewModel.getAllBills().removeObservers(this);
            });
        });
    }

    /**
     * 创建预置账户数据
     */
    private void createDefaultAccounts() {
        // 正常账户
        Account cashAccount = new Account("现金", "现金", 500.0, "日常现金", "NORMAL");
        Account bankCard = new Account("工资卡", "银行卡", 15000.0, "工商银行储蓄卡", "NORMAL");
        Account wechatAccount = new Account("微信", "微信", 200.0, "微信零钱", "NORMAL");
        Account alipayAccount = new Account("支付宝", "支付宝", 300.0, "支付宝余额", "NORMAL");
        
        // 信贷账户
        Account creditCard = new Account("信用卡", "信用卡", 10000.0, "招商银行信用卡", "CREDIT");
        
        // 插入账户
        accountViewModel.insert(cashAccount);
        accountViewModel.insert(bankCard);
        accountViewModel.insert(wechatAccount);
        accountViewModel.insert(alipayAccount);
        accountViewModel.insert(creditCard);
    }

    private void createDefaultBills() {
        // 创建9月份每天至少一笔的账单数据
        Calendar calendar = Calendar.getInstance();
        
        // 定义9月份的天数
        int[] days = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
        
        // 定义各种消费类型和金额
        String[][] expenses = {
            {"早餐", "包子豆浆", "餐饮", "现金", "8.5"},
            {"早餐", "煎饼果子", "餐饮", "微信", "6.0"},
            {"早餐", "面包牛奶", "餐饮", "现金", "12.0"},
            {"早餐", "小笼包", "餐饮", "支付宝", "15.0"},
            {"早餐", "粥铺", "餐饮", "现金", "9.5"},
            {"打车", "滴滴出行", "出行", "微信", "18.0"},
            {"打车", "出租车", "出行", "现金", "25.0"},
            {"打车", "网约车", "出行", "支付宝", "22.0"},
            {"午餐", "快餐", "餐饮", "现金", "28.0"},
            {"午餐", "面食", "餐饮", "微信", "35.0"},
            {"午餐", "盖饭", "餐饮", "支付宝", "32.0"},
            {"午餐", "麻辣烫", "餐饮", "现金", "24.0"},
            {"午餐", "饺子", "餐饮", "微信", "38.0"},
            {"晚餐", "火锅", "餐饮", "信用卡", "128.0"},
            {"晚餐", "烧烤", "餐饮", "花呗", "89.0"},
            {"晚餐", "日料", "餐饮", "信用卡", "156.0"},
            {"晚餐", "川菜", "餐饮", "花呗", "95.0"},
            {"晚餐", "粤菜", "餐饮", "信用卡", "168.0"},
            {"咖啡", "星巴克", "餐饮", "信用卡", "28.0"},
            {"咖啡", "瑞幸", "餐饮", "花呗", "18.0"},
            {"咖啡", "库迪", "餐饮", "信用卡", "15.0"},
            {"购物", "超市", "购物", "现金", "88.0"},
            {"购物", "淘宝", "购物", "花呗", "199.0"},
            {"购物", "京东", "购物", "信用卡", "299.0"},
            {"购物", "拼多多", "购物", "花呗", "45.0"},
            {"购物", "便利店", "购物", "现金", "35.0"},
            {"娱乐", "电影", "娱乐", "微信", "45.0"},
            {"娱乐", "KTV", "娱乐", "信用卡", "168.0"},
            {"娱乐", "游戏", "娱乐", "花呗", "68.0"},
            {"娱乐", "网吧", "娱乐", "现金", "25.0"}
        };
        
        // 为每一天创建账单
        for (int i = 0; i < days.length; i++) {
            int day = days[i];
            String[] expense = expenses[i % expenses.length];
            
            // 随机时间
            int hour = 8 + (i % 12);
            int minute = (i * 7) % 60;
            
            calendar.set(2025, Calendar.SEPTEMBER, day, hour, minute);
            
            // 创建支出账单
            billViewModel.insert(new Bill(
                expense[0], 
                expense[1], 
                calendar.getTime(), 
                Double.parseDouble(expense[4]), 
                BillType.EXPENSE, 
                expense[2], 
                expense[3], 
                null
            ));
            
            // 某些天添加额外账单
            if (i % 3 == 0 && i > 0) {
                // 添加转账账单
                calendar.set(2025, Calendar.SEPTEMBER, day, 14, 30);
                billViewModel.insert(new Bill("转账", "微信充值", calendar.getTime(), 200.0, BillType.TRANSFER, "转账", "工资卡", "微信"));
            }
            
            if (i % 5 == 0 && i > 0) {
                // 添加支付宝充值
                calendar.set(2025, Calendar.SEPTEMBER, day, 16, 45);
                billViewModel.insert(new Bill("转账", "支付宝充值", calendar.getTime(), 150.0, BillType.TRANSFER, "转账", "工资卡", "支付宝"));
            }
        }
        
        // 特殊日期的重要账单
        // 1号：房租
        calendar.set(2025, Calendar.SEPTEMBER, 1, 11, 0);
        billViewModel.insert(new Bill("房租", "9月房租", calendar.getTime(), 2500.0, BillType.EXPENSE, "生活", "工资卡", null));
        
        // 15号：工资收入
        calendar.set(2025, Calendar.SEPTEMBER, 15, 10, 0);
        billViewModel.insert(new Bill("工资", "月薪", calendar.getTime(), 8500.0, BillType.INCOME, "收入", "工资卡", null));
        
        // 20号：大额购物
        calendar.set(2025, Calendar.SEPTEMBER, 20, 15, 0);
        billViewModel.insert(new Bill("购物", "电子产品", calendar.getTime(), 1299.0, BillType.EXPENSE, "购物", "信用卡", null));
        
        // 25号：信用卡还款
        calendar.set(2025, Calendar.SEPTEMBER, 25, 9, 0);
        billViewModel.insert(new Bill("还款", "信用卡还款", calendar.getTime(), 800.0, BillType.REPAYMENT, "还款", "工资卡", "信用卡"));
        
        // 28号：花呗还款
        calendar.set(2025, Calendar.SEPTEMBER, 28, 10, 15);
        billViewModel.insert(new Bill("还款", "花呗还款", calendar.getTime(), 300.0, BillType.REPAYMENT, "还款", "支付宝", "花呗"));
        
        // 30号：月底聚餐
        calendar.set(2025, Calendar.SEPTEMBER, 30, 19, 30);
        billViewModel.insert(new Bill("聚餐", "团建聚餐", calendar.getTime(), 188.0, BillType.EXPENSE, "餐饮", "信用卡", null));
    }

    private void showOnly(String tagToShow) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        for (Fragment f : fm.getFragments()) {
            if (f.getId() == R.id.contentContainer) {
                if (tagToShow.equals(f.getTag())) {
                    tx.show(f);
                } else {
                    tx.hide(f);
                }
            }
        }
        tx.commit();
    }
}