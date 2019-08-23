package com.mg.sn.utils.common;

import freemarker.template.SimpleDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class TimeUtils {

    /**
     * 获取当前时间戳
     * @return
     */
    public static long getCurrentTime () {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前系统时间 yyyy-MM-dd
     * @return
     */
    public static String getDate (Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(date);
        return format;
    }
}
