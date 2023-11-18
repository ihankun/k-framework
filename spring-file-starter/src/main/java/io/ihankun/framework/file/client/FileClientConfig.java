package io.ihankun.framework.file.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 文件客户端的配置
 * 不同实现的客户端，需要不同的配置，通过子类来定义
 *
 * @author hankun
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface FileClientConfig {
}
