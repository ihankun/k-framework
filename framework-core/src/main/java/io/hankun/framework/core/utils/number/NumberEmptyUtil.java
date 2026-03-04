package io.hankun.framework.core.utils.number;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 类名: NumberEmptyUtil
 *
 * @author hankun
 */
public class NumberEmptyUtil {

    public static Boolean isLongEmpty(Long obj) {
        if(ObjectUtils.isEmpty(obj)) {
            return true;
        }
        return obj == -1;
    }
    public static Boolean isIntEmpty(Integer obj) {
        if(ObjectUtils.isEmpty(obj)) {
            return true;
        }
        return obj == -1;
    }
}
