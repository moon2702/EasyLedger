package com.example.easyledger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyledger.R;
import com.example.easyledger.database.Bill;
import com.example.easyledger.database.BillViewModel;
import com.example.easyledger.database.BillType;
import com.example.easyledger.ui.adapter.RecentBillsAdapter;
import com.example.easyledger.ui.model.RecentBill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvCurrentMonth;
    private TextView tvSelectedDateInfo;
    private Button btnPrevMonth;
    private Button btnNextMonth;
    private SimpleDateFormat monthFormat;
    private SimpleDateFormat dateFormat;
    private Calendar selectedDate;
    private int currentYear;
    private int currentMonth;
    private BillViewModel billViewModel;
    
    // 账单列表相关
    private RecyclerView recyclerDateBills;
    private TextView tvBillsListTitle;
    private TextView tvNoBills;
    private RecentBillsAdapter billsAdapter;
    private List<Bill> currentDateBills;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // 初始化控件
        tvCurrentMonth = view.findViewById(R.id.tv_current_month);
        btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        btnNextMonth = view.findViewById(R.id.btn_next_month);
        calendarView = view.findViewById(R.id.calendar_view);
        tvSelectedDateInfo = view.findViewById(R.id.tv_selected_date_info);
        
        // 初始化账单列表相关控件
        recyclerDateBills = view.findViewById(R.id.recycler_date_bills);
        tvBillsListTitle = view.findViewById(R.id.tv_bills_list_title);
        tvNoBills = view.findViewById(R.id.tv_no_bills);

        // 初始化ViewModel
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
        
        // 初始化账单列表
        initBillsList();

        // 初始化日期变量
        selectedDate = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        currentYear = currentCalendar.get(Calendar.YEAR);
        currentMonth = currentCalendar.get(Calendar.MONTH);

        // 初始化日期格式
        monthFormat = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

        // 设置当前月份标题
        updateMonthTitle();
        updateSelectedDateInfo();

        // 设置月份切换按钮点击事件
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            updateMonthTitle();
            updateCalendarView();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            updateMonthTitle();
            updateCalendarView();
        });

        // 设置日期选择监听
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // 更新选中日期信息
            updateSelectedDateInfo();

            // 加载该日期的数据
            loadDataForSelectedDate();
        });

        return view;
    }

    // 初始化账单列表
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

    // 更新月份标题
    private void updateMonthTitle() {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.YEAR, currentYear);
        tempCalendar.set(Calendar.MONTH, currentMonth);
        String currentMonth = monthFormat.format(tempCalendar.getTime());
        tvCurrentMonth.setText(currentMonth);
    }

    // 更新日历视图
    private void updateCalendarView() {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.YEAR, currentYear);
        tempCalendar.set(Calendar.MONTH, currentMonth);
        // 设置日历显示的月份
        long timeInMillis = tempCalendar.getTimeInMillis();
        calendarView.setDate(timeInMillis, false, true);
    }

    // 更新选中日期信息
    private void updateSelectedDateInfo() {
        if (selectedDate != null) {
            String dateStr = dateFormat.format(selectedDate.getTime());
            
            // 在后台线程查询数据
            new Thread(() -> {
                try {
                    long dateInMillis = selectedDate.getTimeInMillis();
                    
                    // 查询该日期的各项数据
                    double expense = billViewModel.getExpenseByDate(dateInMillis);
                    double income = billViewModel.getIncomeByDate(dateInMillis);
                    double transfer = billViewModel.getTransferByDate(dateInMillis);
                    double repayment = billViewModel.getRepaymentByDate(dateInMillis);
                    
                    // 获取该日期的所有账单
                    List<Bill> bills = billViewModel.getBillsByDate(dateInMillis);
                    currentDateBills = bills; // 保存当前日期的账单
                    
                    // 在UI线程更新显示
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            StringBuilder info = new StringBuilder();
                            info.append(dateStr).append("\n");
                            
                            if (expense > 0) {
                                info.append("支出: ¥").append(String.format("%.2f", expense)).append("\n");
                            }
                            if (income > 0) {
                                info.append("收入: ¥").append(String.format("%.2f", income)).append("\n");
                            }
                            if (transfer > 0) {
                                info.append("转账: ¥").append(String.format("%.2f", transfer)).append("\n");
                            }
                            if (repayment > 0) {
                                info.append("还款: ¥").append(String.format("%.2f", repayment)).append("\n");
                            }
                            
                            if (bills != null && !bills.isEmpty()) {
                                info.append("账单数量: ").append(bills.size()).append("笔");
                            } else {
                                info.append("暂无账单记录");
                            }
                            
                            tvSelectedDateInfo.setText(info.toString());
                            
                            // 更新账单列表
                            updateBillsList(bills);
                        });
                    }
                } catch (Exception e) {
                    // 如果查询失败，显示默认信息
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvSelectedDateInfo.setText(dateStr + "\n数据加载失败");
                        });
                    }
                }
            }).start();
        }
    }

    // 加载选中日期的数据
    private void loadDataForSelectedDate() {
        if (selectedDate != null) {
            String dateStr = dateFormat.format(selectedDate.getTime());
            
            // 在后台线程加载详细数据
            new Thread(() -> {
                try {
                    long dateInMillis = selectedDate.getTimeInMillis();
                    
                    // 获取该日期的所有账单
                    List<Bill> bills = billViewModel.getBillsByDate(dateInMillis);
                    
                    // 在UI线程显示提示信息
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (bills != null && !bills.isEmpty()) {
                                // 统计各类型账单数量
                                int expenseCount = 0, incomeCount = 0, transferCount = 0, repaymentCount = 0;
                                for (Bill bill : bills) {
                                    switch (bill.getType()) {
                                        case EXPENSE:
                                            expenseCount++;
                                            break;
                                        case INCOME:
                                            incomeCount++;
                                            break;
                                        case TRANSFER:
                                            transferCount++;
                                            break;
                                        case REPAYMENT:
                                            repaymentCount++;
                                            break;
                                    }
                                }
                                
                                StringBuilder message = new StringBuilder();
                                message.append("已加载 ").append(dateStr).append(" 的数据\n");
                                message.append("共 ").append(bills.size()).append(" 笔账单");
                                
                                if (expenseCount > 0) message.append("，支出 ").append(expenseCount).append(" 笔");
                                if (incomeCount > 0) message.append("，收入 ").append(incomeCount).append(" 笔");
                                if (transferCount > 0) message.append("，转账 ").append(transferCount).append(" 笔");
                                if (repaymentCount > 0) message.append("，还款 ").append(repaymentCount).append(" 笔");
                                
                                Toast.makeText(getContext(), message.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), dateStr + " 暂无账单记录", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                    // 更新选中日期信息显示
                    updateSelectedDateInfo();
                    
                } catch (Exception e) {
                    // 如果加载失败，显示错误信息
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "数据加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        }
    }

    // 更新账单列表显示
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

    /**
     * 将Bill对象列表转换为RecentBill对象列表
     */
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
}


