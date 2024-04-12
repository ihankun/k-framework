package io.ihankun.framework.core.error;

/**
 * @author hankun
 */
public interface IErrorCode {

    /**
     * 设置前缀
     */
    String prefix();

    /**
     * 获取Code
     */
    String getCode();

    /**
     * 获取错误信息
     */
    String getMsg();
}
