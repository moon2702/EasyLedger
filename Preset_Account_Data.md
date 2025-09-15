# MainActivity预置账户数据

## 概述
在MainActivity中添加了预置账户数据功能，应用首次启动时会自动创建一些示例账户。

## 预置账户列表

### 正常账户（NORMAL）

1. **现金账户**
   - 名称：现金
   - 类型：现金
   - 余额：500.00元
   - 描述：日常现金

2. **工资卡**
   - 名称：工资卡
   - 类型：银行卡
   - 余额：15,000.00元
   - 描述：工商银行储蓄卡

3. **微信账户**
   - 名称：微信
   - 类型：微信
   - 余额：200.00元
   - 描述：微信零钱

4. **支付宝账户**
   - 名称：支付宝
   - 类型：支付宝
   - 余额：300.00元
   - 描述：支付宝余额

### 信贷账户（CREDIT）

1. **信用卡**
   - 名称：信用卡
   - 类型：信用卡
   - 信用额度：10,000.00元
   - 描述：招商银行信用卡
   - 已用额度：0.00元（初始状态）

2. **花呗**
   - 名称：花呗
   - 类型：花呗
   - 信用额度：5,000.00元
   - 描述：蚂蚁花呗
   - 已用额度：0.00元（初始状态）

## 实现逻辑

### 初始化时机
- 应用首次启动时
- 数据库中没有任何账户数据时

### 初始化流程
1. 检查数据库中是否已有账户数据
2. 如果为空，则调用`createDefaultAccounts()`方法
3. 创建6个预置账户（4个正常账户 + 2个信贷账户）
4. 插入到数据库中

### 代码实现
```java
private void createDefaultAccounts() {
    // 正常账户
    Account cashAccount = new Account("现金", "现金", 500.0, "日常现金", "NORMAL");
    Account bankCard = new Account("工资卡", "银行卡", 15000.0, "工商银行储蓄卡", "NORMAL");
    Account wechatAccount = new Account("微信", "微信", 200.0, "微信零钱", "NORMAL");
    Account alipayAccount = new Account("支付宝", "支付宝", 300.0, "支付宝余额", "NORMAL");
    
    // 信贷账户
    Account creditCard = new Account("信用卡", "信用卡", 10000.0, "招商银行信用卡", "CREDIT");
    Account loanAccount = new Account("花呗", "花呗", 5000.0, "蚂蚁花呗", "CREDIT");
    
    // 插入账户
    accountViewModel.insert(cashAccount);
    accountViewModel.insert(bankCard);
    accountViewModel.insert(wechatAccount);
    accountViewModel.insert(alipayAccount);
    accountViewModel.insert(creditCard);
    accountViewModel.insert(loanAccount);
}
```

## 使用效果

### 资产页面显示
- **正常账户**：显示实际余额
- **信贷账户**：显示可用额度信息

### 账户选择
在添加账单时，用户可以直接选择这些预置账户，无需手动创建。

### 资产计算
- 总资产：16,000.00元（现金500 + 工资卡15000 + 微信200 + 支付宝300）
- 总负债：0.00元（信贷账户初始已用额度为0）
- 净资产：16,000.00元

## 注意事项

1. **一次性创建**：预置账户只在首次启动时创建，不会重复创建
2. **可编辑**：用户可以编辑这些预置账户的信息
3. **可删除**：用户可以删除不需要的预置账户
4. **可添加**：用户可以继续添加新的账户

## 扩展建议

如果需要添加更多预置账户，可以在`createDefaultAccounts()`方法中继续添加：

```java
// 投资账户示例
Account investmentAccount = new Account("理财账户", "理财", 50000.0, "余额宝", "NORMAL");

// 其他信贷账户示例
Account jiebeiAccount = new Account("借呗", "借呗", 8000.0, "蚂蚁借呗", "CREDIT");
```


