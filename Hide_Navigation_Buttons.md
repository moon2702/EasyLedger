# 隐藏底部导航按钮

## 修改内容
隐藏了底部导航栏中的"统计"（stats）和"设置"（settings）按钮。

## 修改的文件
- `app/src/main/res/menu/menu_bottom_nav.xml`

## 修改详情

### 隐藏的菜单项
1. **统计按钮** (`nav_stats`)
   - 标题：统计
   - 图标：`ic_nav_stats`

2. **设置按钮** (`nav_settings`)
   - 标题：设置
   - 图标：`ic_nav_settings`

### 保留的菜单项
1. **首页** (`nav_home`)
2. **日历** (`nav_calendar`)
3. **资产** (`nav_assets`)

## 实现方式
使用XML注释的方式隐藏菜单项，而不是删除，这样便于以后重新启用：

```xml
<!-- 暂时隐藏stats和settings按钮 -->
<!--
<item
    android:id="@+id/nav_stats"
    android:title="@string/title_stats"
    android:icon="@drawable/ic_nav_stats" />
<item
    android:id="@+id/nav_settings"
    android:title="@string/title_settings"
    android:icon="@drawable/ic_nav_settings" />
-->
```

## 注意事项
1. MainActivity中的相关处理代码已经被注释掉，无需额外修改
2. 如果以后需要重新启用这些按钮，只需取消XML注释即可
3. 隐藏后底部导航栏只显示3个按钮，布局会自动调整

## 效果
- 底部导航栏现在只显示：首页、日历、资产
- 界面更加简洁，专注于核心功能
- 统计和设置功能暂时不可通过底部导航访问


