package io.ihankun.framework.poi.cache;

import io.ihankun.framework.poi.cache.manager.POICacheManager;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 图片缓存处理
 *
 * @author hankun
 */
public class ImageCache {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ImageCache.class);

    public static byte[] getImage(String imagePath) {
        InputStream                 is           = POICacheManager.getFile(imagePath);
        final ByteArrayOutputStream swapStream   = new ByteArrayOutputStream();
        try {
            int ch;
            while ((ch = is.read()) != -1) {
                swapStream.write(ch);
            }

            return swapStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(swapStream);
        }

    }


    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int                   transparency = Transparency.OPAQUE;
            GraphicsDevice        gs           = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc           = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }
}
