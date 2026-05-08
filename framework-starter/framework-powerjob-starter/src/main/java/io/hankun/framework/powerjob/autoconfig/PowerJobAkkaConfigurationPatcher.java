package io.hankun.framework.powerjob.autoconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: PowerJob Akka 配置动态补丁
 * 在 Spring 启动早期，动态探测本机可被 Server 访问的 IP，
 * 并将其设置为 Akka 对外广播的地址，解决多网卡和 NAT 环境下的连接问题。
 * @fileName: PowerJobAkkaConfigurationPatcher.java
 * @author:hankun
 */

@Slf4j
public class PowerJobAkkaConfigurationPatcher implements EnvironmentPostProcessor {

    // Akka 的广播地址配置 Key
    private static final String AKKA_CANONICAL_HOSTNAME_KEY = "akka.remote.artery.canonical.hostname";

    // 修改点 1: 我们现在依赖 Nacos 的地址，而不是 PowerJob 的地址
    // 这是在 bootstrap 阶段一定存在的配置
    private static final String NACOS_SERVER_ADDRESS_KEY = "spring.cloud.nacos.config.server-addr";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        // 检查是否已经手动设置了 Akka 的 hostname，如果已设置，则尊重手动配置
        if (StringUtils.hasText(System.getProperty(AKKA_CANONICAL_HOSTNAME_KEY)) ||
                StringUtils.hasText(environment.getProperty(AKKA_CANONICAL_HOSTNAME_KEY))) {
            log.info("[PowerJobAkkaPatcher] Akka hostname 已被手动配置，跳过动态探测。");
            return;
        }

        // 修改点 2: 从环境中获取 Nacos Server 的地址
        String nacosServerAddress = environment.getProperty(NACOS_SERVER_ADDRESS_KEY);
        if (!StringUtils.hasText(nacosServerAddress)) {
            log.warn("[PowerJobAkkaPatcher] 未在环境中找到 '{}' 配置，无法进行动态 IP 探测。请确保 Nacos 配置在 bootstrap 文件中。", NACOS_SERVER_ADDRESS_KEY);
            return;
        }

        // 解析出 Nacos Server 的 IP 和端口 (取第一个地址即可)
        String[] parts = nacosServerAddress.split(",")[0].split(":");
        if (parts.length != 2) {
            log.error("[PowerJobAkkaPatcher] '{}' 配置格式不正确: {}", NACOS_SERVER_ADDRESS_KEY, nacosServerAddress);
            return;
        }
        String serverHost = parts[0].trim();
        int serverPort;
        try {
            serverPort = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            log.error("[PowerJobAkkaPatcher] '{}' 中的端口号格式不正确: {}", NACOS_SERVER_ADDRESS_KEY, nacosServerAddress);
            return;
        }


        try {
            // 【核心逻辑】尝试与 Nacos Server 建立一个临时的 Socket 连接，
            // 并从这个 Socket 中获取本地绑定的 IP 地址。
            log.info("[PowerJobAkkaPatcher] 正在通过连接 Nacos Server ({}:{}) 来探测本机可用 IP...", serverHost, serverPort);

            try (Socket socket = new Socket(serverHost, serverPort)) {
                InetAddress localAddress = socket.getLocalAddress();
                String reachableIp = localAddress.getHostAddress();

                log.info("[PowerJobAkkaPatcher] 探测成功！本机 IP [{}] 可用于连接 Nacos。准备将其设置为 Akka 的广播地址。", reachableIp);

                // 将探测到的 IP 作为系统属性设置，Akka 会优先读取它
                System.setProperty(AKKA_CANONICAL_HOSTNAME_KEY, reachableIp);

                // (可选但推荐) 也可以将其添加到 Spring 的环境中，以备后用
                Map<String, Object> props = new HashMap<>();
                props.put(AKKA_CANONICAL_HOSTNAME_KEY, reachableIp);
                MutablePropertySources propertySources = environment.getPropertySources();
                propertySources.addFirst(new MapPropertySource("powerjob-dynamic-ip", props));
            }
        } catch (Exception e) {
            log.error("[PowerJobAkkaPatcher] 动态探测本机可达 IP 失败！Worker 可能无法正常注册。请考虑手动在 JVM 参数中设置 '-D{}'。错误: {}",
                    AKKA_CANONICAL_HOSTNAME_KEY, e.getMessage());
        }
    }
}