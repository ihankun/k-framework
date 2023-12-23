package io.ihankun.framework.poi.pdf.entity;

import io.ihankun.framework.poi.excel.entity.ImportParams;
import io.ihankun.framework.poi.pdf.handler.IPdfCellHandler;
import lombok.Data;

@Data
public class PdfImportParams extends ImportParams {

    private IPdfCellHandler cellHandler;
}
