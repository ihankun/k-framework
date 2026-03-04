package io.hankun.framework.gateway.entity;

import io.netty.util.CharsetUtil;
import lombok.Getter;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FilterResult {

    @Getter
    private Map<String, String> requestHeader;

    @Getter
    private Map<String, String> responseHeader;

    @Getter
    private List<String> rmRequestHeader;

    @Getter
    private List<String> rmResponseHeader;


    /**
     * 是否进行下一步检查
     */
    @Getter
    private boolean next = true;

    private static final Base64.Encoder ENCODER = Base64.getEncoder();


    public FilterResult addReqHeader(String headerName, String headerValue, boolean base64) {
        if (requestHeader == null) {
            requestHeader = new ConcurrentHashMap<>(1);
        }
        if (base64) {
            headerValue = ENCODER.encodeToString(headerValue.getBytes(CharsetUtil.UTF_8));
        }
        requestHeader.put(headerName, headerValue);
        return this;
    }


    public FilterResult rmReqHeader(String headerName) {
        if (rmRequestHeader != null) {
            rmRequestHeader = new CopyOnWriteArrayList<>();
        }
        rmRequestHeader.add(headerName);
        return this;
    }

    public FilterResult addRespHeader(String headerName, String headerValue, boolean base64) {
        if (responseHeader == null) {
            responseHeader = new ConcurrentHashMap<>(1);
        }
        if (base64) {
            headerValue = ENCODER.encodeToString(headerValue.getBytes(CharsetUtil.UTF_8));
        }
        responseHeader.put(headerName, headerValue);
        return this;
    }

    public FilterResult rmRespHeader(String headerName) {
        if (rmResponseHeader != null) {
            rmResponseHeader = new CopyOnWriteArrayList<>();
        }
        rmResponseHeader.add(headerName);
        return this;
    }

    public FilterResult abortNext() {
        this.next = false;
        return this;
    }
}
