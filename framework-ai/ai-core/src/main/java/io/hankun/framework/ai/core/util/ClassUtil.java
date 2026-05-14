package io.hankun.framework.ai.core.util;

/**
 * @description:
 * @className: ClassUtil
 * @createAt: 2025/12/1 15:06
 * @author: hankun
 */
public class ClassUtil {

    public static Class<?> stringToClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String classToString(Class<?> clazz) {
        return clazz.getName();
    }
}
