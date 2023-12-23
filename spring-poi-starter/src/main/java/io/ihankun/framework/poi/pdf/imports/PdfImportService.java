package io.ihankun.framework.poi.pdf.imports;

import io.ihankun.framework.poi.annotation.ExcelTarget;
import io.ihankun.framework.poi.excel.entity.params.ExcelCollectionParams;
import io.ihankun.framework.poi.excel.entity.params.ExcelImportEntity;
import io.ihankun.framework.poi.excel.entity.result.ExcelImportResult;
import io.ihankun.framework.poi.excel.imports.base.ImportBaseService;
import io.ihankun.framework.poi.pdf.entity.PdfImportParams;
import io.ihankun.framework.poi.util.PoiPublicUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.tabula.*;
import technology.tabula.extractors.ExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel 导入服务
 * 参考 https://blog.csdn.net/gengzhy/article/details/128386973
 *
 * @author JueYue
 * @dete 2023年7月17日
 */
public class PdfImportService extends ImportBaseService {

    private static Logger LOGGER = LoggerFactory.getLogger(PdfImportService.class);
    private PDDocument document;
    private ExtractionAlgorithm algorithm =  new SpreadsheetExtractionAlgorithm();

    /**
     * Excel 导入 field 字段类型 Integer,Long,Double,Date,String,Boolean
     */
    public ExcelImportResult importExcelByIs(InputStream inputstream, Class<?> pojoClass,
                                             PdfImportParams params, boolean needMore) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel import start ,class is {}", pojoClass);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputstream.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        InputStream userIs = new ByteArrayInputStream(baos.toByteArray());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel clone success");
        }
        return importExcelByIs(PDDocument.load(userIs), pojoClass, params, needMore);
    }

    /**
     * Excel 导入 field 字段类型 Integer,Long,Double,Date,String,Boolean
     */
    public ExcelImportResult importExcelByIs(PDDocument pdDocument, Class<?> pojoClass,
                                             PdfImportParams params, boolean needMore) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel import start ,class is {}", pojoClass);
        }
        List result = new ArrayList();
        ExcelImportResult importResult;
        String targetId = null;
        Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
        ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
        if (etarget != null) {
            targetId = etarget.value();
        }
        Map<String, ExcelImportEntity> excelParams = new HashMap<>();
        List<ExcelCollectionParams> excelCollection = new ArrayList<>();
        getAllExcelField("", fileds, excelParams, excelCollection, pojoClass, null, null);
        document = pdDocument;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Document create success");
        }
        importResult = new ExcelImportResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(" start to read excel by is ,startTime is {}", new Date());
        }
        importExcel(result, document, pojoClass, params, excelParams, excelCollection);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(" end to read excel list by sheet ,endTime is {}", new Date());
        }
        if (params.isReadSingleCell()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(" read Key-Value ,endTime is {}", System.currentTimeMillis());
            }
        }

        importResult.setList(result);

        return importResult;
    }

    private Collection importExcel(List result, PDDocument document, Class<?> pojoClass, PdfImportParams params, Map<String, ExcelImportEntity> excelParams, List<ExcelCollectionParams> excelCollection) throws Exception {
        // 读取表格
        List<Map<String, String>> maps = new ArrayList<>();
        PageIterator pi = new ObjectExtractor(document).extract();
        Map<String, String> cellMap;
        int rowNum = 0;// 行计数器
        int pageNum = 0;// 页码计数器
        Map<Integer, String> titlemap = null;
        while (pi.hasNext()) {
            Page page = pi.next();
            if (pageNum++ < 1) {
                // 第一页获取一下表头
                List<Table> tables = (List<Table>) algorithm.extract(page);
                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    if (rows.size() <= params.getHeadRows()) {
                        throw new RuntimeException("标题行不正确");
                    }
                    for (List<RectangularTextContainer> row : rows) {
                        //跳过表头行
                        if (rowNum < params.getTitleRows()) {
                            rowNum++;
                            continue;
                        }
                        // 拿到标题行
                        if (rowNum < params.getTitleRows() + params.getHeadRows()) {
                            rowNum++;
                            titlemap = getTitleMap(row, params, excelCollection, excelParams);
                            continue;
                        }
                        //跳过无用行数
                        if (rowNum < params.getTitleRows() + params.getHeadRows() + params.getStartRows()) {
                            rowNum++;
                            continue;
                        }
                        Object object = PoiPublicUtil.createObject(pojoClass, "");
                        for (int k = 0; k < row.size(); k++) {
                            RectangularTextContainer cell = row.get(k);
                            String cellText;
                            if (params.getCellHandler() != null){
                                cellText = params.getCellHandler().getValue(cell);
                            } else {
                                cellText = cell.getText();
                                cellText = cellText == null ? "" : cellText.trim();
                            }
                            String titleString = (String) titlemap.get(k);
                            if (excelParams.containsKey(titleString)) {
                                setValues(excelParams.get(titleString), object, cellText);
                            }
                        }
                        result.add(object);
                        rowNum++;
                    }
                }
            } else {
                List<Table> tables = (List<Table>) algorithm.extract(page);
                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    for (List<RectangularTextContainer> row : rows) {
                        Object object = PoiPublicUtil.createObject(pojoClass, "");
                        for (int k = 0; k < row.size(); k++) {
                            RectangularTextContainer cell = row.get(k);
                            String cellText = cell.getText();
                            cellText = cellText == null ? "" : cellText.trim();
                            String titleString = (String) titlemap.get(k);
                            if (excelParams.containsKey(titleString)) {
                                setValues(excelParams.get(titleString), object, cellText);
                            }
                        }
                        result.add(object);
                        rowNum++;
                    }
                }
            }
        }
        return result;
    }

    private Map<Integer, String> getTitleMap(List<RectangularTextContainer> row, PdfImportParams params, List<ExcelCollectionParams> excelCollection, Map<String, ExcelImportEntity> excelParams) {
        Map<Integer, String> titlemap = new LinkedHashMap<Integer, String>();
        String collectionName = null;
        for (int k = 0; k < row.size(); k++) {
            RectangularTextContainer cell = row.get(k);
            String cellText = cell.getText();
            String value = cellText == null ? "" : cellText.trim();
            value = value.replace("\n", "");
            titlemap.put(k, value);
        }
        // 处理指定列的情况
        Set<String> keys = excelParams.keySet();
        for (String key : keys) {
            if (key.startsWith("FIXED_")) {
                String[] arr = key.split("_");
                titlemap.put(Integer.parseInt(arr[1]), key);
            }
        }
        return titlemap;
    }

    public void setAlgorithm(ExtractionAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
