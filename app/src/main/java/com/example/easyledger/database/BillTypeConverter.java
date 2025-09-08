package com.example.easyledger.database;

import androidx.room.TypeConverter;

/**
 * BillType枚举类型转换器，用于Room数据库
 */
public class BillTypeConverter {

    @TypeConverter
    public static BillType toBillType(String value) {
        if (value == null) {
            return null;
        }
        try {
            return BillType.valueOf(value);
        } catch (IllegalArgumentException e) {
            // 如果数据库中的值无法匹配任何枚举常量，返回默认值
            return BillType.EXPENSE;
        }
    }

    @TypeConverter
    public static String toString(BillType billType) {
        if (billType == null) {
            return null;
        }
        return billType.name();
    }
}