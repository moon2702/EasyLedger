# CalendarFragment 账单列表功能说明

## 概述
在CalendarFragment的日期信息卡片下方新增了账单列表功能，用户可以查看选中日期的详细账单信息，并支持点击账单项进行编辑。

## 新增的UI组件

### 1. 布局文件更新 (fragment_calendar.xml)

#### **账单列表容器**
```xml
<!-- 选中日期的账单列表 -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginStart="16dp"
    android:layout_marginEnd="16dp"
    android:layout_marginTop="8dp"
    android:layout_marginBottom="16dp"
    android:orientation="vertical">
```

#### **列表标题**
```xml
<TextView
    android:id="@+id/tv_bills_list_title"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="当日账单"
    android:textAppearance="@style/TextAppearance.Material3.TitleMedium"
    android:layout_marginBottom="8dp"
    android:visibility="gone" />
```

#### **RecyclerView列表**
```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recycler_date_bills"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:nestedScrollingEnabled="false"
    android:visibility="gone" />
```

#### **无账单提示**
```xml
<TextView
    android:id="@+id/tv_no_bills"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="该日期暂无账单记录"
    android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
    android:textColor="@android:color/darker_gray"
    android:gravity="center"
    android:padding="16dp"
    android:visibility="gone" />
```

## 功能实现

### 1. 初始化账单列表

#### **initBillsList() 方法**
```java
private void initBillsList() {
    // 初始化适配器
    billsAdapter = new RecentBillsAdapter(new ArrayList<>());
    
    // 设置列表项点击事件监听器
    billsAdapter.setOnItemClickListener(bill -> {
        // 查找对应的Bill对象并启动编辑界面
        if (currentDateBills != null && !currentDateBills.isEmpty()) {
            for (Bill b : currentDateBills) {
                if (b.getId() == bill.id) {
                    Intent intent = new Intent(getActivity(), com.example.easyledger.BillAddActivity.class);
                    intent.putExtra(com.example.easyledger.BillAddActivity.EXTRA_BILL_ID, b.getId());
                    startActivity(intent);
                    break;
                }
            }
        }
    });
    
    // 设置RecyclerView
    recyclerDateBills.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerDateBills.setAdapter(billsAdapter);
}
```

### 2. 更新账单列表显示

#### **updateBillsList() 方法**
```java
private void updateBillsList(List<Bill> bills) {
    if (bills == null || bills.isEmpty()) {
        // 没有账单时隐藏列表，显示提示
        tvBillsListTitle.setVisibility(View.GONE);
        recyclerDateBills.setVisibility(View.GONE);
        tvNoBills.setVisibility(View.VISIBLE);
    } else {
        // 有账单时显示列表，隐藏提示
        tvBillsListTitle.setVisibility(View.VISIBLE);
        recyclerDateBills.setVisibility(View.VISIBLE);
        tvNoBills.setVisibility(View.GONE);
        
        // 转换数据并更新适配器
        List<RecentBill> recentBills = convertToRecentBills(bills);
        billsAdapter.updateData(recentBills);
    }
}
```

### 3. 数据转换

#### **convertToRecentBills() 方法**
```java
private List<RecentBill> convertToRecentBills(List<Bill> bills) {
    List<RecentBill> recentBills = new ArrayList<>();
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
    
    for (Bill bill : bills) {
        boolean isRepaymentOrTransfer = bill.getType() == BillType.REPAYMENT || bill.getType() == BillType.TRANSFER;
        boolean isExpense = bill.getType() == BillType.EXPENSE;

        String dateText = timeFormat.format(bill.getDate()); // 只显示时间
        String amountText = "";
        String subtitle = "";

        if (isRepaymentOrTransfer) {
            amountText = String.valueOf(bill.getAmount());
            subtitle = bill.getCategory() + " · " + bill.getAccount() + " -> " + bill.getTargetAccount();
        } else {
            if (isExpense) {
                amountText = "-" + String.valueOf(bill.getAmount());
            } else {
                amountText = "+" + String.valueOf(bill.getAmount());
            }
            subtitle = bill.getCategory() + " · " + bill.getAccount();
        }

        recentBills.add(new RecentBill(bill.getId(), bill.getTitle(), subtitle, dateText, amountText, isRepaymentOrTransfer, isExpense));
    }
    return recentBills;
}
```

## 复用的组件

### 1. RecentBillsAdapter
- **复用HomeFragment中的适配器**
- **支持点击事件处理**
- **自动更新数据**

### 2. item_recent_bill.xml
- **复用HomeFragment中的列表项布局**
- **显示账单标题、分类、账户、金额、时间**
- **支持不同账单类型的颜色显示**

### 3. RecentBill模型
- **复用HomeFragment中的数据模型**
- **包含账单的所有显示信息**

## 显示效果

### 1. 有账单时
```
当日账单
┌─────────────────────────────────────┐
│ 午餐费用                     -25.80 │
│ 食品餐饮 · 现金账户           12:30 │
├─────────────────────────────────────┤
│ 工资收入                     +5000  │
│ 工资 · 银行卡               09:00   │
├─────────────────────────────────────┤
│ 转账                         1000   │
│ 转账 · 银行卡 -> 现金账户    14:20  │
└─────────────────────────────────────┘
```

### 2. 无账单时
```
该日期暂无账单记录
```

## 交互功能

### 1. 点击账单项
- **启动编辑界面**：点击任意账单项会启动BillAddActivity
- **传递账单ID**：自动传递选中账单的ID进行编辑
- **保持编辑状态**：进入编辑模式，可以修改账单信息

### 2. 自动更新
- **日期选择时**：选择新日期时自动更新列表
- **数据同步**：与数据库保持实时同步
- **状态管理**：根据账单数量自动显示/隐藏相关组件

## 技术特点

### 1. **组件复用**
- 完全复用HomeFragment中的适配器和布局
- 保持UI风格的一致性
- 减少代码重复

### 2. **性能优化**
- 使用RecyclerView进行高效列表显示
- 异步数据加载，不阻塞UI线程
- 智能显示/隐藏，避免不必要的渲染

### 3. **用户体验**
- 直观的账单列表显示
- 支持点击编辑功能
- 友好的空状态提示

### 4. **数据一致性**
- 与数据库实时同步
- 支持所有账单类型显示
- 准确的时间格式显示

## 使用场景

### 1. **查看当日账单**
用户选择日期后，可以查看该日期的所有账单详情

### 2. **快速编辑账单**
点击账单项可以直接进入编辑界面

### 3. **账单统计**
结合日期信息卡片，提供完整的当日收支概览

## 扩展建议

### 1. **添加筛选功能**
- 按账单类型筛选
- 按金额范围筛选
- 按账户筛选

### 2. **添加排序功能**
- 按时间排序
- 按金额排序
- 按类型排序

### 3. **添加批量操作**
- 批量删除账单
- 批量修改分类
- 批量导出数据

### 4. **添加搜索功能**
- 在当日账单中搜索
- 支持标题和分类搜索

## 注意事项

1. **内存管理**：及时清理不需要的数据引用
2. **线程安全**：所有UI更新都在主线程执行
3. **数据同步**：确保列表数据与数据库保持一致
4. **用户体验**：提供适当的加载状态和错误处理
