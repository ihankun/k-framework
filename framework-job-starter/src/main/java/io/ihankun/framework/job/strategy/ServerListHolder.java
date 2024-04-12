package io.ihankun.framework.job.strategy;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author hankun
 */
@Slf4j
@Component
public class ServerListHolder {

    public static final class ServerHolder {
        private static final ServerListHolder HOLDER = new ServerListHolder();
    }


    public static ServerListHolder ins() {
        return ServerHolder.HOLDER;
    }

    public void setNacosDiscoveryProperties(NacosDiscoveryProperties properties) {
        this.nacosDiscoveryProperties = properties;
    }

    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private static final String META_DATA_VERSION = "mark";

    private static final String GRAY_MARK = "gray";

    private static final String INSTANCE_VERSION = "version";

    public boolean checkVersion(List<String> list) {
        List<String> grayList = new ArrayList<>(1);
        Set<String> versions = new HashSet<>(2);
        try {
            List<Instance> instanceList = nacosDiscoveryProperties.namingServiceInstance().getAllInstances(nacosDiscoveryProperties.getService());
            for (Instance instance : instanceList) {
                String meta = instance.getMetadata().get(META_DATA_VERSION);
                String version = instance.getMetadata().get(INSTANCE_VERSION);
                version = StringUtils.trim(version);
                versions.add(version);
                if (StringUtils.isNotEmpty(meta) && GRAY_MARK.equalsIgnoreCase(meta)) {
                    grayList.add(instance.getIp());
                }
                log.info("ServerListHolder.checkVersion.instance.info,ip={},metadata={}", instance.getIp(), instance.getMetadata());
            }
            log.info("ServerListHolder.checkVersion.get.instance.success,grayIps={},versions={}", grayList, versions);
        } catch (NacosException e) {
            log.error("ServerListHolder.checkVersion.get.instance.fail,e=", e);
            return false;
        }
        if (versions.size() <= 1 || grayList.size() == 0) {
            return false;
        }
        list.addAll(grayList);
        return true;
    }
}
