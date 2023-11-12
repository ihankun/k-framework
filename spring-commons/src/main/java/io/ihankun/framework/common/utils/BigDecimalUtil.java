package io.ihankun.framework.common.utils;

import java.math.BigDecimal;

/**
 * @author hankun
 */
public class BigDecimalUtil {

    /**
     * 默认2位小数
     */
    private static final int DEFAULT_SCALE = 2;

    /**
     * BigDecimal 相加
     *
     * @param amount
     * @param addAmout
     * @return
     */
    public static final BigDecimal add(BigDecimal amount, BigDecimal addAmout) {
        BigDecimal retrunBigDecimal = BigDecimal.ZERO;
        if (amount != null && addAmout != null) {
            retrunBigDecimal = amount.add(addAmout);
        } else if (amount != null) {
            retrunBigDecimal = amount;
        } else {
            retrunBigDecimal = addAmout;
        }
        return round(retrunBigDecimal);
    }

    /**
     * BigDecimal 相减
     *
     * @param amout
     * @param subtractAmout
     * @return
     */
    public static final BigDecimal subtract(BigDecimal amout, BigDecimal subtractAmout) {
        BigDecimal retrunBigDecimal = BigDecimal.ZERO;
        if (amout != null && subtractAmout != null) {
            retrunBigDecimal = amout.subtract(subtractAmout);
        } else if (amout != null) {
            retrunBigDecimal = amout;
        } else {
            retrunBigDecimal = subtractAmout.negate();
        }
        return round(retrunBigDecimal);
    }


    /**
     * BigDecimal 相乘
     *
     * @param amount
     * @param multiplyAmout
     * @return
     */
    public static final BigDecimal multiply(BigDecimal amount, BigDecimal multiplyAmout) {
        BigDecimal retrunBigDecimal = BigDecimal.ZERO;
        if (amount != null && multiplyAmout != null) {
            retrunBigDecimal = amount.multiply(multiplyAmout).setScale(DEFAULT_SCALE, BigDecimal.ROUND_CEILING);
        }
        return round(retrunBigDecimal);
    }

    /**
     * BigDecimal 相除
     *
     * @param amount
     * @param devideAmout
     * @return
     */
    public static final BigDecimal divide(BigDecimal amount, BigDecimal devideAmout) {
        BigDecimal retrunBigDecimal = BigDecimal.ZERO;
        if (amount != null && devideAmout != null && devideAmout.compareTo(BigDecimal.ZERO) != 0) {
            retrunBigDecimal = amount.divide(devideAmout, DEFAULT_SCALE, BigDecimal.ROUND_CEILING);
        }
        return round(retrunBigDecimal);
    }

    /**
     * 四舍五入保留2位小数
     *
     * @param src 原数据
     * @return 四舍五入保留2位小数后的数
     */
    private static final BigDecimal round(BigDecimal src) {
        return src.divide(src, DEFAULT_SCALE, BigDecimal.ROUND_CEILING);
    }

    /**
     * 获取负值
     *
     * @param quantity
     * @return
     */
    public static final BigDecimal getNegate(BigDecimal quantity) {
        if (quantity != null) {
            return quantity.negate();
        }
        return BigDecimal.ZERO;
    }
}
