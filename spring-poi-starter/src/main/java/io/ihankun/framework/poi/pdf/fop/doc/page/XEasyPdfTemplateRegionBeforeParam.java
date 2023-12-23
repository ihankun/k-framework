package io.ihankun.framework.poi.pdf.fop.doc.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf模板-页眉参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateRegionBeforeParam extends XEasyPdfTemplateRegionBaseParam {

    /**
     * 高度
     */
    private String height;
}
