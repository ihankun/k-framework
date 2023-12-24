package io.ihankun.framework.poi.pdf.watermark;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author hankun
 */
public class WatermarkScanner extends PDFStreamEngine {
    Logger logger = LoggerFactory.getLogger(WatermarkScanner.class);

    IWatermarkProcessor remover;
    int pageStartIndex;
    int pageLength;

    public WatermarkScanner(IWatermarkProcessor remover, int pageStartIndex, int pageLength) {
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
        this.remover = remover;
        this.pageStartIndex = pageStartIndex;
        this.pageLength = pageLength;
    }

    /**
     * 开始扫描，检查所有水印
     */
    public void run() {
        try {
            for (int i = pageStartIndex; i < pageStartIndex + pageLength; i++) {
                if (i >= remover.getDocument().getNumberOfPages()) {
                    break;
                }
                processPage(remover.getDocument().getPage(i));
            }
        } catch (Exception e) {
            logger.error("【扫描页面水印出错】", e);
        }
    }

    /**
     * 处理读取的每一个点位
     */
    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        if ("Tj".equals(operation)) {
            COSString textObj = (COSString) operands.get(0);
            String string = textObj.getString();
            // 检查是否是倾斜的水印
            Matrix matrix = getTextLineMatrix();

            if (matrix != null && matrix.getScaleY() != 0 && matrix.getScaleY() != 1 && matrix.getShearY() != 0) {
                if (!remover.isWatermarkWord(string)) {
                    remover.addWatermarkWord(string);
                }
            }
        } else {
            // 此代码必须，必须对else进行处理
            super.processOperator(operator, operands);
        }
    }
}
