package io.ihankun.framework.poi.excel.entity.params;

import lombok.Data;

import java.util.List;

/**
 * 合并单元格使用对象
 *
 * @author hankun
 */
@Data
public class MergeEntity {
    /**
     * 合并开始行
     */
    private int          startRow;
    /**
     * 合并结束行
     */
    private int          endRow;
    /**
     * 文字
     */
    private String       text;
    /**
     * 依赖关系文本
     */
    private List<String> relyList;

    public MergeEntity() {

    }

    public MergeEntity(String text, int startRow, int endRow) {
        this.text = text;
        this.endRow = endRow;
        this.startRow = startRow;
    }

}
