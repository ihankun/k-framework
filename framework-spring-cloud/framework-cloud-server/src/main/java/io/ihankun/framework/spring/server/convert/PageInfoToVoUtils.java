package io.ihankun.framework.spring.server.convert;

import com.github.pagehelper.PageInfo;
import io.ihankun.framework.common.base.BasePageResult;

/**
 * @author hankun
 */
public class PageInfoToVoUtils {
    /**
     * 转换
     *
     * @param pageInfo
     * @param <T>
     * @return
     */
    public static <T> BasePageResult<T> convert(PageInfo<T> pageInfo) {

        BasePageResult vo = new BasePageResult<>();

        vo.setTotal(pageInfo.getTotal());
        vo.setPageSize(pageInfo.getPageSize());
        vo.setPageNum(pageInfo.getPageNum());
        vo.setList(pageInfo.getList());

        return vo;
    }
}
