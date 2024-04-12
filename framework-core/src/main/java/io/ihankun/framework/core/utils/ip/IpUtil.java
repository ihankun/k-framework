package io.ihankun.framework.core.utils.ip;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.core.exception.HostException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author hankun
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtil {

    /**
     * IP地址的正则表达式.
     */
    public static final String IP_REGEX = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})";

    private static volatile String cachedIpAddress;
    private static final String DOCKER_NAME = "docker";

    /**
     * 是否跳过docker网卡
     */
    private static final String SKIP_DOCKER_ENV = "ihankun.skip.docker";

    /**
     * 是否跳过docker网卡
     */
    private static Boolean SKIP_DOCKER = Boolean.TRUE;

    /**
     * 获取本机IP地址.
     *
     * <p>
     * 有限获取外网IP地址.
     * 也有可能是链接着路由器的最终IP地址.
     * </p>
     *
     * @return 本机IP地址
     */
    public static String getIp() {
        if (null != cachedIpAddress) {
            return cachedIpAddress;
        }
        String skipDockerEnv = System.getProperty(SKIP_DOCKER_ENV);
        if (!StringUtils.isEmpty(skipDockerEnv)) {
            try {
                SKIP_DOCKER = Boolean.parseBoolean(skipDockerEnv);
            } catch (Exception e) {
                log.warn("env ihankun.skip.parse failed,", e);
            }
        }
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException ex) {
            throw new HostException(ex);
        }
        String localIpAddress = null;
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            log.info("elastic-job.netInterface.info：{},", JSON.toJSON(netInterface));

            //如果是docker网卡的话直接跳过
            if (SKIP_DOCKER && netInterface.getName().contains(DOCKER_NAME)) {
                log.info("docker网卡信息：{}，直接跳过", netInterface);
                continue;
            }
            Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress ipAddress = ipAddresses.nextElement();
                if (isPublicIpAddress(ipAddress)) {
                    String publicIpAddress = ipAddress.getHostAddress();
                    cachedIpAddress = publicIpAddress;
                    return publicIpAddress;
                }
                if (isLocalIpAddress(ipAddress)) {
                    localIpAddress = ipAddress.getHostAddress();
                }
            }
        }
        cachedIpAddress = localIpAddress;
        log.info("ipUtil.ip:{}", cachedIpAddress);
        return localIpAddress;
    }

    private static boolean isPublicIpAddress(final InetAddress ipAddress) {
        return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isLocalIpAddress(final InetAddress ipAddress) {
        return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isV6IpAddress(final InetAddress ipAddress) {
        return ipAddress.getHostAddress().contains(":");
    }

    /**
     * 获取本机Host名称.
     *
     * @return 本机Host名称
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException ex) {
            throw new HostException(ex);
        }
    }
}
