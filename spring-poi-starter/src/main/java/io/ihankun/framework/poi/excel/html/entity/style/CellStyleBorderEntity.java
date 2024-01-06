package io.ihankun.framework.poi.excel.html.entity.style;

/**
 * 边框样式
 *
 * @author hankun
 */
public class CellStyleBorderEntity {

    private String borderLeftColor;

    private String borderRightColor;

    private String borderTopColor;

    private String borderBottomColor;

    private String borderLeftStyle;

    private String borderRightStyle;

    private String borderTopStyle;

    private String borderBottomStyle;

    private String borderLeftWidth;

    private String borderRightWidth;

    private String borderTopWidth;

    private String borderBottomWidth;

    public String getBorderLeftColor() {
        return borderLeftColor;
    }

    public void setBorderLeftColor(String borderLeftColor) {
        this.borderLeftColor = borderLeftColor;
    }

    public String getBorderRightColor() {
        return borderRightColor;
    }

    public void setBorderRightColor(String borderRightColor) {
        this.borderRightColor = borderRightColor;
    }

    public String getBorderTopColor() {
        return borderTopColor;
    }

    public void setBorderTopColor(String borderTopColor) {
        this.borderTopColor = borderTopColor;
    }

    public String getBorderBottomColor() {
        return borderBottomColor;
    }

    public void setBorderBottomColor(String borderBottomColor) {
        this.borderBottomColor = borderBottomColor;
    }

    public String getBorderLeftStyle() {
        return borderLeftStyle;
    }

    public void setBorderLeftStyle(String borderLeftStyle) {
        this.borderLeftStyle = borderLeftStyle;
    }

    public String getBorderRightStyle() {
        return borderRightStyle;
    }

    public void setBorderRightStyle(String borderRightStyle) {
        this.borderRightStyle = borderRightStyle;
    }

    public String getBorderTopStyle() {
        return borderTopStyle;
    }

    public void setBorderTopStyle(String borderTopStyle) {
        this.borderTopStyle = borderTopStyle;
    }

    public String getBorderBottomStyle() {
        return borderBottomStyle;
    }

    public void setBorderBottomStyle(String borderBottomStyle) {
        this.borderBottomStyle = borderBottomStyle;
    }

    public String getBorderLeftWidth() {
        return borderLeftWidth;
    }

    public void setBorderLeftWidth(String borderLeftWidth) {
        this.borderLeftWidth = borderLeftWidth;
    }

    public String getBorderRightWidth() {
        return borderRightWidth;
    }

    public void setBorderRightWidth(String borderRightWidth) {
        this.borderRightWidth = borderRightWidth;
    }

    public String getBorderTopWidth() {
        return borderTopWidth;
    }

    public void setBorderTopWidth(String borderTopWidth) {
        this.borderTopWidth = borderTopWidth;
    }

    public String getBorderBottomWidth() {
        return borderBottomWidth;
    }

    public void setBorderBottomWidth(String borderBottomWidth) {
        this.borderBottomWidth = borderBottomWidth;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(borderLeftColor).append(borderRightColor)
            .append(borderTopColor).append(borderBottomColor).append(borderLeftStyle)
            .append(borderRightStyle).append(borderTopStyle).append(borderBottomStyle)
            .append(borderLeftWidth).append(borderRightWidth).append(borderTopWidth)
            .append(borderBottomWidth).toString();

    }

}
