package io.hankun.framework.ai.context.model;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description:
 * @className: ModelContextManageService
 * @createAt: 2025/10/17 14:43
 * @author: hankun
 */
@Component
public class ModelContextManageService {

    @Getter
    private final Map<String, ModelContextBuilder> modelContextBuilderMap;

    @Getter
    private final Map<String, UserContextBuilder> userContextBuilderMap;

    public ModelContextManageService(List<ModelContextBuilder> modelContextBuilderMap,
                                     List<UserContextBuilder> userContextBuilderMap) {
        this.modelContextBuilderMap = new HashMap<>(modelContextBuilderMap.size());
        this.userContextBuilderMap = new HashMap<>(userContextBuilderMap.size());
        for (ModelContextBuilder modelContextBuilder : modelContextBuilderMap) {
            this.modelContextBuilderMap.put(modelContextBuilder.code(), modelContextBuilder);
        }
        for (UserContextBuilder userContextBuilder : userContextBuilderMap) {
            this.userContextBuilderMap.put(userContextBuilder.code(), userContextBuilder);
        }
    }

    public Collection<String> allCodes() {
        return modelContextBuilderMap.keySet();
    }

    public ModelContextBuilder getModelContextRegister(String code) {
        return modelContextBuilderMap.get(code);
    }

    @NotNull
    public Map<String, Object> buildContext(CurrentId currentId,
                                            ContextAccess contextAccess,
                                            Collection<String> codes, Map<String, String> paramsData) {
        Map<String, Object> contextMap = new HashMap<>(codes.size());
        for (String code : codes) {
            ModelContextBuilder modelContextBuilder = modelContextBuilderMap.get(code);
            Object value = modelContextBuilder.buildContext(currentId, contextAccess);
            contextMap.put(code, Objects.requireNonNullElse(value, ""));
        }
        contextMap.putAll(paramsData);
        return contextMap;
    }

    @NotNull
    public Map<String, Object> buildUserContext(CurrentId currentId,
                                                ContextAccess contextAccess,
                                                Collection<String> codes) {
        Map<String, Object> contextMap = new HashMap<>(codes.size());
        for (String code : codes) {
            UserContextBuilder userContextRegister = userContextBuilderMap.get(code);
            UserContextInfo userContextInfo = userContextRegister.buildUserContext(currentId, contextAccess);
            if (userContextInfo != null) {
                contextMap.put(userContextInfo.key(), userContextInfo.data());
            }
        }
        return contextMap;
    }
}
