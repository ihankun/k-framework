package io.ihankun.framework.mongoplus.enums;

/**
 * @Description: 排序枚举
 * @BelongsProject: mongo
 * @BelongsPackage: io.ihankun.framework.mongoplus.enums
 * @Author: hankun
 * @CreateTime: 2023-02-19 13:25
 * @Version: 1.0
 */
public enum OrderEnum {

    ORDER_BY(1),

    ORDER_BY_DESC(-1)

    ;

    private final Integer flag;

    public Integer getFlag() {
        return flag;
    }

    OrderEnum(Integer flag) {
        this.flag = flag;
    }
}
