package io.ihankun.framework.poi.excel.html.entity.style;

/**
 * Cell 具有的样式
 *
 * @author hankun
 */
public class CellStyleEntity {
    /**
     * 宽
     */
    private String                width;
    /**
     * 高
     */
    private String                height;
    /**
     * 边框
     */
    private CellStyleBorderEntity border;
    /**
     * 背景
     */
    private String                background;
    /**
     * 水平位置
     */
    private String                align;
    /**
     * 垂直位置
     */
    private String                vetical;
    /**
     * 字体设置
     */
    private CssStyleFontEnity     font;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public CellStyleBorderEntity getBorder() {
        return border;
    }

    public void setBorder(CellStyleBorderEntity border) {
        this.border = border;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getVetical() {
        return vetical;
    }

    public void setVetical(String vetical) {
        this.vetical = vetical;
    }

    public CssStyleFontEnity getFont() {
        return font;
    }

    public void setFont(CssStyleFontEnity font) {
        this.font = font;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(align).append(background).append(border).append(height)
            .append(vetical).append(width).append(font).toString();
    }

}
