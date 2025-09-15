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
        // 尝试从Context获取FragmentActivity
        androidx.fragment.app.FragmentActivity activity = null;
        if (context instanceof androidx.fragment.app.FragmentActivity) {
            activity = (androidx.fragment.app.FragmentActivity) context;
        } else if (context instanceof android.content.ContextWrapper) {
            // 尝试从ContextWrapper中获取Activity
            android.content.Context baseContext = ((android.content.ContextWrapper) context).getBaseContext();
            if (baseContext instanceof androidx.fragment.app.FragmentActivity) {
                activity = (androidx.fragment.app.FragmentActivity) baseContext;
            }
        }
        
        if (activity != null) {
            this.accountViewModel = new ViewModelProvider(activity).get(AccountViewModel.class);
        } else {
            throw new IllegalArgumentException("Context must be a FragmentActivity or have a FragmentActivity as base context");
        }
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
            // 信贷账户的特殊处理
            if (account.isCreditAccount()) {
                return handleCreditAccountUpdate(account, amount, operation);
            }
            
            // 正常账户的处理逻辑
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
     * 处理信贷账户的余额更新
     * @param account 信贷账户
     * @param amount 金额
     * @param operation 操作类型（增加/减少）
     * @return 更新结果
     */
    private BalanceUpdateResult handleCreditAccountUpdate(Account account, double amount, BalanceOperation operation) {
        double currentUsedCredit = account.getUsedCredit();
        double currentAvailableCredit = account.getAvailableCredit();
        
        switch (operation) {
            case INCREASE:
                // 还款：减少使用额度
                if (!account.updateCreditUsage(amount, false)) {
                    return new BalanceUpdateResult(false, "信贷账户还款处理失败", null);
                }
                accountViewModel.update(account);
                
                Log.d(TAG, String.format("信贷账户 %s 还款成功：已用额度 %.2f -> %.2f (还款金额：%.2f)", 
                    account.getName(), currentUsedCredit, account.getUsedCredit(), amount));
                
                return new BalanceUpdateResult(true, "还款成功", account);
                
            case DECREASE:
                // 支出：增加使用额度
                if (account.isOverCreditLimit(amount)) {
                    return new BalanceUpdateResult(false, 
                        String.format("超出信用额度，可用额度：%.2f，需要：%.2f", 
                            currentAvailableCredit, amount), null);
                }
                
                if (!account.updateCreditUsage(amount, true)) {
                    return new BalanceUpdateResult(false, "信贷账户支出处理失败", null);
                }
                accountViewModel.update(account);
                
                Log.d(TAG, String.format("信贷账户 %s 支出成功：已用额度 %.2f -> %.2f (支出金额：%.2f)", 
                    account.getName(), currentUsedCredit, account.getUsedCredit(), amount));
                
                return new BalanceUpdateResult(true, "支出成功", account);
                
            default:
                return new BalanceUpdateResult(false, "未知的操作类型", null);
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
        
        // 验证信贷账户
        if (!creditorAccount.isCreditAccount()) {
            return new BalanceUpdateResult(false, "目标账户必须是信贷账户", null);
        }
        
        // 先减少支出账户余额
        BalanceUpdateResult decreaseResult = updateAccountBalance(debtorAccount, amount, BalanceOperation.DECREASE);
        if (!decreaseResult.isSuccess()) {
            return decreaseResult;
        }
        
        // 再增加信贷账户余额（还款会减少已使用的信用额度）
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
        
        if (account.isCreditAccount()) {
            // 信贷账户：检查可用信用额度
            return account.getAvailableCredit() >= amount;
        } else {
            // 正常账户：检查余额
            return account.getBalance() >= amount;
        }
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
        return String.format("%s：%s", account.getName(), account.getFormattedBalance());
    }
    
    /**
     * 处理删除账单的余额恢复
     * @param bill 要删除的账单
     * @return 更新结果
     */
    public BalanceUpdateResult handleDeleteBill(com.example.easyledger.database.Bill bill) {
        if (bill == null) {
            return new BalanceUpdateResult(false, "账单不能为空", null);
        }
        
        try {
            // 根据账单类型确定余额恢复逻辑
            switch (bill.getType()) {
                case EXPENSE:
                    // 删除支出账单：恢复账户余额（增加金额）
                    return handleExpenseBillDeletion(bill);
                case INCOME:
                    // 删除收入账单：减少账户余额
                    return handleIncomeBillDeletion(bill);
                case TRANSFER:
                    // 删除转账账单：恢复转出账户余额，减少转入账户余额
                    return handleTransferBillDeletion(bill);
                case REPAYMENT:
                    // 删除还款账单：恢复支出账户余额，减少信贷账户余额
                    return handleRepaymentBillDeletion(bill);
                default:
                    return new BalanceUpdateResult(false, "未知的账单类型", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "删除账单时更新余额失败", e);
            return new BalanceUpdateResult(false, "删除失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 处理删除支出账单
     */
    private BalanceUpdateResult handleExpenseBillDeletion(com.example.easyledger.database.Bill bill) {
        // 需要根据账户名称找到对应的账户对象
        // 这里简化处理，实际应用中需要从数据库获取账户对象
        return new BalanceUpdateResult(false, "需要实现根据账户名称获取账户对象的逻辑", null);
    }
    
    /**
     * 处理删除收入账单
     */
    private BalanceUpdateResult handleIncomeBillDeletion(com.example.easyledger.database.Bill bill) {
        // 需要根据账户名称找到对应的账户对象
        return new BalanceUpdateResult(false, "需要实现根据账户名称获取账户对象的逻辑", null);
    }
    
    /**
     * 处理删除转账账单
     */
    private BalanceUpdateResult handleTransferBillDeletion(com.example.easyledger.database.Bill bill) {
        // 转账账单的账户字段可能包含"转出账户 -> 转入账户"的格式
        return new BalanceUpdateResult(false, "需要实现转账账单删除的余额恢复逻辑", null);
    }
    
    /**
     * 处理删除还款账单
     */
    private BalanceUpdateResult handleRepaymentBillDeletion(com.example.easyledger.database.Bill bill) {
        // 还款账单的账户字段可能包含"支出账户 -> 信贷账户"的格式
        return new BalanceUpdateResult(false, "需要实现还款账单删除的余额恢复逻辑", null);
    }
    
    /**
     * 根据账户名称和金额恢复余额（用于删除支出账单）
     * @param accountName 账户名称
     * @param amount 金额
     * @return 更新结果
     */
    public BalanceUpdateResult restoreExpenseAccountBalance(String accountName, double amount) {
        if (accountName == null || accountName.isEmpty()) {
            return new BalanceUpdateResult(false, "账户名称不能为空", null);
        }
        
        if (amount <= 0) {
            return new BalanceUpdateResult(false, "金额必须大于0", null);
        }
        
        try {
            // 获取所有账户
            List<Account> allAccounts = accountViewModel.getAllAccounts().getValue();
            if (allAccounts == null) {
                return new BalanceUpdateResult(false, "无法获取账户列表", null);
            }
            
            // 查找对应的账户
            for (Account account : allAccounts) {
                if (account.getName().equals(accountName)) {
                    // 恢复余额（增加金额，因为删除了支出）
                    return updateAccountBalance(account, amount, BalanceOperation.INCREASE);
                }
            }
            
            return new BalanceUpdateResult(false, "未找到对应的账户：" + accountName, null);
            
        } catch (Exception e) {
            Log.e(TAG, "恢复支出账户余额失败", e);
            return new BalanceUpdateResult(false, "恢复失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 根据账户名称和金额减少余额（用于删除收入账单）
     * @param accountName 账户名称
     * @param amount 金额
     * @return 更新结果
     */
    public BalanceUpdateResult reduceIncomeAccountBalance(String accountName, double amount) {
        if (accountName == null || accountName.isEmpty()) {
            return new BalanceUpdateResult(false, "账户名称不能为空", null);
        }
        
        if (amount <= 0) {
            return new BalanceUpdateResult(false, "金额必须大于0", null);
        }
        
        try {
            // 获取所有账户
            List<Account> allAccounts = accountViewModel.getAllAccounts().getValue();
            if (allAccounts == null) {
                return new BalanceUpdateResult(false, "无法获取账户列表", null);
            }
            
            // 查找对应的账户
            for (Account account : allAccounts) {
                if (account.getName().equals(accountName)) {
                    // 减少余额（因为删除了收入）
                    return updateAccountBalance(account, amount, BalanceOperation.DECREASE);
                }
            }
            
            return new BalanceUpdateResult(false, "未找到对应的账户：" + accountName, null);
            
        } catch (Exception e) {
            Log.e(TAG, "减少收入账户余额失败", e);
            return new BalanceUpdateResult(false, "减少失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 根据账户名称和金额恢复余额（用于删除转账账单）
     * 转账账单删除时：转出账户恢复余额，转入账户减少余额
     * @param fromAccountName 转出账户名称
     * @param toAccountName 转入账户名称
     * @param amount 金额
     * @return 更新结果
     */
    public BalanceUpdateResult restoreTransferAccountBalance(String fromAccountName, String toAccountName, double amount) {
        if (fromAccountName == null || fromAccountName.isEmpty() || 
            toAccountName == null || toAccountName.isEmpty()) {
            return new BalanceUpdateResult(false, "转出账户和转入账户名称不能为空", null);
        }
        
        if (amount <= 0) {
            return new BalanceUpdateResult(false, "金额必须大于0", null);
        }
        
        try {
            // 获取所有账户
            List<Account> allAccounts = accountViewModel.getAllAccounts().getValue();
            if (allAccounts == null) {
                return new BalanceUpdateResult(false, "无法获取账户列表", null);
            }
            
            Account fromAccount = null;
            Account toAccount = null;
            
            // 查找对应的账户
            for (Account account : allAccounts) {
                if (account.getName().equals(fromAccountName)) {
                    fromAccount = account;
                } else if (account.getName().equals(toAccountName)) {
                    toAccount = account;
                }
            }
            
            if (fromAccount == null) {
                return new BalanceUpdateResult(false, "未找到转出账户：" + fromAccountName, null);
            }
            
            if (toAccount == null) {
                return new BalanceUpdateResult(false, "未找到转入账户：" + toAccountName, null);
            }
            
            // 先恢复转出账户余额（增加金额）
            BalanceUpdateResult fromResult = updateAccountBalance(fromAccount, amount, BalanceOperation.INCREASE);
            if (!fromResult.isSuccess()) {
                return fromResult;
            }
            
            // 再减少转入账户余额
            BalanceUpdateResult toResult = updateAccountBalance(toAccount, amount, BalanceOperation.DECREASE);
            if (!toResult.isSuccess()) {
                // 如果减少失败，回滚转出账户的余额
                updateAccountBalance(fromAccount, amount, BalanceOperation.DECREASE);
                return new BalanceUpdateResult(false, "转入账户余额更新失败，已回滚转出账户余额", null);
            }
            
            return new BalanceUpdateResult(true, "转账账单删除成功", null);
            
        } catch (Exception e) {
            Log.e(TAG, "恢复转账账户余额失败", e);
            return new BalanceUpdateResult(false, "恢复失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 根据账户名称和金额恢复余额（用于删除还款账单）
     * 还款账单删除时：支出账户恢复余额，信贷账户减少余额
     * @param debtorAccountName 支出账户名称
     * @param creditorAccountName 信贷账户名称
     * @param amount 金额
     * @return 更新结果
     */
    public BalanceUpdateResult restoreRepaymentAccountBalance(String debtorAccountName, String creditorAccountName, double amount) {
        if (debtorAccountName == null || debtorAccountName.isEmpty() || 
            creditorAccountName == null || creditorAccountName.isEmpty()) {
            return new BalanceUpdateResult(false, "支出账户和信贷账户名称不能为空", null);
        }
        
        if (amount <= 0) {
            return new BalanceUpdateResult(false, "金额必须大于0", null);
        }
        
        try {
            // 获取所有账户
            List<Account> allAccounts = accountViewModel.getAllAccounts().getValue();
            if (allAccounts == null) {
                return new BalanceUpdateResult(false, "无法获取账户列表", null);
            }
            
            Account debtorAccount = null;
            Account creditorAccount = null;
            
            // 查找对应的账户
            for (Account account : allAccounts) {
                if (account.getName().equals(debtorAccountName)) {
                    debtorAccount = account;
                } else if (account.getName().equals(creditorAccountName)) {
                    creditorAccount = account;
                }
            }
            
            if (debtorAccount == null) {
                return new BalanceUpdateResult(false, "未找到支出账户：" + debtorAccountName, null);
            }
            
            if (creditorAccount == null) {
                return new BalanceUpdateResult(false, "未找到信贷账户：" + creditorAccountName, null);
            }
            
            // 先恢复支出账户余额（增加金额）
            BalanceUpdateResult debtorResult = updateAccountBalance(debtorAccount, amount, BalanceOperation.INCREASE);
            if (!debtorResult.isSuccess()) {
                return debtorResult;
            }
            
            // 再减少信贷账户余额
            BalanceUpdateResult creditorResult = updateAccountBalance(creditorAccount, amount, BalanceOperation.DECREASE);
            if (!creditorResult.isSuccess()) {
                // 如果减少失败，回滚支出账户的余额
                updateAccountBalance(debtorAccount, amount, BalanceOperation.DECREASE);
                return new BalanceUpdateResult(false, "信贷账户余额更新失败，已回滚支出账户余额", null);
            }
            
            return new BalanceUpdateResult(true, "还款账单删除成功", null);
            
        } catch (Exception e) {
            Log.e(TAG, "恢复还款账户余额失败", e);
            return new BalanceUpdateResult(false, "恢复失败：" + e.getMessage(), null);
        }
    }
}
