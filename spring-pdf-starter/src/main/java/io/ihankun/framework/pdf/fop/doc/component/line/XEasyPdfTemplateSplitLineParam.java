package io.ihankun.framework.pdf.fop.doc.component.line;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponentParam;

/**
 * pdf模板-分割线参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateSplitLineParam extends XEasyPdfTemplateComponentParam {

    /**
     * 长度
     */
    private String length;
    /**
     * 样式
     * <p>none：无</p>
     * <p>dotted：点线</p>
     * <p>dashed：虚线</p>
     * <p>solid：实线</p>
     * <p>double：双实线</p>
     * <p>groove：槽线</p>
     * <p>ridge：脊线</p>
     */
    private String style;
}
