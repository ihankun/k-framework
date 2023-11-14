package io.ihankun.framework.common.id;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hankun
 */
@Slf4j
public class BusinessCodeGenerator {

    private static final String SPLIT = "_";

    public static class BusinessCodeGeneratorHolder {
        public static final BusinessCodeGenerator HOLDER = new BusinessCodeGenerator();
    }

    public static BusinessCodeGenerator ins() {
        return BusinessCodeGeneratorHolder.HOLDER;
    }


    public String generator(String prefix) {
        Long generator = IdGenerator.ins().generator();
        return prefix + SPLIT + generator;
    }
}
