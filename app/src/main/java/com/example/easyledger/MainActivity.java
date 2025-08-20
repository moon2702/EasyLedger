package com.example.easyledger;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.easyledger.ui.HomeFragment;
import com.example.easyledger.ui.CalendarFragment;
import com.example.easyledger.ui.AssetsFragment;
import com.example.easyledger.ui.StatsFragment;
import com.example.easyledger.ui.SettingsFragment;

public class MainActivity extends AppCompatActivity {

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

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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