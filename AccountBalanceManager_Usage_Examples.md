# AccountBalanceManager 使用示例

## 概述
AccountBalanceManager 是一个统一的账户余额管理器，封装了所有账单类型的余额更新逻辑，确保数据一致性和错误处理。

## 基本使用

### 1. 在Fragment中初始化
```java
public class YourFragment extends Fragment {
    private AccountBalanceManager balanceManager;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.your_layout, container, false);
        
        // 初始化余额管理器
        balanceManager = new AccountBalanceManager(requireContext());
        
        return root;
    }
}
```

## 四种账单类型的使用示例

### 1. 支出账单 (ExpenseBillFragment)
```java
@Override
public boolean saveBill() {
    try {
        // 获取输入数据
        String title = editTextTitle.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        Account selectedAccount = accountSelector.getSelectedAccount();
        
        // 验证输入
        if (title.isEmpty() || amountStr.isEmpty() || selectedAccount == null) {
            Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        double amount = Double.parseDouble(amountStr);
        
        // 先更新账户余额
        AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleExpenseBill(selectedAccount, amount);
        if (!result.isSuccess()) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 再保存账单
        Bill bill = new Bill(title, "", new Date(), amount, BillType.EXPENSE, category, selectedAccount.getName());
        billViewModel.insert(bill);
        
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        return true;
        
    } catch (Exception e) {
        Toast.makeText(getContext(), "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
```

### 2. 收入账单 (IncomeBillFragment)
```java
@Override
public boolean saveBill() {
    try {
        // 获取输入数据
        String title = editTextTitle.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        Account selectedAccount = accountSelector.getSelectedAccount();
        
        // 验证输入
        if (title.isEmpty() || amountStr.isEmpty() || selectedAccount == null) {
            Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        double amount = Double.parseDouble(amountStr);
        
        // 先更新账户余额
        AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleIncomeBill(selectedAccount, amount);
        if (!result.isSuccess()) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 再保存账单
        Bill bill = new Bill(title, "", new Date(), amount, BillType.INCOME, category, selectedAccount.getName());
        billViewModel.insert(bill);
        
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        return true;
        
    } catch (Exception e) {
        Toast.makeText(getContext(), "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
```

### 3. 转账账单 (TransferBillFragment)
```java
@Override
public boolean saveBill() {
    try {
        // 获取输入数据
        String amountStr = editTextAmount.getText().toString().trim();
        Account fromAccount = editTextFromAccount.getSelectedAccount();
        Account toAccount = editTextToAccount.getSelectedAccount();
        
        // 验证输入
        if (amountStr.isEmpty() || fromAccount == null || toAccount == null) {
            Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        double amount = Double.parseDouble(amountStr);
        
        // 先更新账户余额
        AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleTransferBill(fromAccount, toAccount, amount);
        if (!result.isSuccess()) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 再保存账单
        Bill bill = new Bill("转账", "", new Date(), amount, BillType.TRANSFER, "转账", fromAccount.getName() + " -> " + toAccount.getName());
        billViewModel.insert(bill);
        
        Toast.makeText(getContext(), "转账成功", Toast.LENGTH_SHORT).show();
        return true;
        
    } catch (Exception e) {
        Toast.makeText(getContext(), "转账失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
```

### 4. 还款账单 (RepaymentBillFragment)
```java
@Override
public boolean saveBill() {
    try {
        // 获取输入数据
        String amountStr = editTextAmount.getText().toString().trim();
        Account debtorAccount = editTextDebtorAccount.getSelectedAccount();
        Account creditorAccount = editTextCreditorAccount.getSelectedAccount();
        
        // 验证输入
        if (amountStr.isEmpty() || debtorAccount == null || creditorAccount == null) {
            Toast.makeText(getContext(), "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        double amount = Double.parseDouble(amountStr);
        
        // 先更新账户余额
        AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleRepaymentBill(debtorAccount, creditorAccount, amount);
        if (!result.isSuccess()) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 再保存账单
        Bill bill = new Bill("还款", "", new Date(), amount, BillType.REPAYMENT, "还款", debtorAccount.getName() + " -> " + creditorAccount.getName());
        billViewModel.insert(bill);
        
        Toast.makeText(getContext(), "还款成功", Toast.LENGTH_SHORT).show();
        return true;
        
    } catch (Exception e) {
        Toast.makeText(getContext(), "还款失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
```

## 高级用法

### 1. 余额验证
```java
// 在保存前验证余额是否足够
if (!balanceManager.validateAccountBalance(selectedAccount, amount)) {
    Toast.makeText(getContext(), "账户余额不足", Toast.LENGTH_SHORT).show();
    return false;
}
```

### 2. 获取账户余额信息
```java
// 显示账户余额信息
String balanceInfo = balanceManager.getAccountBalanceInfo(selectedAccount);
Toast.makeText(getContext(), balanceInfo, Toast.LENGTH_SHORT).show();
```

### 3. 自定义余额更新
```java
// 使用通用的余额更新方法
AccountBalanceManager.BalanceUpdateResult result = balanceManager.updateAccountBalance(
    account, 
    amount, 
    AccountBalanceManager.BalanceOperation.INCREASE  // 或 DECREASE
);

if (result.isSuccess()) {
    // 更新成功
    Account updatedAccount = result.getUpdatedAccount();
} else {
    // 更新失败
    String errorMessage = result.getMessage();
}
```

## 错误处理

### 1. 余额不足
```java
AccountBalanceManager.BalanceUpdateResult result = balanceManager.handleExpenseBill(account, amount);
if (!result.isSuccess()) {
    if (result.getMessage().contains("余额不足")) {
        // 处理余额不足的情况
        showInsufficientBalanceDialog();
    } else {
        // 处理其他错误
        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
```

### 2. 账户验证
```java
// 检查账户是否有效
if (account == null) {
    Toast.makeText(getContext(), "请选择账户", Toast.LENGTH_SHORT).show();
    return false;
}
```

## 优势

1. **统一管理**：所有余额更新逻辑集中在一个类中
2. **数据一致性**：确保余额更新和账单保存的原子性
3. **错误处理**：完善的错误处理和回滚机制
4. **类型安全**：使用枚举和结果对象，避免魔法数字和字符串
5. **易于测试**：逻辑分离，便于单元测试
6. **易于维护**：修改余额逻辑只需要修改一个文件

## 注意事项

1. **初始化顺序**：确保在Fragment的onCreateView中初始化balanceManager
2. **错误处理**：始终检查BalanceUpdateResult的success状态
3. **数据一致性**：先更新余额，再保存账单，确保数据一致性
4. **内存管理**：balanceManager会在Fragment销毁时自动释放
