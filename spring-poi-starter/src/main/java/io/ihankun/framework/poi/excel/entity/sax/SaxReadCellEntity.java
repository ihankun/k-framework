package io.ihankun.framework.poi.excel.entity.sax;

import io.ihankun.framework.poi.excel.entity.enmus.CellValueType;
import lombok.Data;

/**
 * Cell 对象
 *
 * @author hankun
 */
@Data
public class SaxReadCellEntity {
    /**
     * 值类型
     */
    private CellValueType cellType;
    /**
     * 值
     */
    private Object        value;

    public SaxReadCellEntity(CellValueType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[type=" + cellType.toString() + ",value=" + value + "]";
    }

}
