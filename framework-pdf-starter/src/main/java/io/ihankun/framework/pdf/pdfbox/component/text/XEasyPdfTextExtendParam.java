package io.ihankun.framework.pdf.pdfbox.component.text;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf文本扩展参数
 *
 * @author hankun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
class XEasyPdfTextExtendParam extends XEasyPdfTextScriptParam {

    private static final long serialVersionUID = 724839940655095694L;

    /**
     * 角标文本
     */
    private String scriptText;
}
