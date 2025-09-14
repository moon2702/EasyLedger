package com.example.easyledger;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.example.easyledger.ui.billadd.BillAddPagerAdapter;
import com.example.easyledger.ui.billadd.BillSaveable;
import com.example.easyledger.database.Bill;
import com.example.easyledger.database.BillViewModel;
import com.example.easyledger.database.BillType;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import com.example.easyledger.ui.billadd.ExpenseBillFragment;
import com.example.easyledger.ui.billadd.IncomeBillFragment;
import com.example.easyledger.ui.billadd.TransferBillFragment;
import com.example.easyledger.ui.billadd.RepaymentBillFragment;
import com.example.easyledger.ui.AccountBalanceManager;

import android.util.Log;
import java.util.List;

public class BillAddActivity extends AppCompatActivity {

    public static String TAG = "BillAddActivity_LOG";

    public static final String EXTRA_BILL_ID = "com.example.easyledger.EXTRA_BILL_ID";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BillAddPagerAdapter adapter;
    private BillViewModel billViewModel;
    private int billId = -1; // -1 表示新增模式
    private Bill currentBill;
    private AccountBalanceManager balanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_add);

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(this).get(BillViewModel.class);
        
        // 初始化余额管理器
        balanceManager = new AccountBalanceManager(this);

        // 检查是否有账单ID传入
        if (getIntent().hasExtra(EXTRA_BILL_ID)) {
            billId = getIntent().getIntExtra(EXTRA_BILL_ID, -1);
            if (billId != -1) {
                // 加载账单数据
                loadBillData(billId);
            }
        }

        // 设置工具栏
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // 根据模式设置标题
            if (billId != -1) {
                getSupportActionBar().setTitle("编辑账单");
            } else {
                getSupportActionBar().setTitle("添加账单");
            }
        }

        // 初始化标签栏和ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // 设置适配器
        adapter = new BillAddPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 监听页面变化，确保在Fragment创建后更新账单数据
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (billId != -1 && currentBill != null) {
                    updateCurrentFragmentBill();
                }
            }
        });

        // 如果是编辑模式且账单数据已加载，则设置对应的标签页
        if (billId != -1 && currentBill != null) {
            setCurrentTabByBillType(currentBill.getType());
        }

        // 关联TabLayout和ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            Log.d("111_LOG", "position = " + position);
            switch (position) {
                case 0:
                    Log.d("111_LOG", "切换支出界面");
                    tab.setText("支出");
                    break;
                case 1:
                    Log.d("111_LOG", "切换收入界面");
                    tab.setText("收入");
                    break;
                case 2:
                    Log.d("111_LOG", "切换转账界面");
                    tab.setText("转账");
                    break;
                case 3:
                    Log.d("111_LOG", "切换还款界面");
                    tab.setText("还款");
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bill_add_menu, menu);
        // 只有在编辑模式下才显示删除按钮
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (deleteItem != null) {
            deleteItem.setVisible(billId != -1);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_save) {
            saveCurrentBill();
            return true;
        } else if (id == R.id.action_delete) {
            deleteCurrentBill();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除当前账单
     */
    private void deleteCurrentBill() {
        if (currentBill != null) {
            new Thread(() -> {
                try {
                    // 先恢复账户余额
                    AccountBalanceManager.BalanceUpdateResult result = null;
                    
                    switch (currentBill.getType()) {
                        case EXPENSE:
                            // 删除支出账单：恢复账户余额（增加金额）
                            result = balanceManager.restoreExpenseAccountBalance(currentBill.getAccount(), currentBill.getAmount());
                            break;
                            
                        case INCOME:
                            // 删除收入账单：减少账户余额
                            result = balanceManager.reduceIncomeAccountBalance(currentBill.getAccount(), currentBill.getAmount());
                            break;
                            
                        case TRANSFER:
                            // 删除转账账单：恢复转出账户余额，减少转入账户余额
                            if (currentBill.getTargetAccount() != null) {
                                result = balanceManager.restoreTransferAccountBalance(
                                    currentBill.getAccount(), 
                                    currentBill.getTargetAccount(), 
                                    currentBill.getAmount()
                                );
                            } else {
                                result = new AccountBalanceManager.BalanceUpdateResult(false, "转账账单缺少目标账户信息", null);
                            }
                            break;
                            
                        case REPAYMENT:
                            // 删除还款账单：恢复支出账户余额，减少信贷账户余额
                            if (currentBill.getTargetAccount() != null) {
                                result = balanceManager.restoreRepaymentAccountBalance(
                                    currentBill.getAccount(), 
                                    currentBill.getTargetAccount(), 
                                    currentBill.getAmount()
                                );
                            } else {
                                result = new AccountBalanceManager.BalanceUpdateResult(false, "还款账单缺少目标账户信息", null);
                            }
                            break;
                            
                        default:
                            result = new AccountBalanceManager.BalanceUpdateResult(false, "未知的账单类型", null);
                            break;
                    }
                    
                    // 将result赋值给final变量，以便在lambda中使用
                    final AccountBalanceManager.BalanceUpdateResult finalResult = result;
                    final boolean isSuccess = finalResult != null && finalResult.isSuccess();
                    final String errorMessage = finalResult != null ? finalResult.getMessage() : "删除失败";
                    
                    if (isSuccess) {
                        // 余额恢复成功，删除账单
                        billViewModel.delete(currentBill);
                        
                        runOnUiThread(() -> {
                            Snackbar.make(findViewById(android.R.id.content), "账单已删除", Snackbar.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        // 余额恢复失败
                        runOnUiThread(() -> {
                            Snackbar.make(findViewById(android.R.id.content), "删除失败：" + errorMessage, Snackbar.LENGTH_SHORT).show();
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "删除账单时发生错误", e);
                    final String exceptionMessage = e.getMessage();
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(android.R.id.content), "删除失败：" + exceptionMessage, Snackbar.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    /**
     * 加载账单数据
     */
    private void loadBillData(int billId) {
        // 由于getBillById是同步方法，我们需要在后台线程执行
        new Thread(() -> {
            currentBill = billViewModel.getBillById(billId);
            // 在UI线程更新
            runOnUiThread(() -> {
                if (currentBill != null) {
                    // 更新标题
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("编辑账单");
                    }
                    // 设置对应的标签页
                    setCurrentTabByBillType(currentBill.getType());
                    
                    // 确保Fragment已创建并调用updateBill方法
                    updateCurrentFragmentBill();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "加载账单失败", Snackbar.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 更新当前Fragment的账单数据
     */
    private void updateCurrentFragmentBill() {
        if (currentBill == null) {
            Log.w(TAG, "currentBill is null, cannot update fragment");
            return;
        }
        
        try {
            int currentItem = viewPager.getCurrentItem();
            Log.d(TAG, "Updating fragment for current item: " + currentItem);
            
            // 使用安全的方法获取Fragment
            Fragment currentFragment = adapter.getFragmentSafely(currentItem);
            
            // 如果Fragment已创建且实现了BillSaveable接口
            if (currentFragment instanceof BillSaveable) {
                BillSaveable saveableFragment = (BillSaveable) currentFragment;
                boolean success = saveableFragment.updateBill(currentBill);
                if (success) {
                    Log.d(TAG, "Successfully updated fragment for position: " + currentItem);
                } else {
                    Log.w(TAG, "Failed to update fragment for position: " + currentItem);
                }
            } else {
                Log.w(TAG, "Fragment not ready or not BillSaveable, retrying...");
                // 如果Fragment尚未创建，尝试等待一段时间后再试
                new android.os.Handler().postDelayed(this::updateCurrentFragmentBill, 100);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating current fragment bill", e);
        }
    }

    /**
     * 根据账单类型设置当前标签页
     */
    private void setCurrentTabByBillType(BillType type) {
        int position = 0; // 默认是支出
        switch (type) {
            case INCOME:
                position = 1;
                break;
            case TRANSFER:
                position = 2;
                break;
            case REPAYMENT:
                position = 3;
                break;
        }
        viewPager.setCurrentItem(position, false);
    }

    public void saveCurrentBill() {
        try {
            int currentItem = viewPager.getCurrentItem();
            Log.d(TAG, "Saving current bill for item: " + currentItem);
            
            // 使用安全的方法获取Fragment
            Fragment currentFragment = adapter.getFragmentSafely(currentItem);

            if (currentFragment instanceof BillSaveable) {
                BillSaveable saveableFragment = (BillSaveable) currentFragment;
                boolean saved = false;
                
                try {
                    if (billId != -1 && currentBill != null) {
                        // 编辑模式下，先恢复旧账户的余额
                        String oldAccount = currentBill.getAccount();
                        double oldAmount = currentBill.getAmount();
                        
                        // 恢复旧账户余额
                        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
                        List<Account> allAccounts = accountViewModel.getAllAccounts().getValue();
                        if (allAccounts != null) {
                            for (Account account : allAccounts) {
                                if (account.getName().equals(oldAccount)) {
                                    // 恢复余额（增加回旧金额）
                                    double newBalance = account.getBalance() + oldAmount;
                                    account.setBalance(newBalance);
                                    accountViewModel.update(account);
                                    break;
                                }
                            }
                        }
                        
                        // 然后保存新账单
                        saved = saveableFragment.saveBill();
                        if (saved) {
                            // 保存成功后，删除原账单
                            billViewModel.delete(currentBill);
                        }
                    } else {
                        // 新增模式
                        saved = saveableFragment.saveBill();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error during save operation", e);
                    Snackbar.make(findViewById(android.R.id.content), "保存失败: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                
                if (saved) {
                    Log.d(TAG, "Bill saved successfully");
                    // 保存成功后关闭Activity
                    finish();
                } else {
                    Log.w(TAG, "Failed to save bill");
                    Snackbar.make(findViewById(android.R.id.content), "保存失败", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Current fragment is not BillSaveable or is null");
                Snackbar.make(findViewById(android.R.id.content), "当前页面不支持保存", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in saveCurrentBill", e);
            Snackbar.make(findViewById(android.R.id.content), "保存时发生错误", Snackbar.LENGTH_SHORT).show();
        }
    }
}