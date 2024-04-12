package io.ihankun.framework.poi.pdf;

import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.pdf.entity.PdfExportParams;
import io.ihankun.framework.poi.pdf.export.PdfExportServer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * PDF 导出工具类
 *
 * @author hankun
 */
public class PdfExportUtil {

    /**
     * 根据注解导出数据
     *
     * @param entity    表格标题属性
     * @param pojoClass PDF对象Class
     * @param dataSet   PDF对象数据List
     */
    public static PDDocument exportPdf(PdfExportParams entity, Class<?> pojoClass,
                                       Collection<?> dataSet, OutputStream outStream) {
        return new PdfExportServer(entity).createPdf(entity, pojoClass, dataSet);
    }

    /**
     * 根据Map创建对应的PDF
     *
     * @param entity     表格标题属性
     * @param entityList PDF对象Class
     * @param dataSet    PDF对象数据List
     */
    public static PDDocument exportPdf(PdfExportParams entity, List<ExcelExportEntity> entityList,
                                       Collection<? extends Map<?, ?>> dataSet) {

        return new PdfExportServer(entity).createPdfByExportEntity(entity, entityList,
                dataSet);
    }
}
