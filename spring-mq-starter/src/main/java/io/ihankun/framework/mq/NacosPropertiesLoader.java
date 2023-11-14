package io.ihankun.framework.mq;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * @author hankun
 */
@Component
@Slf4j
public class NacosPropertiesLoader {

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Getter
    private List<String> localIps = null;


    public Map<String, String> getProperties() {
        Map<String, String> res = new HashMap<>(4);
        if (localIps == null) {
            try {
                localIps = getIps();
            } catch (RuntimeException e) {
                log.error("NacosPropertiesLoader.getProperties.get.local.host.fail! e=", e);
            }
        }
        try {
            if (CollectionUtils.isEmpty(localIps)) {
                return null;
            }
            List<Instance> instanceList = nacosDiscoveryProperties.namingServiceInstance().getAllInstances(nacosDiscoveryProperties.getService());
            for (Instance instance : instanceList) {
                if (localIps.contains(instance.getIp())) {
                    res.putAll(instance.getMetadata());
                }
            }
        } catch (NacosException e) {
            log.error("NacosPropertiesLoader.getProperties.get.instance.fail,e=", e);
        }
        return res;
    }

    private static List<String> getIps() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException ex) {
            throw new RuntimeException(ex);
        }
        List<String> localIpAddress = new ArrayList<>();
        List<String> publicIps = new ArrayList<>();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress ipAddress = ipAddresses.nextElement();
                if (isPublicIpAddress(ipAddress)) {
                    publicIps.add(ipAddress.getHostAddress());
                }
                if (isLocalIpAddress(ipAddress)) {
                    localIpAddress.add(ipAddress.getHostAddress());
                }
            }
        }
        if (publicIps.size() > 0) {
            return publicIps;
        }
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
}
