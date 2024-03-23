package io.ihankun.framework.common.v1.qrcode;

import cn.hutool.core.codec.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码生成器
 * @author hankun
 */
@Slf4j
public class QrCodeBuilder {

    public static final class QrCodeHolder {
        public static final QrCodeBuilder QR_CODE_BUILDER = new QrCodeBuilder();
    }

    public static QrCodeBuilder ins() {
        return QrCodeHolder.QR_CODE_BUILDER;
    }

    public String create(String content, int width, int height) {
        BufferedImage image = createImage("utf-8", content, width, height);
        return "data:image/png;base64," + getBase64(image);
    }

    /**
     * 创建二维码
     * @param charSet 编码方式
     * @param content 二维码内容
     * @param qrWidth 二维码长度
     * @param qrHeight 二维码高度
     * @return
     */
    private BufferedImage createImage(String charSet, String content, int qrWidth, int qrHeight){
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, charSet);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,qrWidth , qrHeight, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    private static String getBase64(BufferedImage image){
        String base64 = null;
        try {
            Integer width = image.getWidth();
            Integer height = image.getHeight();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            base64 = Base64.encode(stream.toByteArray());
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return base64;
    }


}
