package io.hankun.framework.ai.context.store;

import io.hankun.framework.ai.core.entity.CurrentId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

/**
 * @description:
 * @className: ContextStoreType
 * @createAt: 2025/10/17 16:20
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum ContextStoreType {

    ACROSS_AGENT("acrossAgent", "跨agent") {
        @Override
        public String buildId(CurrentId currentId) {
            return "acrossAgent-" + currentId.sessionId();
        }

        @Override
        public Duration defaultDuration() {
            return Duration.ofHours(24);
        }
    },

    IN_AGENT("inAgent", "同agent") {
        @Override
        public String buildId(CurrentId currentId) {
            return "inAgent-" + currentId.sessionId() + ":" + currentId.agentId();
        }

        @Override
        public Duration defaultDuration() {
            return Duration.ofHours(24);
        }
    },

    ;
    private final String type;
    private final String desc;


    public abstract String buildId(CurrentId currentId);

    public abstract Duration defaultDuration();
}
