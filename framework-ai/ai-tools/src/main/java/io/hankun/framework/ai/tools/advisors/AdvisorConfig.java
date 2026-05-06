package io.hankun.framework.ai.tools.advisors;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: AdvisorConfig
 * @createAt: 2025/6/5 09:36
 * @author: hankun
 */
public record AdvisorConfig(Map<String, Map<String, Object>> advisorParams) {


    public static class Builder {
        private final Map<String, Map<String, Object>> advisorParams = new HashMap<>();

        public Builder() {
        }

        public Builder withAdvisor(String advisorName, Map<String, Object> params) {
            advisorParams.put(advisorName, params);
            return this;
        }

        public Builder withAdvisor(String advisorName) {
            advisorParams.put(advisorName, new HashMap<>());
            return this;
        }

        public Builder withTraceChatCall() {
            return withAdvisor(TraceChatCallAdvisorRegister.KEY);
        }


        public AdvisorConfig build() {
            return new AdvisorConfig(advisorParams);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AdvisorConfig buildSimple() {
        return AdvisorConfig.builder()
                .build();
    }

    public static AdvisorConfig merge(AdvisorConfig config, AdvisorConfig override) {
        if (override == null) {
            return config;
        }
        if (config == null) {
            return override;
        }
        Map<String, Map<String, Object>> advisorParams = new HashMap<>();
        if (!CollectionUtils.isEmpty(config.advisorParams)) {
            advisorParams.putAll(config.advisorParams);
        }
        if (!CollectionUtils.isEmpty(override.advisorParams)) {
            advisorParams.putAll(override.advisorParams);
        }
        return new AdvisorConfig(advisorParams);
    }
}
