package io.hankun.framework.ai.agent.node.result;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: TextResultReader
 * @createAt: 2025/12/9 10:11
 * @author: hankun
 */
public class TextResultReader implements NodeResultReader {

    private final StringBuilder allText = new StringBuilder();

    @Override
    public Flux<DataWithMeta> convert(Flux<String> result) {
        return result.map(text -> {
            allText.append(text);
            return DataWithMeta.ofText(text);
        });
    }

    @Override
    public String getRawResult() {
        return allText.toString();
    }
}
