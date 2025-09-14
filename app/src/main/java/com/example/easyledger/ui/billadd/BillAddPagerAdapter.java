package com.example.easyledger.ui.billadd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.HashMap;
import android.util.Log;
import com.example.easyledger.ui.billadd.ExpenseBillFragment;
import com.example.easyledger.ui.billadd.IncomeBillFragment;
import com.example.easyledger.ui.billadd.TransferBillFragment;
import com.example.easyledger.ui.billadd.RepaymentBillFragment;

public class BillAddPagerAdapter extends FragmentStateAdapter {
    private static final String TAG = "BillAddPagerAdapter";
    private final HashMap<Integer, Fragment> fragmentMap = new HashMap<>();

    public BillAddPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        try {
            Log.d(TAG, "Creating fragment for position: " + position);
            
            switch (position) {
                case 0:
                    fragment = new ExpenseBillFragment();
                    Log.d(TAG, "Created ExpenseBillFragment");
                    break;
                case 1:
                    fragment = new IncomeBillFragment();
                    Log.d(TAG, "Created IncomeBillFragment");
                    break;
                case 2:
                    fragment = new TransferBillFragment();
                    Log.d(TAG, "Created TransferBillFragment");
                    break;
                case 3:
                    fragment = new RepaymentBillFragment();
                    Log.d(TAG, "Created RepaymentBillFragment");
                    break;
                default:
                    Log.w(TAG, "Unknown position: " + position + ", defaulting to ExpenseBillFragment");
                    fragment = new ExpenseBillFragment();
                    break;
            }
            
            // 确保Fragment不为null再添加到Map中
            if (fragment != null) {
                fragmentMap.put(position, fragment);
                Log.d(TAG, "Fragment added to map for position: " + position);
            } else {
                Log.e(TAG, "Failed to create fragment for position: " + position);
                // 如果创建失败，返回默认的ExpenseBillFragment
                fragment = new ExpenseBillFragment();
                fragmentMap.put(position, fragment);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Exception creating fragment for position " + position, e);
            // 异常情况下返回默认Fragment
            try {
                fragment = new ExpenseBillFragment();
                fragmentMap.put(position, fragment);
            } catch (Exception fallbackException) {
                Log.e(TAG, "Failed to create fallback fragment", fallbackException);
                // 如果连默认Fragment都创建失败，返回null
                return null;
            }
        }
        
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
        if (position < 0 || position >= getItemCount()) {
            Log.w(TAG, "Invalid position: " + position);
            return null;
        }
        return fragmentMap.get(position);
    }
    
    /**
     * 安全地获取Fragment，如果不存在则尝试创建
     * @param position 位置索引
     * @return Fragment实例，如果创建失败则返回null
     */
    @Nullable
    public Fragment getFragmentSafely(int position) {
        Fragment fragment = getFragment(position);
        if (fragment == null) {
            Log.d(TAG, "Fragment not found for position " + position + ", attempting to create");
            try {
                fragment = createFragment(position);
            } catch (Exception e) {
                Log.e(TAG, "Failed to create fragment for position " + position, e);
            }
        }
        return fragment;
    }
}