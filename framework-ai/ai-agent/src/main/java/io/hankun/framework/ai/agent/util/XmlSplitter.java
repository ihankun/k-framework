package io.hankun.framework.ai.agent.util;

import com.alibaba.fastjson2.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: XmlSplitter
 * @createAt: 2025/7/30 08:30
 * @author: hankun
 */
public abstract class XmlSplitter {

    private final StringBuilder cache = new StringBuilder();

    private int start = 0;

    private int end = 0;

    private boolean inXml = false;

    private String currentKey;

    private int xmlStartIndex = -1;


    private final Map<String, StringBuilder> xmlMap = new HashMap<>();

    public String getData(String tag) {
        StringBuilder data = xmlMap.get(tag);
        if (data == null) {
            return "";
        }
        return data.toString();
    }

    public int dataCount() {
        return xmlMap.size();
    }

    public String allText() {
        return cache.toString();
    }


    public String getStartTag(String key) {
        if (key.startsWith("</")) {
            return null;
        }
        if (key.length() <= 1) {
            return null;
        }
        return key.substring(1, key.length() - 1);
    }

    public String getEndTag(String key) {
        if (key.length() <= 2) {
            return null;
        }
        if (key.startsWith("</")) {
            return key.substring(2, key.length() - 1);
        }
        return null;
    }

    protected Map<String, Object> meta() {
        Map<String, Object> meta = new HashMap<>();
        for (Map.Entry<String, StringBuilder> entry : xmlMap.entrySet()) {
            String value = entry.getValue().toString();
            if (JSON.isValid(value)) {
                meta.put(entry.getKey(), JSON.parse(value));
            } else {
                meta.put(entry.getKey(), value);
            }
        }
        return meta;
    }


    public void read(Character character) {
        cache.append(character);
        if (character.equals('<')) {
            reset();
        } else if (character.equals('>')) {
            end = cache.length();
            String key = cache.substring(start, end);
            String startTag = getStartTag(key);
            if (startTag != null && !inXml) {
                // 开始标签
                inXml = true;
                currentKey = startTag;
                xmlStartIndex = start;
                return;
            }
            String endTag = getEndTag(key);
            if (!inXml) {
                //不在xml中，直接输出key
                for (Character k : key.toCharArray()) {
                    output(k);
                }
                return;
            }
            if (endTag == null) {
                xmlMap.computeIfAbsent(currentKey, s -> new StringBuilder())
                        .append(key);
                return;
            }
            if (endTag.equals(currentKey)) {
                inXml = false;
                currentKey = "";
                xmlStartIndex = -1;
            }
        } else if (end != -1) {
            if (inXml) {
                xmlMap.computeIfAbsent(currentKey, s -> new StringBuilder())
                        .append(character);
            } else {
                start = cache.length();
                output(character);
            }
        }
    }

    public abstract void output(Character k);

    public abstract void completeAction();

    public void onComplete() {
        int startIndex = xmlStartIndex;
        if (startIndex == -1) {
            //未进入xml标签
            //判断标签是否完整
            //如果标签不完整，将开始设置为start
            if (start < cache.length() && end == -1) {
                startIndex = start;
            }
        }

        if (startIndex != -1) {
            for (Character c : cache.substring(startIndex).toCharArray()) {
                output(c);
            }
            xmlMap.remove(currentKey);
        }
        completeAction();
    }


    private void reset() {
        start = cache.length() - 1;
        end = -1;
    }
}
