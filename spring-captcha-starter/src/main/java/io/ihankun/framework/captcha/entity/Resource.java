package io.ihankun.framework.captcha.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hankun
 *
 * 资源对象
 */
@Data
@NoArgsConstructor
public class Resource {

    /** 类型. */
    private String type;

    /** 数据 */
    public String data;

    /** 标签.*/
    private String tag;

    /** 提示.*/
    private String tip;

    /** 扩展.*/
    private Object extra;

    public Resource(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public Resource(String type, String data, String tag) {
        this.type = type;
        this.data = data;
        this.tag = tag;
    }

    public Resource(String type, String data, String tag, String tip) {
        this.type = type;
        this.data = data;
        this.tag = tag;
        this.tip = tip;
    }
}
