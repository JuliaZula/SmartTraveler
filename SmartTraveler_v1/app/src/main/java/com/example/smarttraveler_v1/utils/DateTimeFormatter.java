package com.example.smarttraveler_v1.utils;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.util.Locale;

/**
 * Utility class for formatting date and time.
 * Provides methods to generate the start and end of a given day in ISO 8601 format.
 */
public class DateTimeFormatter {

    /** Date-time format in ISO 8601 format (e.g., "YYYY-MM-DD'T'HH:mm:ss"). */
    private static final SimpleDateFormat dateTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    /**
     * Generates an end-of-day timestamp for a given date.
     * The returned format is "YYYY-MM-DDT23:59:59".
     *
     * @param year  The year.
     * @param month The month (1-based, e.g., January = 1).
     * @param day   The day of the month.
     * @return A formatted timestamp string for the end of the day.
     */
    public static String generateEndOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 23, 59, 59); //Calendar的月份从 0 开始
        return dateTimeFormat.format(calendar.getTime());
    }

    /**
     * Generates a start-of-day timestamp for a given date.
     * The returned format is "YYYY-MM-DDT00:00:00".
     *
     * @param year  The year.
     * @param month The month (1-based, e.g., January = 1).
     * @param day   The day of the month.
     * @return A formatted timestamp string for the start of the day.
     */
    public static String generateStartOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0); // 注意：月份要减 1
        return dateTimeFormat.format(calendar.getTime());
    }
}
