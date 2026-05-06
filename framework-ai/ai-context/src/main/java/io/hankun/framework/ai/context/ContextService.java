package io.hankun.framework.ai.context;

import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.context.entity.ContextDesc;
import io.hankun.framework.ai.context.model.ModelContextBuilder;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import io.hankun.framework.ai.context.model.UserContextBuilder;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ContextService
 * @createAt: 2025/11/27 09:49
 * @author: hankun
 */
@Component
public class ContextService {

    private final ModelContextManageService modelContextManageService;

    private final ContextManageService contextManageService;

    public ContextService(ModelContextManageService modelContextManageService,
                          ContextManageService contextManageService) {
        this.modelContextManageService = modelContextManageService;
        this.contextManageService = contextManageService;
    }

    public List<ContextDesc> listSystemContexts() {
        Map<String, ModelContextBuilder> modelContextRegisterMap = modelContextManageService.getModelContextBuilderMap();
        List<ContextDesc> contextDescList = new ArrayList<>(modelContextRegisterMap.size());
        for (ModelContextBuilder modelContextBuilder : modelContextRegisterMap.values()) {
            contextDescList.add(new ContextDesc(modelContextBuilder.code(), modelContextBuilder.desc(), "system"));
        }
        return contextDescList;
    }

    public List<ContextDesc> listUserContexts() {
        Map<String, UserContextBuilder> userContextRegisterMap = modelContextManageService.getUserContextBuilderMap();
        List<ContextDesc> contextDescList = new ArrayList<>(userContextRegisterMap.size());
        for (UserContextBuilder userContextBuilder : userContextRegisterMap.values()) {
            contextDescList.add(new ContextDesc(userContextBuilder.code(), userContextBuilder.desc(), "user"));
        }
        return contextDescList;
    }

    public List<ContextDesc> listProgramContexts() {
        List<ContextRegister<?>> contextRegisters = contextManageService.getContextRegisters();
        List<ContextDesc> contextDescList = new ArrayList<>(contextRegisters.size());
        for (ContextRegister<?> contextRegister : contextRegisters) {
            contextDescList.add(new ContextDesc(contextRegister.getKey(), contextRegister.desc(), "program"));
        }
        return contextDescList;
    }

    public Class<? extends IContext> getContextClass(String code) {
        ContextRegister<?> contextRegister = contextManageService.getContextRegister(code);
        if (contextRegister == null) {
            return null;
        }
        return contextRegister.dataType();
    }
}
