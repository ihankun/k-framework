package io.ihankun.framework.core.id;

import cn.hutool.core.util.RandomUtil;
import io.ihankun.framework.core.utils.date.DateUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hankun
 */
@Slf4j
public class NumberCodeGenerator {

    private static final String SPLIT = "";

    public static class NumberCodeGeneratorHolder {
        public static final NumberCodeGenerator HOLDER = new NumberCodeGenerator();
    }

    public static NumberCodeGenerator ins() {
        return NumberCodeGeneratorHolder.HOLDER;
    }


    /**
     * 生成单号，分布式场景下需要主动判重
     */
    public String generator(String prefix) {
        StringBuilder builder = new StringBuilder(prefix);
        String dateStr = DateUtils.getDateStr(DateUtils.getNowDate(), "yyyyMMddHHmmssSSS");
        String random = RandomUtil.randomNumbers(6);
        builder.append(SPLIT).append(dateStr).append(SPLIT).append(random);
        return builder.toString();
    }
}
