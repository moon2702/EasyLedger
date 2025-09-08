package com.example.easyledger.ui.billadd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.HashMap;import com.example.easyledger.ui.billadd.ExpenseBillFragment;
import com.example.easyledger.ui.billadd.IncomeBillFragment;
import com.example.easyledger.ui.billadd.TransferBillFragment;
import com.example.easyledger.ui.billadd.RepaymentBillFragment;

public class BillAddPagerAdapter extends FragmentStateAdapter {
    private final HashMap<Integer, Fragment> fragmentMap = new HashMap<>();

    public BillAddPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ExpenseBillFragment();
                break;
            case 1:
                fragment = new IncomeBillFragment();
                break;
            case 2:
                fragment = new TransferBillFragment();
                break;
            case 3:
                fragment = new RepaymentBillFragment();
                break;
            default:
                fragment = new ExpenseBillFragment();
                break;
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    /**
     * 获取指定位置的Fragment实例
     * @param position 位置索引
     * @return Fragment实例，如果不存在则返回null
     */
    @Nullable
    public Fragment getFragment(int position) {
        return fragmentMap.get(position);
    }
}