package io.hankun.framework.ai.agent.util;

/**
 * @description:
 * @className: FileUtil
 * @createAt: 2025/8/5 14:10
 * @author: hankun
 */
public class FileUtil {


    public static final String BASE = System.getProperty("user.dir");

    public static String validateFileName(String fileName) {
        return fileName.replaceAll("[<>:\"|?* /]", "_");
    }
}
