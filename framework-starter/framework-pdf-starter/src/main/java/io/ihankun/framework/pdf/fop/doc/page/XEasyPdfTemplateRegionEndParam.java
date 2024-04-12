package io.ihankun.framework.pdf.fop.doc.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf模板-右侧栏参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateRegionEndParam extends XEasyPdfTemplateRegionBaseParam {

    /**
     * 宽度
     */
    private String width;
}
