package io.hankun.framework.poi.pdf.entity;

import io.hankun.framework.poi.excel.entity.ImportParams;
import io.hankun.framework.poi.pdf.handler.IPdfCellHandler;
import lombok.Data;

/**
 * @author hankun
 */
@Data
public class PdfImportParams extends ImportParams {

    private IPdfCellHandler cellHandler;
}
