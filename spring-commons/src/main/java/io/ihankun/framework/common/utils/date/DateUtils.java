package io.ihankun.framework.common.utils.date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hankun
 */
@Slf4j
public class DateUtils {

    static Map<Integer, SimpleDateFormat> formatMap = new HashMap<>(16);

    /**
     * 默认每天分隔时间点
     */
    private static final String DAY_SPLIT = "00:00:00";

    private static SimpleDateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    /**
     * 获取当前时间
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取今日零点的时间
     */
    public static Date getToday() {
        return getToday(getNowDate());
    }

    /**
     * 获取某个日期的起始时间
     */
    public static Date getToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取本月月初的日期
     */
    public static Date getFirstDayOfMonth() {
        return getFirstDayOfMonth(getNowDate());
    }

    /**
     * 获取某个日期所在月份月初的日期
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今日周几
     */
    public static int getWeek() {
        return getWeek(getNowDate());
    }

    /**
     * 获取某个日期星期几，从1开始
     */
    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            return 7;
        }
        return week - 1;
    }

    /**
     * 时间格式化后的字符串转换为时间格式
     */
    public static Date dateStrToDate(String dateStr) {
        try {
            return getFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据指定format进行转换
     */
    public static Date dateStrTo(String dateStr, String format) {
        try {
            return getFormat(format).parse(dateStr);
        } catch (Exception e) {
            log.error("日期参数：{},format：{},日期解析报错：{}", e,dateStr,format);
            try {
                return getFormat(format).parse(getNowStr());
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /**
     * 获取当前时间格式化字符串
     * yyyy-MM-dd HH:mm:ss
     */
    public static String getNowStr() {
        return getFormat("yyyy-MM-dd HH:mm:ss").format(getNowDate());
    }


    /**
     * 获取今日精确到天的字符串
     */
    public static String getNowDayStr() {
        return getFormat("yyyy-MM-dd").format(getNowDate());
    }

    /**
     * 获取某个时间格式化字符串
     * yyyy-MM-dd HH:mm:ss
     */
    public static String getDateStr(Date date) {
        return getFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 获取某个时间自定义格式化字符串
     */
    public static String getDateStr(Date date, String format) {
        return getFormat(format).format(date);
    }

    /**
     * 判断当前时间是否在今天范围内
     */
    public static boolean nowInToday() {
        return nowInToday(null);
    }

    /**
     * 判断当前时间是否在今天范围内
     */
    public static boolean nowInToday(String split) {
        return dateInDay(getNowDate(), getDateStr(getNowDate(), "yyyy-MM-dd"), split);
    }

    /**
     * 判断某个时间是否在今天内
     */
    public static boolean dateInToday(Date date) {
        return dateInToday(date, null);
    }

    /**
     * 判断某个时间是否在今天内
     */
    public static boolean dateInToday(Date date, String split) {
        return dateInDay(date, getDateStr(getNowDate(), "yyyy-MM-dd"), split);
    }


    /**
     * 判断某个时间是否在某天内
     * @param date 某个时间
     * @param day  某一天 格式：yyyy-MM-dd
     */
    public static boolean dateInDay(Date date, String day) {
        return dateInDay(date, day, null);
    }

    /**
     * 判断某个时间是否在某天内
     * @param date  某个时间
     * @param day   某一天 格式：yyyy-MM-dd
     * @param split 分隔时间节点，默认为 00:00:00
     */
    public static boolean dateInDay(Date date, String day, String split) {
        split = StringUtils.isEmpty(split) ? DAY_SPLIT : split;
        day = day + " " + split;
        //标准时间-起始时间
        Date standardStart = dateStrToDate(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(standardStart);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        //标准时间-截止时间
        Date standardEnd = calendar.getTime();
        return date.after(standardStart) && date.before(standardEnd);
    }

    /**
     * 获取当前时间点是在今天的第x小时
     */
    public static int getHourOfDay(Date date, String split) {
        int minute = getMinuteOfDay(date, split);
        return minute / 60;
    }

    /**
     * 获取当前时间点是在今天的第x小时
     * @param date 时间
     * @return 0-23
     */
    public static int getHourOfDay(Date date) {
        return getHourOfDay(date, null);
    }

    /**
     * 获取当前时间点是在今天的第x分钟
     */
    public static int getMinuteOfDay(Date date, String split) {
        int second = getSecondOfDay(date, split);
        return second / 60;
    }

    /**
     * 获取当前时间点是在今天的第x分钟
     */
    public static int getMinuteOfDay(Date date) {
        return getMinuteOfDay(date, null);
    }

    /**
     * 获取当前时间点是在今天的第x秒
     */
    public static int getSecondOfDay(Date date) {
        return getSecondOfDay(date, null);
    }

    /**
     * 获取当前时间点是在今天的第x秒
     */
    public static int getSecondOfDay(Date date, String split) {
        split = StringUtils.isEmpty(split) ? DAY_SPLIT : split;
        Date standardTime = dateStrToDate(getDateStr(date, "yyyy-MM-dd") + " " + split);
        long standardSecond = standardTime.getTime() / 1000;
        long dateSecond = date.getTime() / 1000;
        return (int) (dateSecond - standardSecond);
    }


    /**
     * 时间加减
     */
    public static Date dateAddSub(Date date, int unit, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(unit, num);
        return calendar.getTime();
    }

    /**
     * 时间按天加减
     */
    public static Date dateAddSubDay(Date date, int num) {
        return dateAddSub(date, Calendar.DAY_OF_MONTH, num);
    }


    /**
     * 判断某个时间是否在某个范围内
     */
    public static boolean checkBetween(Date base, Date start, Date end) {
        Date date = getNowDate();
        return date.before(start) && date.after(end);
    }

    /**
     * 判断当前时间是否在某个范围内
     */
    public static boolean checkBetween(Date start, Date end) {
        return checkBetween(getNowDate(), start, end);
    }
}
