package io.ihankun.framework.poi.pdf.watermark;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WatermarkRemover {
    Logger logger = LoggerFactory.getLogger(WatermarkRemover.class);

    IWatermarkProcessor remover;
    List<RemoveResult> pageTokens = new ArrayList<>();
    List<String> watermarks = null;
    int pageStartIndex;
    int pageLength;

    public WatermarkRemover(IWatermarkProcessor remover, int pageStartIndex, int pageLength, List<String> watermarks) {
        this.remover = remover;
        this.pageStartIndex = pageStartIndex;
        this.pageLength = pageLength;
        this.watermarks = watermarks;
    }

    public void removeWatermark() {
        for (int i = pageStartIndex; i < pageStartIndex + pageLength; i++) {
            if (i >= remover.getDocument().getNumberOfPages()) {
                break;
            }
            try {
                processPage(i, remover.getDocument().getPage(i));
            } catch (Exception e) {
                logger.error("【解析PDF页面失败】", e);
            }
        }
    }

    public void processPage(int index, PDPage page) throws Exception {
        Object next;
        Operator op;

        PDFStreamParser parser = new PDFStreamParser(page);
        parser.parse();
        List<?> tokens = parser.getTokens();
        if (Objects.nonNull(tokens)) {
            for (int j = 0; j < tokens.size(); j++) {
                next = tokens.get(j);
                if (Objects.isNull(next))
                    continue;

                if (next instanceof Operator) {
                    op = (Operator) next;

                    if (op.getName().equals("Tj")) {
                        COSString previous = (COSString) tokens.get(j - 1);
                        String string = previous.getString();
                        // 判断是否是水印
                        if (null != watermarks && watermarks.contains(string)) {
                            previous.setValue("".getBytes("GBK"));
                        } else if (remover.isWatermarkWord(string)) {
                            // 判断是否是水印
                            previous.setValue("".getBytes("GBK"));
                        }
                    }
                }
            }
        }

        RemoveResult pageResult = new RemoveResult(page, index, tokens);
        pageTokens.add(pageResult);
    }

    public List<RemoveResult> getPageTokens() {
        return pageTokens;
    }
}
