package io.ihankun.framework.poi.entity;

/**
 * word导出,图片设置和图片信息
 *
 * @author hankun
 */
public class ImageEntity {

    public static int EMBED  = 0;
    public static int ABOVE  = 1;
    public static int BEHIND  = 2;

    public static String URL  = "url";

    public static String Data = "data";
    /**
     * 图片输入方式
     */
    private String       type = URL;
    /**
     * 图片宽度
     */
    private int          width;
    // 图片高度
    private int          height;
    // 图片地址
    private String       url;
    // 图片信息
    private byte[]       data;

    private int          rowspan = 1;
    private int          colspan = 1;
    /**
     * 图片方式
     * EMBED 围绕
     * ABOVE 浮于上方
     * BEHIND 浮于下方
     */
    private int          locationType = ImageEntity.EMBED;

    public ImageEntity() {

    }

    public ImageEntity(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.type = Data;
    }

    public ImageEntity(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public byte[] getData() {
        return data;
    }

    public int getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }
}
