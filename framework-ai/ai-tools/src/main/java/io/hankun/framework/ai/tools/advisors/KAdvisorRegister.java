package io.hankun.framework.ai.tools.advisors;


import org.springframework.ai.chat.client.advisor.api.Advisor;

import java.util.Map;

/**
 * @description:
 * @className: KAdvisorRegister
 * @createAt: 2025/10/16 15:38
 * @author: hankun
 */
public interface KAdvisorRegister {

    String name();

    Advisor create(Map<String, Object> params);

    default boolean isDefault() {
        return false;
    }
}
