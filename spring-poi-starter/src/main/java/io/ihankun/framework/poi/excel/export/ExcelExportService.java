package io.ihankun.framework.poi.excel.export;

import io.ihankun.framework.poi.annotation.ExcelTarget;
import io.ihankun.framework.poi.excel.entity.ExportParams;
import io.ihankun.framework.poi.excel.entity.enmus.ExcelType;
import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.excel.export.base.BaseExportService;
import io.ihankun.framework.poi.excel.export.styler.IExcelExportStyler;
import io.ihankun.framework.poi.exception.excel.ExcelExportException;
import io.ihankun.framework.poi.exception.excel.enums.ExcelExportEnum;
import io.ihankun.framework.poi.util.PoiExcelGraphDataUtil;
import io.ihankun.framework.poi.util.PoiMergeCellUtil;
import io.ihankun.framework.poi.util.PoiPublicUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Excel导出服务
 *
 * @author hankun
 */
public class ExcelExportService extends BaseExportService {

    /**
     * 最大行数,超过自动多Sheet
     */
    private static int MAX_NUM = 60000;

    protected int createHeaderAndTitle(ExportParams entity, Sheet sheet, Workbook workbook,
                                       List<ExcelExportEntity> excelParams) {
        int rows = 0, fieldLength = getFieldLength(excelParams);
        if (entity.getTitle() != null) {
            rows += createTitle2Row(entity, sheet, workbook, fieldLength);
        }
        createHeaderRow(entity, sheet, workbook, rows, excelParams, 0);
        rows += getRowNums(excelParams, true);
        if (entity.isFixedTitle()) {
            sheet.createFreezePane(0, rows, 0, rows);
        }
        return rows;
    }

    /**
     * 创建表头
     */
    private int createHeaderRow(ExportParams title, Sheet sheet, Workbook workbook, int index,
                                List<ExcelExportEntity> excelParams, int cellIndex) {
        Row row  = sheet.getRow(index) == null ? sheet.createRow(index) : sheet.getRow(index);
        int rows = getRowNums(excelParams, true);
        row.setHeight(title.getHeaderHeight());
        Row listRow = null;
        if (rows >= 2) {
            listRow = sheet.getRow(index + 1);
            if (listRow == null) {
                listRow = sheet.createRow(index + 1);
                listRow.setHeight(title.getHeaderHeight());

            }
        }
        int       groupCellLength = 0;
        CellStyle titleStyle      = getExcelExportStyler().getTitleStyle(title.getColor());
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            // 加入换了groupName或者结束就，就把之前的那个换行
            if (StringUtils.isBlank(entity.getGroupName()) || i == 0 || !entity.getGroupName().equals(excelParams.get(i - 1).getGroupName())) {
                if (groupCellLength > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(index, index, cellIndex - groupCellLength, cellIndex - 1));
                }
                groupCellLength = 0;
            }
            if (StringUtils.isNotBlank(entity.getGroupName())) {
                createStringCell(row, cellIndex, entity.getGroupName(), titleStyle, entity);
                createStringCell(listRow, cellIndex, entity.getName(), titleStyle, entity);
                groupCellLength++;
            } else if (StringUtils.isNotBlank(entity.getName())) {
                createStringCell(row, cellIndex, entity.getName(), titleStyle, entity);
            }
            if (entity.getList() != null) {
                // 保持原来的
                int tempCellIndex = cellIndex;
                cellIndex = createHeaderRow(title, sheet, workbook, rows == 1 ? index : index + 1, entity.getList(), cellIndex);
                List<ExcelExportEntity> sTitel = entity.getList();
                if (StringUtils.isNotBlank(entity.getName()) && sTitel.size() > 1) {
                    PoiMergeCellUtil.addMergedRegion(sheet, index, index, tempCellIndex, tempCellIndex + getFieldLength(sTitel));
                }
                /*for (int j = 0, size = sTitel.size(); j < size; j++) {

                    createStringCell(rows == 2 ? listRow : row, cellIndex, sTitel.get(j).getName(),
                            titleStyle, entity);
                    cellIndex++;
                }*/
                cellIndex--;
            } else if (rows > 1 && StringUtils.isBlank(entity.getGroupName())) {
                createStringCell(listRow, cellIndex, "", titleStyle, entity);
                PoiMergeCellUtil.addMergedRegion(sheet, index, index + rows - 1, cellIndex, cellIndex);
            }
            cellIndex++;
        }
        if (groupCellLength > 1) {
            PoiMergeCellUtil.addMergedRegion(sheet, index, index, cellIndex - groupCellLength, cellIndex - 1);
        }
        return cellIndex;

    }

    /**
     * 创建 表头改变
     */
    public int createTitle2Row(ExportParams entity, Sheet sheet, Workbook workbook,
                               int fieldWidth) {

        Row row = sheet.createRow(0);
        row.setHeight(entity.getTitleHeight());
        createStringCell(row, 0, entity.getTitle(),
                getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
        for (int i = 1; i <= fieldWidth; i++) {
            createStringCell(row, i, "",
                    getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
        }
        PoiMergeCellUtil.addMergedRegion(sheet, 0, 0, 0, fieldWidth);
        if (entity.getSecondTitle() != null) {
            row = sheet.createRow(1);
            row.setHeight(entity.getSecondTitleHeight());
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.RIGHT);
            createStringCell(row, 0, entity.getSecondTitle(), style, null);
            for (int i = 1; i <= fieldWidth; i++) {
                createStringCell(row, i, "",
                        getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
            }
            PoiMergeCellUtil.addMergedRegion(sheet, 1, 1, 0, fieldWidth);
            return 2;
        }
        return 1;
    }

    public void createSheet(Workbook workbook, ExportParams entity, Class<?> pojoClass,
                            Collection<?> dataSet) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel export start ,class is {}", pojoClass);
            LOGGER.debug("Excel version is {}",
                    entity.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }
        if (workbook == null || entity == null || pojoClass == null || dataSet == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            i18nHandler = entity.getI18nHandler();
            // 得到所有字段
            Field[]     fileds   = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget  = pojoClass.getAnnotation(ExcelTarget.class);
            String      targetId = etarget == null ? null : etarget.value();
            getAllExcelField(entity.getExclusions(), targetId, fileds, excelParams, pojoClass,
                    null, null);
            //获取所有参数后,后面的逻辑判断就一致了
            createSheetForMap(workbook, entity, excelParams, dataSet);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    public void createSheetForMap(Workbook workbook, ExportParams entity,
                                  List<ExcelExportEntity> entityList, Collection<?> dataSet) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel version is {}",
                    entity.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }
        if (workbook == null || entity == null || entityList == null || dataSet == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        super.type = entity.getType();
        if (type.equals(ExcelType.XSSF)) {
            MAX_NUM = 1000000;
        }
        if (entity.getMaxNum() > 0) {
            MAX_NUM = entity.getMaxNum();
        }
        Sheet sheet = null;
        try {
            sheet = workbook.createSheet(entity.getSheetName());
        } catch (Exception e) {
            // 重复遍历,出现了重名现象,创建非指定的名称Sheet
            sheet = workbook.createSheet();
        }
        if (dataSet.getClass().getClass().getName().contains("Unmodifiable")) {
            List dataTemp = new ArrayList<>();
            dataTemp.addAll(dataSet);
            dataSet = dataTemp;
        }
        insertDataToSheet(workbook, entity, entityList, dataSet, sheet);
        if (entity.isReadonly()) {
            sheet.protectSheet(UUID.randomUUID().toString());
        }
        sheet.setForceFormulaRecalculation(true);
        if (isAutoSize(entity, entityList)) {
            for (int len = Math.max(sheet.getRow(0).getLastCellNum(), sheet.getRow(1).getLastCellNum()), i = 0; i < len; i++) {
                sheet.autoSizeColumn(i, true);
            }
        }
    }

    private boolean isAutoSize(ExportParams entity, List<ExcelExportEntity> entityList) {
        if (entity.isAutoSize()) {
            return true;
        }
        AtomicBoolean autoSize = new AtomicBoolean(true);
        entityList.forEach(e -> {
            if (e.getWidth() != 10) {
                autoSize.set(false);
                return;
            }
            if (CollectionUtils.isNotEmpty(e.getList()) && !isAutoSize(entity, e.getList())) {
                autoSize.set(false);
                return;
            }
        });
        return autoSize.get();
    }

    protected void insertDataToSheet(Workbook workbook, ExportParams entity,
                                     List<ExcelExportEntity> entityList, Collection<?> dataSet,
                                     Sheet sheet) {
        try {
            dataHandler = entity.getDataHandler();
            if (dataHandler != null && dataHandler.getNeedHandlerFields() != null) {
                needHandlerList = Arrays.asList(dataHandler.getNeedHandlerFields());
            }
            dictHandler = entity.getDictHandler();
            commentHandler = entity.getCommentHandler();
            // 创建表格样式
            setExcelExportStyler((IExcelExportStyler) entity.getStyle()
                    .getConstructor(Workbook.class).newInstance(workbook));
            Drawing                 patriarch   = PoiExcelGraphDataUtil.getDrawingPatriarch(sheet);
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            if (entity.isAddIndex()) {
                excelParams.add(indexExcelEntity(entity));
            }
            excelParams.addAll(entityList);
            sortAllParams(excelParams);
            int index = entity.isCreateHeadRows()
                    ? createHeaderAndTitle(entity, sheet, workbook, excelParams) : 0;
            int titleHeight = index;
            setCellWith(excelParams, sheet);
            setColumnHidden(excelParams, sheet);
            short rowHeight = entity.getHeight() != 0 ? entity.getHeight() : getRowHeight(excelParams);
            setCurrentIndex(1);
            createAddressList(sheet, index, excelParams, 0);
            Iterator<?>  its      = dataSet.iterator();
            List<Object> tempList = new ArrayList<Object>();
            while (its.hasNext()) {
                Object t = its.next();
                index += createCells(patriarch, index, t, excelParams, sheet, workbook, rowHeight, 0)[0];
                tempList.add(t);
                if (index >= MAX_NUM) {
                    break;
                }
            }
            if (entity.getFreezeCol() != 0) {
                sheet.createFreezePane(entity.getFreezeCol(), 0, entity.getFreezeCol(), 0);
            }

            mergeCells(sheet, excelParams, titleHeight);

            its = dataSet.iterator();
            for (int i = 0, le = tempList.size(); i < le; i++) {
                its.next();
                its.remove();
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("List data more than max ,data size is {}",
                        dataSet.size());
            }
            // 发现还有剩余list 继续循环创建Sheet
            if (dataSet.size() > 0) {
                createSheetForMap(workbook, entity, entityList, dataSet);
            } else {
                // 创建合计信息
                addStatisticsRow(getExcelExportStyler().getStyles(true, null), sheet);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    /**
     * 插入下拉
     *
     * @param sheet
     * @param index
     * @param excelParams
     */
    private int createAddressList(Sheet sheet, int index, List<ExcelExportEntity> excelParams, int cellIndex) {
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).isAddressList()) {
                ExcelExportEntity entity = excelParams.get(i);
                CellRangeAddressList regions = new CellRangeAddressList(index,
                        this.type.equals(ExcelType.XSSF) ? 1000000 : 65535, cellIndex, cellIndex);
                DataValidation dataValidation = sheet.getDataValidationHelper().createValidation(sheet.getDataValidationHelper().createExplicitListConstraint(getAddressListValues(entity)), regions);
                // 处理Excel兼容性问题
                if (dataValidation instanceof XSSFDataValidation) {
                    dataValidation.setSuppressDropDownArrow(true);
                    dataValidation.setShowErrorBox(true);
                } else {
                    dataValidation.setSuppressDropDownArrow(false);
                }
                sheet.addValidationData(dataValidation);
            }
            if (excelParams.get(i).getList() != null) {
                cellIndex = createAddressList(sheet, index, excelParams.get(i).getList(), cellIndex);
            } else {
                cellIndex++;
            }
        }
        return cellIndex;
    }

    private String[] getAddressListValues(ExcelExportEntity entity) {
        if (StringUtils.isNotEmpty(entity.getDict())) {
            String[] arr = new String[this.dictHandler.getList(entity.getDict()).size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = this.dictHandler.getList(entity.getDict()).get(i).get("dictValue").toString();
            }
            return arr;
        } else if (entity.getReplace() != null && entity.getReplace().length > 0) {
            String[] arr = new String[entity.getReplace().length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = entity.getReplace()[i].split("_")[0];
            }
            return arr;
        }
        throw new ExcelExportException(entity.getName() + "没有可以创建下来的数据,请addressList不要设置为true");
    }


}
