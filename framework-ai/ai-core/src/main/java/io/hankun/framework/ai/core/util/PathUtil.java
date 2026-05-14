package io.hankun.framework.ai.core.util;

import org.springframework.util.ObjectUtils;

/**
 * @description:
 * @className: PathUtil
 * @createAt: 2025/6/5 17:39
 * @author: hankun
 */
public class PathUtil {

    public static final String SPLIT = "/";

    /**
     * 合并两个路径字符串，确保正确处理路径分隔符
     *
     * @param pathA 第一个路径部分
     * @param pathB 第二个路径部分
     * @return 合并后的完整路径
     */
    public static String mergePath(String pathA, String pathB) {
        //如果pathB为空，则返回pathA
        if (ObjectUtils.isEmpty(pathB)) {
            return pathA;
        }

        // 如果 pathA 以 '/' 结尾
        if (pathA.endsWith(SPLIT)) {
            // 如果 pathB 也以 '/' 开头，则去掉 pathB 的第一个 '/'
            if (pathB.startsWith(SPLIT)) {
                return pathA + pathB.substring(1);
            }
            // pathB 不以 '/' 开头，直接拼接
            return pathA + pathB;
        }

        // 如果 pathA 不以 '/' 结尾，但 pathB 以 '/' 开头，直接拼接
        if (pathB.startsWith(SPLIT)) {
            return pathA + pathB;
        }

        // pathA 不以 '/' 结尾，pathB 也不以 '/' 开头，添加 '/' 后拼接
        return pathA + SPLIT + pathB;
    }
}
