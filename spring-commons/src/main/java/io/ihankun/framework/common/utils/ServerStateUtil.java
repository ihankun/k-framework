package io.ihankun.framework.common.utils;

/**
 * @author hankun
 */
public class ServerStateUtil {

    private static final String GRAY_MARK = "kun.config.gray";
    private static final String SINGLE_MARK = "kun.config.single";
    private static final String VERSION = "kun.config.version";

    private static String version = null;
    private static Boolean singlePoint = null;
    private static String markValue;

    /**
     * 判断当前节点是否为灰度节点
     *
     * @return 当前节点是否为灰度节点
     */
    public static String getGrayMark() {
        //无需加锁
        if (markValue == null) {
            markValue = System.getProperty(GRAY_MARK);
            return markValue;
        }
        return markValue;
    }

    public static boolean getSinglePoint() {
        //无需加锁
        if (singlePoint == null) {
            boolean mark = Boolean.parseBoolean(System.getProperty(SINGLE_MARK));
            singlePoint = mark;
            return mark;
        }
        return singlePoint;
    }

    /**
     * 获取当前节点的版本号
     *
     * @return 版本号
     */
    public static String getVersion() {
        if (version == null) {
            String versions = System.getProperty(VERSION);
            version = versions;
            return versions;
        }
        return version;
    }

    /**
     * 设置节点灰度状态（不影响nacos），仅用于测试
     *
     * @param mark 设置的灰度标识，null->重置
     */
    public static void mockGrayMark(String mark) {
        markValue = mark;
    }

    /**
     * 设置节点版本号（不影响nacos），仅用于测试
     *
     * @param serverVersion 设置的版本号，null->重置
     */
    public static void mockVersion(String serverVersion) {
        version = serverVersion;
    }

    /**
     * 设置单点信息（不影响nacos），仅用于测试
     *
     * @param single 设置是否为单点，null->重置
     */
    public static void mockSingle(Boolean single) {
        singlePoint = single;
    }
}
