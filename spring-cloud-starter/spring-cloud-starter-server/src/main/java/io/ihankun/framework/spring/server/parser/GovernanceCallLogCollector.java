package io.ihankun.framework.spring.server.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.ihankun.framework.common.context.GovernanceContext;
import io.ihankun.framework.common.utils.DateUtils;
import io.ihankun.framework.spring.server.utils.HttpConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class GovernanceCallLogCollector {

    @Resource
    private GovernanceAuthProp governanceAuthProp;

    @Value("${spring.application.name}")
    public String currServerAppName;

    private final static ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("kun-governance-callcollect-%d").build());

    private final ConcurrentLinkedQueue<JSONObject> logQueue = new ConcurrentLinkedQueue<>();

    private final LogSender logSender = new LogSender();

    @PostConstruct
    public void init() {
        log.warn(String.format("接口管控调用日志采集器已启动。配置参数：%s", JSON.toJSONString(governanceAuthProp)));
        // 定时调度推送
        scheduledExecutorService.scheduleWithFixedDelay(logSender, governanceAuthProp.getCollectorInitialDelay(), governanceAuthProp.getCollectorDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * 采集接口调用数据
     *
     * @param request 请求Request实例
     * @param validResult 校验结果
     * @param elapsedTime 拦截耗时
     * @return void
     */
    public void collectCallLog(HttpServletRequest request, String validResult, Long elapsedTime) {
        if (!governanceAuthProp.isCollectorEnable()) {
            return;
        }

        String apiServiceName = currServerAppName;
        String apiUri = request.getRequestURI();
        String applyServiceName = request.getHeader(GovernanceContext.UPSTREAM_SERVER_NAME);

        JSONObject extraJson = new JSONObject() {{
            put("rt", DateUtils.getNowStr());
            put("et", elapsedTime);
            put("ip", String.format("%s:%s", request.getLocalAddr(), request.getLocalPort()));
            put("cs", logQueue.size());
        }};
        String extraInfo = JSON.toJSONString(extraJson);

        final JSONObject paramJson = new JSONObject();
        paramJson.put("serviceNacosName", apiServiceName);
        paramJson.put("apiUri", apiUri);
        paramJson.put("applyServiceNacosName", applyServiceName);
        paramJson.put("authState", validResult);
        paramJson.put("extraInfo", extraInfo);

        // 超过最大换存量时，直接丢弃当前日志
        if (logQueue.size() < governanceAuthProp.getCollectorMaxCacheSize()) {
            logQueue.offer(paramJson);
        }

        // 缓存的日志量到阀值时，立即调度推送（调度等待队列最大允许3个等待）
        if (logQueue.size() >= governanceAuthProp.getCollectorLogsPerSend() &&
                scheduledExecutorService.getQueue().size() < 3) {
            scheduledExecutorService.schedule(logSender, 0, TimeUnit.SECONDS);
        }
    }

    /**
     * 采集日志发送器
     */
    private class LogSender implements Runnable {
        private boolean running = false;

        @Override
        public void run() {
            if (!governanceAuthProp.isCollectorEnable()) {
                return;
            }

            if (running) {
                return;
            }

            try {
                running = true;
                do {
                    JSONArray paramArray = new JSONArray();
                    while (logQueue.size() > 0 && paramArray.size() < governanceAuthProp.getCollectorLogsPerSend()) {
                        paramArray.add(logQueue.poll());
                    }

                    if (paramArray.size() > 0) {
                        HttpConnectionUtil.doPost(String.format("%s/govering/collectapicall", governanceAuthProp.getHost()), paramArray.toJSONString());
                    }
                } while (logQueue.size() >= governanceAuthProp.getCollectorLogsPerSend()); //如果还有达量的缓存则继续批量推送
            } catch (Exception e) {
                log.error(String.format("LogSender.run异常：%s", e.getMessage()), e);
            } finally {
                running = false;
            }
        }

        /**
         * 是否正在运行
         *
         * @return boolean
         */
        public boolean isRunning() {
            return this.running;
        }
    }
}
