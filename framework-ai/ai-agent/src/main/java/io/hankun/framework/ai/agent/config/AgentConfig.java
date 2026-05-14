package io.hankun.framework.ai.agent.config;

import io.hankun.framework.ai.core.context.IContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: AgentConfigData
 * @createAt: 2025/10/23 16:00
 * @author: hankun
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentConfig {
    private String agentCode;
    private String agentDesc;
    private String version;
    private String loadCode;
    private int maxExecTimeSeconds = 2 * 60;
    private List<Class<? extends IContext>> contexts = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
}
