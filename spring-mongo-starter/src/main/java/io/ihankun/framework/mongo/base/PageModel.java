package io.ihankun.framework.mongo.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author hankun
 */
@Data
@ToString
@NoArgsConstructor
public class PageModel implements Serializable {

    /**
     * 当前页
     */
    private Integer pageNo;

    /**
     * 当前页条数
     */
    private Integer pageSize;

    /**
     * 总条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 实体类集合
     */
    private List<?> data;
}
