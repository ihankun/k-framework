package io.hankun.framework.ai.tools.contexts;

import io.hankun.framework.ai.common.context.IContext;

import java.time.LocalDateTime;

/**
 * @description:
 * @className: TimeContext
 * @createAt: 2025/10/20 11:44
 * @author: hankun
 */
public record TimeContext(LocalDateTime startTime) implements IContext {
}
