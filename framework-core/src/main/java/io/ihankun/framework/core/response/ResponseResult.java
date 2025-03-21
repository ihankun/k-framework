package io.ihankun.framework.core.response;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ihankun.framework.core.enums.ResponseLevelEnum;
import io.ihankun.framework.core.error.IErrorCode;
import io.ihankun.framework.core.error.impl.BaseErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 通用返回对象基类
 * @author hankun
 */
@Slf4j
@Data
public class ResponseResult<T> implements Serializable {

    @ApiModelProperty("标记是否成功")
    private boolean success;

    @ApiModelProperty("是否将返回值包装为ResponseResult")
    @Getter(onMethod = @__( @JsonIgnore))
    private boolean decorate = Boolean.TRUE;

    @ApiModelProperty("错误码")
    private String code;

    @ApiModelProperty("错误信息")
    private String message;

    @ApiModelProperty("业务对象，继承自BaseEntity")
    private T data;

    @ApiModelProperty("跟踪ID")
    private String traceId;

    @ApiModelProperty("版本号")
    private String version;

//    @ApiModelProperty("异常名称")
//    @Getter(onMethod = @__( @JsonIgnore))
//    private String exceptionName;
//
//    @ApiModelProperty("级别，正常返回为info,业务异常为warn,未捕获异常为error")
//    @Getter(onMethod = @__( @JsonIgnore))
//    private String level;
//
//    @ApiModelProperty("当前服务")
//    @Getter(onMethod = @__( @JsonIgnore))
//    private String service;

    private static final String REPLACE_STR = "$";
    private static final String CODE_SPLIT = "@";
    private static final String NO_PASS = "1";

    public boolean getSuccess() {
        return success;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * 构建错误信息
     */
    private static <T> ResponseResult build(IErrorCode code, T data, String[] params) {
        return build(code, null, ResponseLevelEnum.INFO, data, params);
    }

    /**
     * 构建返回消息
     */
    private static <T> ResponseResult build(IErrorCode code, String service, ResponseLevelEnum level, T data, String[] params) {
        ResponseResult result = new ResponseResult();
        result.setSuccess(code.getCode().equals(BaseErrorCode.SUCCESS.getCode()) ? Boolean.TRUE : Boolean.FALSE);
        if (StringUtils.isEmpty(code.prefix())) {
            result.setCode(code.getCode());
        } else {
            result.setCode(code.prefix() + CODE_SPLIT + code.getCode());
        }
        String msg = code.getMsg();
        //如果包含占位符
        if (msg.contains(REPLACE_STR) && params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                msg = msg.replaceAll("\\$" + (i + 1), param);
            }
        }
        //result.setService(service);
        //result.setLevel(level.name().toLowerCase());
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

//    /**
//     * 判断是否为业务异常
//     */
//    public boolean isBusinessException() {
//        return BusinessException.class.getName().equals(getExceptionName());
//    }

    /**
     * 重新构造消息
     */
    public ResponseResult rebuildMsg(String msg) {
        this.setMessage(msg);
        return this;
    }

    public ResponseResult setException(Throwable exception) {
        //this.exceptionName = exception.getClass().getName();
        return this;
    }

    /**
     * 根据返回对象获取errCode
     */
    public IErrorCode convert() {

        String[] split = this.getCode().split(CODE_SPLIT);
        String prefix = "";
        String code = "";
        int length = 1;
        if (split.length == length) {
            code = split[0];
            prefix = "";
        }
        length = 2;
        if (split.length == length) {
            code = split[0];
            prefix = split[1];
        }
        String message = this.getMessage();

        String finalPrefix = prefix;
        String finalCode = code;
        return new IErrorCode() {
            @Override
            public String prefix() {
                return finalPrefix;
            }

            @Override
            public String getCode() {
                return finalCode;
            }

            @Override
            public String getMsg() {
                return message;
            }
        };
    }

    /**
     * 获取成功的结果
     */
    public static <T> ResponseResult<T> success() {
        return build(BaseErrorCode.SUCCESS, null, null);
    }

    /**
     * 获取 成功结果
     */
    public static <T> ResponseResult<T> success(T data) {
        return build(BaseErrorCode.SUCCESS, data, null);
    }

    /**
     * 获取失败的结果
     */
    public static <T> ResponseResult<T> error() {
        return build(BaseErrorCode.SYSTEM_ERROR, null, null);
    }

    /**
     * 获取失败的结果
     */
    public static <T> ResponseResult<T> error(IErrorCode code, String... params) {
        return build(code, null, params);
    }


    /**
     * 获取失败的结果
     */
    public static <T> ResponseResult<T> error(T data, IErrorCode code, String... params) {
        return build(code, data, params);
    }

    /**
     * 获取失败的结果
     */
    public static <T> ResponseResult<T> error(IErrorCode code, String service, ResponseLevelEnum level, String... params) {
        return build(code, null, params);
    }


    /**
     * 返回熔断结果
     */
    public static <T> ResponseResult<T> fallback(Throwable throwable) {
        log.error("ResponseResult.fallback,e={}", throwable);
        return build(BaseErrorCode.FALLBACK, null, new String[]{throwable.getMessage()});
    }

//    /**
//     * 获取业务异常的ErrorCode
//     */
//    public IErrorCode convertErrorCode() {
//        if (isBusinessException()) {
//            String code = getCode();
//            return new IErrorCode() {
//                @Override
//                public String prefix() {
//                    return "BusinessExceptionErrorCode";
//                }
//
//                @Override
//                public String getCode() {
//                    return code;
//                }
//
//                @Override
//                public String getMsg() {
//                    return getMessage();
//                }
//            };
//        }
//        return convert();
//    }
}
