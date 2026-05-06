package io.hankun.framework.ai.agent.graph;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: KeyType
 * @createAt: 2025/7/22 09:07
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum KeyType {

    APPEND("append", "追加") {
        @Override
        public KeyStrategy buildStrategy() {
            return new AppendStrategy();
        }
    },

    REPLACE("replace", "替换") {
        @Override
        public KeyStrategy buildStrategy() {
            return new ReplaceStrategy();
        }
    },

    ;
    private final String key;

    private final String desc;

    public abstract KeyStrategy buildStrategy();


    public static KeyType getByKey(String key) {
        return switch (key) {
            case "append" -> APPEND;
            case "replace" -> REPLACE;
            case null, default -> null;
        };
    }

    public static <T extends KeyStrategy> KeyType getByKeyStrategy(T keyStrategy) {
        return switch (keyStrategy) {
            case AppendStrategy appendStrategy -> APPEND;
            case ReplaceStrategy replaceStrategy -> REPLACE;
            default -> null;
        };
    }
}
