package io.hankun.framework.poi.excel.imports.sax.parse;

import io.hankun.framework.poi.excel.entity.sax.SaxReadCellEntity;

import java.util.List;

/**
 * @author hankun
 */
public interface ISaxRowRead {
    /**
     * 解析数据
     * @param index
     * @param cellList
     */
    void parse(int index, List<SaxReadCellEntity> cellList);

}
