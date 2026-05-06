package io.hankun.framework.ai.agent.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @description:
 * @className: FluxXmlSplitter
 * @createAt: 2025/7/25 11:19
 * @author: hankun
 */
@Slf4j
public class FluxXmlSplitter {

    private final Splitter splitter;

    @Getter
    private final Map<String, Object> meta = new HashMap<>(0);

    public FluxXmlSplitter() {
        splitter = new Splitter(meta);
    }

    public String getData(String tag) {
        return splitter.getData(tag);
    }

    public int dataCount() {
        return splitter.dataCount();
    }


    public String allText() {
        return splitter.allText();
    }


    public String filterText() {
        return splitter.filterResult.toString();
    }

    /**
     * 过滤，识别所有tag对应的xml标签，将其从流中移除，并写入到xmlMao中
     *
     * @param output 输入流
     * @return 输出结果
     */
    public Flux<Character> filter(Flux<Character> output) {

        output.doFinally(new Consumer<SignalType>() {
                    @Override
                    public void accept(SignalType signalType) {
                        splitter.onComplete();
                    }
                })
                .subscribe(splitter::read);
        return splitter.bridgeSink.asFlux();
    }

    @Getter
    private static class Splitter extends XmlSplitter {

        private final Sinks.Many<Character> bridgeSink = Sinks.many().unicast().onBackpressureBuffer();

        private final StringBuilder filterResult = new StringBuilder();

        private final Map<String, Object> metaMap;

        private Splitter(Map<String, Object> meta) {
            this.metaMap = meta;
        }

        @Override
        public void output(Character k) {
            bridgeSink.tryEmitNext(k);
            filterResult.append(k);
        }

        @Override
        public void completeAction() {
            metaMap.putAll(meta());
            bridgeSink.tryEmitComplete();
        }
    }
}
