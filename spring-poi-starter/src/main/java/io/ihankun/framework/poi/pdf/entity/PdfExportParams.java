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
package io.ihankun.framework.poi.pdf.entity;

import io.ihankun.framework.poi.excel.entity.ExcelBaseParams;
import io.ihankun.framework.poi.pdf.styler.IPdfExportStyler;
import lombok.Data;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * PDF 导出参数设置
 *
 * @author JueYue
 * 2016年1月8日 下午1:52:06
 */
@Data
public class PdfExportParams extends ExcelBaseParams {

    /**
     * 表格名称
     */
    private String title;

    /**
     * 表格名称
     */
    private short titleHeight = 30;

    /**
     * 第二行名称
     */
    private String secondTitle;

    /**
     * 表格名称
     */
    private short secondTitleHeight = 25;
    /**
     * 过滤的属性
     */
    private String[] exclusions;
    /**
     * 是否添加需要需要
     */
    private boolean addIndex;
    /**
     * 是否添加需要需要
     */
    private String indexName = "序号";

    private PDRectangle pageSize = PDRectangle.A4;

    private IPdfExportStyler styler;

    public PdfExportParams() {

    }

    public PdfExportParams(String title) {
        this.title = title;
    }

    public PdfExportParams(String title, String secondTitle) {
        this.title = title;
        this.secondTitle = secondTitle;
    }

}
