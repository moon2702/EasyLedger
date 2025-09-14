# CalendarFragment 收支汇总卡片功能说明

## 概述
将CalendarFragment中的收入支出指示器替换为HomeFragment风格的收支汇总卡片，提供更直观的当日收支概览。

## 布局更新

### 1. 替换前的收入支出指示器
```xml
<!-- 支出/收入指示器 -->
<LinearLayout
    android:id="@+id/expense_indicator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center"
    android:padding="16dp"
    android:layout_marginTop="16dp">

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_marginEnd="16dp">
        <View
            android:layout_width="12dp"
            android:layout_height="12dp"
            android:background="@color/red"
            android:layout_marginEnd="4dp" />
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/expense"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center">
        <View
            android:layout_width="12dp"
            android:layout_height="12dp"
            android:background="@color/green"
            android:layout_marginEnd="4dp" />
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/income"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium" />
    </LinearLayout>
</LinearLayout>
```

### 2. 替换后的收支汇总卡片
```xml
<!-- 本日收支汇总卡片 -->
<com.google.android.material.card.MaterialCardView
    android:id="@+id/summaryCard"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginStart="16dp"
    android:layout_marginEnd="16dp"
    android:layout_marginTop="16dp"
    app:shapeAppearance="@style/ShapeAppearance.EasyLedger.Medium">

    <LinearLayout
        android:id="@+id/summaryContent"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/summaryTitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="本日收支"
            android:textAppearance="@style/TextAppearance.Material3.TitleMedium" />

        <TextView
            android:id="@+id/summaryValue"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:text="+0.00"
            android:textAppearance="@style/TextAppearance.Material3.DisplaySmall" />

        <TextView
            android:id="@+id/summaryDesc"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:text="收入 0.00 · 支出 0.00"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
            android:textColor="@android:color/darker_gray" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

## 功能实现

### 1. 控件声明
```java
// 收支汇总卡片相关
private TextView summaryValue;
private TextView summaryDesc;
```

### 2. 控件初始化
```java
// 初始化收支汇总卡片控件
summaryValue = view.findViewById(R.id.summaryValue);
summaryDesc = view.findViewById(R.id.summaryDesc);
```

### 3. 数据更新逻辑
```java
// 更新收支汇总卡片
updateSummaryCard(income, expense);
```

### 4. updateSummaryCard方法
```java
/**
 * 更新收支汇总卡片
 */
private void updateSummaryCard(double income, double expense) {
    if (summaryValue == null || summaryDesc == null) {
        return;
    }

    // 计算净收支
    double total = income - expense;

    // 更新UI
    summaryValue.setText(String.format("%s%.2f", total >= 0 ? "+" : "", total));
    summaryDesc.setText(String.format("收入 %.2f · 支出 %.2f", income, expense));
}
```

## 显示效果对比

### 1. 替换前（收入支出指示器）
```
    🔴 支出    🟢 收入
```

### 2. 替换后（收支汇总卡片）
```
┌─────────────────────────────────────┐
│ 本日收支                            │
│ +125.50                            │
│ 收入 500.00 · 支出 374.50          │
└─────────────────────────────────────┘
```

## 功能特点

### 1. **视觉一致性**
- 与HomeFragment的summaryCard保持完全一致的样式
- 使用相同的MaterialCardView和文本样式
- 保持统一的UI设计语言

### 2. **信息丰富性**
- **标题**：显示"本日收支"
- **净收支**：显示当日收入减去支出的结果
- **详细描述**：显示具体的收入和支出金额

### 3. **数据实时性**
- 选择不同日期时自动更新
- 与数据库保持实时同步
- 支持所有账单类型的统计

### 4. **用户体验**
- 更直观的收支概览
- 清晰的数值显示
- 符合用户习惯的卡片式设计

## 数据计算逻辑

### 1. **收入统计**
```java
// 只统计INCOME类型的账单
if (bill.getType() == BillType.INCOME) {
    income += bill.getAmount();
}
```

### 2. **支出统计**
```java
// 只统计EXPENSE类型的账单
if (bill.getType() == BillType.EXPENSE) {
    expense += bill.getAmount();
}
```

### 3. **净收支计算**
```java
// 净收支 = 收入 - 支出
double total = income - expense;
```

### 4. **显示格式**
```java
// 净收支显示（正数显示+号，负数显示-号）
summaryValue.setText(String.format("%s%.2f", total >= 0 ? "+" : "", total));

// 详细描述显示
summaryDesc.setText(String.format("收入 %.2f · 支出 %.2f", income, expense));
```

## 样式特点

### 1. **卡片样式**
- 使用MaterialCardView提供阴影效果
- 圆角设计，符合Material Design规范
- 与HomeFragment保持一致的边距和形状

### 2. **文本样式**
- **标题**：使用TitleMedium样式，突出显示
- **净收支**：使用DisplaySmall样式，大字体显示
- **描述**：使用BodyMedium样式，灰色显示

### 3. **布局结构**
- 垂直布局，信息层次清晰
- 合理的间距设置
- 响应式设计，适配不同屏幕

## 使用场景

### 1. **当日收支概览**
用户选择日期后，可以快速了解该日期的收支情况

### 2. **财务分析**
通过净收支数值，用户可以判断当日是盈利还是亏损

### 3. **数据对比**
结合账单列表，用户可以详细分析收支构成

## 技术优势

### 1. **代码复用**
- 完全复用HomeFragment的样式和逻辑
- 减少代码重复，提高维护性
- 保持UI一致性

### 2. **性能优化**
- 异步数据查询，不阻塞UI
- 智能更新，只在数据变化时刷新
- 内存友好的数据管理

### 3. **扩展性**
- 易于添加新的统计维度
- 支持自定义显示格式
- 可扩展为更复杂的财务分析

## 注意事项

1. **数据准确性**：确保只统计INCOME和EXPENSE类型的账单
2. **显示格式**：保持与HomeFragment一致的数值格式
3. **空值处理**：当没有数据时显示默认值
4. **线程安全**：所有UI更新都在主线程执行

## 扩展建议

### 1. **添加更多统计维度**
- 按账户统计收支
- 按分类统计收支
- 按时间段统计收支

### 2. **增强视觉效果**
- 添加趋势图表
- 使用颜色区分正负值
- 添加动画效果

### 3. **添加交互功能**
- 点击卡片查看详细统计
- 支持数据导出
- 添加历史对比功能
