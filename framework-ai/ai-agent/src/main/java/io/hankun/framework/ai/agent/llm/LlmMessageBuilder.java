package io.hankun.framework.ai.agent.llm;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @description:
 * @className: LlmMessageBuilder
 * @createAt: 2025/10/29 10:25
 * @author: hankun
 */
public class LlmMessageBuilder {

    public static final String USER_CONTEXT_PREFIX = "上下文信息：";

    public static Message buildPrompt(PromptInfo promptInfo,
                                      Map<String, Object> context, String expandPrompt) {
        if (promptInfo == null) {
            return null;
        }
        if (StringUtils.hasText(promptInfo.prompt())) {
            String prompt = promptInfo.prompt();
            if (!CollectionUtils.isEmpty(promptInfo.params()) || !CollectionUtils.isEmpty(context)) {
                PromptTemplate promptTemplate = new PromptTemplate(prompt);
                for (Map.Entry<String, String> entry : promptInfo.paramsData().entrySet()) {
                    promptTemplate.add(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<String, Object> entry : context.entrySet()) {
                    promptTemplate.add(entry.getKey(), entry.getValue());
                }
                prompt = promptTemplate.render();
            }
            if (StringUtils.hasText(expandPrompt)) {
                return new SystemMessage(prompt + "\n----------------------------------------\n" + expandPrompt);
            }
            return new SystemMessage(prompt);
        }
        return null;
    }

    public static Message buildUser(String user, Map<String, Object> userParams) {
        if (StringUtils.hasText(user)) {
            if (!CollectionUtils.isEmpty(userParams)) {
                user = PromptTemplate.builder().
                        template(user).variables(userParams)
                        .renderer(StTemplateRenderer.builder().build())
                        .build().render();
            }
            return new UserMessage(user);
        }
        return null;
    }

    public static Message buildUserContext(Map<String, Object> userContexts) {
        return new UserMessage(USER_CONTEXT_PREFIX + JSON.toJSONString(userContexts));
    }
}
