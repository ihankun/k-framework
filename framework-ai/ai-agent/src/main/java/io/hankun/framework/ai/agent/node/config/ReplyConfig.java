package io.hankun.framework.ai.agent.node.config;

/**
 * @description:
 * @className: ReplyConfig
 * @createAt: 2025/11/18 15:29
 * @author: hankun
 */
public record ReplyConfig(boolean replyOutput) {

    public static ReplyConfig def() {
        return new ReplyConfig(true);
    }
}
