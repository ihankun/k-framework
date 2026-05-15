package io.hankun.framework.nat.config;

import io.hankun.framework.nat.core.DefaultNatsStreamTemplate;
import io.hankun.framework.nat.core.NatsStreamListenerDetector;
import io.hankun.framework.nat.core.NatsStreamTemplate;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * nats stream 配置
 *
 * @author hankun
 */
@Slf4j
@AutoConfiguration(after = NatsConfiguration.class)
@ConditionalOnProperty(
    prefix = NatsStreamProperties.PREFIX,
    name = "enable",
    havingValue = "true"
)
@ConditionalOnClass(Options.class)
public class NatsStreamConfiguration {

    @Bean
    public JetStream natsJetStream(Connection natsConnection) throws IOException {
        return natsConnection.jetStream();
    }

    @Bean
    public NatsStreamListenerDetector natsStreamListenerDetector(NatsStreamProperties properties,
                                                                 ObjectProvider<NatsStreamCustomizer> natsStreamCustomizerObjectProvider,
                                                                 Connection natsConnection,
                                                                 JetStream natsJetStream) {
        return new NatsStreamListenerDetector(properties, natsStreamCustomizerObjectProvider, natsConnection, natsJetStream);
    }

    @Bean
    public NatsStreamTemplate natsStreamTemplate(JetStream natsJetStream) {
        return new DefaultNatsStreamTemplate(natsJetStream);
    }

}
