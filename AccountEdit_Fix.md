# 账户编辑界面修复

## 问题描述
在AccountAddActivity的编辑界面中，信贷账户和正常账户没有正确区分：
1. 信贷账户编辑时显示的是余额（balance），而不是信用额度（creditLimit）
2. 保存时没有正确更新对应的字段

## 修复内容

### 1. populateFields方法修复
**修复前**：
```java
balanceEditText.setText(String.valueOf(account.getBalance()));
```

**修复后**：
```java
// 根据账户类型显示不同的值
if (account.isCreditAccount()) {
    balanceEditText.setText(String.valueOf(account.getCreditLimit()));
} else {
    balanceEditText.setText(String.valueOf(account.getBalance()));
}
```

### 2. 保存逻辑修复
**修复前**：
```java
currentAccount.setBalance(balance); // 所有账户都更新balance字段
```

**修复后**：
```java
// 根据账户类型更新不同字段
if ("CREDIT".equals(category)) {
    // 信贷账户：更新信用额度
    currentAccount.setCreditLimit(balance);
    currentAccount.setBalance(0); // 信贷账户余额保持为0
} else {
    // 正常账户：更新余额
    currentAccount.setBalance(balance);
    currentAccount.setCreditLimit(0); // 正常账户信用额度保持为0
    currentAccount.setUsedCredit(0); // 正常账户已用额度保持为0
}
```

## 修复效果

### 信贷账户编辑
- **显示**：编辑界面显示信用额度（如：10000.00）
- **标签**：显示"最大额度"
- **提示**：显示"请输入最大额度"
- **保存**：更新creditLimit字段，balance保持为0

### 正常账户编辑
- **显示**：编辑界面显示余额（如：5000.00）
- **标签**：显示"初始余额"
- **提示**：显示"请输入初始余额"
- **保存**：更新balance字段，creditLimit和usedCredit保持为0

## 使用场景

### 场景1：编辑信贷账户
1. 用户点击信贷账户进入编辑界面
2. 界面显示"最大额度：10000.00"
3. 用户修改为"15000.00"
4. 保存后，信贷账户的creditLimit更新为15000.00
5. 余额（balance）保持为0，已用额度（usedCredit）不变

### 场景2：编辑正常账户
1. 用户点击正常账户进入编辑界面
2. 界面显示"初始余额：5000.00"
3. 用户修改为"6000.00"
4. 保存后，正常账户的balance更新为6000.00
5. 信用额度相关字段保持为0

## 技术细节

### 字段区分
- **信贷账户**：
  - `balance`：始终为0
  - `creditLimit`：信用额度（可编辑）
  - `usedCredit`：已使用额度（系统自动维护）

- **正常账户**：
  - `balance`：账户余额（可编辑）
  - `creditLimit`：始终为0
  - `usedCredit`：始终为0

### 数据一致性
- 编辑时确保字段对应关系正确
- 保存时清理无关字段，避免数据混乱
- UI标签和提示文本与字段含义一致

## 测试建议

1. **创建信贷账户**：
   - 设置信用额度为10000元
   - 验证编辑界面显示"最大额度：10000.00"

2. **编辑信贷账户**：
   - 修改信用额度为15000元
   - 验证保存后creditLimit字段更新为15000

3. **创建正常账户**：
   - 设置初始余额为5000元
   - 验证编辑界面显示"初始余额：5000.00"

4. **编辑正常账户**：
   - 修改余额为6000元
   - 验证保存后balance字段更新为6000

5. **账户类型切换**：
   - 从正常账户切换到信贷账户
   - 验证UI标签和字段显示正确更新
