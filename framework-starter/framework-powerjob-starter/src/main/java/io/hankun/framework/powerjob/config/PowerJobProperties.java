package io.hankun.framework.powerjob.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @fileName: PowerJobProperties.java
 * @author: hankun
 */


@Data
@Component
@ConfigurationProperties(prefix = "k.job")
public class PowerJobProperties {

    /**
     * PowerJob Server 地址，多个地址用逗号分隔.
     * 这是所有应用共享的全局配置。
     */
    private String serverAddress;

    /**
     * PowerJob 自动化运维（初始化）的 Server 管理员账户.
     * 这是全局配置，不应暴露给业务研发。
     */
    private String opsUsername;

    /**
     * PowerJob 自动化运维（初始化）的 Server 管理员密码.
     * 这是全局配置，不应暴露给业务研发。
     */
    private String opsPassword;

    /**
     * 用于接收所有应用的专属配置
     * Spring 会自动将 k.job.apps 下的所有配置项，
     * 按 appName 作为 Key，绑定到这个 Map 中。
     */
    private Map<String, AppSpecificProps> apps;

    /**
     * 内部类，用于表示每个应用的专属属性
     */
    @Data
    public static class AppSpecificProps {

        /**
         * PowerJob 应用密码
         */
        private String password = "123456";

        /**
         * PowerJob 命名空间
         */
        private String namespace = "default_namespace";
    }
}