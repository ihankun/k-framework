package io.hankun.framework.ai.agent.audio.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: AudioClientStatus
 * @createAt: 2025/7/24 10:44
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum AudioClientStatus {
    INIT("init", "初始化中，建立连接"),
    READY("ready", "已就绪，可以调用start准备会话"),
    STARTING("starting", "正在准备会话中"),
    RUNNING("running", "正在运行中，可以发送文本"),
    STOPPING("stopping", "正在停止会话中"),
    CLOSED("closed", "已关闭，不可用"),
    ERROR("error", "错误"),
    ;

    private final String status;

    private final String desc;
}
