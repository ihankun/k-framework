package io.hankun.framework.ai.agent.node.result;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.util.FluxXmlSplitter;
import io.hankun.framework.ai.core.util.FluxUtil;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: NodeXmlReader
 * @createAt: 2025/10/30 15:18
 * @author: hankun
 */
public class NodeXmlReader implements NodeResultReader {

    private final FluxXmlSplitter splitter = new FluxXmlSplitter();

    @Override
    public Flux<DataWithMeta> convert(Flux<String> result) {
        return splitter.filter(FluxUtil.toCharacterFlux(result))
                .map(c -> {
                    return DataWithMeta.ofText(c.toString());
                })
                .concatWith(Flux.just(DataWithMeta.ofMeta(splitter.getMeta())));
    }

    @Override
    public String getRawResult() {
        return splitter.allText();
    }
}
