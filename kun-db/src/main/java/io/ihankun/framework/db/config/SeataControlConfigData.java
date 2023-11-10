package io.ihankun.framework.db.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Data
public class SeataControlConfigData {

    @Data
    public static class Config {
        private SeataConfig seata;
    }

    @Data
    public static class SeataConfig {
        private ControlConfig control;
    }

    @Data
    public static class ControlConfig {
        private boolean open = false;
        private Map<String, List<String>> scanners;
    }
}
