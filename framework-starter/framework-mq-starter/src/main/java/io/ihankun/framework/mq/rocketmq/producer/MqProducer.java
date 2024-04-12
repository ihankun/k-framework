package io.ihankun.framework.mq.rocketmq.producer;

import io.ihankun.framework.core.utils.ip.IpUtil;
import io.ihankun.framework.core.utils.spring.ServerStateUtil;
import io.ihankun.framework.mq.constants.EnvMark;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.latency.MQFaultStrategy;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Optional;

/**
 * @author hankun
 */
public class MqProducer extends DefaultMQProducer {

    private final InternalLogger log = ClientLogger.getLog();
    protected final transient DefaultMQProducerImpl defaultMQProducerImpl;
    public static final String CLIENT_ID_SPLIT = "@";
    private String grayMark;

    public MqProducer(String producerGroup, MQFaultStrategy mqFaultStrategy) {
        this(producerGroup, mqFaultStrategy, null, Optional.ofNullable(ServerStateUtil.getGrayMark()).orElse("others"));
    }

    public MqProducer(String producerGroup, MQFaultStrategy mqFaultStrategy, RPCHook rpcHook) {
        this(producerGroup, mqFaultStrategy, rpcHook, Optional.ofNullable(ServerStateUtil.getGrayMark()).orElse("others"));
    }

    public MqProducer(String producerGroup, MQFaultStrategy mqFaultStrategy, RPCHook rpcHook, String grayMark) {
        this.setProducerGroup(producerGroup);
        this.defaultMQProducerImpl = new MqProducerImpl(this, mqFaultStrategy, rpcHook);
        this.grayMark = grayMark;
    }

    @Override
    public void start() throws MQClientException {
        this.defaultMQProducerImpl.start();
        if (null != this.getTraceDispatcher()) {
            try {
                this.getTraceDispatcher().start(this.getNamesrvAddr());
            } catch (MQClientException e) {
                log.warn("trace dispatcher start failed ", e);
            }
        }
    }

    @Override
    public SendResult send(
            Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQProducerImpl.send(msg);
    }

    @Override
    public void send(Message msg, SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException {
        this.defaultMQProducerImpl.send(msg, sendCallback);
    }

    /**
     * 重写生产者ClientId的生成方式，用于区分生产和灰度节点
     * @return
     */
    @Override
    public String buildMQClientId() {
        StringBuilder sb = new StringBuilder();
        if (Boolean.FALSE.toString().equalsIgnoreCase(grayMark)) {
            // 生产
            sb.append(EnvMark.PROD.getEnv());
        } else if (Boolean.TRUE.toString().equalsIgnoreCase(grayMark)) {
            // 灰度
            sb.append(EnvMark.GRAY.getEnv());
        }
        sb.append(CLIENT_ID_SPLIT);
        // sb.append(this.getClientIP());
        sb.append(IpUtil.getIp());
        sb.append(CLIENT_ID_SPLIT);
        sb.append(this.getInstanceName());
        if (!UtilAll.isBlank(this.getUnitName())) {
            sb.append(CLIENT_ID_SPLIT);
            sb.append(this.getUnitName());
        }

        return sb.toString();
    }
}
