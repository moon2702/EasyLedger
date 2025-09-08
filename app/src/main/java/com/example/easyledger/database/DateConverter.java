package com.example.easyledger.database;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * 日期类型转换器，用于Room数据库将Date类型转换为可存储的Long类型
 */
public class DateConverter {
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}