package io.ihankun.framework.poi.entity;

import lombok.Data;

/**
 * 特殊符号
 *
 * @author hankun
 */
@Data
public class SpecialSymbolsEntity {

    public static final SpecialSymbolsEntity CHECKMARK = new SpecialSymbolsEntity("对号", "\u0050");
    public static final SpecialSymbolsEntity CHECKMARK_BOX = new SpecialSymbolsEntity("方框对号", "\u0052");
    public static final SpecialSymbolsEntity CROSS = new SpecialSymbolsEntity("叉号", "\u00CD");
    public static final SpecialSymbolsEntity CROSS_BOX = new SpecialSymbolsEntity("方框叉号", "\u0053");
    public static final SpecialSymbolsEntity CROSS_CIRCLE = new SpecialSymbolsEntity("圆圈叉号", "\u0055");
    public static final SpecialSymbolsEntity CROSS_BOLD = new SpecialSymbolsEntity("叉号加粗", "\u00CE");
    public static final SpecialSymbolsEntity SMALL_BOX = new SpecialSymbolsEntity("小方框", "\u002A");
    public static final SpecialSymbolsEntity BIG_BOX = new SpecialSymbolsEntity("大方框", "\u00A3");

    private String font;
    private String unicode;
    private String name;


    public SpecialSymbolsEntity(String name, String unicode) {
        this.name = name;
        this.unicode = unicode;
        this.font = "Wingdings 2";
    }

    public SpecialSymbolsEntity(String name, String unicode, String font) {
        this.name = name;
        this.unicode = unicode;
        this.font = font;
    }
}
