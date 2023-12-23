/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ihankun.framework.poi.pdf.export;

import io.ihankun.framework.poi.annotation.ExcelTarget;
import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.excel.export.base.ExportCommonService;
import io.ihankun.framework.poi.pdf.entity.PdfExportParams;
import io.ihankun.framework.poi.pdf.styler.IPdfExportStyler;
import io.ihankun.framework.poi.pdf.styler.PdfExportStylerDefaultImpl;
import io.ihankun.framework.poi.util.PoiPublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * PDF导出服务,基于Excel基础的导出
 *
 * @author JueYue
 * 2015年10月6日 下午8:21:08
 */
public class PdfExportServer extends ExportCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfExportServer.class);

    private PDDocument document;
    private IPdfExportStyler styler = new PdfExportStylerDefaultImpl();

    private boolean isListData = false;

    public PdfExportServer(PdfExportParams entity) {
        try {
            styler = entity.getStyler() == null ? styler : entity.getStyler();
            document = new PDDocument();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public PdfExportServer() {

    }

    /**
     * 创建Pdf的表格数据
     *
     * @param entity
     * @param pojoClass
     * @param dataSet
     * @return
     */
    public PDDocument createPdf(PdfExportParams entity, Class<?> pojoClass, Collection<?> dataSet) {
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            if (entity.isAddIndex()) {
                //excelParams.add(indexExcelEntity(entity));
            }
            // 得到所有字段
            Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = etarget == null ? null : etarget.value();
            getAllExcelField(entity.getExclusions(), targetId, fileds, excelParams, pojoClass,
                    null, null);
            createPdfByExportEntity(entity, excelParams, dataSet);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return document;
    }

    public PDDocument createPdfByExportEntity(PdfExportParams entity,
                                              List<ExcelExportEntity> excelParams,
                                              Collection<?> dataSet) {
        try {
            sortAllParams(excelParams);
            for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
                if (excelParams.get(k).getList() != null) {
                    isListData = true;
                    break;
                }
            }

            PDPage page = new PDPage(entity.getPageSize());
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDFont font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\simfang.ttf"));
            contentStream.setFont(font, 12);
            //设置表头PDType0Font.load(doc, new File("C:\\Windows\\Fonts\\simfang.TTF"))
            Table.TableBuilder table = Table.builder().font(font);
            //设置各个列的宽度
            float[] width = getCellWidths(table, excelParams);
            createHeaderAndTitle(table, entity, excelParams, width.length);
            int rowHeight = getRowHeight(excelParams) / 50;
            Iterator<?> its = dataSet.iterator();
            while (its.hasNext()) {
                Object t = its.next();
                createCells(table, t, excelParams, rowHeight);
            }
            TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(20f)
                    .startY(page.getMediaBox().getUpperRightY() - 20f)
                    .table(table.build())
                    .build().draw();
            contentStream.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return document;
    }

    private void createCells(Table.TableBuilder table, Object t, List<ExcelExportEntity> excelParams,
                             int rowHeight) throws Exception {
        ExcelExportEntity entity;
        int maxHeight = getThisMaxHeight(t, excelParams);
        Row.RowBuilder row = Row.builder();
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            if (entity.getList() != null) {
                Collection<?> list = getListCellValue(entity, t);
                int i = 0;
                for (Object obj : list) {
                    if (i == 0) {
                        createListCells(row, obj, entity.getList(), rowHeight);
                    } else {
                        Row.RowBuilder listRow = Row.builder();
                        createListCells(listRow, obj, entity.getList(), rowHeight);
                        table.addRow(listRow.build());
                    }
                }
            } else {
                Object value = getCellValue(entity, t);
                if (entity.getType() == 1) {
                    createStringCell(row, value == null ? "" : value.toString(), entity,
                            rowHeight, 1, maxHeight);
                } else {
                    // 事后支持 createImageCell(table, value == null ? "" : value.toString(), entity, rowHeight,1, maxHeight);
                }
            }
        }
        table.addRow(row.build());
    }

    /**
     * 创建集合对象
     *
     * @param row
     * @param obj
     * @param rowHeight
     * @param excelParams
     * @throws Exception
     */
    private void createListCells(Row.RowBuilder row, Object obj, List<ExcelExportEntity> excelParams,
                                 int rowHeight) throws Exception {
        ExcelExportEntity entity;
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            Object value = getCellValue(entity, obj);
            if (entity.getType() == 1) {
                createStringCell(row, value == null ? "" : value.toString(), entity, rowHeight);
            } else {
                // TODO 事后支持图片
                // createImageCell(table, value == null ? "" : value.toString(), entity, rowHeight, 1,1);
            }
        }
    }

    /**
     * 获取这一列的高度
     *
     * @param t           对象
     * @param excelParams 属性列表
     * @return
     * @throws Exception 通过反射过去值得异常
     */
    private int getThisMaxHeight(Object t, List<ExcelExportEntity> excelParams) throws Exception {
        if (isListData) {
            ExcelExportEntity entity;
            int maxHeight = 1;
            for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
                entity = excelParams.get(k);
                if (entity.getList() != null) {
                    Collection<?> list = getListCellValue(entity, t);
                    maxHeight = (list == null || maxHeight > list.size()) ? maxHeight : list.size();
                }
            }
            return maxHeight;
        }
        return 1;
    }

    /**
     * 获取Cells的宽度数组
     *
     * @param excelParams
     * @return
     */
    private float[] getCellWidths(Table.TableBuilder table, List<ExcelExportEntity> excelParams) {
        List<Float> widths = new ArrayList<Float>();
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).getList() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getList();
                for (int j = 0; j < list.size(); j++) {
                    widths.add((float) (8 * list.get(j).getWidth()));
                }
            } else {
                widths.add((float) (8 * excelParams.get(i).getWidth()));
            }
        }
        float[] widthArr = new float[widths.size()];
        for (int i = 0; i < widthArr.length; i++) {
            table.addColumnOfWidth(widths.get(i));
        }
        return widthArr;
    }

    private void createHeaderAndTitle(Table.TableBuilder table, PdfExportParams entity,
                                      List<ExcelExportEntity> excelParams, int colspan) {
        if (entity.getTitle() != null) {
            createTitleRow(entity, table, colspan);
        }
        createHeaderRow(entity, table, excelParams);
    }

    /**
     * 创建表头
     *
     * @param title
     * @param table
     */
    private int createHeaderRow(PdfExportParams title, Table.TableBuilder table,
                                List<ExcelExportEntity> excelParams) {
        int rows = getRowNums(excelParams, false);
        Row.RowBuilder row = Row.builder();
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            if (entity.getList() != null) {
                if (StringUtils.isNotBlank(entity.getName())) {
                    createStringCell(row, entity.getName(), entity, 10, entity.getList().size(),
                            1);
                }
                Row.RowBuilder listRow = Row.builder();
                List<ExcelExportEntity> sTitel = entity.getList();
                for (int j = 0, size = sTitel.size(); j < size; j++) {
                    createStringCell(listRow, sTitel.get(j).getName(), sTitel.get(j), 10);
                }
                table.addRow(listRow.build());
            } else {
                createStringCell(row, entity.getName(), entity, 10, 1, rows == 2 ? 2 : 1);
            }
        }
        table.addRow(row.build());
        return rows;

    }

    private void createTitleRow(PdfExportParams entity, Table.TableBuilder table, int colspan) {
        table.addRow(Row.builder().add(
                TextCell.builder().text(entity.getTitle()).verticalAlignment(VerticalAlignment.MIDDLE)
                        .horizontalAlignment(HorizontalAlignment.CENTER).textHeight((float) entity.getTitleHeight())
                        .fontSize(entity.getTitleHeight() - 10).borderColor(Color.BLACK).borderWidth(1).borderStyle(BorderStyle.SOLID)
                        .colSpan(colspan).build()
        ).build());
        if (entity.getSecondTitle() != null) {
            table.addRow(Row.builder().add(
                    TextCell.builder().text(entity.getSecondTitle()).verticalAlignment(VerticalAlignment.MIDDLE)
                            .horizontalAlignment(HorizontalAlignment.CENTER).textHeight((float) entity.getTitleHeight())
                            .fontSize(entity.getTitleHeight() - 10).borderColor(Color.BLACK).borderWidth(1).borderStyle(BorderStyle.SOLID)
                            .colSpan(colspan).build()
            ).build());
        }
    }

    private TextCell.TextCellBuilder createStringCell(Row.RowBuilder row, String text, ExcelExportEntity entity,
                                                      int rowHeight, int colspan, int rowspan) {

        TextCell.TextCellBuilder cell = TextCell.builder().text(text).textHeight((float) (rowHeight * 2.5));
        cell.rowSpan(rowspan).colSpan(colspan);
        styler.setCellStyler(cell, entity, text);
        row.add(cell.build());
        return cell;
    }

    private TextCell.TextCellBuilder createStringCell(Row.RowBuilder row, String text, ExcelExportEntity entity,
                                                      int rowHeight) {
        TextCell.TextCellBuilder cell = TextCell.builder().text(text).textHeight((float) (rowHeight * 2.5));
        styler.setCellStyler(cell, entity, text);
        row.add(cell.build());
        return cell;
    }

}
