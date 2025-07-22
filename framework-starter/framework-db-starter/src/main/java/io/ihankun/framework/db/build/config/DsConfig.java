package io.ihankun.framework.db.build.config;

import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "kun.ds")
public class DsConfig {

    public static final String URL_SUFFIX = "useUnicode=true&characterEncoding=utf8&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai";

    /**
     * 数据库地址密码配置，key为${domain}.xxx，value为对应值
     * kun.ds.db.test.com.HIS_MASTER=10.1.1.1:5432
     * HIS_MASTER的地址为10.1.1.1:5432
     * kun.ds.db.test.kunhis.com.comm_app=xxxx
     * comm_app用户的密码为xxxx
     * kun.ds.db.test.kunhis.com.HIS_MASTER.comm_app=xxx
     * HIS_MASTER的comm_app用户的密码为xxx
     */
    private Map<String, String> db = new HashMap<>();

    /**
     * 数据源配置，服务配置于boostrap中的数据源配置，key为数据源别名（ds），value为数据库连接信息
     * 示例：kun.ds.datasource.master=HIS_MASTER@chis.comm_app
     */
    private Map<String, String> datasource = new HashMap<>();

    /**
     * 老版本的数据源druid配置，用于配置兼容
     */
    private Map<String, Map<String, DruidConfig>> druid = new HashMap<>();

    /**
     * 数据源key，key为域名，value为数据源key，用于密码生成
     */
    private Map<String, String> key = new HashMap<>();

    /**
     * 数据源域名重定向，key为原域名，value为重定向后的域名，若为空，则不重定向
     */
    private Map<String, String> reroute = new HashMap<>();

    /**
     * 密码生成类型，key为域名，value为密码生成类型，若为空，则使用默认密码生成类型
     */
    private Map<String, String> passGenerateType = new HashMap<>();

    /**
     * 密码生成模板，key为域名，value为密码生成模板，若为空，则使用默认模板
     */
    private Map<String, String> passGenerateInfo = new HashMap<>();

    /**
     * 默认域名，原版本需要，当前版本下，默认域名允许为空，若不为空，则启动时优先加载默认域名对应的数据源
     */
    @Value("${kun.ds.hospital.default:}")
    private String defaultDomain;

    /**
     * 是否启用数据源域名动态重定向
     */
    @Value("${kun.ds.db.domain.reroute.enable:true}")
    private Boolean rerouteEnable;

    /**
     * 是否将环境key视为空
     */
    @Value("${kun.ds.disable.env.key:false}")
    private Boolean disableEnvKey;

    /**
     * 数据库连接后缀，如：useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
     */
    @Value("${kun.datasource.url.suffix:}")
    private String urlSuffix;

    /**
     * 当前服务的名称
     */
    @Value("${spring.application.name}")
    private String currentService;

    @Value("${kun.datasource.config.properties:}")
    private Boolean isProperties;

    /**
     * 是否使用旧版本加载代码，若为true，将使用旧版本的配置，否则使用新版本的代码，修改后需要重启服务
     */
    @Value("${kun.ds.switch.to.old:false}")
    private Boolean switchToOld;

    @Value("${kun.ds.dynamic.seata.disable:}")
    private String seataDisableList;

    /**
     * 是否延迟加载数据源（若为true，只有服务访问时才会加载对应数据源）
     */
    private Boolean lazyLoad = false;

    /**
     * 是否加载旧配置，若为true，将支持老版本的数据库配置格式，会和bootstrap版本的配置进行合并
     * 老配置优先级低于新配置
     * 老配置方式无法触发数据源创建事件（注意，该问题在原版本也存在）
     */
    private Boolean loadOldConfig;

    /**
     * 在非延时加载的前提下，是否在服务启动时创建数据源失败时停止服务
     */
    private Boolean stopWhenInitFailed = false;

}
