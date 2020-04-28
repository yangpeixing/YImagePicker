package com.ypx.imagepicker.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.ypx.imagepicker.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间工具类
 */
@SuppressLint("SimpleDateFormat")
public class PDateUtil {

    public static String getStrTime(Context context, long cc_time) {
        if (cc_time == 0) {
            return "";
        }
        if (String.valueOf(cc_time).length() <= 10) {
            cc_time = cc_time * 1000L;
        }
        Date date = new Date(cc_time);
        if (isToday(date)) {
            return context.getString(R.string.picker_str_today);
        }
        if (isThisWeek(date)) {
            return context.getString(R.string.picker_str_this_week);
        }
        if (isThisMonth(date)) {
            return context.getString(R.string.picker_str_this_months);
        }
        return new SimpleDateFormat(context.getString(R.string.picker_str_time_format)).format(date);
    }

    //判断选择的日期是否是本周
    private static boolean isThisWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(date);
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    //判断选择的日期是否是今天
    private static boolean isToday(Date date) {
        return isThisTime(date, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    private static boolean isThisMonth(Date date) {
        return isThisTime(date, "yyyy-MM");
    }

    private static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        return param.equals(now);
    }


    /**
     * 获取视频时长（格式化）
     */
    public static String getVideoDuration(long timestamp) {
        if (timestamp < 1000) {
            return "00:01";
        }
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(date);
    }


    /*
     * 毫秒转化
     */
    public static String formatTime(Context context,Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append(context.getString(R.string.picker_str_day));
        }
        if (hour > 0) {
            sb.append(hour).append(context.getString(R.string.picker_str_hour));
        }
        if (minute > 0) {
            sb.append(minute).append(context.getString(R.string.picker_str_minute));
        }
        if (second > 0) {
            sb.append(second).append(context.getString(R.string.picker_str_second));
        }
        if (milliSecond > 0) {
            sb.append(milliSecond).append(context.getString(R.string.picker_str_milli));
        }
        return sb.toString();
    }
}
