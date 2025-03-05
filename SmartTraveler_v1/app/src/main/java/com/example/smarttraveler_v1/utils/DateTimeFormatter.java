package com.example.smarttraveler_v1.utils;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.util.Locale;

public class DateTimeFormatter {
    private static final SimpleDateFormat dateTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    // "YYYY-MM-DDT23:59:59"
    public static String generateEndOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 23, 59, 59); // 注意：Calendar的月份从 0 开始
        return dateTimeFormat.format(calendar.getTime());
    }

    // "YYYY-MM-DDT00:00:00"
    public static String generateStartOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0); // 注意：月份要减 1
        return dateTimeFormat.format(calendar.getTime());
    }
}
