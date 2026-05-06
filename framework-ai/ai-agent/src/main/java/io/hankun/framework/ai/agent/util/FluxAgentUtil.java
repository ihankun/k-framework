package io.hankun.framework.ai.agent.util;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.function.Function;

/**
 * @description:
 * @className: FluxAgentUtil
 * @createAt: 2025/12/22 11:06
 * @author: hankun
 */
public class FluxAgentUtil {
    public static <T> Flux<ServerSentEvent<T>> convert(Flux<T> stream) {
        return stream.map(new Function<T, ServerSentEvent<T>>() {
            @Override
            public ServerSentEvent<T> apply(T data) {
                return ServerSentEvent.<T>builder()
                        .data(data)
                        .id(String.valueOf(System.currentTimeMillis()))
                        .build();
            }
        });
    }
}
