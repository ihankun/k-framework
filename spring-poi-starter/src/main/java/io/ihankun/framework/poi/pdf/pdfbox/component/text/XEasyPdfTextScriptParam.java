package io.ihankun.framework.poi.pdf.pdfbox.component.text;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf文本角标参数
 *
 * @author hankun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
class XEasyPdfTextScriptParam extends XEasyPdfTextParam {

    private static final long serialVersionUID = 1221986832133209214L;

    /**
     * 角标类型
     */
    private XEasyPdfTextScriptType scriptType;
}
