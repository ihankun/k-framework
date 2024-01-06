package io.ihankun.framework.pdf.fop.doc.component.text;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponentParam;

/**
 * pdf模板-文本基础参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateTextBaseParam extends XEasyPdfTemplateComponentParam {

    /**
     * 内部地址
     * <p>注：标签id</p>
     */
    private String linkInternalDestination;
    /**
     * 外部地址
     * <p>注：url</p>
     */
    private String linkExternalDestination;
    /**
     * 删除线颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    protected String deleteLineColor;
    /**
     * 下划线宽度
     */
    protected String underLineWidth;
    /**
     * 下划线颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    protected String underLineColor;
    /**
     * 是否包含删除线
     */
    protected Boolean hasDeleteLine = Boolean.FALSE;
    /**
     * 是否包含下划线
     */
    protected Boolean hasUnderLine = Boolean.FALSE;
    /**
     * 是否包含超链接
     */
    protected Boolean hasLink = Boolean.FALSE;
}
