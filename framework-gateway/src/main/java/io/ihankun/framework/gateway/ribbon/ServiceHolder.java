package io.ihankun.framework.gateway.ribbon;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Data
public class ServiceHolder {

    public ServiceHolder() {
        prod = new ArrayList<>(5);
        dev = new ArrayList<>(5);
        gray = new ArrayList<>(5);
        empty = new ArrayList<>(0);

        debug = new HashMap<>();
        other = new HashMap<>(0);
    }


    /**
     * 标记为prod的服务
     */
    private List<ServiceInstanceWarp> prod;

    /**
     * 标记为dev的服务
     */
    private List<ServiceInstanceWarp> dev;

    /**
     * 标记为gray的服务
     */
    private List<ServiceInstanceWarp> gray;

    /**
     * 没有标记的服务
     */
    private List<ServiceInstanceWarp> empty;

    /**
     * 本地启动的 debug 服务
     */
    private Map<String, List<ServiceInstanceWarp>> debug;

    /**
     * 自定义别名服务
     */
    private Map<String, List<ServiceInstanceWarp>> other;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
