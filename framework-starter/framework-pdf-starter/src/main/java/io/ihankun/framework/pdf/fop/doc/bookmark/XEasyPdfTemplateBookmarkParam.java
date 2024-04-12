package io.ihankun.framework.pdf.fop.doc.bookmark;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf模板-书签参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
class XEasyPdfTemplateBookmarkParam {

    /**
     * 内部地址
     * <p>注：标签id</p>
     */
    private String internalDestination;
    /**
     * 标题
     */
    private String title;
    /**
     * 字体名称
     */
    private String fontFamily;
    /**
     * 字体样式
     * <p>normal：正常</p>
     * <p>oblique：斜体</p>
     * <p>italic：斜体</p>
     * <p>backslant：斜体</p>
     */
    private String fontStyle;
    /**
     * 字体大小
     */
    private String fontSize;
    /**
     * 字体大小调整
     */
    private String fontSizeAdjust;
    /**
     * 字体重量
     * <p>normal：正常（400）</p>
     * <p>bold：粗体（700）</p>
     * <p>bolder：加粗（900）</p>
     * <p>lighter：细体（100）</p>
     */
    private String fontWeight;
    /**
     * 字体颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     */
    private String fontColor;
    /**
     * 开始状态
     * <p>show：展开</p>
     * <p>hide：折叠</p>
     */
    private String startingState;
    /**
     * 子书签列表
     */
    private List<XEasyPdfTemplateBookmark> children = new ArrayList<>(10);
}
