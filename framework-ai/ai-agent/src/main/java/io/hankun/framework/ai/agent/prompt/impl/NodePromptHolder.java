package io.hankun.framework.ai.agent.prompt.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @description:
 * @className: NodePromptHolder
 * @createAt: 2025/7/10 15:32
 * @author: hankun
 */
@Slf4j
public class NodePromptHolder {

    private final ConfigService configService;

    private final ModelContextManageService modelContextManageService;

    @Getter
    private volatile String prompt;

    @Getter
    private volatile List<String> params;

    private void setChatPrompt(String prompt) {
        this.prompt = prompt;
        List<String> newParms = new ArrayList<>();
        this.params = newParms;
        if (StringUtils.hasText(prompt)) {
            Collection<String> allCodes = modelContextManageService.allCodes();
            for (String code : allCodes) {
                if (prompt.contains("{" + code + "}")) {
                    newParms.add(code);
                }
            }
        }
    }

    public NodePromptHolder(ConfigService configService, String configId, String nacosGroup,
                            ModelContextManageService modelContextManageService) {
        this.configService = configService;
        this.modelContextManageService = modelContextManageService;
        addListener(configService, configId, nacosGroup, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                setChatPrompt(configInfo);
            }
        });
        setChatPrompt(getPromptFromNacos(configId, nacosGroup));
    }


    private void addListener(ConfigService configService, String configId, String group, Listener listener) {
        if (ObjectUtils.isEmpty(configId)) {
            return;
        }
        try {
            configService.addListener(configId, group, listener);
        } catch (NacosException e) {
            log.error("获取prompt失败", e);
            throw new RuntimeException(e);
        }
    }


    private String getPromptFromNacos(String configId, String group) {
        try {
            return configService.getConfig(configId, group, 5000);
        } catch (NacosException e) {
            log.error("获取prompt失败", e);
            return "";
        }
    }
}
