package io.ihankun.framework.spring.server.parser;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author hankun
 */
@ConfigurationProperties(prefix = "kun.governance.config.auth")
@Component
@RefreshScope
@Data
@NoArgsConstructor
public class GovernanceAuthProp {

    /**服务鉴权启用总开关*/
    private boolean enabled;
    /**服务鉴权动态开关，可在服务鉴权过程中动态调整是否需要鉴权*/
    private boolean authflag;
    /**上送交易链路信息开关，用于记录链路调用分析，仅在需要链路分析时放开*/
    private boolean recordflag;
    /**服务鉴权服务端url*/
    private String serverAddr = "localhost";
    /**服务端http://ip:port*/
    private String host = "localhost:30027";
    /**定时任务初始化后延迟启动时间，单位毫秒，默认1000毫秒*/
    private long initialDelay = 1000;
    /**定时任务运行间隔时间，单位毫秒，默认30000毫秒*/
    private long delay = 30000;
    /**接口调用收集开关，用于收集调用方服务名、被调用服务名和地址*/
    private boolean collectorEnable = false;
    /**日志采集任务延迟启动时间，单位毫秒*/
    private long collectorInitialDelay = 30000;
    /**日志采集任务运行间隔时间，单位毫秒*/
    private long collectorDelay = 10000;
    /**一次发送到收集接口的日志量*/
    private long collectorLogsPerSend = 10;
    /**最大缓存的采集日志量*/
    private int collectorMaxCacheSize = 2048;
}
