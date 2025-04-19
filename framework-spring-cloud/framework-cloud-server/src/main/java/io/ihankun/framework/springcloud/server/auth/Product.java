package io.ihankun.framework.springcloud.server.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum Product {

    TEST("test", "测试")
    ;


    /**
     * 属性值
     */
    private String value;

    private String desc;


    @Override
    public String toString() {
        return this.getValue() + "-" + this.getDesc();
    }
}
