package io.ihankun.framework.pdf.fop.datasource;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import io.ihankun.framework.pdf.fop.doc.XEasyPdfTemplateDocumentComponent;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;

/**
 * pdf模板-document数据源
 *
 * @author hankun
 */
@Setter
@Accessors(chain = true)
public class XEasyPdfTemplateDocumentDataSource extends XEasyPdfTemplateAbstractDataSource {

    /**
     * 文档组件
     */
    private XEasyPdfTemplateDocumentComponent document;

    /**
     * 无参构造
     */
    public XEasyPdfTemplateDocumentDataSource() {
        // 初始化模板数据
        this.templateData = Collections.singletonMap("", "");
    }

    /**
     * 处理模板
     *
     * @return 返回模板输入流
     */
    @SneakyThrows
    @Override
    protected InputStream processTemplate() {
        // 如果模板数据不为空，则处理模板
        if (this.document == null) {
            // 提示错误信息
            throw new IllegalArgumentException("the document can not be null");
        }
        // 创建输出流
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192)) {
            // 转换文档（document转为xsl-fo）
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(this.document.getDocument()), new StreamResult(outputStream));
            // 返回输入流
            return new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        }
    }
}
