# 信贷账户同步更新问题修复

## 问题描述
用户反映使用信贷账户添加支出账单后，信贷账户列表项中的余额没有同步更新。

## 问题排查和修复

### 1. 主要问题：AccountAdapter显示逻辑
**问题**：`AccountAdapter`中的`onBindViewHolder`方法使用`current.getBalance()`显示余额，这对于信贷账户来说是不正确的。

**修复**：将余额显示改为使用`current.getFormattedBalance()`方法，该方法会根据账户类型显示正确的格式：
- 正常账户：显示实际余额
- 信贷账户：显示可用额度或已用额度信息

```java
// 修复前
holder.balanceTextView.setText(String.valueOf(current.getBalance()));

// 修复后  
holder.balanceTextView.setText(current.getFormattedBalance());
```

### 2. 次要问题：AccountBalanceManager初始化
**问题**：在Fragment中初始化`AccountBalanceManager`时，使用`requireContext()`传递Context，但`AccountBalanceManager`期望的是`FragmentActivity`。

**修复**：将所有Fragment中的初始化改为使用`requireActivity()`：

```java
// 修复前
balanceManager = new AccountBalanceManager(requireContext());

// 修复后
balanceManager = new AccountBalanceManager(requireActivity());
```

同时增强了`AccountBalanceManager`的构造函数，使其能够更好地处理不同的Context类型。

### 3. 验证的组件
确认以下组件工作正常：
- ✅ `AccountDao`：正确实现了`getNormalAccounts()`和`getCreditAccounts()`查询
- ✅ `AccountRepository`：正确实现了账户分类查询方法
- ✅ `AccountViewModel`：正确暴露了分类查询的LiveData
- ✅ `AssetsFragment`：正确分离显示正常账户和信贷账户
- ✅ `AccountBalanceManager`：正确处理信贷账户的余额更新逻辑

## 修复的文件
1. `app/src/main/java/com/example/easyledger/ui/adapter/AccountAdapter.java`
2. `app/src/main/java/com/example/easyledger/ui/AccountBalanceManager.java`
3. `app/src/main/java/com/example/easyledger/ui/billadd/ExpenseBillFragment.java`
4. `app/src/main/java/com/example/easyledger/ui/billadd/RepaymentBillFragment.java`
5. `app/src/main/java/com/example/easyledger/ui/billadd/IncomeBillFragment.java`
6. `app/src/main/java/com/example/easyledger/ui/billadd/TransferBillFragment.java`

## 预期效果
修复后，当用户使用信贷账户添加支出账单时：
1. 信贷账户的已使用额度会正确更新
2. 账户列表会立即显示更新后的额度信息
3. 信贷账户显示格式：`已用: 3000.00 / 总额度: 10000.00`
4. 正常账户显示格式：`5000.00`

## 测试建议
1. 创建信贷账户并设置信用额度
2. 使用信贷账户添加支出账单
3. 检查账户列表中信贷账户的显示是否更新
4. 验证显示格式是否正确
5. 测试还款功能，确认额度恢复
