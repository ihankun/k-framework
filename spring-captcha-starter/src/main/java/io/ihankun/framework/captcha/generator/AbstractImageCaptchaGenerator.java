package io.ihankun.framework.captcha.generator;

import io.ihankun.framework.captcha.entity.CaptchaExchange;
import io.ihankun.framework.captcha.entity.CustomData;
import io.ihankun.framework.captcha.entity.GenerateParam;
import io.ihankun.framework.captcha.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.generator.impl.transform.Base64ImageTransform;
import io.ihankun.framework.captcha.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.entity.Resource;
import io.ihankun.framework.captcha.entity.ResourceMap;
import io.ihankun.framework.common.exception.ImageCaptchaException;
import io.ihankun.framework.common.utils.captcha.CaptchaImageUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.ihankun.framework.captcha.generator.impl.StaticCaptchaPostProcessorManager.*;

/**
 * @author hankun
 * 
 * 抽象的验证码生成器
 */
@Slf4j
public abstract class AbstractImageCaptchaGenerator implements ImageCaptchaGenerator {

    public static String DEFAULT_BG_IMAGE_TYPE = "jpeg";

    public static String DEFAULT_SLIDER_IMAGE_TYPE = "png";

    @Getter
    @Setter
    /** 默认背景图片类型. */
    public String defaultBgImageType = DEFAULT_BG_IMAGE_TYPE;

    @Getter
    @Setter
    /** 默认滑块图片类型. */
    public String defaultSliderImageType = DEFAULT_SLIDER_IMAGE_TYPE;

    /** 资源管理器. */
    protected ImageCaptchaResourceManager imageCaptchaResourceManager;

    /** 图片转换器. */
    protected ImageTransform imageTransform;

    @Getter
    private boolean init = false;

    public AbstractImageCaptchaGenerator() {
    }

    @Override
    public ImageCaptchaGenerator init(boolean initDefaultResource) {
        if (init) {
            return this;
        }
        init = true;
        try {
            log.info("图片验证码[{}]初始化...", this.getClass().getSimpleName());
            // 设置默认图片转换器
            if (getImageTransform() == null) {
                setImageTransform(new Base64ImageTransform());
            }
            doInit(initDefaultResource);
        } catch (Exception e) {
            init = false;
            log.error("[{}]初始化失败,ex", this.getClass().getSimpleName(), e);
            throw e;
        }
        return this;
    }

    public AbstractImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type) {
        return generateCaptchaImage(type, defaultBgImageType, defaultSliderImageType);
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo generateCaptchaImage(String type, String backgroundFormatName, String templateFormatName) {
        return generateCaptchaImage(GenerateParam.builder()
                .type(type)
                .backgroundFormatName(backgroundFormatName)
                .templateFormatName(templateFormatName)
                .obfuscate(false)
                .build());
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        assertInit();
        CustomData data = new CustomData();
        CaptchaExchange captchaExchange = CaptchaExchange.create(data, param);
        ImageCaptchaInfo imageCaptchaInfo = applyPostProcessorBeforeGenerate(captchaExchange, this);
        if (imageCaptchaInfo != null) {
            return imageCaptchaInfo;
        }
        doGenerateCaptchaImage(captchaExchange);
        applyPostProcessorBeforeWrapImageCaptchaInfo(captchaExchange, this);
        imageCaptchaInfo = wrapImageCaptchaInfo(captchaExchange);
        applyPostProcessorAfterGenerateCaptchaImage(captchaExchange, imageCaptchaInfo, this);
        return imageCaptchaInfo;
    }

    public ImageCaptchaInfo wrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        ImageCaptchaInfo imageCaptchaInfo = doWrapImageCaptchaInfo(captchaExchange);
        imageCaptchaInfo.setData(captchaExchange.getCustomData());
        return imageCaptchaInfo;
    }

    protected ResourceMap requiredRandomGetTemplate(String type, String tag) {
        ResourceMap templateMap = imageCaptchaResourceManager.randomGetTemplate(type, tag);
        if (templateMap == null || CollectionUtils.isEmpty(templateMap.getResourceMap())) {
            throw new ImageCaptchaException("随机获取模板资源失败， 获取到的资源为空, type=" + type + ",tag=" + tag);
        }
        return templateMap;
    }

    protected Resource requiredRandomGetResource(String type, String tag) {
        Resource resource = imageCaptchaResourceManager.randomGetResource(type, tag);
        if (resource == null) {
            throw new ImageCaptchaException("随机获取资源失败， 获取到的资源为空, type=" + type + ",tag=" + tag);
        }
        return resource;
    }


    protected InputStream getTemplateFile(ResourceMap templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            throw new IllegalArgumentException("查找模板异常， 该模板下未找到 ".concat(imageName));
        }
        return getResourceInputStream(resource, null);
    }

    protected BufferedImage getTemplateImage(ResourceMap templateImages, String imageName) {
        InputStream stream = getTemplateFile(templateImages, imageName);
        BufferedImage bufferedImage = CaptchaImageUtils.wrapFile2BufferedImage(stream);
        closeStream(stream);
        return bufferedImage;
    }


    protected BufferedImage getResourceImage(Resource resource) {
        InputStream stream = getResourceInputStream(resource, null);
        BufferedImage bufferedImage = CaptchaImageUtils.wrapFile2BufferedImage(stream);
        closeStream(stream);
        return bufferedImage;
    }

    protected int randomInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    protected boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    protected int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public void closeStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    protected InputStream getResourceInputStream(Resource resource, Collection<InputStream> inputStreams) {
        InputStream stream = getImageResourceManager().getResourceInputStream(resource);
        if (stream != null && inputStreams != null) {
            inputStreams.add(stream);
        }
        return stream;
    }

    protected Optional<BufferedImage> getTemplateImageOfOptional(ResourceMap templateImages, String imageName) {
        Optional<InputStream> optional = getTemplateFileOfOptional(templateImages, imageName);
        if (optional.isPresent()) {
            InputStream inputStream = optional.get();
            BufferedImage bufferedImage = CaptchaImageUtils.wrapFile2BufferedImage(inputStream);
            closeStream(inputStream);
            return Optional.ofNullable(bufferedImage);
        }
        return Optional.empty();
    }

    protected Optional<InputStream> getTemplateFileOfOptional(ResourceMap templateImages, String imageName) {
        Resource resource = templateImages.get(imageName);
        if (resource == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(getResourceInputStream(resource, null));
    }

    protected void assertInit() {
        if (!init) {
            throw new IllegalStateException("请先调用 init(...) 初始化方法进行初始化");
        }
    }

    /**
     * 初始化
     *
     * @param initDefaultResource 是否初始化默认资源
     */
    protected abstract void doInit(boolean initDefaultResource);

    /**
     * 生成验证码方法
     *
     * @param captchaExchange captchaExchange
     * @return ImageCaptchaInfo
     */
    protected abstract void doGenerateCaptchaImage(CaptchaExchange captchaExchange);

    protected abstract ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange);

    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return imageCaptchaResourceManager;
    }

    @Override
    public void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        this.imageCaptchaResourceManager = imageCaptchaResourceManager;
    }

    @Override
    public ImageTransform getImageTransform() {
        return imageTransform;
    }

    @Override
    public void setImageTransform(ImageTransform imageTransform) {
        this.imageTransform = imageTransform;
    }
}
