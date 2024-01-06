package io.ihankun.framework.poi.excel.entity;

import io.ihankun.framework.poi.excel.entity.enmus.ExcelType;
import io.ihankun.framework.poi.excel.export.styler.ExcelExportStylerDefaultImpl;
import lombok.Data;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * Excel 导出参数
 *
 * @author hankun
 */
@Data
public class ExportParams extends ExcelBaseParams {

    /**
     * 表格名称
     */
    private String  title;

    /**
     * 表格名称
     */
    private short   titleHeight = 10;

    /**
     * 第二行名称
     */
    private String   secondTitle;

    /**
     * 表格名称
     */
    private short    secondTitleHeight = 8;
    /**
     * sheetName
     */
    private String    sheetName;
    /**
     * 过滤的属性
     */
    private String[]  exclusions;
    /**
     * 是否添加需要需要
     */
    private boolean   addIndex;
    /**
     * 是否添加需要需要
     */
    private String    indexName         = "序号";
    /**
     * 冰冻列
     */
    private int       freezeCol;
    /**
     * 表头颜色 &  标题颜色
     */
    private short     color             = HSSFColor.HSSFColorPredefined.WHITE.getIndex();
    /**
     * 第二行标题颜色
     * 属性说明行的颜色 例如:HSSFColor.SKY_BLUE.index 默认
     */
    private short     headerColor       = HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex();
    /**
     * Excel 导出版本
     */
    private ExcelType type              = ExcelType.XSSF;
    /**
     * Excel 导出style
     */
    private Class<?>  style             = ExcelExportStylerDefaultImpl.class;

    /**
     * 表头高度
     */
    private double  headerHeight     = 9D;
    /**
     * 是否创建表头
     */
    private boolean isCreateHeadRows = true;
    /**
     * 是否动态获取数据
     */
    private boolean isDynamicData    = false;
    /**
     * 是否追加图形
     */
    private boolean isAppendGraph    = true;
    /**
     * 是否固定表头
     */
    private boolean isFixedTitle     = true;
    /**
     * 单sheet最大值
     * 03版本默认6W行,07默认100W
     */
    private int     maxNum           = 0;

    /**
     * 导出时在excel中每个列的高度 单位为字符，一个汉字=2个字符
     * 全局设置,优先使用
     */
    private short height = 0;

    /**
     * 只读
     */
    private boolean readonly = false;
    /**
     * 列宽自适应，如果没有设置width 也自适应宽度
     */
    private boolean autoSize = false;

    public ExportParams() {

    }

    public ExportParams(String title, String sheetName) {
        this.title = title;
        this.sheetName = sheetName;
    }

    public ExportParams(String title, String sheetName, ExcelType type) {
        this.title = title;
        this.sheetName = sheetName;
        this.type = type;
    }

    public ExportParams(String title, String secondTitle, String sheetName) {
        this.title = title;
        this.secondTitle = secondTitle;
        this.sheetName = sheetName;
    }

    public short getSecondTitleHeight() {
        return (short) (secondTitleHeight * 50);
    }

    public short getTitleHeight() {
        return (short) (titleHeight * 50);
    }

    public short getHeight() {
        return height == -1 ? -1 : (short) (height * 50);
    }


    public short getHeaderHeight() {
        return (short) (titleHeight * 50);
    }

}
