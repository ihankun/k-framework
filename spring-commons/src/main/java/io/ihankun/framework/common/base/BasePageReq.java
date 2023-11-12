package io.ihankun.framework.common.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hankun
 */
@Data
public class BasePageReq extends BaseReq{

    @ApiModelProperty("起始页数，从1开始计算")
    private Integer pageNum = 1;

    @ApiModelProperty("每页大小，默认为10")
    private Integer pageSize = 10;
}
