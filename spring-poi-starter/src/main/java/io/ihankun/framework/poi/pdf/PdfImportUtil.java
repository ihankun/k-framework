package io.ihankun.framework.poi.pdf;

import io.ihankun.framework.poi.pdf.entity.PdfImportParams;
import io.ihankun.framework.poi.pdf.imports.PdfImportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.extractors.ExtractionAlgorithm;

import java.io.InputStream;
import java.util.List;

/**
 * PDF 导入工具类
 *
 * @author hankun
 */
public class PdfImportUtil {

    /**
     * PDF的Excel导入 数据源IO流,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param inputstream
     * @param pojoClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(InputStream inputstream, Class<?> pojoClass,
                                          PdfImportParams params) throws Exception {
        return new PdfImportService().importExcelByIs(inputstream, pojoClass, params, false).getList();
    }

    /**
     * PDF的Excel导入 数据源PDDocument,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param document
     * @param pojoClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(PDDocument document, Class<?> pojoClass,
                                          PdfImportParams params) throws Exception {
        return new PdfImportService().importExcelByIs(document, pojoClass, params, false).getList();
    }

    /**
     * PDF的Excel导入 数据源IO流,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param inputstream
     * @param pojoClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(InputStream inputstream, Class<?> pojoClass,
                                          PdfImportParams params, ExtractionAlgorithm algorithm) throws Exception {
        PdfImportService pdfImportService = new PdfImportService();
        pdfImportService.setAlgorithm(algorithm);
        return pdfImportService.importExcelByIs(inputstream, pojoClass, params, false).getList();
    }

    /**
     * PDF的Excel导入 数据源PDDocument,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param document
     * @param pojoClass
     * @param params
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(PDDocument document, Class<?> pojoClass,
                                          PdfImportParams params, ExtractionAlgorithm algorithm) throws Exception {
        PdfImportService pdfImportService = new PdfImportService();
        pdfImportService.setAlgorithm(algorithm);
        return pdfImportService.importExcelByIs(document, pojoClass, params, false).getList();
    }
}
