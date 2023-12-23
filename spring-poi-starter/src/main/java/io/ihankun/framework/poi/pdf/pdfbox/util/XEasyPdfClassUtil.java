package io.ihankun.framework.poi.pdf.pdfbox.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 类工具
 *
 * @author hankun
 */
public class XEasyPdfClassUtil {

    /**
     * 重置属性
     *
     * @param className 全类名
     */
    @SneakyThrows
    public static void resetField(String className) {
        Class<?> target = Class.forName(className);
        Field[] fields = target.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null, field.get(null));
        }
    }
}
