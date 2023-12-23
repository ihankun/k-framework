package io.ihankun.framework.poi.pdf.fop.ext.barcode;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

/**
 * 条形码加载器工厂
 *
 * @author hankun
 */
public class XEasyPdfTemplateBarcodeLoaderFactory extends AbstractImageLoaderFactory {

    /**
     * 获取支持的类型名称
     *
     * @return 返回类型名称
     */
    @Override
    public String[] getSupportedMIMETypes() {
        return new String[]{XEasyPdfTemplateBarcodeImageHandler.MIME_TYPE};
    }

    /**
     * 获取支持的图像类型
     *
     * @param mime 类型名称
     * @return 返回图像类型
     */
    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        return XEasyPdfTemplateBarcodeImageHandler.IMAGE_FLAVORS;
    }

    /**
     * 获取新图像加载器
     *
     * @param targetFlavor 目标图像类型
     * @return 返回新图像加载器
     */
    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        return new XEasyPdfTemplateBarcodeLoader();
    }

    /**
     * 是否可用
     *
     * @return 返回是
     */
    @Override
    public boolean isAvailable() {
        return true;
    }
}
