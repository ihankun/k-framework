package io.ihankun.framework.pdf.fop.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * pdf模板图片工具
 *
 * @author hankun
 */
public class XEasyPdfTemplateImageUtil {

    /**
     * 旋转图片
     *
     * @param sourceImage 源图片
     * @param rectangle   旋转尺寸
     * @param radians     旋转弧度
     * @return 返回旋转后的图片对象
     */
    public static BufferedImage rotate(BufferedImage sourceImage, Rectangle rectangle, double radians) {
        // 如果源图片为空，则提示错误信息
        if (sourceImage == null) {
            // 提示错误信息
            throw new IllegalArgumentException("Image can not be null");
        }
        // 如果旋转弧度为360的整数倍，则返回源图片
        if (radians % 360 == 0) {
            // 返回源图片
            return sourceImage;
        }
        // 获取图片宽度
        int imageWidth = sourceImage.getWidth();
        // 获取图片高度
        int imageHeight = sourceImage.getHeight();
        // 创建图片
        BufferedImage image = new BufferedImage(rectangle.width, rectangle.height, sourceImage.getType());
        // 创建2d图像
        Graphics2D graphics = image.createGraphics();
        // 设置插值
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // 设置图像抗锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置文本抗锯齿
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 设置笔划规范化控制参数
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        // 转换
        graphics.translate((rectangle.width - imageWidth) / 2D, (rectangle.height - imageHeight) / 2D);
        // 旋转
        graphics.rotate(Math.toRadians(radians), imageWidth / 2D, imageHeight / 2D);
        // 绘制图像
        graphics.drawImage(sourceImage, 0, 0, null);
        // 关闭资源
        graphics.dispose();
        // 刷新图片
        sourceImage.flush();
        // 返回图片
        return image;
    }
}
