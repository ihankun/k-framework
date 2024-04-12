package io.ihankun.framework.job.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Data
public class JobControlConfigData {

    @Data
    public static class Config {
        private JobConfig job;
    }

    @Data
    public static class JobConfig {
        private ControlConfig control;
    }

    @Data
    public static class ControlConfig {
        private boolean open = false;
        private Map<String, List<String>> jobs;
    }
}
