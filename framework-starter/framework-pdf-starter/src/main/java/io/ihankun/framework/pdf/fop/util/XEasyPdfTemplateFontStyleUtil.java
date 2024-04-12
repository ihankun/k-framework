package io.ihankun.framework.pdf.fop.util;

import java.awt.*;

/**
 * pdf模板文字样式
 *
 * @author hankun
 */
public class XEasyPdfTemplateFontStyleUtil {

    /**
     * 获取样式
     *
     * @param name 名称
     * @return 返回样式
     */
    public static int getStyle(String name) {
        return FontStyle.valueOf(name.toUpperCase()).style;
    }

    /**
     * 文字样式
     */
    private enum FontStyle {
        /**
         * 正常
         */
        NORMAL(Font.PLAIN),
        /**
         * 粗体
         */
        BOLD(Font.BOLD),
        /**
         * 粗体斜体
         */
        BOLD_ITALIC(Font.BOLD | Font.ITALIC),
        /**
         * 斜体
         */
        ITALIC(Font.ITALIC);
        /**
         * 样式
         */
        private final int style;

        /**
         * 有参构造
         *
         * @param style 样式
         */
        FontStyle(int style) {
            this.style = style;
        }

        /**
         * 获取样式
         *
         * @param name 名称
         * @return 返回样式
         */
        public static int getStyle(String name) {
            return valueOf(name.toUpperCase()).style;
        }
    }
}
