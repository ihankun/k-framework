package io.ihankun.framework.poi.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 辅助脱敏工具类
 *
 * @author jueyue on 21-2-7.
 */
public class PoiDataDesensitizationUtil {

    private static String SPILT_START_END = "_";
    private static String SPILT_MAX       = ",";
    private static String SPILT_MARK      = "~";

    /**
     * @param rule 规则
     * @param data 数据
     *             数据脱敏规则
     *             规则1: 采用保留头和尾的方式,中间数据加星号
     *             如: 身份证  6_4 则保留 370101********1234
     *             手机号   3_4 则保留 131****1234
     *             规则2: 采用确定隐藏字段的进行隐藏,优先保留头
     *             如: 姓名   1,3 表示最大隐藏3位,最小一位
     *             李 -->  *
     *             李三 --> 李*
     *             张全蛋  --> 张*蛋
     *             李张全蛋 --> 李**蛋
     *             尼古拉斯.李张全蛋 -> 尼古拉***张全蛋
     *             规则3: 特殊符号后保留
     *             如: 邮箱    1~@ 表示只保留第一位和@之后的字段
     *             afterturn@wupaas.com -> a********@wupaas.com
     *             复杂版本请使用接口
     *             {@link io.ihankun.framework.poi.handler.inter.IExcelDataHandler}
     */
    public static String desensitization(String rule, Object data) {
        String value = data.toString();
        if (rule.contains(SPILT_START_END)) {
            String[] arr = rule.split(SPILT_START_END);
            return subStartEndString(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), value);

        }
        if (rule.contains(SPILT_MAX)) {
            String[] arr = rule.split(SPILT_MAX);
            return subMaxString(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), value);

        }
        if (rule.contains(SPILT_MARK)) {
            String[] arr = rule.split(SPILT_MARK);
            return markSpilt(Integer.parseInt(arr[0]), arr[1], value);

        }
        return value;
    }

    /**
     * 特定字符分隔，添加星号
     * @param start
     * @param mark
     * @param value
     * @return
     */
    private static String markSpilt(int start, String mark, String value) {
        if (value == null) {
            return null;
        }
        int end = value.lastIndexOf(mark);
        if (end <= start) {
            return value;
        }
        return StringUtils.left(value, start).concat(StringUtils.leftPad(StringUtils.right(value, value.length() - end), value.length() - start, "*"));
    }

    /**
     * 部分数据截取，优先对称截取
     * @param start
     * @param end
     * @param value
     * @return
     */
    private static String subMaxString(int start, int end, String value) {
        if (value == null) {
            return null;
        }
        if (start > end) {
            throw new IllegalArgumentException("start must less end");
        }
        int len = value.length();
        if (len <= start) {
            return StringUtils.leftPad("", len, "*");
        } else if (len > start && len <= end) {
            if (len == 1) {
                return value;
            }
            if (len == 2) {
                return StringUtils.left(value, 1).concat("*");
            }
            return StringUtils.left(value, 1).concat(StringUtils.leftPad(StringUtils.right(value, 1), StringUtils.length(value) - 1, "*"));
        } else {
            start = (int) Math.ceil((len - end + 0.0D) / 2);
            end = len - start - end;
            end = end == 0 ? 1 : end;
            return StringUtils.left(value, start).concat(StringUtils.leftPad(StringUtils.right(value, end), len - start, "*"));
        }
    }

    /**
     * 收尾截取数据
     * @param start
     * @param end
     * @param value
     * @return
     */
    private static String subStartEndString(int start, int end, String value) {
        if (value == null) {
            return null;
        }
        if (value.length() <= start + end) {
            return value;
        }
        return StringUtils.left(value, start).concat(StringUtils.leftPad(StringUtils.right(value, end), StringUtils.length(value) - start, "*"));
    }
}
