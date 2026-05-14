package io.hankun.framework.ai.core.entity;

import java.util.Date;

/**
 * @description:
 * @className: ServiceInfo
 * @createAt: 2025/6/5 16:53
 * @author: hankun
 */
public record ServiceInfo(String serviceName,
                          String grayMark,
                          String serviceVersion,
                          String ip,
                          Integer port,
                          Date serviceStartTime) {
}
