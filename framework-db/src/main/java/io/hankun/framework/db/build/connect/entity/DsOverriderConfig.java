package io.hankun.framework.db.build.connect.entity;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class DsOverriderConfig {
    /**
     * 修改的目标，ds、db、user、schema、protocol、parameter、driver
     */
    private String type;
    /**
     * 分组，可设置多个，通过 , 分割
     * 全局级：无意义
     * 域名级：域名
     * 服务级：服务名
     * 服务域名级：服务名@域名
     */
    private String group;
    /**
     * 待修改的配置的内容（正则匹配）
     */
    private String source;
    /**
     * 修改后的配置的内容（正则替换）
     */
    private String target;
    /**
     * 是否在连接成功后修改配置，默认为false
     */
    private Boolean afterConnectSuc;
}
