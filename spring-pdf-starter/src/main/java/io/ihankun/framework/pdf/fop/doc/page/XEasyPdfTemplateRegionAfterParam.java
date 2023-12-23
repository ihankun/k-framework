package io.ihankun.framework.pdf.fop.doc.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf模板-页脚参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateRegionAfterParam extends XEasyPdfTemplateRegionBaseParam {

    /**
     * 高度
     */
    private String height;
}
