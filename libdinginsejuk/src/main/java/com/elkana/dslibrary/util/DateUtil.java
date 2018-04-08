package com.elkana.dslibrary.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eric on 11-Nov-17.
 */

public class DateUtil {
    public static final long TIME_ONE_MINUTE_MILLIS = 1 * 60 * 1000;
    public static final long TIME_ONE_HOUR_MILLIS = 60 * TIME_ONE_MINUTE_MILLIS;
    public static final long TIME_FIVE_MINUTE_MILLIS = 5 * TIME_ONE_MINUTE_MILLIS;
    public static final long TIME_TEN_MINUTE_MILLIS = 2 * TIME_FIVE_MINUTE_MILLIS;
    public static final long TIME_FIFTHEEN_MINUTE_MILLIS = 15 * TIME_ONE_MINUTE_MILLIS;
    public static final long TIME_ONE_DAYS_MILLIS = 1 * 24 * TIME_ONE_HOUR_MILLIS;
    public static final int TIME_ONE_HOUR_MINUTES = 60;

    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * <p>Checks if the first date is before the second date ignoring time.</p>
     * @param date1 the first date, not altered, not null
     * @param date2 the second date, not altered, not null
     * @return true if the first date day is before the second date day.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isBeforeDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isBeforeDay(cal1, cal2);
    }

    /**
     * <p>Checks if the first calendar date is before the second calendar date ignoring time.</p>
     * @param cal1 the first calendar, not altered, not null.
     * @param cal2 the second calendar, not altered, not null.
     * @return true if cal1 date is before cal2 date ignoring time.
     * @throws IllegalArgumentException if either of the calendars are <code>null</code>
     */
    public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * <p>Checks if the first date is after the second date ignoring time.</p>
     * @param date1 the first date, not altered, not null
     * @param date2 the second date, not altered, not null
     * @return true if the first date day is after the second date day.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isAfterDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isAfterDay(cal1, cal2);
    }

    /**
     * <p>Checks if the first calendar date is after the second calendar date ignoring time.</p>
     * @param cal1 the first calendar, not altered, not null.
     * @param cal2 the second calendar, not altered, not null.
     * @return true if cal1 date is after cal2 date ignoring time.
     * @throws IllegalArgumentException if either of the calendars are <code>null</code>
     */
    public static boolean isAfterDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return false;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return true;
        return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isToday(Date date) {
        return isSameDay(new Date(), date);
    }
    public static boolean isToday(long timestamp) {
        return isSameDay(new Date(), new Date(timestamp));
    }

    /**
     // fungsi yg cukup bahaya. hanya boleh dipake di mitra yg jamnya lebih akurat
     * 1 hour(60 minutes) from now
     *
     * @param timestamp
     * @param lastMinutes if 30, 30 minutes from now is expired
     * @return > 0 of how long time is expired(in milliseconds). < 1 if not expired.
     */
    public static long isExpiredTime(long timestamp, int lastMinutes) {
//        Date date = new Date(timeMillisToCheck);
//        // 2017-12-13 07:22
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.add(Calendar.MINUTE, lastMinutes);
//        // 2017-12-13  07:22 + minuteToleransi
//
//        if (date.getTime() < c.getTimeInMillis()) {
//            return true;
//        } else {
//            return false;
//        }
        Date date = new Date(timestamp);
        // 2017-12-13 07:22

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, lastMinutes);
        // 2017-12-13  07:22 + minuteToleransi

        long selisih = new Date().getTime() - c.getTimeInMillis();

        return selisih;

//        if (new Date().getTime() > c.getTimeInMillis()) {
//            return true;
//        } else {
//            return false;
//        }
    }

    public static String formatDateToSimple(long timestamp) {
        return Util.convertDateToString(new Date(timestamp), "dd MMM yyyy HH:mm:ss");
    }

    public static String formatMillisToMinutesSeconds(long millis) {
        return String.format("%02d : %02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static String displayTimeInJakarta(long timestamp, String pattern) {
//        SimpleDateFormat formatIncoming =
//                new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat formatOutgoing = new SimpleDateFormat(pattern);
        TimeZone tz = TimeZone.getTimeZone("Asia/Jakarta");
//        System.out.println(tz.getDisplayName(false, TimeZone.SHORT, Locale.ENGLISH)); // WIB

        formatOutgoing.setTimeZone(tz);
        String ss = formatOutgoing.format(new Date(timestamp));

        return ss;
//        String ss = formatOutgoing.format(formatIncoming.parse("Tue Mar 03 00:00:00 WIB 2015"));

    }

    /**
     * time is between 100 - 2400.
     * any below 100 will be multiplied, so if 8, will be assume 8:00. if 83, will be asume 8:30
     * 800 for 8:00
     * 830 for 8:30.
     * @param openTime
     * @param closeTime
     * @param minuteStep
     * @return
     */
    public static String[] generateWorkingHours(int openTime, int closeTime, int minuteStep) {

        int digitCount = Util.getDigitsCount(openTime);

        if (digitCount == 1 || digitCount == 2)
            openTime *= 100;
        if (digitCount == 3)
            openTime *= 10;

        digitCount = Util.getDigitsCount(closeTime);
        if (digitCount == 1 || digitCount == 2)
            closeTime *= 100;
        if (digitCount == 3)
            closeTime *= 10;


        List<String> list = new ArrayList<>();

        int counterHour = openTime / 100;
        int counterMin = openTime % 100;
        int stopHour = closeTime / 100;
        int stopMin = closeTime % 100;

        while (counterHour <= stopHour){

            if (counterMin >= 60){
                counterMin = 0;
                counterHour += 1;
            }

            list.add(String.format("%02d", counterHour) + ":" + String.format("%02d", counterMin));

            if (counterHour >= stopHour && counterMin >= stopMin)
                break;

            counterMin += minuteStep;
        }

        return list.toArray(new String[list.size()]);
    }
}
