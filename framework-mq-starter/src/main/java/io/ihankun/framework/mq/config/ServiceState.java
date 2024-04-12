package io.ihankun.framework.mq.config;

import lombok.Getter;

/**
 * @author hankun
 */
@Getter
public enum ServiceState {

    /**
     * 无
     */
    NODE(0),
    /**
     * 灰度
     */
    GRY(1),

    /**
     * 生产
     */
    PROD(2),

    /**
     * 全部
     */
    ALL(3);

    private final int code;

    ServiceState(int code) {
        this.code = code;
    }

    public static ServiceState getByCode(int code) {
        for (ServiceState state : ServiceState.values()) {
            if (state.code == code) {
                return state;
            }
        }
        return ServiceState.NODE;
    }
}
