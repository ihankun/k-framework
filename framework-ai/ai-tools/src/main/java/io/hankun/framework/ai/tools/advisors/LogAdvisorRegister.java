package io.hankun.framework.ai.tools.advisors;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @className: LogAdvisorRegister
 * @createAt: 2025/10/16 15:58
 * @author: hankun
 */
@Component
public class LogAdvisorRegister implements KAdvisorRegister {

    public static final String KEY = "logAdvisor";

    @Override
    public String name() {
        return KEY;
    }

    @Override
    public Advisor create(Map<String, Object> params) {
        return SimpleLoggerAdvisor.builder().build();
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
