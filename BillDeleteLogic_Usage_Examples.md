# 账单删除逻辑使用示例

## 概述
修改后的BillAddActivity现在使用AccountBalanceManager来统一处理所有四种账单类型的删除逻辑，确保余额恢复的正确性和数据一致性。

## 四种账单类型的删除逻辑

### 1. 支出账单 (EXPENSE)
```java
case EXPENSE:
    // 删除支出账单：恢复账户余额（增加金额）
    result = balanceManager.restoreExpenseAccountBalance(currentBill.getAccount(), currentBill.getAmount());
    break;
```

**逻辑说明**：
- 删除支出账单时，需要将之前减少的金额加回到账户中
- 例如：删除100元支出账单，账户余额增加100元

### 2. 收入账单 (INCOME)
```java
case INCOME:
    // 删除收入账单：减少账户余额
    result = balanceManager.reduceIncomeAccountBalance(currentBill.getAccount(), currentBill.getAmount());
    break;
```

**逻辑说明**：
- 删除收入账单时，需要将之前增加的金额从账户中减去
- 例如：删除100元收入账单，账户余额减少100元

### 3. 转账账单 (TRANSFER)
```java
case TRANSFER:
    // 删除转账账单：恢复转出账户余额，减少转入账户余额
    if (currentBill.getTargetAccount() != null) {
        result = balanceManager.restoreTransferAccountBalance(
            currentBill.getAccount(),        // 转出账户
            currentBill.getTargetAccount(),  // 转入账户
            currentBill.getAmount()
        );
    } else {
        result = new AccountBalanceManager.BalanceUpdateResult(false, "转账账单缺少目标账户信息", null);
    }
    break;
```

**逻辑说明**：
- 删除转账账单时，需要撤销转账操作
- 转出账户：恢复余额（增加金额）
- 转入账户：减少余额
- 例如：删除100元转账账单，转出账户增加100元，转入账户减少100元

### 4. 还款账单 (REPAYMENT)
```java
case REPAYMENT:
    // 删除还款账单：恢复支出账户余额，减少信贷账户余额
    if (currentBill.getTargetAccount() != null) {
        result = balanceManager.restoreRepaymentAccountBalance(
            currentBill.getAccount(),        // 支出账户
            currentBill.getTargetAccount(),  // 信贷账户
            currentBill.getAmount()
        );
    } else {
        result = new AccountBalanceManager.BalanceUpdateResult(false, "还款账单缺少目标账户信息", null);
    }
    break;
```

**逻辑说明**：
- 删除还款账单时，需要撤销还款操作
- 支出账户：恢复余额（增加金额）
- 信贷账户：减少余额
- 例如：删除100元还款账单，支出账户增加100元，信贷账户减少100元

## 数据一致性保障

### 1. 先恢复余额，再删除账单
```java
if (result != null && result.isSuccess()) {
    // 余额恢复成功，删除账单
    billViewModel.delete(currentBill);
    
    runOnUiThread(() -> {
        Snackbar.make(findViewById(android.R.id.content), "账单已删除", Snackbar.LENGTH_SHORT).show();
        finish();
    });
} else {
    // 余额恢复失败，不删除账单
    runOnUiThread(() -> {
        String errorMsg = result != null ? result.getMessage() : "删除失败";
        Snackbar.make(findViewById(android.R.id.content), "删除失败：" + errorMsg, Snackbar.LENGTH_SHORT).show();
    });
}
```

### 2. 回滚机制
在转账和还款账单的删除中，如果第二个账户的余额更新失败，会自动回滚第一个账户的余额：

```java
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
```

## 错误处理

### 1. 账户信息验证
```java
if (currentBill.getTargetAccount() != null) {
    // 处理转账/还款账单
} else {
    result = new AccountBalanceManager.BalanceUpdateResult(false, "账单缺少目标账户信息", null);
}
```

### 2. 异常处理
```java
try {
    // 删除逻辑
} catch (Exception e) {
    Log.e(TAG, "删除账单时发生错误", e);
    runOnUiThread(() -> {
        Snackbar.make(findViewById(android.R.id.content), "删除失败：" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
    });
}
```

## 与Fragment的适配

### 1. 统一的删除入口
所有四个Fragment都通过BillAddActivity的删除按钮来删除账单，不需要在Fragment中实现删除逻辑。

### 2. 自动识别账单类型
BillAddActivity会根据`currentBill.getType()`自动识别账单类型，并调用相应的删除逻辑。

### 3. 数据格式兼容
- **支出/收入账单**：使用`currentBill.getAccount()`获取账户名称
- **转账/还款账单**：使用`currentBill.getAccount()`和`currentBill.getTargetAccount()`获取两个账户名称

## 优势

1. **逻辑统一**：所有删除逻辑集中在AccountBalanceManager中
2. **类型安全**：根据账单类型自动选择正确的删除逻辑
3. **数据一致性**：先恢复余额，再删除账单，确保数据一致性
4. **错误处理**：完善的错误处理和回滚机制
5. **易于维护**：修改删除逻辑只需要修改AccountBalanceManager
6. **易于测试**：逻辑分离，便于单元测试

## 注意事项

1. **账户信息完整性**：确保转账和还款账单的targetAccount字段不为空
2. **余额验证**：删除收入账单时，需要确保账户余额足够
3. **并发安全**：删除操作在后台线程执行，避免阻塞UI
4. **用户反馈**：删除成功或失败都会给用户相应的提示

## 测试建议

1. **测试各种账单类型的删除**
2. **测试余额不足的情况**
3. **测试账户不存在的情况**
4. **测试网络异常的情况**
5. **测试并发删除的情况**
