package io.ihankun.framework.ribbon;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class ServiceInstanceWarp {

    /**
     * 实际对象实例
     */
    private Object instance;

    private String ip;

    private int port;

    private String mark;

    private String version;

    private String gray;

    public ServiceInstanceWarp(Object instance, String ip, int port, String mark, String version, String gray) {
        this.instance = instance;
        this.ip = ip;
        this.port = port;
        this.mark = mark;
        this.version = version;
        this.gray = gray;
    }


    @Override
    public String toString() {
        return "ServiceInstanceWarp{" +
                "instance=" + instance +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", mark='" + mark + '\'' +
                ", version='" + version + '\'' +
                ", gray='" + gray + '\'' +
                '}';
    }
}
