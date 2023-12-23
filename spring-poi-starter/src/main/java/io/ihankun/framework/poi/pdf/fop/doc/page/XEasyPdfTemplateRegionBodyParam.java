package io.ihankun.framework.poi.pdf.fop.doc.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf模板-页面区域主体参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateRegionBodyParam extends XEasyPdfTemplateRegionBaseParam {

    /**
     * 上边距
     */
    private String marginTop;
    /**
     * 下边距
     */
    private String marginBottom;
    /**
     * 左边距
     */
    private String marginLeft;
    /**
     * 右边距
     */
    private String marginRight;
}
