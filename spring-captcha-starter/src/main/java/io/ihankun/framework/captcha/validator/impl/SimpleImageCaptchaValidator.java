package io.ihankun.framework.captcha.validator.impl;

import io.ihankun.framework.captcha.validator.SliderCaptchaPercentageValidator;
import io.ihankun.framework.common.error.impl.CaptchaErrorCode;
import io.ihankun.framework.captcha.entity.ClickImageCheckDefinition;
import io.ihankun.framework.captcha.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.validator.ImageCaptchaValidator;
import io.ihankun.framework.common.constants.base.TrackTypeConstant;
import io.ihankun.framework.captcha.entity.ImageCaptchaTrack;
import io.ihankun.framework.common.constants.captcha.CaptchaTypeConstant;
import io.ihankun.framework.common.response.ResponseResult;
import io.ihankun.framework.common.utils.captcha.CaptchaTypeClassifier;
import io.ihankun.framework.common.utils.object.AbsObjectUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hankun
 *
 * 基本的滑块验证校验 ， 值进行基本校验， 目前只校验用户是否滑动到缺口处，不校验行为轨迹
 */
@Slf4j
public class SimpleImageCaptchaValidator implements ImageCaptchaValidator, SliderCaptchaPercentageValidator {

    /** 默认的容错值. */
    public static float DEFAULT_TOLERANT = 0.02f;
    /** 验证数据 key. */
    public static final String PERCENTAGE_KEY = "percentage";
    /** 容错值key. */
    public static final String TOLERANT_KEY = "tolerant";
    /** 类型 key， 标识是哪张类型的验证码. */
    public static final String TYPE_KEY = "type";
    /** 计算当前验证码用户滑动的百分比率 - 生成时的百分比率, 多个的话取均值. */
    public static final String USER_CURRENT_PERCENTAGE_STD = "user_current_percentage_std";
    public static final String USER_CURRENT_PERCENTAGE = "user_current_percentage";
    /** 容错值. */
    @Getter
    @Setter
    public float defaultTolerant = DEFAULT_TOLERANT;

    public SimpleImageCaptchaValidator() {
        CaptchaTypeClassifier.addSliderCaptchaType(CaptchaTypeConstant.CONCAT);
        CaptchaTypeClassifier.addSliderCaptchaType(CaptchaTypeConstant.ROTATE);
        CaptchaTypeClassifier.addSliderCaptchaType(CaptchaTypeConstant.SLIDER);
        CaptchaTypeClassifier.addClickCaptchaType(CaptchaTypeConstant.WORD_IMAGE_CLICK);
    }

    public SimpleImageCaptchaValidator(float defaultTolerant) {
        this();
        this.defaultTolerant = defaultTolerant;
    }

    @Override
    public float calcPercentage(Number pos, Number maxPos) {
        return pos.floatValue() / maxPos.floatValue();
    }

    @Override
    public boolean checkPercentage(Float newPercentage, Float oriPercentage) {
        return checkPercentage(newPercentage, oriPercentage, defaultTolerant);
    }

    @Override
    public boolean checkPercentage(Float newPercentage, Float oriPercentage, float tolerant) {
        if (newPercentage == null || Float.isNaN(newPercentage) || Float.isInfinite(newPercentage)
                || oriPercentage == null || Float.isNaN(oriPercentage) || Float.isInfinite(oriPercentage)) {
            return false;
        }
        // 容错值
        float maxTolerant = oriPercentage + tolerant;
        float minTolerant = oriPercentage - tolerant;
        return newPercentage >= minTolerant && newPercentage <= maxTolerant;
    }

    @Override
    public Map<String, Object> generateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo) {
        Map<String, Object> map = new HashMap<>(8);
        if (beforeGenerateImageCaptchaValidData(imageCaptchaInfo, map)) {
            doGenerateImageCaptchaValidData(map, imageCaptchaInfo);
        }
        afterGenerateImageCaptchaValidData(imageCaptchaInfo, map);
        return map;
    }

    public boolean beforeGenerateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> map) {
        // 容错值
        Float tolerant = imageCaptchaInfo.getTolerant();
        if (tolerant != null && tolerant > 0) {
            map.put(TOLERANT_KEY, tolerant);
        }
        // 类型
        String type = imageCaptchaInfo.getType();
        if (AbsObjectUtils.isEmpty(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        map.put(TYPE_KEY, type);
        return true;
    }

    public void afterGenerateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> map) {

    }

    public void doGenerateImageCaptchaValidData(Map<String, Object> map,
                                                ImageCaptchaInfo imageCaptchaInfo) {
        // type
        String type = (String) map.getOrDefault(TYPE_KEY, CaptchaTypeConstant.SLIDER);
        Object expand = imageCaptchaInfo.getData() == null ? null : imageCaptchaInfo.getData().getExpand();
        if (CaptchaTypeClassifier.isSliderCaptcha(type)) {
            // 滑动验证码
            addPercentage(imageCaptchaInfo, map);
        } else if (CaptchaTypeClassifier.isClickCaptcha(type)) {
            // 图片点选验证码
            if (expand == null) {
                throw new IllegalArgumentException("点选验证码扩展数据转换为 List<ClickImageCheckDefinition> 失败， info=" + imageCaptchaInfo);
            }
            List<ClickImageCheckDefinition> clickImageCheckDefinitionList;
            try {
                clickImageCheckDefinitionList = (List<ClickImageCheckDefinition>) expand;
            } catch (Exception e) {
                throw new IllegalArgumentException("点选验证码扩展数据转换为 List<ClickImageCheckDefinition> 失败， info=" + imageCaptchaInfo);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < clickImageCheckDefinitionList.size(); i++) {
                ClickImageCheckDefinition definition = clickImageCheckDefinitionList.get(i);
                Integer x = definition.getX();
                Integer y = definition.getY();
                Integer width = imageCaptchaInfo.getBackgroundImageWidth();
                Integer height = imageCaptchaInfo.getBackgroundImageHeight();
                float vx = calcPercentage(x, width);
                float vy = calcPercentage(y, height);
                sb.append(vx).append(",").append(vy).append(";");
                if (i == 0 && !map.containsKey(TOLERANT_KEY)) {
                    // 重新计算容错值
                    float minLeft = calcPercentage(x - definition.getWidth() / 2f, width);
                    float tolerant = vx - minLeft;
                    map.put(TOLERANT_KEY, tolerant);
                }
            }
            // 添加点选验证数据
            map.put(PERCENTAGE_KEY, sb.toString());
        } else if (CaptchaTypeClassifier.isJigsawCaptcha(type)) {
            // 拼图验证码
            map.put(PERCENTAGE_KEY, expand);
        }
    }

    @Override
    public ResponseResult<?> valid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> imageCaptchaValidData) {
        // 读容错值
        Float tolerant = getFloatParam(TOLERANT_KEY, imageCaptchaValidData, defaultTolerant);
        // 读验证码类型
        String type = getStringParam(TYPE_KEY, imageCaptchaValidData, CaptchaTypeConstant.SLIDER);
        // 验证前
        // 在验证前必须读取 容错值 和验证码类型
        ResponseResult<?> beforeValid = beforeValid(imageCaptchaTrack, imageCaptchaValidData, tolerant, type);
        if (!beforeValid.isSuccess()) {
            return beforeValid;
        }
        Integer bgImageWidth = imageCaptchaTrack.getBgImageWidth();
        if (bgImageWidth == null || bgImageWidth < 1) {
            // 没有背景图片宽度
            return ResponseResult.error(CaptchaErrorCode.NOT_VALID_PARAM,"验证码背景图片宽度参数错误");
        }
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        if (CollectionUtils.isEmpty(trackList)) {
            // 没有滑动轨迹
            return ResponseResult.error(CaptchaErrorCode.NOT_VALID_PARAM,"没有解析到滑动轨迹");
        }
        // 验证
        ResponseResult<?> response;
        boolean valid = doValid(imageCaptchaTrack, imageCaptchaValidData, tolerant, type);
        if (valid) {
            // 验证后
            response = afterValid(imageCaptchaTrack, imageCaptchaValidData, tolerant, type);
        } else {
            // 缺口位置校验失败
            response = ResponseResult.error(CaptchaErrorCode.BASIC_CHECK_FAIL);
        }
        return response;
    }

    /**
     * 验证前
     *
     * @param imageCaptchaTrack sliderCaptchaTrack
     * @param captchaValidData  captchaValidData
     * @param tolerant          tolerant
     * @param type              type
     * @return boolean
     */
    public ResponseResult<?> beforeValid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> captchaValidData, Float tolerant, String type) {
        return ResponseResult.success();
    }

    /**
     * 验证后
     *
     * @param imageCaptchaTrack sliderCaptchaTrack
     * @param captchaValidData  captchaValidData
     * @param tolerant          tolerant
     * @param type              type
     * @return boolean
     */
    public ResponseResult<?> afterValid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> captchaValidData, Float tolerant, String type) {
        return ResponseResult.success();
    }

    public boolean doValid(ImageCaptchaTrack imageCaptchaTrack,
                           Map<String, Object> imageCaptchaValidData,
                           Float tolerant,
                           String type) {
        if (CaptchaTypeClassifier.isSliderCaptcha(type)) {
            // 滑动类型验证码
            return doValidSliderCaptcha(imageCaptchaTrack, imageCaptchaValidData, tolerant, type);
        } else if (CaptchaTypeClassifier.isClickCaptcha(type)) {
            // 点选类型验证码
            return doValidClickCaptcha(imageCaptchaTrack, imageCaptchaValidData, tolerant, type);
        } else if (CaptchaTypeClassifier.isJigsawCaptcha(type)) {
            // 拼图类型验证码
            return doValidJigsawCaptcha(imageCaptchaTrack, imageCaptchaValidData, tolerant, type);
        }
        // 不支持的类型
        log.warn("校验验证码警告， 不支持的验证码类型:{}, 请手动扩展 cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator.doValid 进行校验扩展", type);
        return false;
    }

    public boolean doValidJigsawCaptcha(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> imageCaptchaValidData, Float tolerant, String type) {
        if (imageCaptchaTrack.getData() == null || !(imageCaptchaTrack.getData() instanceof String)) {
            throw new IllegalArgumentException("拼图验证码必须传data数据，且必须是字符串类型逗号分隔数据");
        }
        String posArr = (String) imageCaptchaTrack.getData();
        String successPosStr = getStringParam(PERCENTAGE_KEY, imageCaptchaValidData, null);
        return successPosStr.equals(posArr);
    }

    /**
     * 校验点选验证码
     *
     * @param imageCaptchaTrack     sliderCaptchaTrack
     * @param imageCaptchaValidData imageCaptchaValidData
     * @param tolerant              tolerant
     * @param type                  type
     * @return boolean
     */
    public boolean doValidClickCaptcha(ImageCaptchaTrack imageCaptchaTrack,
                                       Map<String, Object> imageCaptchaValidData,
                                       Float tolerant,
                                       String type) {
        String validStr = getStringParam(PERCENTAGE_KEY, imageCaptchaValidData, null);
        if (AbsObjectUtils.isEmpty(validStr)) {
            return false;
        }
        String[] splitArr = validStr.split(";");
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        if (trackList.size() < splitArr.length) {
            return false;
        }
        // 取出点击事件的轨迹数据
        List<ImageCaptchaTrack.Track> clickTrackList = trackList
                .stream()
                .filter(t -> TrackTypeConstant.CLICK.equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());
        if (clickTrackList.size() != splitArr.length) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        List<Double> percentages = new ArrayList<>();
        for (int i = 0; i < splitArr.length; i++) {
            ImageCaptchaTrack.Track track = clickTrackList.get(i);
            String posStr = splitArr[i];
            String[] posArr = posStr.split(",");
            float xPercentage = Float.parseFloat(posArr[0]);
            float yPercentage = Float.parseFloat(posArr[1]);

            float calcXPercentage = calcPercentage(track.getX(), imageCaptchaTrack.getBgImageWidth());
            float calcYPercentage = calcPercentage(track.getY(), imageCaptchaTrack.getBgImageHeight());
            if (!checkPercentage(calcXPercentage, xPercentage, tolerant)
                    || !checkPercentage(calcYPercentage, yPercentage, tolerant)) {
                return false;
            }
            if (i > 0) {
                sb.append("|");
            }
            sb.append(calcXPercentage).append(",").append(calcYPercentage);
            percentages.add((double) ((calcXPercentage - xPercentage) + (calcYPercentage - yPercentage)));
        }
        // 存储一下当前计算出来的值
        return true;
    }

    /**
     * 校验滑动验证码
     *
     * @param imageCaptchaTrack     sliderCaptchaTrack
     * @param imageCaptchaValidData imageCaptchaValidData
     * @param tolerant              tolerant
     * @param type                  type
     * @return boolean
     */
    public boolean doValidSliderCaptcha(ImageCaptchaTrack imageCaptchaTrack,
                                        Map<String, Object> imageCaptchaValidData,
                                        Float tolerant,
                                        String type) {
        Float oriPercentage = getFloatParam(PERCENTAGE_KEY, imageCaptchaValidData);
        if (oriPercentage == null) {
            // 没读取到百分比
            return false;
        }
        List<ImageCaptchaTrack.Track> trackList = imageCaptchaTrack.getTrackList();
        // 取最后一个滑动轨迹
        ImageCaptchaTrack.Track lastTrack = trackList.get(trackList.size() - 1);
        // 计算百分比
        float calcPercentage = calcPercentage(lastTrack.getX(), imageCaptchaTrack.getBgImageWidth());
        // 校验百分比
        boolean percentage = checkPercentage(calcPercentage, oriPercentage, tolerant);
        if (percentage) {
            // 校验成功
            // 存储一下当前计算出来的值
            imageCaptchaValidData.put(USER_CURRENT_PERCENTAGE, String.valueOf(calcPercentage));
            imageCaptchaValidData.put(USER_CURRENT_PERCENTAGE_STD, String.valueOf(calcPercentage - oriPercentage));
        }
        return percentage;
    }

    public Float getFloatParam(String key, Map<String, Object> imageCaptchaValidData) {
        return getFloatParam(key, imageCaptchaValidData, null);
    }

    public Float getFloatParam(String key, Map<String, Object> imageCaptchaValidData, Float defaultData) {
        Object data = imageCaptchaValidData.get(key);
        if (data != null) {
            if (data instanceof Number) {
                return ((Number) data).floatValue();
            }
            try {
                if (data instanceof String) {
                    return Float.parseFloat((String) data);
                }
            } catch (NumberFormatException e) {
                log.error("从 imageCaptchaValidData 读取到的 " + key + "无法转换成float类型, [{}]", data);
                throw e;
            }
        }
        return defaultData;
    }

    public String getStringParam(String key, Map<String, Object> imageCaptchaValidData, String defaultData) {
        if (CollectionUtils.isEmpty(imageCaptchaValidData)) {
            return defaultData;
        }
        Object data = imageCaptchaValidData.get(key);
        if (data != null) {
            if (data instanceof String) {
                return (String) data;
            }
            try {
                return String.valueOf(data);
            } catch (NumberFormatException e) {
                log.error("从 imageCaptchaValidData 读取到的 " + key + "无法转换成String类型, [{}]", data);
                throw e;
            }
        }
        return defaultData;
    }

    protected void addPercentage(ImageCaptchaInfo imageCaptchaInfo, Map<String, Object> imageCaptchaValidData) {
        float percentage = calcPercentage(imageCaptchaInfo.getRandomX(), imageCaptchaInfo.getBackgroundImageWidth());
        imageCaptchaValidData.put(PERCENTAGE_KEY, percentage);
    }
}
