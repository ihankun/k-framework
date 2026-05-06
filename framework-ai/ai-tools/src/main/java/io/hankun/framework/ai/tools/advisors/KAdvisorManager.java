package io.hankun.framework.ai.tools.advisors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KAdvisorManager
 * @createAt: 2025/10/16 15:37
 * @author: hankun
 */
@Component
public class KAdvisorManager {

    private final Map<String, KAdvisorRegister> registerMap = new HashMap<>();

    private final List<KAdvisorRegister> defaultAdvisorRegisters = new ArrayList<>();

    public KAdvisorManager(List<KAdvisorRegister> kAdvisorRegisters) {
        for (KAdvisorRegister kAdvisorRegister : kAdvisorRegisters) {
            registerMap.put(kAdvisorRegister.name(), kAdvisorRegister);
            if (kAdvisorRegister.isDefault()) {
                defaultAdvisorRegisters.add(kAdvisorRegister);
            }
        }
    }

    public void withDefAdvisor(ChatClient.Builder builder, AdvisorConfig config) {
        for (KAdvisorRegister register : defaultAdvisorRegisters) {
            Advisor advisor = register.create(null);
            if (advisor != null) {
                builder.defaultAdvisors(advisor);
            }
        }
        if (config == null) {
            return;
        }
        for (Map.Entry<String, Map<String, Object>> entry : config.advisorParams().entrySet()) {
            String advisorName = entry.getKey();
            Map<String, Object> params = entry.getValue();
            KAdvisorRegister register = registerMap.get(advisorName);
            if (register != null) {
                Advisor advisor = register.create(params);
                if (advisor != null) {
                    builder.defaultAdvisors(advisor);
                }
            }
        }
    }

    public ChatClient.ChatClientRequestSpec withAdvisor(ChatClient.ChatClientRequestSpec requestSpec, AdvisorConfig config) {
        if (config == null) {
            return requestSpec;
        }
        for (Map.Entry<String, Map<String, Object>> entry : config.advisorParams().entrySet()) {
            String advisorName = entry.getKey();
            Map<String, Object> params = entry.getValue();
            KAdvisorRegister register = registerMap.get(advisorName);
            if (register != null) {
                Advisor advisor = register.create(params);
                if (advisor != null) {
                    requestSpec = requestSpec.advisors(advisor);
                }
            }
        }
        return requestSpec;
    }
}

