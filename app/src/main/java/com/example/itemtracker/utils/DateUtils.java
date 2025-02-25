package com.example.itemtracker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static long getDaysBetween(String startDate, String endDate) throws ParseException {
        Date start = DATE_FORMAT.parse(startDate);
        Date end = DATE_FORMAT.parse(endDate);
        long diffInMillies = Math.abs(end.getTime() - start.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static String getCurrentDate() {
        return DATE_FORMAT.format(new Date());
    }
}
