package io.hankun.framework.ai.context.entity;

import io.hankun.framework.ai.context.register.NoBuildContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: InputParamsContextRegister
 * @createAt: 2025/11/5 10:12
 * @author: hankun
 */
@Component
public class InputParamsContextRegister implements NoBuildContextRegister<InputParams> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE;
    }

    @Override
    public String desc() {
        return "输入参数";
    }

    @NotNull
    @Override
    public Class<InputParams> dataType() {
        return InputParams.class;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
