package io.ihankun.framework.poi.pdf.fop.doc.component.text;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf模板-文本扩展参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateTextExtendParam extends XEasyPdfTemplateTextBaseParam {

    /**
     * 文本组件列表
     */
    private List<XEasyPdfTemplateText> textList = new ArrayList<>(10);
    /**
     * 文本间隔
     */
    private String textSpacing;
}
