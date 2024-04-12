package io.ihankun.framework.pdf.pdfbox.component.line;

/**
 * 线型样式枚举
 *
 * @author hankun
 */
public enum XEasyPdfLineCapStyle {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 圆角
     */
    ROUND(1),
    /**
     * 方角
     */
    SQUARE(2);
    /**
     * 类型
     */
    private final int type;

    /**
     * 有参构造
     *
     * @param type 类型
     */
    XEasyPdfLineCapStyle(int type) {
        this.type = type;
    }

    /**
     * 获取类型
     *
     * @return 返回类型
     */
    public int getType() {
        return this.type;
    }
}
