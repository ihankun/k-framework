package io.hankun.framework.ai.agent.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: StringXmlSplitter
 * @createAt: 2025/8/22 08:54
 * @author: hankun
 */
public class StringXmlSplitter {


    private final Splitter splitter;

    @Getter
    private final Map<String, Object> meta = new HashMap<>(0);

    public StringXmlSplitter() {
        this.splitter = new Splitter(meta);
    }

    public String split(String data) {
        for (char c : data.toCharArray()) {
            splitter.read(c);
        }
        String result = splitter.getResult();
        splitter.completeAction();
        return result;
    }

    public Object metaData(String key) {
        return meta.get(key);
    }

    private static class Splitter extends XmlSplitter {

        private final StringBuilder result = new StringBuilder();

        private final Map<String, Object> metaMap;

        private Splitter(Map<String, Object> meta) {
            this.metaMap = meta;
        }

        public String getResult() {
            return result.toString();
        }

        @Override
        public void output(Character k) {
            result.append(k);
        }

        @Override
        public void completeAction() {
            metaMap.putAll(meta());
        }
    }
}
