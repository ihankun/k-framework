package io.ihankun.framework.job.dataflow;

import io.ihankun.framework.common.utils.spring.ServerStateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractJob {
    private static final String DOMAIN_PROTOCOL_SPLIT = "://";
    private static final String DOMAIN_PORT_SPLIT = ":";

    private static final String RUN_ONLY_ONCE = "runOnlyOnce=true";

    /**
     * 是否灰度节点
     */
    protected String isGray() {
        String grayMark = ServerStateUtil.getGrayMark();
        log.info("灰度标识：{}", grayMark);
        return grayMark;
    }

    /**
     * 格式化 domain 域名，去掉端口
     */
    protected static String formatDomain(String domain) {

        if (!StringUtils.isEmpty(domain) && domain.contains(DOMAIN_PROTOCOL_SPLIT)) {
            domain = domain.split(DOMAIN_PROTOCOL_SPLIT)[1];
        }

        if (!StringUtils.isEmpty(domain) && domain.contains(DOMAIN_PORT_SPLIT)) {
            domain = domain.split(DOMAIN_PORT_SPLIT)[0];
        }
        return domain;
    }

    /**
     * 是否只运行1次
     */
    protected static boolean isRunOnlyOnce(String jobParameter) {
        if (StringUtils.isEmpty(jobParameter)) {
            return Boolean.FALSE;
        }

        if (jobParameter.contains(RUN_ONLY_ONCE)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
