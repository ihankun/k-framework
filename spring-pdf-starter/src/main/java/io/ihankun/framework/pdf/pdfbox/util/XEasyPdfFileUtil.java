package io.ihankun.framework.pdf.pdfbox.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件工具
 *
 * @author hankun
 */
public class XEasyPdfFileUtil {

    /**
     * 创建目录
     *
     * @param path 路径
     * @return 返回路径
     */
    public static Path createDirectories(Path path) {
        // 如果目录不存在，则创建
        if (!Files.exists(path)) {
            try {
                // 定义目录路径
                Path directoryPath;
                // 如果给定路径不为目录，则初始化为父目录
                if (!Files.isDirectory(path)) {
                    // 初始化为父目录
                    directoryPath = path.getParent();
                }
                // 否则初始化为给定目录
                else {
                    // 初始化为给定目录
                    directoryPath = path;
                }
                // 创建目录
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                // 提示错误信息
                throw new IllegalArgumentException("the path is illegal and create directory fail");
            }
        }
        // 返回路径
        return path;
    }
}
