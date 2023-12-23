package io.ihankun.framework.poi.pdf.watermark;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfWatermarkUtil {

    public static void remover(PDDocument document) throws Exception {
        WatermarkProcessor processor = new WatermarkProcessor();
        processor.init(document);
        processor.removeWatermark(document);

    }
}
