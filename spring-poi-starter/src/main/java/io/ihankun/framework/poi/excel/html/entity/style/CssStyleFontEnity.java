package io.ihankun.framework.poi.excel.html.entity.style;

/**
 * 字体样式
 *
 * @author hankun
 */
public class CssStyleFontEnity {
    /**
     *  italic  浏览器会显示一个斜体的字体样式。
     */
    private String style;
    /**
     *  bold 定义粗体字符
     */
    private String weight;
    /**
     * 仅支持**px获取不带px的数字大小
     */
    private int    size;
    private String family;
    private String decoration;
    private String color;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(style).append(decoration).append(color).append(family)
            .append(size).append(weight).toString();
    }
}
