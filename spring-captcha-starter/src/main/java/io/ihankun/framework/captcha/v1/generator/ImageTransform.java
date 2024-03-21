package io.ihankun.framework.captcha.v1.generator;


import io.ihankun.framework.captcha.v1.entity.CustomData;
import io.ihankun.framework.captcha.v1.entity.GenerateParam;
import io.ihankun.framework.captcha.v1.entity.ImageTransformData;
import io.ihankun.framework.captcha.v1.entity.Resource;

import java.awt.image.BufferedImage;

/**
 * @author hankun
 *
 * 图片转换为字符串， 扩展接口, 可以转换为文件地址等
 */
public interface ImageTransform {

    /**
     * 转换
     *
     * @param backgroundImage    背景图片
     * @param param              参数
     * @param backgroundResource 背景资源对象
     * @param data               自定义透传数据
     * @return ImageTransformData
     */
    default ImageTransformData transform(GenerateParam param, BufferedImage backgroundImage, Resource backgroundResource, CustomData data) {
        return transform(param, backgroundImage, null, backgroundResource, null, data);
    }

    /**
     * 转换
     *
     * @param backgroundImage    背景图片
     * @param templateImage      模板图片(可能为空)
     * @param param              参数
     * @param backgroundResource 背景资源对象
     * @param templateResource   模板资源对象(可能为空)
     * @param data               自定义透传数据
     * @return String
     */
    ImageTransformData transform(GenerateParam param,
                                 BufferedImage backgroundImage,
                                 BufferedImage templateImage,
                                 Object backgroundResource,
                                 Object templateResource,
                                 CustomData data);
}
