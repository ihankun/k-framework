package io.ihankun.framework.pdf.fop.doc.component.link;

import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponentParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * pdf模板-超链接参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateLinkParam extends XEasyPdfTemplateComponentParam {

    /**
     * pdf模板组件
     */
    private XEasyPdfTemplateComponent component;

    /**
     * 内部地址
     * <p>注：标签id</p>
     */
    private String internalDestination;
    /**
     * 外部地址
     * <p>注：url</p>
     */
    private String externalDestination;
}
