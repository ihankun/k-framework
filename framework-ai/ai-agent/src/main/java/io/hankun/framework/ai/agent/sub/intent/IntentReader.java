package io.hankun.framework.ai.agent.sub.intent;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.core.util.FluxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @description:
 * @className: IntentReader
 * @createAt: 2025/10/9 10:37
 * @author: hankun
 */
@Slf4j
public class IntentReader {

    private final MultiValueMap<String, Consumer<Character>> consumers = new LinkedMultiValueMap<>();

    private final Map<String, StringBuilder> resultMap = new HashMap<>();

    private final String splitMark;

    private final Splitter splitter;

    public IntentReader(String splitMark) {
        this.splitMark = splitMark;
        this.splitter = new Splitter();
    }

    public IntentReader() {
        this("");
    }

    public String getRawResult() {
        return splitter.cache.toString();
    }

    public void setConsumer(String key, Consumer<Character> consumer) {
        consumers.add(key, consumer);
    }

    public String getResult(String key) {
        return resultMap.get(key).toString();
    }

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>(resultMap.size());
        for (Map.Entry<String, StringBuilder> entry : resultMap.entrySet()) {
            String value = entry.getValue().toString();
            if (JSON.isValid(value)) {
                params.put(entry.getKey(), JSON.parse(value));
            } else {
                params.put(entry.getKey(), value);
            }
        }
        return params;
    }

    public Flux<Character> read(Flux<String> flux) {
        Flux<Character> stream = FluxUtil.toCharacterFlux(flux)
                .concatMap(c -> {
                    return Mono.fromCallable(() -> {
                        splitter.accept(c);
                        return c;
                    });
                });
        stream = stream
                .doFinally(new Consumer<SignalType>() {
                    @Override
                    public void accept(SignalType signalType) {
                        splitter.onComplete();
                    }
                });
        return stream;
    }


    private class Splitter implements Consumer<Character> {
        private final StringBuilder cache = new StringBuilder();
        private int start = 0;
        private int end = -1;
        private StringBuilder resultCache = new StringBuilder();
        private String currentKey;

        @Override
        public void accept(Character character) {
            cache.append(character);
            if (character.equals('[')) {
                reset();
            } else if (character.equals(']') && end == -1) {
                end = cache.length();
                String key = cache.substring(start, end);
                key = key.substring(1, key.length() - 1);
                StringBuilder result = resultMap.get(key);
                boolean repeat = false;
                if (result == null) {
                    result = new StringBuilder();
                    resultMap.put(key, result);
                } else {
                    repeat = true;
                }
                resultCache = result;
                currentKey = key;
                if (repeat) {
                    if (splitMark != null) {
                        for (char c : splitMark.toCharArray()) {
                            consumeData(c);
                        }
                    }
                }
            } else if (end != -1) {
                start = cache.length();
                consumeData(character);
            }
        }

        private void consumeData(Character character) {
            if (resultCache == null) {
                return;
            }
            resultCache.append(character);
            List<Consumer<Character>> consumerList = consumers.get(currentKey);
            if (!CollectionUtils.isEmpty(consumerList)) {
                for (Consumer<Character> consumer : consumerList) {
                    consumer.accept(character);
                }
            }
        }


        public void onComplete() {
            if (start < cache.length() && end == -1) {
                for (Character c : cache.substring(start).toCharArray()) {
                    consumeData(c);
                }
            }
            log.info("解释数据完成");
        }

        private void reset() {
            start = cache.length() - 1;
            end = -1;
            resultCache = null;
            currentKey = null;
        }
    }
}
