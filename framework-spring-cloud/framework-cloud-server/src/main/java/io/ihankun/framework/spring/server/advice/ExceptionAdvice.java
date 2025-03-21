package io.ihankun.framework.spring.server.advice;

import io.ihankun.framework.core.context.*;
import io.ihankun.framework.core.enums.ResponseLevelEnum;
import io.ihankun.framework.core.error.IErrorCode;
import io.ihankun.framework.core.exception.BusinessException;
import io.ihankun.framework.core.response.ResponseResult;
import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.spring.server.error.AdviceErrorCode;
import io.seata.core.context.RootContext;
import io.seata.integration.http.XidResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

/**
 * @author hankun
 */
@Slf4j
@ConditionalOnProperty(prefix = "project.separate", value = {"enable"}, havingValue = "true", matchIfMissing = true)
@RestControllerAdvice
public class ExceptionAdvice {

    @Value("${spring.application.name:none}")
    private String service;

    public static final int MAX_DEEP = 4;

    /**
     * hibernate validator 数据绑定验证异常拦截
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public ResponseResult validateErrorHandler(HttpServletRequest request, BindException e) {
        ObjectError error = e.getAllErrors().get(0);
        log.error("参数绑定异常(BindException),{}", e);
        ResponseResult result = ResponseResult.error(AdviceErrorCode.BIND_EX,service, ResponseLevelEnum.WARN, error.getDefaultMessage()).setException(e);
        result.setTraceId(TraceLogContext.get());
        clearContext(request);
        return result;
    }

    /**
     * hibernate validator 数据绑定验证异常拦截
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult validateErrorHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        log.warn("参数校验错误(MethodArgumentNotValidException),{}", e);
        ResponseResult result = ResponseResult.error(AdviceErrorCode.PARAM_NOT_VALID_EX,service,ResponseLevelEnum.WARN, error.getDefaultMessage()).setException(e);
        result.setTraceId(TraceLogContext.get());
        clearContext(request);
        return result;
    }

    /**
     * spring validator 方法参数验证异常拦截
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult defaultErrorHandler(HttpServletRequest request, ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        log.warn("参数校验错误(ConstraintViolationException),{}", e);
        ResponseResult result = ResponseResult.error(AdviceErrorCode.CONSTRAINT_EX,service,ResponseLevelEnum.WARN, violation.getMessage()).setException(e);
        result.setTraceId(TraceLogContext.get());
        clearContext(request);
        return result;
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult handleBusinessException(HttpServletRequest request, BusinessException e) {
        log.warn("业务异常(BusinessException),{}", e);
        IErrorCode errorCode = new IErrorCode() {
            @Override
            public String prefix() {
                return e.getPrefix();
            }

            @Override
            public String getCode() {
                return e.getCode();
            }

            @Override
            public String getMsg() {
                return e.getMessage();
            }
        };

        ResponseResult result = ResponseResult.error(errorCode).setException(e);
        result.setTraceId(TraceLogContext.get());
        //result.setLevel(ResponseLevelEnum.WARN.name().toLowerCase());
        //result.setService(service);
        clearContext(request);
        return result;
    }


    public BusinessException getNestException(Exception e) {
        Throwable throwable = e;
        int deep = 0;
        while (!(throwable instanceof BusinessException)) {
            throwable = throwable.getCause();
            deep++;
            if (deep > MAX_DEEP) {
                return null;
            }
            if (throwable == null) {
                return null;
            }
        }
        return (BusinessException) throwable;
    }


    /**
     * 处理异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult handleException(HttpServletRequest request, Exception e) {
        BusinessException businessException = getNestException(e);
        if (businessException != null) {
            return handleBusinessException(request, businessException);
        }
        log.error("通用异常:{}", getExceptionStack(e));
        log.error("通用异常: stack info=", e);
        ResponseResult result = ResponseResult.error(AdviceErrorCode.OTHER_EX, e.getMessage()).setException(e);
        result.setTraceId(TraceLogContext.get());
        //result.setLevel(ResponseLevelEnum.ERROR.name().toLowerCase());
        //result.setService(service);
        clearContext(request);
        return result;
    }

    private void clearContext(HttpServletRequest request) {
        String xid = request.getHeader(RootContext.KEY_XID);
        if (!StringUtils.isEmpty(xid)) {
            XidResource.cleanXid(xid);
            log.info("ExceptionAdvice.doResolveException.clear.seata,xid={}", xid);
        }

        LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo != null) {
            LoginUserContext.clear();
        }

        String domain = DomainContext.get();
        if (!StringUtils.isEmpty(domain)) {
            DomainContext.clear();
            log.info("ExceptionAdvice.doResolveException.clear.domain,domain={}", domain);
        }

        String timeStr = BusinessTimeContext.get();
        if (!StringUtils.isEmpty(timeStr)) {
            BusinessTimeContext.clear();
            log.info("ExceptionAdvice.doResolveException.clear.time,timeStr={}", timeStr);
        }

        String traceId = TraceLogContext.get();
        if (!StringUtils.isEmpty(traceId)) {
            TraceLogContext.reset();
            log.info("ExceptionAdvice.doResolveException.clear.trace,traceId={}", traceId);
        }

        String gray = GrayContext.get();
        if (!StringUtils.isEmpty(gray)) {
            GrayContext.clear();
            log.info("ExceptionAdvice.doResolveException.clear.gray,gray={}", gray);
        }
    }

    public static String getExceptionStack(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
