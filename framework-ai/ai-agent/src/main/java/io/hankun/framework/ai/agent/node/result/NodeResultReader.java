package io.hankun.framework.ai.agent.node.result;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: NodeResultReader
 * @createAt: 2025/10/30 15:16
 * @author: hankun
 */
public interface NodeResultReader {

    Flux<DataWithMeta> convert(Flux<String> result);

    String getRawResult();
}
