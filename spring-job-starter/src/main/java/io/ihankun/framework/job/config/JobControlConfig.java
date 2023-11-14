package io.ihankun.framework.job.config;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Component
public class JobControlConfig {

    private static final String DATA_ID = "kun-job-control.yml";
    private static final String GROUP = "DEFAULT_GROUP";
    private static final long TIME_OUT_MS = 5000;

    @Value("${spring.application.name}")
    private String applicationName;
    @Resource
    private NacosConfigManager nacosConfigManager;

    public boolean isControl(String className) {
        ConfigService configService = nacosConfigManager.getConfigService();

        try {
            String config = configService.getConfig(DATA_ID, GROUP, TIME_OUT_MS);
            if (StringUtils.isBlank(config)) {
                return true;
            }

            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> yamlMap = objectMapper.readValue(config, Map.class);

            JobControlConfigData.Config databaseConfig = objectMapper.convertValue(yamlMap.get("kun"), JobControlConfigData.Config.class);
            JobControlConfigData.ControlConfig control = databaseConfig.getJob().getControl();
            if (! control.isOpen()) {
                return true;
            }

            Map<String, List<String>> scanners = control.getJobs();
            List<String> jobs = scanners.get(applicationName);
            if (CollUtil.isEmpty(jobs)) {
                return true;
            }

            return jobs.contains(className);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
