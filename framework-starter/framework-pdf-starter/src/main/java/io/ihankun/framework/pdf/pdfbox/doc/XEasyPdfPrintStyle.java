package io.ihankun.framework.pdf.pdfbox.doc;

/**
 * pdf打印形式
 *
 * @author hankun
 */
public enum XEasyPdfPrintStyle {
    /**
     * 横向
     */
    LANDSCAPE(0),
    /**
     * 纵向
     */
    PORTRAIT(1),
    /**
     * 反向横向
     */
    REVERSE_LANDSCAPE(2);
    /**
     * 方向
     */
    private final int orientation;

    /**
     * 有参构造
     *
     * @param orientation 方向
     */
    XEasyPdfPrintStyle(int orientation) {
        this.orientation = orientation;
    }

    /**
     * 获取方向
     *
     * @return 返回方向
     */
    public int getOrientation() {
        return orientation;
    }
}
