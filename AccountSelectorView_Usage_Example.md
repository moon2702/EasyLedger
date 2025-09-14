# AccountSelectorView 使用示例

## 概述
AccountSelectorView 是一个封装了账户选择逻辑的自定义View组件，可以在任何需要账户选择的地方复用。

## 基本使用

### 1. 在布局文件中添加
```xml
<com.example.easyledger.ui.AccountSelectorView
    android:id="@+id/accountSelector"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

### 2. 在Fragment/Activity中初始化
```java
public class YourFragment extends Fragment implements AccountSelectorListener {
    
    private AccountSelectorView accountSelector;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.your_layout, container, false);
        
        // 获取控件
        accountSelector = root.findViewById(R.id.accountSelector);
        
        // 设置生命周期所有者（用于观察数据变化）
        accountSelector.setLifecycleOwner(getViewLifecycleOwner());
        
        // 设置监听器
        accountSelector.setAccountSelectorListener(this);
        
        // 设置提示文本
        accountSelector.setHint("选择账户");
        
        return root;
    }
    
    // 实现AccountSelectorListener接口
    @Override
    public void onAccountSelected(Account selectedAccount) {
        // 账户被选择时的处理逻辑
        Log.d("AccountSelector", "Selected account: " + selectedAccount.getName());
    }
    
    @Override
    public void onAccountCleared() {
        // 账户选择被清除时的处理逻辑
        Log.d("AccountSelector", "Account selection cleared");
    }
    
    @Override
    public boolean onAccountSelectorClicked() {
        // 返回true允许显示选择对话框
        return true;
    }
}
```

## 高级用法

### 1. 获取选中的账户
```java
// 获取选中的账户对象
Account selectedAccount = accountSelector.getSelectedAccount();

// 获取选中的账户名称
String accountName = accountSelector.getSelectedAccountName();

// 检查是否有选中账户
boolean hasSelection = accountSelector.hasSelection();
```

### 2. 程序化设置选中账户
```java
// 根据账户对象设置
accountSelector.setSelectedAccount(account);

// 根据账户名称设置
accountSelector.setSelectedAccountByName("现金账户");

// 清除选择
accountSelector.clearSelection();
```

### 3. 自定义提示文本
```java
// 设置自定义提示文本
accountSelector.setHint("请选择支出账户");

// 通过资源ID设置提示文本
accountSelector.setHint(R.string.select_account_hint);
```

## 转账场景示例

对于转账功能，需要两个账户选择器：

```xml
<com.example.easyledger.ui.AccountSelectorView
    android:id="@+id/fromAccountSelector"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<com.example.easyledger.ui.AccountSelectorView
    android:id="@+id/toAccountSelector"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```java
public class TransferFragment extends Fragment implements AccountSelectorListener {
    
    private AccountSelectorView fromAccountSelector;
    private AccountSelectorView toAccountSelector;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transfer, container, false);
        
        fromAccountSelector = root.findViewById(R.id.fromAccountSelector);
        toAccountSelector = root.findViewById(R.id.toAccountSelector);
        
        // 设置两个选择器
        setupAccountSelector(fromAccountSelector, "转出账户");
        setupAccountSelector(toAccountSelector, "转入账户");
        
        return root;
    }
    
    private void setupAccountSelector(AccountSelectorView selector, String hint) {
        selector.setLifecycleOwner(getViewLifecycleOwner());
        selector.setAccountSelectorListener(this);
        selector.setHint(hint);
    }
    
    @Override
    public void onAccountSelected(Account selectedAccount) {
        // 可以在这里添加转账相关的验证逻辑
        // 比如检查转出账户余额是否足够等
    }
    
    @Override
    public void onAccountCleared() {
        // 处理账户选择清除
    }
    
    @Override
    public boolean onAccountSelectorClicked() {
        return true;
    }
}
```

## 注意事项

1. **生命周期管理**：必须调用 `setLifecycleOwner()` 方法，否则无法观察账户数据变化
2. **监听器设置**：建议实现 `AccountSelectorListener` 接口来处理选择事件
3. **数据验证**：在保存数据前，使用 `hasSelection()` 检查是否已选择账户
4. **错误处理**：组件内部已处理账户数据为空的情况，会显示相应的提示信息

## 优势

1. **代码复用**：一次编写，多处使用
2. **逻辑封装**：所有账户选择相关逻辑都封装在组件内部
3. **易于维护**：修改账户选择逻辑只需要修改一个文件
4. **类型安全**：直接返回Account对象，避免字符串操作错误
5. **生命周期感知**：自动处理数据观察和内存泄漏问题
