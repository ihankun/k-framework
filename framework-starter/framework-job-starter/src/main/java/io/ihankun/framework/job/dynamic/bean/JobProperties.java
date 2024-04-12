package io.ihankun.framework.job.dynamic.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
public class JobProperties {

    /**
     * 自定义异常处理类
     */
    @JsonProperty("job_exception_handler")
    private String jobExceptionHandler = "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";

    /**
     * 自定义业务处理线程池
     */
    @JsonProperty("executor_service_handler")
    private String executorServiceHandler = "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";

}
