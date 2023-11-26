package io.ihankun.framework.captcha.build.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FontWrapper {
    private Font font;
    private float currentFontTopCoef;
}
