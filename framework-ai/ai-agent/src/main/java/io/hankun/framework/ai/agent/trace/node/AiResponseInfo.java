package io.hankun.framework.ai.agent.trace.node;

/**
 * @description:
 * @className: AiResponseInfo
 * @createAt: 2025/7/18 14:17
 * @author: hankun
 */
public record AiResponseInfo(int inputToken, int outputToken, int ttft, float tpot) {

}
