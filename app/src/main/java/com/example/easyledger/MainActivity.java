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
import com.example.easyledger.ui.HomeFragment;
import com.example.easyledger.ui.CalendarFragment;
import com.example.easyledger.ui.AssetsFragment;
import com.example.easyledger.ui.StatsFragment;
import com.example.easyledger.ui.SettingsFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private BillViewModel billViewModel;

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

        // 初始化测试数据
        initTestData();

        // FAB 迁移到 HomeFragment 中

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            // 初始化并缓存 Fragments
            HomeFragment homeFragment = new HomeFragment();
            CalendarFragment calendarFragment = new CalendarFragment();
            AssetsFragment assetsFragment = new AssetsFragment();
            StatsFragment statsFragment = new StatsFragment();
            SettingsFragment settingsFragment = new SettingsFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentContainer, settingsFragment, "settings").hide(settingsFragment)
                    .add(R.id.contentContainer, statsFragment, "stats").hide(statsFragment)
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
                } else if (id == R.id.nav_stats) {
                    showOnly("stats");
                    return true;
                } else if (id == R.id.nav_settings) {
                    showOnly("settings");
                    return true;
                }
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
            // 检查是否已有数据
            billViewModel.getAllBills().observe(this, bills -> {
                if (bills.isEmpty()) {
                    // 创建一些测试数据
                    Calendar calendar = Calendar.getInstance();

                    // 今天的支出
                    calendar.set(2025, Calendar.AUGUST, 20, 8, 12);
                    billViewModel.insert(new Bill("早餐", "肯德基", calendar.getTime(), 12.5, BillType.EXPENSE, "餐饮", "现金", null));

                    calendar.set(2025, Calendar.AUGUST, 20, 9, 5);
                    billViewModel.insert(new Bill("打车", "滴滴出行", calendar.getTime(), 23.0, BillType.EXPENSE, "出行", "微信", null));

                    // 工资收入
                    calendar.set(2025, Calendar.AUGUST, 15, 10, 0);
                    billViewModel.insert(new Bill("工资", "月薪", calendar.getTime(), 8500.0, BillType.INCOME, "收入", null, "工资卡"));

                    // 其他测试数据
                    calendar.set(2025, Calendar.AUGUST, 19, 14, 20);
                    billViewModel.insert(new Bill("咖啡", "星巴克", calendar.getTime(), 18.0, BillType.EXPENSE, "餐饮", "信用卡", null));

                    calendar.set(2025, Calendar.AUGUST, 18, 12, 30);
                    billViewModel.insert(new Bill("退款", "淘宝购物", calendar.getTime(), 35.0, BillType.EXPENSE, "其他", "支付宝", null));

                    calendar.set(2025, Calendar.AUGUST, 20, 19, 30);
                    billViewModel.insert(new Bill("晚餐", "湘菜馆", calendar.getTime(), 89.0, BillType.EXPENSE, "餐饮", "信用卡", null));

                    calendar.set(2025, Calendar.AUGUST, 19, 21, 15);
                    billViewModel.insert(new Bill("电影票", "复仇者联盟", calendar.getTime(), 55.0, BillType.EXPENSE, "娱乐", "支付宝", null));

                    calendar.set(2025, Calendar.AUGUST, 1, 11, 0);
                    billViewModel.insert(new Bill("房租", "8月房租", calendar.getTime(), 2500.0, BillType.EXPENSE, "生活", "银行记账", null));

                }
                // 只观察一次
                billViewModel.getAllBills().removeObservers(this);
            });
        });
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