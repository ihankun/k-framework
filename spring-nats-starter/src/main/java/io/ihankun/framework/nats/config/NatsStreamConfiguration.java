package io.ihankun.framework.nats.config;

import io.ihankun.framework.nats.core.DefaultNatsStreamTemplate;
import io.ihankun.framework.nats.core.NatsStreamListenerDetector;
import io.ihankun.framework.nats.core.NatsStreamTemplate;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * nats 配置
 *
 * @author hankun
 */
@Slf4j
//@AutoConfiguration(after = NatsConfiguration.class)
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
