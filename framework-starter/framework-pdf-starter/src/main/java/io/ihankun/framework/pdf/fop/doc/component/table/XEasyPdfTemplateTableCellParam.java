package io.ihankun.framework.pdf.fop.doc.component.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf模板-表格单元格参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateTableCellParam extends XEasyPdfTemplateTableRowParam {

    /**
     * pdf模板组件
     */
    private List<XEasyPdfTemplateComponent> components = new ArrayList<>(10);
    /**
     * 上边框
     */
    private String borderTop;
    /**
     * 上边框样式
     * <p>none：无</p>
     * <p>hidden：隐藏</p>
     * <p>dotted：点虚线</p>
     * <p>dashed：短虚线</p>
     * <p>solid：实线</p>
     * <p>double：双实线</p>
     * <p>groove：凹线（槽）</p>
     * <p>ridge：凸线（脊）</p>
     * <p>inset：嵌入</p>
     * <p>outset：凸出</p>
     */
    private String borderTopStyle;
    /**
     * 上边框颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    private String borderTopColor;
    /**
     * 上边框宽度
     */
    private String borderTopWidth;
    /**
     * 下边框
     */
    private String borderBottom;
    /**
     * 下边框样式
     * <p>none：无</p>
     * <p>hidden：隐藏</p>
     * <p>dotted：点虚线</p>
     * <p>dashed：短虚线</p>
     * <p>solid：实线</p>
     * <p>double：双实线</p>
     * <p>groove：凹线（槽）</p>
     * <p>ridge：凸线（脊）</p>
     * <p>inset：嵌入</p>
     * <p>outset：凸出</p>
     */
    private String borderBottomStyle;
    /**
     * 下边框颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    private String borderBottomColor;
    /**
     * 下边框宽度
     */
    private String borderBottomWidth;
    /**
     * 左边框
     */
    private String borderLeft;
    /**
     * 左边框样式
     * <p>none：无</p>
     * <p>hidden：隐藏</p>
     * <p>dotted：点虚线</p>
     * <p>dashed：短虚线</p>
     * <p>solid：实线</p>
     * <p>double：双实线</p>
     * <p>groove：凹线（槽）</p>
     * <p>ridge：凸线（脊）</p>
     * <p>inset：嵌入</p>
     * <p>outset：凸出</p>
     */
    private String borderLeftStyle;
    /**
     * 左边框颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    private String borderLeftColor;
    /**
     * 左边框宽度
     */
    private String borderLeftWidth;
    /**
     * 右边框
     */
    private String borderRight;
    /**
     * 右边框样式
     * <p>none：无</p>
     * <p>hidden：隐藏</p>
     * <p>dotted：点虚线</p>
     * <p>dashed：短虚线</p>
     * <p>solid：实线</p>
     * <p>double：双实线</p>
     * <p>groove：凹线（槽）</p>
     * <p>ridge：凸线（脊）</p>
     * <p>inset：嵌入</p>
     * <p>outset：凸出</p>
     */
    private String borderRightStyle;
    /**
     * 右边框颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    private String borderRightColor;
    /**
     * 右边框宽度
     */
    private String borderRightWidth;
    /**
     * 合并行
     */
    private Integer rowSpan;
    /**
     * 合并列
     */
    private Integer columnSpan;
}
