package io.hankun.framework.powerjob.autoconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hankun.framework.powerjob.config.PowerJobProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: PowerJob 自动化初始化器 (最终版)
 * 在应用启动时，自动完成登录、创建命名空间、创建应用三大步骤。
 * @fileName: NamespaceInitializer.java
 * @author: hankun
 */

@Service("powerJobInitializer")
@Slf4j
public class PowerJobInitializer {

    // 这是最终的 appName ，用于向 PowerJob Server 注册应用
    @Value("${spring.application.name}")
    private String finalAppName;

    // 【新增】这是基础的 appName ，用于查找 Nacos 配置
    @Value("${k.job.base-app-name}")
    private String baseAppName;

    // 唯一需要从环境中直接获取的，就是当前应用的名称
    @Value("${spring.application.name}")
    private String appName;

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private PowerJobProperties powerJobProperties; // 注入统一的配置类

    private static final String LOGIN_TYPE_POWERJOB = "PWJB";
    private static final String JWT_HEADER_NAME = "PowerJwt";

    @PostConstruct
    public void initialize() {
        // 1. 从 PowerJobProperties 获取所有需要的配置
        String serverAddress = powerJobProperties.getServerAddress();
        String opsUsername = powerJobProperties.getOpsUsername();
        String opsPassword = powerJobProperties.getOpsPassword();

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

        String namespaceCode = appProps.getNamespace();
        String appPassword = appProps.getPassword();

        // 2. 校验核心配置是否存在
        if (!StringUtils.hasText(serverAddress) || !StringUtils.hasText(opsUsername) || !StringUtils.hasText(opsPassword)) {
            log.warn("[PowerJobInitializer] 未配置 'k.job' 下的 serverAddress, opsUsername 或 opsPassword，跳过自动初始化。");
            return;
        }

        try {
            // 步骤 1: 登录并获取 JWT Token
            String jwtToken = loginAndGetJwtToken(serverAddress, opsUsername, opsPassword);
            if (jwtToken == null) {
                log.error("[PowerJobInitializer] 登录 PowerJob Server 失败，初始化中断。");
                return;
            }
            log.info("[PowerJobInitializer] 登录成功，已获取认证 Token。");

            // 步骤 2: 确保命名空间存在
            boolean namespaceReady = ensureNamespaceExists(jwtToken, serverAddress, namespaceCode);
            if (!namespaceReady) {
                log.error("[PowerJobInitializer] 命名空间 [{}] 初始化失败，应用创建中断。", namespaceCode);
                return;
            }

            // 步骤 3: 确保应用存在
            // 在创建应用时，使用 finalAppName！
            ensureAppExists(jwtToken, serverAddress, namespaceCode, appPassword);

        } catch (Exception e) {
            log.error("[PowerJobInitializer] 执行自动化初始化时发生未知异常。", e);
        }
    }

    private String loginAndGetJwtToken(String serverAddress, String opsUsername, String opsPassword) throws JsonProcessingException {
        String loginUrl = "http://" + serverAddress + "/auth/thirdPartyLoginDirect";

        Map<String, String> originParams = new HashMap<>();
        originParams.put("username", opsUsername);
        originParams.put("password", opsPassword);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("loginType", LOGIN_TYPE_POWERJOB);
        requestBody.put("originParams", objectMapper.writeValueAsString(originParams));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, new HttpHeaders());

        log.info("[PowerJobInitializer] (Phase 1/3) 尝试以用户 '{}' 登录...", opsUsername);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("jwtToken") != null) {
                    String token = (String) data.get("jwtToken");
                    log.info("[PowerJobInitializer] 成功从登录响应体中获取 JWT Token。");
                    return token;
                }
            }
            log.error("[PowerJobInitializer] 登录业务失败或响应体中不包含 jwtToken！Server 响应: {}", response);
            return null;
        } catch (Exception e) {
            log.error("[PowerJobInitializer] 登录请求失败！", e);
            return null;
        }
    }

    private boolean ensureNamespaceExists(String jwtToken, String serverAddress, String namespaceCode) {
        String apiUrl = "http://" + serverAddress + "/namespace/save";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_HEADER_NAME, jwtToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", namespaceCode);
        requestBody.put("code", namespaceCode);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("[PowerJobInitializer] (Phase 2/3) 检查并创建命名空间 [{}]...", namespaceCode);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                log.info("[PowerJobInitializer] 命名空间 [{}] 创建成功或已就绪。", namespaceCode);
                return true;
            } else {
                String message = response != null ? (String) response.get("message") : "无响应消息";
                if (message.contains("already exists") || message.contains("已存在")) {
                    log.info("[PowerJobInitializer] 命名空间 [{}] 已存在，无需创建。", namespaceCode);
                    return true;
                }
                log.error("[PowerJobInitializer] 命名空间 [{}] 创建失败！Server 响应: {}", namespaceCode, message);
                return false;
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("already exists") || responseBody.contains("已存在")) {
                log.info("[PowerJobInitializer] 命名空间 [{}] 已存在 (HTTP {} 错误)，无需创建。", namespaceCode, e.getStatusCode());
                return true;
            } else {
                log.error("[PowerJobInitializer] 命名空间操作失败！Status: {}, Response: {}", namespaceCode, e.getStatusCode(), responseBody, e);
                return false;
            }
        }
    }

    private void ensureAppExists(String jwtToken, String serverAddress, String namespaceCode, String appPassword) {
        String apiUrl = "http://" + serverAddress + "/appInfo/save";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_HEADER_NAME, jwtToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appName", finalAppName);
        requestBody.put("password", appPassword);
        requestBody.put("namespaceCode", namespaceCode);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("[PowerJobInitializer] (Phase 3/3) 检查并创建应用 [{}]...", appName);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                log.info("[PowerJobInitializer] 应用 [{}] 在命名空间 [{}] 下创建成功或已就绪。", appName, namespaceCode);
            } else {
                String message = response != null ? (String) response.get("message") : "无响应消息";
                if (message.contains("already exists") || message.contains("已存在")) {
                    log.info("[PowerJobInitializer] 应用 [{}] 已存在，无需创建。", appName);
                } else {
                    log.error("[PowerJobInitializer] 应用 [{}] 创建失败！Server 响应: {}", appName, message);
                }
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("already exists") || responseBody.contains("已存在")) {
                log.info("[PowerJobInitializer] 应用 [{}] 已存在 (HTTP {} 错误)，无需创建。", appName, e.getStatusCode());
            } else {
                log.error("[PowerJobInitializer] 应用操作失败！Status: {}, Response: {}", appName, e.getStatusCode(), responseBody, e);
            }
        }
    }
}