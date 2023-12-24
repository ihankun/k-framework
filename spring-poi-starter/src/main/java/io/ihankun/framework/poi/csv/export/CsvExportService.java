package io.ihankun.framework.poi.csv.export;

import io.ihankun.framework.poi.csv.entity.CsvExportParams;
import io.ihankun.framework.poi.entity.BaseTypeConstants;
import io.ihankun.framework.poi.annotation.ExcelTarget;
import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.excel.export.base.BaseExportService;
import io.ihankun.framework.poi.exception.excel.ExcelExportException;
import io.ihankun.framework.poi.exception.excel.enums.ExcelExportEnum;
import io.ihankun.framework.poi.handler.inter.IWriter;
import io.ihankun.framework.poi.util.PoiPublicUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author hankun
 */
public class CsvExportService extends BaseExportService implements IWriter<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvExportService.class);

    private CsvExportParams         params;
    private List<ExcelExportEntity> excelParams;
    private Writer                  writer;

    /**
     * 导出Csv类文件
     *
     * @param outputStream 输出流
     * @param params       输出参数
     * @param pojoClass    输出类
     */
    public CsvExportService(OutputStream outputStream, CsvExportParams params, Class<?> pojoClass) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("CSV export start ,class is {}", pojoClass);
        }
        if (params == null || pojoClass == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            // 得到所有字段
            Field[]     fields   = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget  = pojoClass.getAnnotation(ExcelTarget.class);
            String      targetId = etarget == null ? null : etarget.value();
            getAllExcelField(params.getExclusions(), targetId, fields, excelParams, pojoClass,
                    null, null);
            createCsvOfList(outputStream, params, excelParams);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    /**
     * @param outputStream 输出流
     * @param params       输出参数
     * @param excelParams  列参数
     */
    public CsvExportService(OutputStream outputStream, CsvExportParams params, List<ExcelExportEntity> excelParams) {
        createCsvOfList(outputStream, params, excelParams);
    }

    /**
     * @param outputStream 输出流
     * @param params       输出参数
     * @param excelParams  列参数
     */
    private CsvExportService createCsvOfList(OutputStream outputStream, CsvExportParams params, List<ExcelExportEntity> excelParams) {
        try {

            Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, params.getEncoding()));
            this.params = params;
            this.excelParams = excelParams;
            this.writer = writer;
            dataHandler = params.getDataHandler();
            if (dataHandler != null && dataHandler.getNeedHandlerFields() != null) {
                needHandlerList = Arrays.asList(dataHandler.getNeedHandlerFields());
            }
            dictHandler = params.getDictHandler();
            i18nHandler = params.getI18nHandler();
            sortAllParams(excelParams);
            if (params.isCreateHeadRows()) {
                writer.write(createHeaderRow(params, excelParams));
            }
            return this;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    /**
     * 创建行
     *
     * @param excelParams
     * @param params
     * @param t
     * @return
     */
    private String createRow(List<ExcelExportEntity> excelParams, CsvExportParams params, Object t) {
        StringBuilder sb = new StringBuilder();
        try {
            ExcelExportEntity entity;
            for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
                entity = excelParams.get(k);
                Object value = getCellValue(entity, t);
                if (entity.getType() == BaseTypeConstants.STRING_TYPE) {
                    sb.append(params.getTextMark());
                    sb.append(value.toString());
                    sb.append(params.getTextMark());
                } else if (entity.getType() == BaseTypeConstants.DOUBLE_TYPE) {
                    sb.append(value.toString());
                }
                if (k < paramSize - 1) {
                    sb.append(params.getSpiltMark());
                }
            }
            return sb.append(getLineMark()).toString();
        } catch (Exception e) {
            LOGGER.error("csv export error ,data is :{}", ReflectionToStringBuilder.toString(t));
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }


    /**
     * 创建表头
     */
    private String createHeaderRow(CsvExportParams params, List<ExcelExportEntity> excelParams) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            sb.append(entity.getName());
            if (i < exportFieldTitleSize - 1) {
                sb.append(params.getSpiltMark());
            }
        }
        return sb.append(getLineMark()).toString();

    }

    private String getLineMark() {
        return "\n";
    }


    @Override
    public IWriter write(Collection data) {
        try {
            Iterator<?> iterator = data.iterator();
            String      line     = null;
            int         i        = 0;
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                line = createRow(excelParams, params, obj);
                writer.write(line);
                if (i % 10000 == 0) {
                    writer.flush();
                }
                i++;
            }
            writer.flush();
            return this;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    @Override
    public Void close() {
        IOUtils.closeQuietly(this.writer);
        return null;
    }
}
