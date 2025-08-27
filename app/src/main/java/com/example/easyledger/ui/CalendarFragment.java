package com.example.easyledger.ui;

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
import com.example.easyledger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
            selectedDate = new Calendar.Builder()
                    .set(Calendar.YEAR, year)
                    .set(Calendar.MONTH, month)
                    .set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    .build();

            // 更新选中日期信息
            updateSelectedDateInfo();

            // 加载该日期的数据
            loadDataForSelectedDate();
        });

        return view;
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
            // 模拟加载数据
            double expense = Math.random() * 1000;
            double income = Math.random() * 5000;

            // 更新显示
            String info = dateStr + "\n支出: ¥" + String.format("%.2f", expense) + "\n收入: ¥" + String.format("%.2f", income);
            tvSelectedDateInfo.setText(info);
        }
    }

    // 加载选中日期的数据
    private void loadDataForSelectedDate() {
        if (selectedDate != null) {
            String dateStr = dateFormat.format(selectedDate.getTime());
            updateSelectedDateInfo();
            // 显示简短提示
            // Toast.makeText(getContext(), "已加载 " + dateStr + " 的数据", Toast.LENGTH_SHORT).show();
        }
    }
}


