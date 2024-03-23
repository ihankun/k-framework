package io.ihankun.framework.common.v1.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author hankun
 */
@Data
public class BasePageParam extends BaseReq{

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    @ApiModelProperty("起始页数，从1开始计算")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNum = PAGE_NO;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 500, message = "每页条数最大值为 500")
    @ApiModelProperty("每页大小，默认为10")
    private Integer pageSize = PAGE_SIZE;
}
