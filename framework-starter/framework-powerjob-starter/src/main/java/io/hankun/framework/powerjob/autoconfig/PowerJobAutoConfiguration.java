package io.hankun.framework.powerjob.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hankun.framework.powerjob.config.PowerJobProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.exception.PowerJobException;
import tech.powerjob.worker.PowerJobSpringWorker;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tech.powerjob.worker.common.constants.StoreStrategy;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: hankun
 */

@Slf4j
@Configuration
@ComponentScan("io.hankun.framework.powerjob")
public class PowerJobAutoConfiguration {

    // 这是最终的 appName，用于创建 Worker 和 Client
    @Value("${spring.application.name}")
    private String finalAppName;

    // 【新增】这是基础的 appName ，用于查找 Nacos 配置
    @Value("${k.job.base-app-name}")
    private String baseAppName;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${powerjob.client.init.retry.times:5}")
    private int retryTimes;

    @Value("${powerjob.client.init.retry.interval-seconds:3}")
    private int retryIntervalSeconds;

    // 【核心修正1】: 注入 powerjob.worker.akka-port 属性。
    // @Value 注解会优先读取 -D 系统参数，然后是 properties 文件，
    // 如果都没有，则使用默认值 27777。这恢复了 PowerJob 的标准行为。
    @Value("${powerjob.worker.akka-port:27777}")
    private int port;

    /**
     * 创建 PowerJobWorkerConfig Bean。
     * 密码将通过 System.setProperty 的方式传递给 Worker 核心。
     */
    @Bean
    @ConditionalOnMissingBean
    public PowerJobWorkerConfig powerJobWorkerConfig(PowerJobProperties powerJobProperties) {
        log.info("[PowerJobAutoConfiguration] 开始为应用 [{}] 构建 PowerJobWorkerConfig...", appName);

        String serverAddress = powerJobProperties.getServerAddress();
        if (!StringUtils.hasText(serverAddress)) {
            throw new IllegalStateException("PowerJob server 地址未配置 (k.job.serverAddress)!");
        }

        Map<String, PowerJobProperties.AppSpecificProps> appsConfig = powerJobProperties.getApps();

        PowerJobProperties.AppSpecificProps appProps;
        if (appsConfig == null || !appsConfig.containsKey(baseAppName)) {
            log.warn("[PowerJobAutoConfiguration] 警告：在 Nacos 配置 'k.job.apps' 中未找到应用 '{}' 的专属配置块。将使用所有默认配置（密码、命名空间等）。", baseAppName);
            // 如果找不到配置，我们就创建一个全新的、包含所有默认值的对象。
            appProps = new PowerJobProperties.AppSpecificProps();
        } else {
            // 如果找到了，就正常使用。
            appProps = appsConfig.get(baseAppName);
        }

        // 【核心修正】: 直接获取密码，它要么是 Nacos 配置的值，要么是 Java 类中的默认值。
        String password = appProps.getPassword();

        // 【核心修正】: 移除异常检查，改为添加一个警告日志，提醒用户正在使用默认密码。
        if ("123456".equals(password)) {
            log.warn("[PowerJobAutoConfiguration] 警告：在 Nacos 中未找到应用 '{}' 的密码配置，正在使用默认密码 '123456'。这在生产环境中是不安全的！", baseAppName);
        }

        // 【修正2 & 核心】将密码设置到系统属性中。
        // PowerJobWorker 的核心初始化逻辑会从系统属性中读取这个密码用于向 Server 认证。
        log.info("[PowerJobAutoConfiguration] 将为 PowerJob Worker 设置系统属性 'powerjob.worker.password'");
        System.setProperty("powerjob.worker.password", password);

        // 填充 PowerJobWorkerConfig 对象 (不包含密码)
        PowerJobWorkerConfig config = new PowerJobWorkerConfig();
        // 【核心修正】在配置 Worker 时，使用最终的、带后缀的 appName！
        config.setAppName(finalAppName);
        config.setServerAddress(Arrays.asList(serverAddress.split(",")));

        // 【核心修正2】: 将我们从环境中读取到的端口号设置到 Config 对象中！
        config.setPort(this.port);
        log.info("[PowerJobAutoConfiguration] PowerJob Worker Akka 端口已配置为: {}", this.port);

        // 设置一些合理的默认值
        config.setStoreStrategy(StoreStrategy.DISK);
        config.setAllowLazyConnectServer(true);
        config.setMaxResultLength(8192);

        log.info("[PowerJobAutoConfiguration] PowerJobWorkerConfig for app [{}] 构建完成。", appName);
        return config;
    }

    /**
     * 创建 PowerJobSpringWorker Bean.
     * 【修正1】移除 initMethod = "init"，因为 PowerJobSpringWorker 实现了 InitializingBean 接口，
     * Spring 会自动调用其 afterPropertiesSet 方法进行初始化。
     */
    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public PowerJobSpringWorker powerJobSpringWorker(PowerJobWorkerConfig config) {
        return new PowerJobSpringWorker(config);
    }


    /**
     * 创建 PowerJobClient Bean.
     * 【已修正】确保在配置缺失时也能使用默认值，不会抛出 NPE。
     */
    @Bean
    @ConditionalOnMissingBean
    public PowerJobClient powerJobClient(PowerJobProperties powerJobProperties) {
        log.info("[PowerJobAutoConfiguration] 开始创建 PowerJobClient...");

        String serverAddress = powerJobProperties.getServerAddress();
        Map<String, PowerJobProperties.AppSpecificProps> appsConfig = powerJobProperties.getApps();
        PowerJobProperties.AppSpecificProps appProps;

        if (appsConfig == null || !appsConfig.containsKey(baseAppName)) {
            log.warn("[PowerJobAutoConfiguration] PowerJobClient: 在 Nacos 中未找到应用 '{}' 的专属配置块，将使用默认密码。", baseAppName);
            // 如果找不到配置，我们就创建一个全新的、包含所有默认值的对象。
            appProps = new PowerJobProperties.AppSpecificProps();
        } else {
            // 如果找到了，就正常使用。
            appProps = appsConfig.get(baseAppName);
        }

        // 现在 appProps 永远不会为 null
        String password = appProps.getPassword();

        // 重试逻辑保持不变
        for (int i = 1; i <= retryTimes; i++) {
            try {
                // 注意：这里创建 Client 时，使用的是 finalAppName
                PowerJobClient client = new PowerJobClient(Arrays.asList(serverAddress.split(",")), finalAppName, password);
                log.info("[PowerJobAutoConfiguration] PowerJobClient for app [{}] on server [{}] 创建成功！(尝试第 {} 次)", finalAppName, serverAddress, i);
                return client;
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("AUTH_FAILED_INVALID_APP")) {
                    log.warn("[PowerJobAutoConfiguration] 创建 PowerJobClient 失败 (尝试第 {}/{})，原因: 应用 '{}' 可能尚未被 Server 缓存。错误: {}", i, retryTimes, appName, e.getMessage());
                    if (i == retryTimes) {
                        log.error("[PowerJobAutoConfiguration] 已达到最大重试次数，创建 PowerJobClient 彻底失败。请检查 Server 状态或网络。", e);
                        throw e;
                    }
                    try {
                        log.info("[PowerJobAutoConfiguration] 将在 {} 秒后进行下一次重试...", retryIntervalSeconds);
                        TimeUnit.SECONDS.sleep(retryIntervalSeconds);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new PowerJobException("PowerJobClient initialization retry was interrupted", ie);
                    }
                } else {
                    log.error("[PowerJobAutoConfiguration] 创建 PowerJobClient 时发生非预期的严重错误，将立即失败。", e);
                    throw e;
                }
            }
        }
        throw new PowerJobException("Failed to initialize PowerJobClient after " + retryTimes + " retries.");
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate powerJobRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}