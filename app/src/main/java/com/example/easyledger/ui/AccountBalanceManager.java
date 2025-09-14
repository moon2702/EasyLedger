package com.example.easyledger.ui;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import com.example.easyledger.database.Account;
import com.example.easyledger.database.AccountViewModel;
import java.util.List;

/**
 * 账户余额管理器
 * 统一处理所有账单类型的账户余额更新逻辑
 */
public class AccountBalanceManager {
    
    private static final String TAG = "AccountBalanceManager";
    private final AccountViewModel accountViewModel;
    private final Context context;
    
    public AccountBalanceManager(Context context) {
        this.context = context;
        this.accountViewModel = new ViewModelProvider((androidx.fragment.app.FragmentActivity) context)
                .get(AccountViewModel.class);
    }
    
    /**
     * 余额更新操作类型
     */
    public enum BalanceOperation {
        INCREASE,  // 增加余额
        DECREASE   // 减少余额
    }
    
    /**
     * 余额更新结果
     */
    public static class BalanceUpdateResult {
        private final boolean success;
        private final String message;
        private final Account updatedAccount;
        
        public BalanceUpdateResult(boolean success, String message, Account updatedAccount) {
            this.success = success;
            this.message = message;
            this.updatedAccount = updatedAccount;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Account getUpdatedAccount() { return updatedAccount; }
    }
    
    /**
     * 更新单个账户余额
     * @param account 要更新的账户
     * @param amount 金额
     * @param operation 操作类型（增加/减少）
     * @return 更新结果
     */
    public BalanceUpdateResult updateAccountBalance(Account account, double amount, BalanceOperation operation) {
        if (account == null) {
            return new BalanceUpdateResult(false, "账户不能为空", null);
        }
        
        if (amount <= 0) {
            return new BalanceUpdateResult(false, "金额必须大于0", null);
        }
        
        try {
            double currentBalance = account.getBalance();
            double newBalance;
            
            switch (operation) {
                case INCREASE:
                    newBalance = currentBalance + amount;
                    break;
                case DECREASE:
                    if (currentBalance < amount) {
                        return new BalanceUpdateResult(false, 
                            String.format("账户余额不足，当前余额：%.2f，需要：%.2f", currentBalance, amount), null);
                    }
                    newBalance = currentBalance - amount;
                    break;
                default:
                    return new BalanceUpdateResult(false, "未知的操作类型", null);
            }
            
            // 更新账户余额
            account.setBalance(newBalance);
            accountViewModel.update(account);
            
            Log.d(TAG, String.format("账户 %s 余额更新成功：%.2f -> %.2f (操作：%s, 金额：%.2f)", 
                account.getName(), currentBalance, newBalance, operation, amount));
            
            return new BalanceUpdateResult(true, "余额更新成功", account);
            
        } catch (Exception e) {
            Log.e(TAG, "更新账户余额失败", e);
            return new BalanceUpdateResult(false, "更新失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 处理支出账单的余额更新
     * @param account 支出账户
     * @param amount 支出金额
     * @return 更新结果
     */
    public BalanceUpdateResult handleExpenseBill(Account account, double amount) {
        return updateAccountBalance(account, amount, BalanceOperation.DECREASE);
    }
    
    /**
     * 处理收入账单的余额更新
     * @param account 收入账户
     * @param amount 收入金额
     * @return 更新结果
     */
    public BalanceUpdateResult handleIncomeBill(Account account, double amount) {
        return updateAccountBalance(account, amount, BalanceOperation.INCREASE);
    }
    
    /**
     * 处理转账账单的余额更新
     * @param fromAccount 转出账户
     * @param toAccount 转入账户
     * @param amount 转账金额
     * @return 更新结果
     */
    public BalanceUpdateResult handleTransferBill(Account fromAccount, Account toAccount, double amount) {
        if (fromAccount == null || toAccount == null) {
            return new BalanceUpdateResult(false, "转出账户和转入账户不能为空", null);
        }
        
        if (fromAccount.getName().equals(toAccount.getName())) {
            return new BalanceUpdateResult(false, "转出账户和转入账户不能相同", null);
        }
        
        // 先减少转出账户余额
        BalanceUpdateResult decreaseResult = updateAccountBalance(fromAccount, amount, BalanceOperation.DECREASE);
        if (!decreaseResult.isSuccess()) {
            return decreaseResult;
        }
        
        // 再增加转入账户余额
        BalanceUpdateResult increaseResult = updateAccountBalance(toAccount, amount, BalanceOperation.INCREASE);
        if (!increaseResult.isSuccess()) {
            // 如果增加失败，需要回滚转出账户的余额
            updateAccountBalance(fromAccount, amount, BalanceOperation.INCREASE);
            return new BalanceUpdateResult(false, "转入账户更新失败，已回滚转出账户余额", null);
        }
        
        return new BalanceUpdateResult(true, "转账成功", null);
    }
    
    /**
     * 处理还款账单的余额更新
     * @param debtorAccount 支出账户（还款方）
     * @param creditorAccount 信贷账户（收款方）
     * @param amount 还款金额
     * @return 更新结果
     */
    public BalanceUpdateResult handleRepaymentBill(Account debtorAccount, Account creditorAccount, double amount) {
        if (debtorAccount == null || creditorAccount == null) {
            return new BalanceUpdateResult(false, "支出账户和信贷账户不能为空", null);
        }
        
        if (debtorAccount.getName().equals(creditorAccount.getName())) {
            return new BalanceUpdateResult(false, "支出账户和信贷账户不能相同", null);
        }
        
        // 先减少支出账户余额
        BalanceUpdateResult decreaseResult = updateAccountBalance(debtorAccount, amount, BalanceOperation.DECREASE);
        if (!decreaseResult.isSuccess()) {
            return decreaseResult;
        }
        
        // 再增加信贷账户余额
        BalanceUpdateResult increaseResult = updateAccountBalance(creditorAccount, amount, BalanceOperation.INCREASE);
        if (!increaseResult.isSuccess()) {
            // 如果增加失败，需要回滚支出账户的余额
            updateAccountBalance(debtorAccount, amount, BalanceOperation.INCREASE);
            return new BalanceUpdateResult(false, "信贷账户更新失败，已回滚支出账户余额", null);
        }
        
        return new BalanceUpdateResult(true, "还款成功", null);
    }
    
    /**
     * 验证账户余额是否足够
     * @param account 账户
     * @param amount 需要的金额
     * @return 是否足够
     */
    public boolean validateAccountBalance(Account account, double amount) {
        if (account == null || amount <= 0) {
            return false;
        }
        return account.getBalance() >= amount;
    }
    
    /**
     * 获取账户余额信息
     * @param account 账户
     * @return 余额信息字符串
     */
    public String getAccountBalanceInfo(Account account) {
        if (account == null) {
            return "账户信息无效";
        }
        return String.format("%s：%.2f", account.getName(), account.getBalance());
    }
}
