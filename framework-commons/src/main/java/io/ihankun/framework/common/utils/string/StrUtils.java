package io.ihankun.framework.common.utils.string;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;

/**
 * 字符串工具类
 *
 * @author hankun
 */
public class StrUtils {

    public static String maxLength(CharSequence str, int maxLength) {
        Assert.isTrue(maxLength > 0);
        if (null == str) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str.toString();
        }
        return StrUtil.sub(str, 0, maxLength - 3) + "..."; // -3 的原因，是该方法会补充 ... 恰好
    }

    /**
     * 给定字符串是否以任何一个字符串开始
     * 给定字符串和数组为空都返回 false
     *
     * @param str      给定字符串
     * @param prefixes 需要检测的开始字符串
     * @since 3.0.6
     */
    public static boolean startWithAny(String str, Collection<String> prefixes) {
        if (StrUtil.isEmpty(str) || ArrayUtil.isEmpty(prefixes)) {
            return false;
        }

        for (CharSequence suffix : prefixes) {
            if (StrUtil.startWith(str, suffix, false)) {
                return true;
            }
        }
        return false;
    }

}
