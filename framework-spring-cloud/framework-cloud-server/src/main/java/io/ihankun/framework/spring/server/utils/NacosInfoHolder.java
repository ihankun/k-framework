package io.ihankun.framework.spring.server.utils;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import io.ihankun.framework.core.utils.date.DateUtils;
import io.ihankun.framework.core.utils.spring.ServerStateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@ConfigurationProperties("kun.nacos.config")
public class NacosInfoHolder {

    @Setter
    @Getter
    private boolean testEnv = false;

    private static final String MARK_DEVELOPER = "dev";

    private static final String MARK_GRAY = "gray";

    @Bean
    public NacosDiscoveryProperties nacosProperties() {
        //更改服务详情中的元数据，增加服务注册时间
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        properties.getMetadata().put("startup.time", DateUtils.getNowStr());
        properties.getMetadata().put("version", ServerStateUtil.getVersion());

        // 根据启动的服务状态设置 mark 标记
//        if (!StringUtils.isEmpty(ServerStateUtil.getGrayMark()) && !Boolean.FALSE.toString().equalsIgnoreCase(ServerStateUtil.getGrayMark())) {
//            if (Boolean.TRUE.toString().equalsIgnoreCase(ServerStateUtil.getGrayMark())) {
//                properties.getMetadata().put("mark", MARK_GRAY);
//            } else {
//                properties.getMetadata().put("mark", ServerStateUtil.getGrayMark());
//            }
//        }

        //本地启动服务时 mark 标记处理
//        if (CheckRunInIDEA() && testEnv) {
//            runIdea(properties);
//        }

        log.info("NacosInfoHolder.nacosProperties.init.with.data,version={},gray={}", ServerStateUtil.getVersion(), ServerStateUtil.getGrayMark());
        return properties;
    }


    private void runIdea(NacosDiscoveryProperties properties) {
        //如果没有内容，则设置默认内容为 develop
        if (StringUtils.isEmpty(ServerStateUtil.getGrayMark())) {
            properties.getMetadata().put("mark", MARK_DEVELOPER);
        }

        //设置 nacos 中 mark 标记
//        String mark = properties.getMetadata().get("mark");
//        String debug = ServerStateUtil.getDebug();
//        if (StringUtils.isNotEmpty(debug) && !mark.contains(debug)) {
//            properties.getMetadata().put("mark", mark + "@" + debug);
//        }
    }

    /**
     * 判断是否在本地 IDEA 中运行，如果是，则认为是本地调试，不参与到负载均衡逻辑中
     */
    private boolean checkRunInIdea() {
        try {
            Class.forName("com.intellij.rt.execution.application.AppMainV2");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
