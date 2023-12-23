/**
 * Copyright 2013-2023 JueYue (qrb.jueyue@foxmail.com)
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
 * @author JueYue
 * 2023年7月19日 下午8:14:01
 * @version 1.0
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
