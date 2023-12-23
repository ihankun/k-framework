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
package io.ihankun.framework.poi.pdf.styler;

import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;

/**
 * 默认的PDFstyler 实现
 *
 * @author JueYue
 * 2016年1月8日 下午2:06:26
 */
public class PdfExportStylerDefaultImpl implements IPdfExportStyler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfExportStylerDefaultImpl.class);

    @Override
    public void setCellStyler(TextCell.TextCellBuilder cell, ExcelExportEntity entity, String text) {
        cell.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.BOTTOM);
        cell.borderColor(Color.BLACK).borderStyle(BorderStyle.SOLID).borderWidth(1);
    }

    @Override
    public PDFont getFont(ExcelExportEntity entity, String text) {
        try {
            //用以支持中文
            //"STSong-Light", "UniGB-UCS2-H"
            return PDFontFactory.createFont(new COSDictionary());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public PDFont getFont() {
        return getFont(null, null);
    }

}
