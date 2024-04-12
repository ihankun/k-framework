package io.ihankun.framework.pdf.fop.doc.component.image;

import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponentParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf模板-图像参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateImageParam extends XEasyPdfTemplateComponentParam {
    /**
     * 宽度
     */
    private String width;
    /**
     * 高度
     */
    private String height;
    /**
     * 图像路径
     */
    private String path;
}
