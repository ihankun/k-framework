package io.ihankun.framework.common.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hankun
 */
@Data
public class Age implements Serializable {

    private static final long serialVersionUID = -8378143243612310246L;

    /**
     * 年
     */
    private int year;

    /**
     * 月
     */
    private int month;

    /**
     * 日
     */
    private int day;

    /**
     * 时
     */
    private int hour;

    /**
     * 分
     */
    private int minue;

    /**
     * 秒
     */
    private int secound;

    public Age() {

    }

    public Age(int year, int month, int day, int hour, int minue, int secound) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minue = minue;
        this.secound = secound;
    }

    /**
     * 判断是否整岁
     *
     * @return
     */
    public boolean isFullYear() {
        return getMonth() == 0 && getDay() == 0 ? true : false;
    }

    /**
     * 判断是否整月
     *
     * @return
     */
    public boolean isFullMonth() {
        return getYear() == 0 && getDay() == 0 ? true : false;
    }
}
