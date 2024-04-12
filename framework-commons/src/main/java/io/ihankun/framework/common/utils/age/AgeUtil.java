package io.ihankun.framework.common.utils.age;


import io.ihankun.framework.common.error.impl.CommonErrorCode;
import io.ihankun.framework.common.utils.date.DateUtils;
import io.ihankun.framework.common.exception.BusinessException;

import java.util.Calendar;
import java.util.Date;

/**
 * @author hankun
 */
public class AgeUtil {

    /**
     * 根据年龄倒推出生日期
     *
     * @param year   岁
     * @param month  月
     * @param day    天
     * @param hour   小时
     * @param minute 分钟
     * @return
     */
    public static final Date getBirthDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.getNowDate());
        if (year > 0) {
            calendar.add(Calendar.YEAR, -year);
        }
        if (month > 0) {
            calendar.add(Calendar.MONTH, -month);
        }
        if (day > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, -day);
        }
        if (hour > 0) {
            calendar.add(Calendar.HOUR, -hour);
        }
        if (minute > 0) {
            calendar.add(Calendar.MINUTE, -minute);
        }
        return calendar.getTime();
    }

    /**
     * 计算年龄：年、月、日、时、分、秒
     *
     * @param birthTime：出生时间
     * @return 年龄：{"Y":xx, "M":xx, "D":xx, "H":xx, "m":xx, "S":xx}
     */
    public static Age getAge(Date birthTime) {
        if (birthTime == null) {
            throw BusinessException.build(CommonErrorCode.DATE_FORMAT_ERROR);
        }

        // 获取当前时间：年、月、日、时、分、秒
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH) + 1;
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteNow = calendar.get(Calendar.MINUTE);
        int secondNow = calendar.get(Calendar.SECOND);

        // 获取出生时间：年、月、日、时、分、秒
        calendar.setTime(birthTime);
        int yearBirth = calendar.get(Calendar.YEAR);
        int monthBirth = calendar.get(Calendar.MONTH) + 1;
        int dayBirth = calendar.get(Calendar.DATE);
        int hourBirth = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteBirth = calendar.get(Calendar.MINUTE);
        int secondBirth = calendar.get(Calendar.SECOND);

        // 当前时间：秒 < 出生时间：秒 处理：秒 + 60，分 - 1
        if (secondNow < secondBirth) {
            secondNow = secondNow + 60;
            minuteNow = minuteNow - 1;
        }

        // 当前时间：分 < 出生时间：分 处理：分 + 60，时 - 1
        if (minuteNow < minuteBirth) {
            minuteNow = minuteNow + 60;
            hourNow = hourNow - 1;
        }

        // 当前时间：时 < 出生时间：时 处理：时 + 24，天 - 1
        if (hourNow < hourBirth) {
            hourNow = hourNow + 24;
            dayNow = dayNow - 1;
        }

        // 当前时间：天 < 出生时间：天 处理：天 + 上月天数，月 - 1
        if (dayNow < dayBirth) {
            int dayOfMonth = dayByMonth(yearNow, monthNow);
            dayNow = dayNow + dayOfMonth;
            monthNow = monthNow - 1;
        }

        // 当前时间：月 < 出生时间：月 处理：月 + 12，年 - 1
        if (monthNow < monthBirth) {
            monthNow = monthNow + 12;
            yearNow = yearNow - 1;
        }
        int year = yearNow - yearBirth;
        int month = monthNow - monthBirth;
        int day = dayNow - dayBirth;
        int hour = hourNow - hourBirth;
        int minute = minuteNow - minuteBirth;
        int secound = secondNow - secondBirth;
        return new Age(year, month, day, hour, minute, secound);
    }

    /**
     * 根据日期判断上个月有多少天
     *
     * @param year：年
     * @param month：月
     * @return 天数
     */
    private static int dayByMonth(int year, int month) {

        int four = 4;
        int hundred = 100;
        int fourHundred = 400;

        // 月
        switch (month) {

            // 上月为31天的月份
            case 2:
            case 4:
            case 6:
            case 8:
            case 9:
            case 11:
            case 1:
                return 31;

            // 上月为30天的月份
            case 5:
            case 7:
            case 10:
            case 12:
                return 30;

            // 对于2月份需要判断是否为闰年
            case 3:
                if (year % fourHundred == 0) {
                    return 29;
                }
                if (year % four == 0 && year % hundred != 0) {
                    return 29;
                }
                return 28;
            default:
                return 0;
        }
    }
}
