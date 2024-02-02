package io.ihankun.framework.mongoplus.enums;

/**
 * @author hankun
 * @project mongo-plus
 * @description
 * @date 2023-02-23 11:00
 **/
public enum DefaultPageParamEnum {

    PAGE_NUM(1),

    PAGE_SIZE(10);

    public Integer getNum() {
        return num;
    }

    DefaultPageParamEnum(Integer num) {
        this.num = num;
    }

    private final Integer num;
}
