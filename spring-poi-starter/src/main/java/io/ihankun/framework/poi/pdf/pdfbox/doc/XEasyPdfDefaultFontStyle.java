package io.ihankun.framework.poi.pdf.pdfbox.doc;

/**
 * 默认字体样式枚举
 *
 * @author hankun
 */
public enum XEasyPdfDefaultFontStyle {
    /**
     * 细体
     */
    LIGHT("org/dromara/pdf/pdfbox/ttf/HarmonyOS_Sans_SC_Light.ttf", "HarmonyOS_Sans_SC_Light"),
    /**
     * 正常
     */
    NORMAL("org/dromara/pdf/pdfbox/ttf/HarmonyOS_Sans_SC_Medium.ttf", "HarmonyOS_Sans_SC_Medium"),
    /**
     * 粗体
     */
    BOLD("org/dromara/pdf/pdfbox/ttf/HarmonyOS_Sans_SC_Bold.ttf", "/HarmonyOS_Sans_SC_Bold");

    /**
     * 字体路径
     */
    private final String path;

    /**
     * 字体名称
     */
    private final String name;

    /**
     * 有参构造
     *
     * @param path 字体路径
     * @param name 字体名称
     */
    XEasyPdfDefaultFontStyle(String path, String name) {
        this.path = path;
        this.name = name;
    }

    /**
     * 获取字体路径
     *
     * @return 返回字体路径
     */
    public String getPath() {
        return this.path;
    }

    /**
     * 获取字体名称
     *
     * @return 返回字体名称
     */
    public String getName() {
        return this.name;
    }
}
