package io.ihankun.framework.pdf.fop.doc.watermark;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf模板-水印（文本）参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
class XEasyPdfTemplateWatermarkParam {

    /**
     * 临时目录
     * <p>注：用于生成水印图片目录</p>
     */
    private String tempDir = ".";
    /**
     * 水印id
     * <p>注：水印id须唯一</p>
     */
    private String id;
    /**
     * 宽度
     */
    private String width;
    /**
     * 高度
     */
    private String height;
    /**
     * 显示宽度
     */
    private String showWidth;
    /**
     * 显示高度
     */
    private String showHeight;
    /**
     * 文字名称
     */
    private String fontFamily;
    /**
     * 文字样式
     * <p>normal：正常</p>
     * <p>bold：粗体</p>
     * <p>bold_italic：粗体斜体</p>
     * <p>italic：斜体</p>
     */
    private String fontStyle = "normal";
    /**
     * 字体大小
     */
    private String fontSize = "12pt";
    /**
     * 字体颜色
     */
    private String fontColor = "black";
    /**
     * 字体透明度
     * <p>0-255之间，值越小越透明</p>
     */
    private String fontAlpha;
    /**
     * 定位
     * <p>第一个参数为X轴</p>
     * <p>第二个参数为Y轴</p>
     */
    private String position;
    /**
     * 水平定位
     * <p>left：左</p>
     * <p>center：中</p>
     * <p>right：右</p>
     */
    private String positionHorizontal;
    /**
     * 垂直定位
     * <p>top：上</p>
     * <p>center：中</p>
     * <p>bottom：下</p>
     */
    private String positionVertical;
    /**
     * 重复
     * <p>repeat：水平垂直重复</p>
     * <p>repeat-x：水平重复</p>
     * <p>repeat-y：垂直重复</p>
     * <p>no-repeat：不重复</p>
     */
    private String repeat;
    /**
     * 旋转弧度
     */
    private String radians = "0";
    /**
     * 是否覆盖
     */
    private Boolean isOverwrite = Boolean.FALSE;
    /**
     * 文本列表
     */
    private List<String> texts = new ArrayList<>(10);
}
