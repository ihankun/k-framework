package io.ihankun.framework.captcha.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * @author hankun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FontWrapper {

    private Font font;

    private float currentFontTopCoef;
}
