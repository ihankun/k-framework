package io.ihankun.framework.poi.excel;

import io.ihankun.framework.poi.excel.entity.enmus.ExcelType;
import io.ihankun.framework.poi.util.PoiMergeCellUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * 链式导出工具，按照行级逐渐迭代
 *
 * @author hankun
 */
public class ExcelBuilder {

    private Workbook workbook;
    private Sheet sheet;
    private CellStyle centerStyle;
    private Short rowHeight = (short) 300;

    private ExcelBuilder() {

    }

    public static ExcelBuilder create() {
        return create(ExcelType.XSSF);
    }

    public Workbook getWorkbook() {
        return this.workbook;
    }

    public ExcelBuilder setRowHeight(int rowHeight) {
        this.rowHeight = (short) (rowHeight * 20);
        return this;

    }

    public static ExcelBuilder create(ExcelType type) {
        ExcelBuilder eb = new ExcelBuilder();
        if (ExcelType.HSSF.equals(type)) {
            eb.workbook = new HSSFWorkbook();
            eb.sheet = eb.workbook.createSheet();
        } else {
            eb.workbook = new XSSFWorkbook();
            eb.sheet = eb.workbook.createSheet();
        }
        eb.centerStyle = eb.workbook.createCellStyle();
        eb.centerStyle.setAlignment(HorizontalAlignment.CENTER);
        eb.centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return eb;
    }

    public void write(String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            this.workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public ExcelBuilder addNoneRow() {
        Row row = getNewRow();
        row.setHeight(rowHeight);
        return this;
    }

    private Row getNewRow() {
        return this.workbook.getSheetAt(0).createRow(this.sheet.getRow(0) == null ? 0 : this.sheet.getLastRowNum() + 1);
    }

    /**
     * 在excel的最后一行的基础上增加一行
     * 合并单元格，按照上一行的size合并
     *
     * @return
     */
    public ExcelBuilder addOneRow(String content) {
        Row row = getNewRow();
        row.setHeight(rowHeight);
        row.createCell(0).setCellValue(content);
        row.getCell(0).setCellStyle(this.centerStyle);
        return this;
    }

    /**
     * 在excel的最后一行的基础上增加一行
     *
     * @param content  行内容
     * @param cellSize 合并的宽度
     * @return
     */
    public ExcelBuilder addOneRow(String content, int cellSize) {
        Row row = getNewRow();
        row.setHeight(rowHeight);
        row.createCell(0).setCellValue(content);
        row.getCell(0).setCellStyle(this.centerStyle);
        PoiMergeCellUtil.addMergedRegion(this.sheet, row.getRowNum(), row.getRowNum(), 0, cellSize - 1);
        return this;
    }

    /**
     * 在excel的最后一行的基础上增加一行
     *
     * @param content   行内容
     * @param cellSize  合并的宽度
     * @param rowHeight 行高
     * @return
     */
    public ExcelBuilder addOneRow(String content, int cellSize, int rowHeight, CellStyle style) {
        Row row = getNewRow();
        row.setHeight((short) (rowHeight * 20));
        row.createCell(0).setCellValue(content);
        row.getCell(0).setCellStyle(style);
        PoiMergeCellUtil.addMergedRegion(this.sheet, row.getRowNum(), row.getRowNum(), 0, cellSize - 1);
        return this;
    }

    /**
     * 在excel的最后一行的基础上增加一行
     *
     * @return
     */
    public ExcelBuilder addOneRow(List<String> content) {
        Row row = getNewRow();
        return this;
    }

    /**
     * 增加一个Cell 指定位置
     *
     * @param content   文本内容
     * @param cellIndex cell 位置
     * @param rowIndex  row 位置
     * @return
     */
    public ExcelBuilder addOneCell(String content, int rowIndex, int cellIndex) {
        Cell cell = this.sheet.getRow(rowIndex).getCell(cellIndex) == null ? this.sheet.getRow(rowIndex).createCell(cellIndex)
                : this.sheet.getRow(rowIndex).getCell(cellIndex);
        cell.setCellValue(content);
        cell.setCellStyle(this.centerStyle);
        return this;
    }

    /**
     * 增加一个Cell 指定位置
     *
     * @param content   文本内容
     * @param cellIndex cell 位置
     * @param rowIndex  row 位置
     * @return
     */
    public ExcelBuilder addOneCell(String content, int rowIndex, int cellIndex, int lastRow, int lastCell) {
        Cell cell = this.sheet.getRow(rowIndex).getCell(cellIndex) == null ? this.sheet.getRow(rowIndex).createCell(cellIndex)
                : this.sheet.getRow(rowIndex).getCell(cellIndex);
        cell.setCellValue(content);
        cell.setCellStyle(this.centerStyle);
        PoiMergeCellUtil.addMergedRegion(this.sheet, rowIndex, lastRow, cellIndex, lastCell);
        return this;
    }

    /**
     * 增加一个Cell 指定位置
     *
     * @param content   文本内容
     * @param cellIndex cell 位置
     * @param rowIndex  row 位置
     * @return
     */
    public ExcelBuilder addOneCell(String content, int rowIndex, int cellIndex, int lastRow, int lastCell, BorderStyle borderStyle) {
        Cell cell = this.sheet.getRow(rowIndex).getCell(cellIndex) == null ? this.sheet.getRow(rowIndex).createCell(cellIndex)
                : this.sheet.getRow(rowIndex).getCell(cellIndex);
        cell.setCellValue(content);
        cell.setCellStyle(this.centerStyle);
        PoiMergeCellUtil.addMergedRegion(this.sheet, rowIndex, lastRow, cellIndex, lastCell);
        PoiMergeCellUtil.setBorder(borderStyle, this.sheet, rowIndex, lastRow, cellIndex, lastCell);
        return this;
    }


    /**
     * 增加一组Cell 指定位置
     *
     * @param contentArr 文本内容
     * @param cellIndex  cell 位置
     * @param rowIndex   row 位置
     * @return
     */
    public ExcelBuilder addCells(String[] contentArr, int rowIndex, int cellIndex) {
        for (int i = 0; i < contentArr.length; i++) {
            addOneCell(contentArr[i], rowIndex, cellIndex + i);
        }
        return this;
    }

    /**
     * 增加一组Cell 指定位置
     *
     * @param contentList 文本内容
     * @param cellIndex   cell 位置
     * @param rowIndex    row 位置
     * @return
     */
    public ExcelBuilder addCells(List<String> contentList, int rowIndex, int cellIndex) {
        for (int i = 0; i < contentList.size(); i++) {
            addOneCell(contentList.get(i), rowIndex, cellIndex + i);
        }
        return this;
    }

    /**
     * 增加一组Cell 指定位置
     *
     * @param dataList  数据列表
     * @param names     数据列
     * @param cellIndex cell 位置
     * @param rowIndex  row 位置
     * @return
     */
    public ExcelBuilder addRows(List dataList, String[] names, int rowIndex, int cellIndex) {
        for (int i = 0; i < dataList.size(); i++) {
            Row row = getRow(rowIndex + i);
            Object obj = dataList.get(i);
            if (obj instanceof Map) {
                Map map = (Map) obj;
                for (int j = 0; j < names.length; j++) {
                    addOneCell((String) map.get(names[j]), rowIndex + i, cellIndex + j);
                }
            }

        }
        return this;
    }

    public ExcelBuilder addRows(List dataList, String[] names, int rowIndex, int cellIndex, BorderStyle borderStyle) {
        addRows(dataList, names, rowIndex, cellIndex);
        PoiMergeCellUtil.setBorder(borderStyle, this.sheet, rowIndex, rowIndex + dataList.size() - 1, cellIndex, cellIndex + names.length - 1);
        return this;
    }

    private Row getRow(int index) {
        if (this.sheet.getRow(index) == null) {
            return this.sheet.createRow(index);
        }
        return this.sheet.getRow(index);
    }

}
