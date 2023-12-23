package io.ihankun.framework.poi.pdf.fop.doc;

import lombok.SneakyThrows;
import org.w3c.dom.Document;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * pdf模板-文档组件
 *
 * @author hankun
 */
public interface XEasyPdfTemplateDocumentComponent {

    /**
     * 转换
     *
     * @param outputPath 输出路径
     */
    @SneakyThrows
    default void transform(String outputPath) {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(outputPath))) {
            this.transform(outputStream);
        }
    }

    /**
     * 转换
     *
     * @param outputStream 输出流
     */
    void transform(OutputStream outputStream);

    /**
     * 获取xsl-fo文档
     *
     * @return 返回xsl-fo文档
     */
    Document getDocument();
}
