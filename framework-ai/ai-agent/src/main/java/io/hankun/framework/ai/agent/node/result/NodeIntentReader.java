package io.hankun.framework.ai.agent.node.result;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.sub.intent.IntentReader;
import lombok.Getter;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: NodeIntentReader
 * @createAt: 2025/10/30 15:20
 * @author: hankun
 */
public class NodeIntentReader implements NodeResultReader {

    @Getter
    private final IntentReader intentReader = new IntentReader("、");

    @Override
    public Flux<DataWithMeta> convert(Flux<String> result) {
        intentReader.read(result).then().block();
        return Flux.just(DataWithMeta.ofMeta(intentReader.getParams()));
    }

    @Override
    public String getRawResult() {
        return intentReader.getRawResult();
    }
}
