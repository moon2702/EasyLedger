# 信贷账户功能使用示例

## 概述

信贷账户功能已经成功集成到EasyLedger应用中，支持信用额度管理和自动余额计算。

## 主要功能

### 1. 信贷账户创建
```java
// 创建信贷账户（信用额度10000元）
Account creditCard = new Account("信用卡", "信用卡", 10000.0, "招商银行信用卡", "CREDIT");
```

### 2. 信贷账户状态检查
```java
// 检查是否为信贷账户
if (account.isCreditAccount()) {
    // 获取可用信用额度
    double availableCredit = account.getAvailableCredit();
    
    // 检查是否超出信用额度
    boolean isOverLimit = account.isOverCreditLimit(5000.0);
    
    // 获取格式化的余额显示
    String displayInfo = account.getFormattedBalance();
    // 输出：已用: 3000.00 / 总额度: 10000.00
}
```

### 3. 信贷账户余额更新
```java
// 使用AccountBalanceManager处理信贷账户
AccountBalanceManager balanceManager = new AccountBalanceManager(context);

// 支出操作（增加已使用额度）
BalanceUpdateResult result = balanceManager.updateAccountBalance(creditAccount, 1000.0, BalanceOperation.DECREASE);

// 还款操作（减少已使用额度）
BalanceUpdateResult result = balanceManager.updateAccountBalance(creditAccount, 500.0, BalanceOperation.INCREASE);
```

### 4. 还款账单处理
```java
// 从储蓄账户向信贷账户还款
Account savingsAccount = new Account("储蓄卡", "银行卡", 5000.0, "工商银行储蓄卡", "NORMAL");
Account creditAccount = new Account("信用卡", "信用卡", 10000.0, "招商银行信用卡", "CREDIT");

// 处理还款
BalanceUpdateResult result = balanceManager.handleRepaymentBill(savingsAccount, creditAccount, 2000.0);
```

## 字段说明

### Account类新增字段
- `creditLimit`: 信贷账户的信用额度
- `usedCredit`: 已使用的信用额度
- `balance`: 对于信贷账户，此字段设为0；对于正常账户，此字段为实际余额

### 新增方法
- `isCreditAccount()`: 判断是否为信贷账户
- `getAvailableCredit()`: 获取可用信用额度
- `getCreditDisplayBalance()`: 获取信贷账户显示余额（负数表示欠款）
- `isOverCreditLimit(double amount)`: 检查是否超出信用额度
- `updateCreditUsage(double amount, boolean isExpense)`: 更新信贷使用额度
- `getFormattedBalance()`: 获取格式化的余额显示

## 使用场景

### 1. 创建信贷账户
在AccountAddActivity中，用户可以选择"信贷账户"类型，系统会自动设置信用额度。

### 2. 支出操作
当用户使用信贷账户进行支出时：
- 系统检查可用信用额度
- 如果额度足够，增加已使用额度
- 如果超出额度，显示错误信息

### 3. 还款操作
当用户进行还款时：
- 从正常账户扣除相应金额
- 减少信贷账户的已使用额度
- 更新可用信用额度

### 4. 余额显示
在账户列表中，信贷账户会显示：
- 未使用时：`可用额度: 10000.00`
- 有欠款时：`已用: 3000.00 / 总额度: 10000.00`

## 注意事项

1. **数据库迁移**: 由于添加了新字段，需要更新数据库版本并处理现有数据
2. **向后兼容**: 现有的正常账户不受影响
3. **验证逻辑**: 所有涉及信贷账户的操作都会自动验证信用额度
4. **错误处理**: 超出信用额度时会显示详细的错误信息

## 测试建议

1. 创建信贷账户并验证初始状态
2. 进行支出操作，测试信用额度检查
3. 进行还款操作，验证额度恢复
4. 测试超出信用额度的情况
5. 验证余额显示格式
