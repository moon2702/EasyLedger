package com.example.easyledger.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.easyledger.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

/**
 * 统计页面Fragment，显示各类财务统计信息
 */
public class StatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // 标签栏切换事件
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String[] tabTitles = {"日常", "月统计", "年统计", "自定义"};
                Snackbar.make(view, "切换到" + tabTitles[tab.getPosition()] + "视图", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 不需要处理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 不需要处理
            }
        });

        // 为统计卡片添加点击事件
        setupCardClickListeners(view);

        return view;
    }

    /**
     * 为统计卡片设置点击事件监听器
     * @param view 根视图
     */
    private void setupCardClickListeners(View view) {
        // 卡片ID数组
        int[] cardIds = {
                R.id.bar_chart_card,  // 近7日统计卡片
                R.id.pie_chart_card,  // 资产汇总卡片
                R.id.donut_chart_card // 预算占比卡片
        };

        // 卡片名称数组
        String[] cardNames = {
                "近7日统计",
                "资产汇总",
                "预算占比"
        };

        // 为每个卡片添加点击事件
        for (int i = 0; i < cardIds.length; i++) {
            MaterialCardView card = view.findViewById(cardIds[i]);
            if (card != null) {
                final int index = i;
                card.setOnClickListener(v -> {
                    Snackbar.make(v, "查看" + cardNames[index] + "详情", Snackbar.LENGTH_SHORT).show();
                });
            } else {
                // 如果卡片未找到，输出日志信息
                if (getContext() != null) {
                    Snackbar.make(view, "未找到卡片: " + cardNames[i], Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }
}


