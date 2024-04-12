package io.ihankun.framework.core.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 */
@AllArgsConstructor
@Data
public class BasePageResult<T> extends BaseEntity {

    @ApiModelProperty("总条数")
    private Long total;

    @ApiModelProperty("当前页数")
    private Integer pageNum;

    @ApiModelProperty("每页数量")
    private Integer pageSize;

    @ApiModelProperty("分页数据")
    private List<T> list;

    public BasePageResult() {
    }

    public BasePageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public BasePageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> BasePageResult<T> empty() {
        return new BasePageResult<>(0L);
    }

    public static <T> BasePageResult<T> empty(Long total) {
        return new BasePageResult<>(total);
    }
}
