package io.hankun.framework.ai.agent.exceptions;

import lombok.Getter;

/**
 * @description:
 * @className: KAiException
 * @createAt: 2025/11/17 08:58
 * @author: hankun
 */
public class KAiException extends RuntimeException {

    @Getter
    public final boolean clearMem;

    public KAiException(String message, boolean clearMem) {
        super(message);
        this.clearMem = clearMem;
    }

    public KAiException(String message) {
        this(message, false);
    }
}
