package com.example.finalyearproject.utils;

import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    private static final String[] monthAbb = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    /* month number to abbreviation. e.g. 0 -> Mar; month: 0-11 */
    public static String monthNumberToAbbreviation(int month) {
        return monthAbb[month];
    }

    public static int monthAbbreviationToNumber(String abb) {
        int month = -1;
        for (int i = 0; i < monthAbb.length; i++) {
            if (monthAbb[i].equals(abb)) {
                month = i;
                break;
            }
        }
        return month;
    }

    public static String[] getMonthAbb() {
        return monthAbb;
    }

    /* return year month string in form "2023 Mar"; month: 0-11 */
    public static String getYearMonthStr(int year, int month){
        return String.format(Locale.US, "%04d %s", year, DateUtils.monthNumberToAbbreviation(month));
    }

    /* return date in form yyyy-MM-dd */
    public static String getDateStrYYYYMMDD(int year, int month, int dayOfMonth) {
        return String.format(Locale.US, "%4d-%02d-%02d", year, month + 1, dayOfMonth);
    }


    /* return date in form dd/MM/yyyy */
    public static String getDateStrDDMMYYYY(int year, int month, int dayOfMonth) {
        return String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year);
    }



}
