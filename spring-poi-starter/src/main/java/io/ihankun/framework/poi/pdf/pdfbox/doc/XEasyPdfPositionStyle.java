package io.ihankun.framework.poi.pdf.pdfbox.doc;

/**
 * pdf位置样式
 *
 * @author hankun
 */
public enum XEasyPdfPositionStyle {
    /**
     * 居上
     */
    TOP,
    /**
     * 居中
     */
    CENTER,
    /**
     * 居左
     */
    LEFT,
    /**
     * 居右
     */
    RIGHT,
    /**
     * 居下
     */
    BOTTOM;

    /**
     * 检查水平样式
     *
     * @param style 水平样式
     */
    public static void checkHorizontalStyle(XEasyPdfPositionStyle style) {
        // 如果给定样式为居上或居下，则提示错误
        if (style == XEasyPdfPositionStyle.TOP || style == XEasyPdfPositionStyle.BOTTOM) {
            // 提示错误
            throw new IllegalArgumentException("only set LEFT, CENTER or RIGHT style");
        }
    }

    /**
     * 检查垂直样式
     *
     * @param style 垂直样式
     */
    public static void checkVerticalStyle(XEasyPdfPositionStyle style) {
        // 如果给定样式为居左或居右，则提示错误
        if (style == XEasyPdfPositionStyle.LEFT || style == XEasyPdfPositionStyle.RIGHT) {
            // 提示错误
            throw new IllegalArgumentException("only set TOP, CENTER or BOTTOM style");
        }
    }
}
