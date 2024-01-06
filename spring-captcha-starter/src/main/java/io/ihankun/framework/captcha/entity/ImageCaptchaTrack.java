package io.ihankun.framework.captcha.entity;

import io.ihankun.framework.common.constants.base.TrackTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author hankun
 *
 * 图片验证码滑动轨迹
 */
@Data
public class ImageCaptchaTrack {

    /** 背景图片宽度. */
    private Integer bgImageWidth;

    /** 背景图片高度. */
    private Integer bgImageHeight;

    /** 模板图片宽度. */
    private Integer templateImageWidth;

    /** 模板图片高度. */
    private Integer templateImageHeight;

    /** 滑动开始时间. */
    private Date startSlidingTime;

    /** 滑动结束时间. */
    private Date endSlidingTime;

    /** 滑动的轨迹. */
    private List<Track> trackList;

    /** 扩展数据，用户传输加密数据等.*/
    private Object data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Track {
        /** x. */
        private Float x;
        /** y. */
        private Float y;
        /** 时间. */
        private Float t;
        /** 类型. */
        private String type = TrackTypeConstant.MOVE;
    }
}
