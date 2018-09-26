package com.ypx.imagepicker.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者：yangpeixing on 2018/9/20 17:26
 * 功能：时间转化工具类
 * 产权：南京婚尚信息技术
 */
public class DateUtil {

    public static String getStrTime(long cc_time) {
        Date date = new Date(cc_time * 1000L);
        if (isToday(date)) {
            return "今天";
        }
        if (isThisWeek(date)) {
            return "本周";
        }

        if (isThisMonth(date)) {
            return "这个月";
        }

        String re_StrTime = null;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        re_StrTime = sdf.format(date);
        return re_StrTime;
    }

    //判断选择的日期是否是本周
    public static boolean isThisWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(date);
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(Date date) {
        return isThisTime(date, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(Date date) {
        return isThisTime(date, "yyyy-MM");
    }

    private static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        return param.equals(now);
    }


}
