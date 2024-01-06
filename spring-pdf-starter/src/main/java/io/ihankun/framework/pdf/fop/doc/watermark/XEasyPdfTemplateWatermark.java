package io.ihankun.framework.pdf.fop.doc.watermark;

import io.ihankun.framework.pdf.fop.doc.page.XEasyPdfTemplatePageRectangle;
import lombok.SneakyThrows;
import org.apache.fop.util.ColorUtil;
import org.apache.xmlgraphics.util.UnitConv;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateAttributes;
import io.ihankun.framework.pdf.fop.util.XEasyPdfTemplateFontStyleUtil;
import io.ihankun.framework.pdf.fop.util.XEasyPdfTemplateImageUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * pdf模板-水印（文字）组件
 *
 * @author hankun
 */
public class XEasyPdfTemplateWatermark implements XEasyPdfTemplateWatermarkComponent {

    /**
     * 水印（文本）参数
     */
    private final XEasyPdfTemplateWatermarkParam param = new XEasyPdfTemplateWatermarkParam();
    /**
     * 图像路径缓存
     */
    private static final Map<String, String> CACHE = new HashMap<>(10);
    /**
     * 缓存锁
     */
    private static final Object LOCK = new Object();

    /**
     * 设置临时目录
     *
     * @param tempDir 临时目录
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setTempDir(String tempDir) {
        Optional.ofNullable(tempDir).ifPresent(this.param::setTempDir);
        return this;
    }

    /**
     * 设置水印id
     *
     * @param id 水印id须唯一
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setId(String id) {
        this.param.setId(id);
        return this;
    }

    /**
     * 设置图像宽度
     *
     * @param width 图像宽度
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setWidth(String width) {
        this.param.setWidth(width);
        return this;
    }

    /**
     * 设置图像高度
     *
     * @param height 图像高度
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setHeight(String height) {
        this.param.setHeight(height);
        return this;
    }

    /**
     * 设置图像显示宽度
     *
     * @param width 图像显示宽度
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setShowWidth(String width) {
        this.param.setShowWidth(width);
        return this;
    }

    /**
     * 设置图像显示高度
     *
     * @param height 图像显示高度
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setShowHeight(String height) {
        this.param.setShowHeight(height);
        return this;
    }

    /**
     * 设置字体名称
     *
     * @param fontFamily 字体名称
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setFontFamily(String fontFamily) {
        this.param.setFontFamily(fontFamily);
        return this;
    }

    /**
     * 设置字体样式
     * <p>normal：正常</p>
     * <p>oblique：斜体</p>
     * <p>italic：斜体</p>
     * <p>backslant：斜体</p>
     *
     * @param fontStyle 字体样式
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setFontStyle(String fontStyle) {
        Optional.ofNullable(fontStyle).ifPresent(this.param::setFontStyle);
        return this;
    }

    /**
     * 设置字体大小
     *
     * @param fontSize 字体大小
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setFontSize(String fontSize) {
        Optional.ofNullable(fontSize).ifPresent(this.param::setFontSize);
        return this;
    }

    /**
     * 设置字体颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     *
     * @param color 字体颜色
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setFontColor(String color) {
        Optional.ofNullable(color).ifPresent(this.param::setFontColor);
        return this;
    }

    /**
     * 设置字体透明度
     * <p>0-255之间，值越小越透明</p>
     *
     * @param alpha 字体透明度
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setFontAlpha(String alpha) {
        this.param.setFontAlpha(alpha);
        return this;
    }

    /**
     * 设置背景图片定位
     * <p>第一个参数为X轴</p>
     * <p>第二个参数为Y轴</p>
     *
     * @param position 定位
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setPosition(String position) {
        this.param.setPosition(position);
        return this;
    }

    /**
     * 设置背景图片水平定位
     *
     * @param position 定位
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setHorizontalPosition(String position) {
        this.param.setPositionHorizontal(position);
        return this;
    }

    /**
     * 设置背景图片垂直定位
     *
     * @param position 定位
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setVerticalPosition(String position) {
        this.param.setPositionVertical(position);
        return this;
    }

    /**
     * 设置背景图片重复
     * <p>repeat：水平垂直重复</p>
     * <p>repeat-x：水平重复</p>
     * <p>repeat-y：垂直重复</p>
     * <p>no-repeat：不重复</p>
     *
     * @param repeat 重复
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setRepeat(String repeat) {
        this.param.setRepeat(repeat);
        return this;
    }

    /**
     * 设置旋转弧度
     *
     * @param radians 旋转弧度
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setRadians(String radians) {
        Optional.ofNullable(radians).ifPresent(this.param::setRadians);
        return this;
    }

    /**
     * 开启覆盖
     *
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark enableOverwrite() {
        this.param.setIsOverwrite(Boolean.TRUE);
        return this;
    }

    /**
     * 设置文本列表
     *
     * @param texts 文本列表
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setText(String... texts) {
        Optional.ofNullable(texts).ifPresent(v -> Collections.addAll(this.param.getTexts(), v));
        return this;
    }

    /**
     * 设置文本列表
     *
     * @param texts 文本列表
     * @return 返回水印（文字）组件
     */
    public XEasyPdfTemplateWatermark setText(List<String> texts) {
        Optional.ofNullable(texts).ifPresent(this.param.getTexts()::addAll);
        return this;
    }

    /**
     * 保存水印图像
     */
    public void saveImage() {
        this.getImageFile();
    }

    /**
     * 创建水印
     *
     * @param document fo文档
     * @param element  fo元素
     */
    @Override
    public void createWatermark(Document document, Element element) {
        // 如果元素为空，则直接返回
        if (element == null) {
            // 直接返回
            return;
        }
        // 如果文本列表为空，则直接返回
        if (this.param.getTexts().isEmpty()) {
            // 直接返回
            return;
        }
        // 设置背景图片
        element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_IMAGE, this.getImageUrl().intern());
        // 设置背景图片宽度
        Optional.ofNullable(this.param.getShowWidth()).ifPresent(v -> element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_IMAGE_WIDTH, v.intern().toLowerCase()));
        // 设置背景图片高度
        Optional.ofNullable(this.param.getShowHeight()).ifPresent(v -> element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_IMAGE_HEIGHT, v.intern().toLowerCase()));
        // 设置背景图片定位
        Optional.ofNullable(this.param.getPosition()).ifPresent(v -> element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_POSITION, v.intern().toLowerCase()));
        // 设置背景图片水平定位
        Optional.ofNullable(this.param.getPositionHorizontal()).ifPresent(v -> element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_POSITION_HORIZONTAL, v.intern().toLowerCase()));
        // 设置背景图片垂直定位
        Optional.ofNullable(this.param.getPositionVertical()).ifPresent(v -> element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_POSITION_VERTICAL, v.intern().toLowerCase()));
        // 设置背景图片重复
        Optional.ofNullable(this.param.getRepeat()).ifPresent(v -> element.setAttribute(XEasyPdfTemplateAttributes.BACKGROUND_REPEAT, v.intern().toLowerCase()));
    }

    /**
     * 获取图像路径
     *
     * @return 返回图像路径
     */
    private String getImageUrl() {
        // 从缓存中获取图像路径
        String url = CACHE.get(this.param.getId());
        // 如果图像路径为空，则创建图像
        if (url == null) {
            // 加锁
            synchronized (LOCK) {
                // 再次从缓存中获取图像路径
                url = CACHE.get(this.param.getId());
                // 如果图像路径仍然为空，则创建图像
                if (url == null) {
                    // 写入缓存
                    url = this.putCache();
                }
            }
        }
        return url;
    }

    /**
     * 放入缓存
     *
     * @return 返回图像路径
     */
    @SneakyThrows
    private String putCache() {
        // 获取图像文件
        File file = this.getImageFile();
        // 获取文件路径
        String url = "url('" + file.toURI().getPath() + "')";
        // 添加文件路径缓存
        CACHE.put(this.param.getId(), url);
        // 返回文件路径
        return url;
    }

    /**
     * 获取图像文件
     *
     * @return 返回文件
     */
    @SneakyThrows
    private File getImageFile() {
        // 创建文件
        File file = new File(this.param.getTempDir(), this.param.getId() + ".png");
        // 如果文件不存在或开启文件覆盖，则创建新图像
        if (!file.exists() || this.param.getIsOverwrite()) {
            // 如果水印id为空，则提示信息
            Optional.ofNullable(this.param.getId()).orElseThrow(() -> new IllegalArgumentException("the watermark id can not be null"));
            // 如果宽度为空，则提示信息
            Optional.ofNullable(this.param.getWidth()).orElseThrow(() -> new IllegalArgumentException("the watermark width can not be null"));
            // 如果高度为空，则提示信息
            Optional.ofNullable(this.param.getHeight()).orElseThrow(() -> new IllegalArgumentException("the watermark height can not be null"));
            // 创建图像
            BufferedImage image = this.createImage();
            // 写入文件
            ImageIO.write(image, "png", file);
            // 刷新图像
            image.flush();
        }
        // 返回文件
        return file;
    }

    /**
     * 创建图像
     *
     * @return 返回图像
     */
    @SneakyThrows
    @SuppressWarnings("all")
    private BufferedImage createImage() {
        // 获取宽度
        int width = this.parseUnit(this.param.getWidth());
        // 获取高度
        int height = this.parseUnit(this.param.getHeight());
        // 获取字体大小
        int fontSize = this.parseUnit(this.param.getFontSize());
        // 获取字体颜色
        Color fontColor = ColorUtil.parseColorString(null, this.param.getFontColor());
        // 获取字体透明度
        int fontAlpha = Optional.ofNullable(this.param.getFontAlpha()).map(Integer::parseInt).orElse(fontColor.getAlpha());
        // 获取旋转弧度
        double radians = Double.parseDouble(this.param.getRadians());
        // 定义转换图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // 创建图像图形
        Graphics2D graphics = image.createGraphics();
        // 设置插值
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // 设置图像抗锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置文本抗锯齿
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 设置笔划规范化控制参数
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        // 设置笔划
        graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        // 设置文字颜色
        graphics.setColor(new Color(fontColor.getRed(), fontColor.getGreen(), fontColor.getBlue(), fontAlpha));
        // 设置字体
        graphics.setFont(new Font(this.param.getFontFamily(), XEasyPdfTemplateFontStyleUtil.getStyle(this.param.getFontStyle()), fontSize));
        // 获取文本列表
        List<String> texts = this.param.getTexts();
        // 定义Y轴开始坐标（居中显示）
        int beginY = (height - fontSize * texts.size()) / 2 + fontSize;
        // 遍历文本列表
        for (String word : texts) {
            // 文字长度
            int wordWidth = graphics.getFontMetrics().stringWidth(word);
            // 定义X轴开始坐标（居中显示）
            int beginX = (width - wordWidth) / 2;
            // 设置文字
            graphics.drawString(word, beginX, beginY);
            // 重置Y轴开始坐标
            beginY = beginY + fontSize;
        }
        // 资源释放
        graphics.dispose();
        // 返回旋转后的图像
        return XEasyPdfTemplateImageUtil.rotate(image, XEasyPdfTemplatePageRectangle.getRotateRectangle(width, height, radians), radians);
    }

    /**
     * 解析单位
     *
     * @param unit 单位
     * @return 返回单位
     */
    @SneakyThrows
    private int parseUnit(String unit) {
        return UnitConv.convert(unit) / 1000;
    }
}
