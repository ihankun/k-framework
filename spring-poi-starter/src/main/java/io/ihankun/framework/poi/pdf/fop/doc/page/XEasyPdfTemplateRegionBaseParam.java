package io.ihankun.framework.poi.pdf.fop.doc.page;

import lombok.Data;
import lombok.experimental.Accessors;
import io.ihankun.framework.poi.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import io.ihankun.framework.poi.pdf.fop.doc.watermark.XEasyPdfTemplateWatermarkComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf模板-区域基础参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
class XEasyPdfTemplateRegionBaseParam {

    /**
     * 背景
     */
    protected String background;
    /**
     * 背景图片
     * <p>注：路径须写为”url('xxx.png')“的形式</p>
     * <p>注：当为windows系统绝对路径时，须添加前缀“/”，例如：”url('/E:\test\test.png')“</p>
     */
    protected String backgroundImage;
    /**
     * 背景图片宽度
     */
    protected String backgroundImageWidth;
    /**
     * 背景图片高度
     */
    protected String backgroundImageHeight;
    /**
     * 背景附件
     * <p>scroll：滚动</p>
     * <p>fixed：固定</p>
     */
    protected String backgroundAttachment;
    /**
     * 背景颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    protected String backgroundColor;
    /**
     * 背景图片定位
     * <p>第一个参数为X轴</p>
     * <p>第二个参数为Y轴</p>
     */
    protected String backgroundPosition;
    /**
     * 背景图片水平定位
     */
    protected String backgroundPositionHorizontal;
    /**
     * 背景图片垂直定位
     */
    protected String backgroundPositionVertical;
    /**
     * 背景图片重复
     * <p>repeat：水平垂直重复</p>
     * <p>repeat-x：水平重复</p>
     * <p>repeat-y：垂直重复</p>
     * <p>no-repeat：不重复</p>
     */
    protected String backgroundRepeat;
    /**
     * 组件列表
     */
    protected final List<XEasyPdfTemplateComponent> components = new ArrayList<>(10);
    /**
     * 水印组件
     */
    protected XEasyPdfTemplateWatermarkComponent watermark;
}
