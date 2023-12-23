package io.ihankun.framework.pdf.fop.ext.barcode;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.ImageBuffered;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 条形码加载器
 *
 * @author hankun
 */
public class XEasyPdfTemplateBarcodeLoader extends AbstractImageLoader {

    /**
     * 加载图像
     *
     * @param info    图像信息
     * @param hints   提示映射
     * @param session 会话上下文
     * @return 返回图像
     */
    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) {
        return new ImageBuffered(info, (BufferedImage) info.getCustomObjects().get(XEasyPdfTemplateBarcodeImageHandler.IMAGE_TYPE), null);
    }

    /**
     * 获取目标图像类型
     *
     * @return 返回图像类型
     */
    @Override
    public ImageFlavor getTargetFlavor() {
        return XEasyPdfTemplateBarcodeImageHandler.IMAGE_FLAVOR;
    }
}
