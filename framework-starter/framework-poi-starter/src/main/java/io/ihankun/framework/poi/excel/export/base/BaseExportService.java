package io.ihankun.framework.poi.excel.export.base;

import io.ihankun.framework.poi.cache.ImageCache;
import io.ihankun.framework.poi.entity.BaseTypeConstants;
import io.ihankun.framework.poi.entity.PoiBaseConstants;
import io.ihankun.framework.poi.entity.SpecialSymbolsEntity;
import io.ihankun.framework.poi.excel.entity.enmus.ExcelType;
import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.excel.export.styler.IExcelExportStyler;
import io.ihankun.framework.poi.exception.excel.ExcelExportException;
import io.ihankun.framework.poi.exception.excel.enums.ExcelExportEnum;
import io.ihankun.framework.poi.util.PoiExcelGraphDataUtil;
import io.ihankun.framework.poi.util.PoiMergeCellUtil;
import io.ihankun.framework.poi.util.PoiPublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 提供POI基础操作服务
 *
 * @author hankun
 */
@SuppressWarnings("unchecked")
public abstract class BaseExportService extends ExportCommonService {

    private int currentIndex = 0;

    protected ExcelType type = ExcelType.XSSF;

    private Map<Integer, Double> statistics = new HashMap<Integer, Double>();

    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

    protected IExcelExportStyler excelExportStyler;

    /**
     * 创建 最主要的 Cells
     */
    public int[] createCells(Drawing patriarch, int index, Object t,
                             List<ExcelExportEntity> excelParams, Sheet sheet, Workbook workbook,
                             short rowHeight, int cellNum) {
        try {
            ExcelExportEntity entity;
            Row row = sheet.getRow(index) == null ? sheet.createRow(index) : sheet.getRow(index);
            if (rowHeight != -1) {
                row.setHeight(rowHeight);
            }
            int maxHeight = 1, listMaxHeight = 1;
            // 合并需要合并的单元格
            int margeCellNum = cellNum;
            int indexKey = 0;
            if (excelParams != null && !excelParams.isEmpty()) {
                indexKey = createIndexCell(row, index, excelParams.get(0));
            }
            cellNum += indexKey;
            for (int k = indexKey, paramSize = excelParams.size(); k < paramSize; k++) {
                entity = excelParams.get(k);
                //不论数据是否为空都应该把该列的数据跳过去
                if (entity.getList() != null) {
                    Collection<?> list = getListCellValue(entity, t);
                    int tmpListHeight = 0;
                    if (list != null && list.size() > 0) {
                        int tempCellNum = 0;
                        for (Object obj : list) {
                            int[] temp = createCells(patriarch, index + tmpListHeight, obj, entity.getList(), sheet, workbook, rowHeight, cellNum);
                            tempCellNum = temp[1];
                            tmpListHeight += temp[0];
                        }
                        cellNum = tempCellNum;
                        listMaxHeight = Math.max(listMaxHeight, tmpListHeight);
                    } else {
                        cellNum = cellNum + getListCellSize(entity.getList());
                    }
                } else {
                    Object value = getCellValue(entity, t);

                    if (entity.getType() == BaseTypeConstants.STRING_TYPE) {
                        createStringCell(row, cellNum++, value == null ? "" : value.toString(),
                                index % 2 == 0 ? getStyles(false, entity) : getStyles(true, entity),
                                entity);

                    } else if (entity.getType() == BaseTypeConstants.DOUBLE_TYPE) {
                        createDoubleCell(row, cellNum++, value == null ? "" : value.toString(),
                                index % 2 == 0 ? getStyles(false, entity) : getStyles(true, entity),
                                entity);
                    } else if (entity.getType() == BaseTypeConstants.Symbol_TYPE) {
                        createSymbolCell(row, cellNum++, value,
                                index % 2 == 0 ? getStyles(false, entity) : getStyles(true, entity),
                                entity);
                    } else {
                        createImageCell(patriarch, entity, row, cellNum++,
                                value == null ? "" : value.toString(), t);
                    }
                    if (entity.isHyperlink()) {
                        row.getCell(cellNum - 1)
                                .setHyperlink(dataHandler.getHyperlink(
                                        row.getSheet().getWorkbook().getCreationHelper(), t,
                                        entity.getName(), value));
                    }
                }
            }
            maxHeight += listMaxHeight - 1;
            if (indexKey == 1 && excelParams.get(1).isNeedMerge()) {
                excelParams.get(0).setNeedMerge(true);
            }
            for (int k = indexKey, paramSize = excelParams.size(); k < paramSize; k++) {
                entity = excelParams.get(k);
                if (entity.getList() != null) {
                    margeCellNum += entity.getList().size();
                } else if (entity.isNeedMerge() && maxHeight > 1) {
                    for (int i = index + 1; i < index + maxHeight; i++) {
                        if (sheet instanceof SXSSFSheet && i <= ((SXSSFSheet) sheet).getLastFlushedRowNum()) {
                            continue;
                        }
                        if (sheet.getRow(i) == null) {
                            try {
                                sheet.createRow(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        sheet.getRow(i).createCell(margeCellNum);
                        sheet.getRow(i).getCell(margeCellNum).setCellStyle(getStyles(false, entity));
                    }
                    PoiMergeCellUtil.addMergedRegion(sheet, index, index + maxHeight - 1, margeCellNum, margeCellNum);
                    margeCellNum++;
                }
            }
            return new int[]{maxHeight, cellNum};
        } catch (Exception e) {
            LOGGER.error("excel cell export error ,data is :{}", ReflectionToStringBuilder.toString(t));
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }

    }

    private void createSymbolCell(Row row, int index, Object specialSymbolsEntity, CellStyle style,
                                  ExcelExportEntity entity) {
        SpecialSymbolsEntity symbol = (SpecialSymbolsEntity) specialSymbolsEntity;
        Cell cell = row.createCell(index);
        Font font = cell.getSheet().getWorkbook().createFont();
        font.setFontName(symbol.getFont());
        RichTextString rtext;
        if (cell instanceof HSSFCell) {
            rtext = new HSSFRichTextString(symbol.getUnicode());
            rtext.applyFont(font);
        } else {
            rtext = new XSSFRichTextString(symbol.getUnicode());
            rtext.applyFont(font);
        }
        cell.setCellValue(rtext);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    /**
     * 获取集合的宽度
     *
     * @param list
     * @return
     */
    protected int getListCellSize(List<ExcelExportEntity> list) {
        int cellSize = 0;
        for (ExcelExportEntity ee : list) {
            if (ee.getList() != null) {
                cellSize += getListCellSize(ee.getList());
            } else {
                cellSize++;
            }
        }
        return cellSize;
    }

    /**
     * 图片类型的Cell
     */
    public void createImageCell(Drawing patriarch, ExcelExportEntity entity, Row row, int i,
                                String imagePath, Object obj) throws Exception {
        Cell cell = row.createCell(i);
        byte[] value = null;
        if (entity.getExportImageType() != 1) {
            if (entity.getMethods() == null && entity.getMethod() == null) {
                value = (byte[]) PoiPublicUtil.getParamsValue(entity.getKey().toString(), obj);
            } else {
                value = (byte[]) (entity.getMethods() != null ? getFieldBySomeMethod(entity.getMethods(), obj,entity.getMethodsParams())
                        : getFieldByMethod(entity.getMethod(),obj,entity.getMethodParams()));
            }
        }
        createImageCell(cell, 50 * entity.getHeight(), entity.getExportImageType() == 1 ? imagePath : null, value);

    }


    /**
     * 图片类型的Cell
     */
    public void createImageCell(Cell cell, double height,
                                String imagePath, byte[] data) throws Exception {
        if (height > cell.getRow().getHeight()) {
            cell.getRow().setHeight((short) height);
        }
        ClientAnchor anchor;
        if (type.equals(ExcelType.HSSF)) {
            // x range 0-1023 y range 0-255
            anchor = new HSSFClientAnchor(10, 10, 1010, 245, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex()),
                    cell.getRow().getRowNum());
        } else {
            anchor = new XSSFClientAnchor(10 * Units.EMU_PER_POINT, 10 * Units.EMU_PER_POINT, 1010 * Units.EMU_PER_POINT, 245 * Units.EMU_PER_POINT, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex()),
                    cell.getRow().getRowNum());
        }
        if (StringUtils.isNotEmpty(imagePath)) {
            data = ImageCache.getImage(imagePath);
        }
        if (data != null) {
            PoiExcelGraphDataUtil.getDrawingPatriarch(cell.getSheet()).createPicture(anchor,
                    cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
        }

    }

    /**
     * 图片类型的Cell
     */
    public void createImageCell(Cell cell, double height, int rowspan, int colspan,
                                String imagePath, byte[] data) throws Exception {
        if (height > cell.getRow().getHeight()) {
            cell.getRow().setHeight((short) height);
        }
        ClientAnchor anchor;
        if (type.equals(ExcelType.HSSF)) {
            anchor = new HSSFClientAnchor(10, 10, 1010, 245, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + colspan - 1),
                    cell.getRow().getRowNum() + rowspan - 1);
        } else {
            anchor = new XSSFClientAnchor(10, 10, 1010, 245, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + colspan - 1),
                    cell.getRow().getRowNum() + rowspan - 1);
        }
        if (StringUtils.isNotEmpty(imagePath)) {
            data = ImageCache.getImage(imagePath);
        }
        if (data != null) {
            PoiExcelGraphDataUtil.getDrawingPatriarch(cell.getSheet()).createPicture(anchor,
                    cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
        }

    }

    private int createIndexCell(Row row, int index, ExcelExportEntity excelExportEntity) {
        if (excelExportEntity.getFormat() != null && excelExportEntity.getFormat().equals(PoiBaseConstants.IS_ADD_INDEX)) {
            createStringCell(row, 0, currentIndex + "",
                    index % 2 == 0 ? getStyles(false, null) : getStyles(true, null), null);
            currentIndex = currentIndex + 1;
            return 1;
        }
        return 0;
    }

    /**
     * 创建List之后的各个Cells
     */
    public void createListCells(Drawing patriarch, int index, int cellNum, Object obj,
                                List<ExcelExportEntity> excelParams, Sheet sheet,
                                Workbook workbook, short rowHeight) throws Exception {
        ExcelExportEntity entity;
        Row row;
        if (sheet.getRow(index) == null) {
            row = sheet.createRow(index);
            if (rowHeight != -1) {
                row.setHeight(rowHeight);
            }
        } else {
            row = sheet.getRow(index);
            if (rowHeight != -1) {
                row.setHeight(rowHeight);
            }
        }
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            Object value = getCellValue(entity, obj);
            if (entity.getType() == BaseTypeConstants.STRING_TYPE) {
                createStringCell(row, cellNum++, value == null ? "" : value.toString(),
                        row.getRowNum() % 2 == 0 ? getStyles(false, entity) : getStyles(true, entity),
                        entity);
                if (entity.isHyperlink()) {
                    row.getCell(cellNum - 1)
                            .setHyperlink(dataHandler.getHyperlink(
                                    row.getSheet().getWorkbook().getCreationHelper(), obj, entity.getName(),
                                    value));
                }
            } else if (entity.getType() == BaseTypeConstants.DOUBLE_TYPE) {
                createDoubleCell(row, cellNum++, value == null ? "" : value.toString(),
                        index % 2 == 0 ? getStyles(false, entity) : getStyles(true, entity), entity);
                if (entity.isHyperlink()) {
                    row.getCell(cellNum - 1)
                            .setHyperlink(dataHandler.getHyperlink(
                                    row.getSheet().getWorkbook().getCreationHelper(), obj, entity.getName(),
                                    value));
                }
            } else {
                createImageCell(patriarch, entity, row, cellNum++,
                        value == null ? "" : value.toString(), obj);
            }
        }
    }

    /**
     * 创建文本类型的Cell
     */
    public void createStringCell(Row row, int index, String text, CellStyle style,
                                 ExcelExportEntity entity) {
        Cell cell = row.createCell(index);
        if (style != null && style.getDataFormat() > 0 && style.getDataFormat() < 12) {
            cell.setCellValue(Double.parseDouble(text));
        } else {
            RichTextString rtext;
            if (cell instanceof HSSFCell) {
                rtext = new HSSFRichTextString(text);
            } else {
                rtext = new XSSFRichTextString(text);
            }
            cell.setCellValue(rtext);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
        createCellComment(row, cell, text, entity);
        addStatisticsData(index, text, entity);
    }

    /**
     * 创建数字类型的Cell
     */
    public void createDoubleCell(Row row, int index, String text, CellStyle style,
                                 ExcelExportEntity entity) {
        Cell cell = row.createCell(index);
        if (text != null && text.length() > 0) {
            try {
                cell.setCellValue(Double.parseDouble(text));
            } catch (NumberFormatException e) {
                cell.setCellValue(text);
            }
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
        createCellComment(row, cell, text, entity);
        addStatisticsData(index, text, entity);
    }

    /**
     * 创建批注
     *
     * @param row
     * @param cell
     * @param text
     * @param entity
     */
    private void createCellComment(Row row, Cell cell, String text, ExcelExportEntity entity) {
        if (commentHandler != null) {
            String comment = entity == null || entity.getName().equals(text)
                    || (entity.getGroupName() != null && entity.getGroupName().equals(text)) ?
                    commentHandler.getComment(text) : commentHandler.getComment(entity.getName(), text);
            if (StringUtils.isNotBlank(comment)) {
                cell.setCellComment(getComment(cell, comment, commentHandler.getAuthor()));
            }
        }
    }

    /**
     * 获取注释对象
     *
     * @param cell
     * @param commentText
     * @param author
     * @return
     */
    private Comment getComment(Cell cell, String commentText, String author) {
        Comment comment = null;
        if (cell instanceof HSSFCell) {
            //前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
            comment = cell.getSheet().createDrawingPatriarch().createCellComment(
                    new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 2, (short) 5,
                            commentText.length() / 15 + 2));
            comment.setString(new HSSFRichTextString(commentText));
        } else {
            comment = cell.getSheet().createDrawingPatriarch().createCellComment(
                    new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 2, (short) 5,
                            commentText.length() / 15 + 2));
            comment.setString(new XSSFRichTextString(commentText));
        }
        if (StringUtils.isNotBlank(author)) {
            comment.setAuthor(author);
        }
        return comment;
    }

    /**
     * 创建统计行
     */
    public void addStatisticsRow(CellStyle styles, Sheet sheet) {
        if (statistics.size() > 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("add statistics data ,size is {}", statistics.size());
            }
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Set<Integer> keys = statistics.keySet();
            createStringCell(row, 0, "合计", styles, null);
            for (Integer key : keys) {
                createStringCell(row, key, DOUBLE_FORMAT.format(statistics.get(key)), styles, null);
            }
            statistics.clear();
        }

    }

    /**
     * 合计统计信息
     */
    private void addStatisticsData(Integer index, String text, ExcelExportEntity entity) {
        if (entity != null && entity.isStatistics()) {
            Double temp = 0D;
            if (!statistics.containsKey(index)) {
                statistics.put(index, temp);
            }
            try {
                temp = Double.valueOf(text);
            } catch (NumberFormatException e) {
            }
            statistics.put(index, statistics.get(index) + temp);
        }
    }

    /**
     * 获取图片类型,设置图片插入类型
     *
     * @author hankun 2013年11月25日
     */
    public int getImageType(byte[] value) {
        String type = PoiPublicUtil.getFileExtendName(value);
        if ("JPG".equalsIgnoreCase(type)) {
            return Workbook.PICTURE_TYPE_JPEG;
        } else if ("PNG".equalsIgnoreCase(type)) {
            return Workbook.PICTURE_TYPE_PNG;
        }

        return Workbook.PICTURE_TYPE_JPEG;
    }

    private Map<Integer, int[]> getMergeDataMap(List<ExcelExportEntity> excelParams) {
        Map<Integer, int[]> mergeMap = new HashMap<Integer, int[]>();
        // 设置参数顺序,为之后合并单元格做准备
        int i = 0;
        for (ExcelExportEntity entity : excelParams) {
            if (entity.isMergeVertical()) {
                mergeMap.put(i, entity.getMergeRely());
            }
            if (entity.getList() != null) {
                for (ExcelExportEntity inner : entity.getList()) {
                    if (inner.isMergeVertical()) {
                        mergeMap.put(i, inner.getMergeRely());
                    }
                    i++;
                }
            } else {
                i++;
            }
        }
        return mergeMap;
    }

    /**
     * 获取样式
     */
    public CellStyle getStyles(boolean needOne, ExcelExportEntity entity) {
        return excelExportStyler.getStyles(needOne, entity);
    }

    /**
     * 合并单元格
     */
    public void mergeCells(Sheet sheet, List<ExcelExportEntity> excelParams, int titleHeight) {
        Map<Integer, int[]> mergeMap = getMergeDataMap(excelParams);
        PoiMergeCellUtil.mergeCells(sheet, mergeMap, titleHeight);
    }

    public void setCellWith(List<ExcelExportEntity> excelParams, Sheet sheet) {
        int index = 0;
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).getList() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getList();
                for (int j = 0; j < list.size(); j++) {
                    sheet.setColumnWidth(index, (int) (256 * list.get(j).getWidth()));
                    index++;
                }
            } else {
                sheet.setColumnWidth(index, (int) (256 * excelParams.get(i).getWidth()));
                index++;
            }
        }
    }


    public void setColumnHidden(List<ExcelExportEntity> excelParams, Sheet sheet) {
        int index = 0;
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).getList() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getList();
                for (int j = 0; j < list.size(); j++) {
                    sheet.setColumnHidden(index, list.get(j).isColumnHidden());
                    index++;
                }
            } else {
                sheet.setColumnHidden(index, excelParams.get(i).isColumnHidden());
                index++;
            }
        }
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setExcelExportStyler(IExcelExportStyler excelExportStyler) {
        this.excelExportStyler = excelExportStyler;
    }

    public IExcelExportStyler getExcelExportStyler() {
        return excelExportStyler;
    }

}
