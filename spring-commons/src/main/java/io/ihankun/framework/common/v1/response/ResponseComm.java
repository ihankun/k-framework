package io.ihankun.framework.common.v1.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author hankun
 */
@Slf4j
@Data
public class ResponseComm<T> implements Serializable {

    @ApiModelProperty("错误码")
    private Integer code;

    @ApiModelProperty("信息")
    private String msg;

    @ApiModelProperty("业务对象")
    private T data;
}
