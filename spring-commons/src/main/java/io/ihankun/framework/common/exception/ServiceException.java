package io.ihankun.framework.common.exception;

import io.ihankun.framework.common.response.IResultCode;
import io.ihankun.framework.common.response.R;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

/**
 * 业务逻辑异常 Exception
 * @author hankun
 */
@Data
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 2359767895161832954L;

    /**
     * 业务错误码
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    @Nullable
    private R<?> result;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public ServiceException setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    public ServiceException(R<?> result) {
        super(result.getMsg());
        this.result = result;
    }

    public ServiceException(IResultCode rCode) {
        this(rCode, rCode.getMsg());
    }

    public ServiceException(IResultCode rCode, String message) {
        super(message);
        this.result = R.fail(rCode, message);
    }

    public ServiceException(String message) {
        super(message);
        this.result = null;
    }

    public ServiceException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        doFillInStackTrace();
        this.result = null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> R<T> getResult() {
        return (R<T>) result;
    }

    /**
     * 提高性能
     * @return Throwable
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }

}
