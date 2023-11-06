package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import com.fasterxml.jackson.databind.util.StdDateFormat;
//import org.apache.commons.lang3.StringUtils;
//
//import java.text.FieldPosition;
//import java.text.ParsePosition;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author hankun
// */
//public class DateConverterConfig extends StdDateFormat {
//
//    private static final List<String> FORMATS = new ArrayList<>(4);
//
//    static {
//        FORMATS.add("yyyy-MM");
//        FORMATS.add("yyyy-MM-dd");
//        FORMATS.add("yyyy-MM-dd HH:mm");
//        FORMATS.add("yyyy-MM-dd HH:mm:ss");
//        FORMATS.add("yyyy-MM-dd HH:mm:ss.SSS");
//    }
//
//    private static final String REGEX_DATE_1 = "^\\d{4}-\\d{1,2}$";
//    private static final String REGEX_DATE_2 = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
//    private static final String REGEX_DATE_3 = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$";
//    private static final String REGEX_DATE_4 = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
//    private static final String REGEX_DATE_5 = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3}$";
//
//    /**
//     * 根据不同的日期格式转换为指定格式的日期
//     *
//     * @param dateStr
//     * @return
//     */
//    @Override
//    public Date parse(String dateStr) {
//        ParsePosition pos = new ParsePosition(0);
//        SimpleDateFormat sdf = null;
//        if (StringUtils.isBlank(dateStr)) {
//            return null;
//        }
//        if (dateStr.matches(REGEX_DATE_1)) {
//            sdf = new SimpleDateFormat(FORMATS.get(0));
//            return sdf.parse(dateStr, pos);
//        } else if (dateStr.matches(REGEX_DATE_2)) {
//            sdf = new SimpleDateFormat(FORMATS.get(1));
//            return sdf.parse(dateStr, pos);
//        } else if (dateStr.matches(REGEX_DATE_3)) {
//            sdf = new SimpleDateFormat(FORMATS.get(2));
//            return sdf.parse(dateStr, pos);
//        } else if (dateStr.matches(REGEX_DATE_4)) {
//            sdf = new SimpleDateFormat(FORMATS.get(3));
//            return sdf.parse(dateStr, pos);
//        } else if (dateStr.matches(REGEX_DATE_5)) {
//            sdf = new SimpleDateFormat(FORMATS.get(4));
//            return sdf.parse(dateStr, pos);
//        }
//        return super.parse(dateStr, pos);
//    }
//
//    @Override
//    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(date, toAppendTo, fieldPosition);
//    }
//}
