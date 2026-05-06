package io.hankun.framework.ai.agent.audio.client;

import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: KAudioApi
 * @createAt: 2025/12/17 13:50
 * @author: hankun
 */
public interface KAudioApi<T, R> {

    String getCode();

    Flux<R> process(Flux<T> audioFlux);

    void sendData(String text);

    void close();
}
