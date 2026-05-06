package io.hankun.framework.ai.tools.advisors;

import io.hankun.framework.ai.tools.trace.aspect.TraceChatCallAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @className: TraceChatCallAdvisorRegister
 * @createAt: 2025/10/20 10:38
 * @author: hankun
 */
@Component
public class TraceChatCallAdvisorRegister implements KAdvisorRegister {

    public static final String KEY = "traceChatCall";

    @Override
    public String name() {
        return KEY;
    }

    @Override
    public Advisor create(Map<String, Object> params) {
        return new TraceChatCallAdvisor();
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
