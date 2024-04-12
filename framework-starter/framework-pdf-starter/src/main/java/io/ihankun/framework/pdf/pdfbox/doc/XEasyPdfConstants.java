package io.ihankun.framework.pdf.pdfbox.doc;

/**
 * pdf常量
 *
 * @author hankun
 */
public final class XEasyPdfConstants {

    /**
     * pdfbox生产者
     */
    public static final String PDFBOX_PRODUCER = "x-easypdf/pdfbox";
    /**
     * 每英寸像素点
     */
    public static final Integer POINTS_PER_INCH = 72;
    /**
     * 每毫米像素点
     */
    public static final Float POINTS_PER_MM = 1 / 25.4f * POINTS_PER_INCH;
    /**
     * 总页码占位符
     */
    public static final String TOTAL_PAGE_PLACEHOLDER = "${TPN}";
    /**
     * 当前页码占位符
     */
    public static final String CURRENT_PAGE_PLACEHOLDER = "${CPN}";
    /**
     * 字体映射策略键
     */
    public static final String FONT_MAPPING_POLICY_KEY = "x-easypdf.font.mapping";
}
