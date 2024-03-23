package io.ihankun.framework.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.ihankun.framework.common.v1.utils.spring.SpringHelpers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractConsumer {

    private static final String CONSUMER_KEY = "mq:consume:";
    private static final int CONSUMER_TIME = 1;
    private static final TimeUnit CONSUMER_TIME_UNIT = TimeUnit.HOURS;


    private StringRedisTemplate getMqRedisHelper() {

        try {
            StringRedisTemplate template = SpringHelpers.context().getBean(StringRedisTemplate.class);
            return template;
        } catch (Exception e) {
            log.error("AbstractConsumer.getMqRedisHelper,exception", e);
            throw e;
        }
    }

    /**
     * 标记此消息已经被消费,确认消费
     */

    protected void confirmConsumed(String messageId) {
        try {
            Boolean result = getMqRedisHelper().opsForValue().setIfAbsent(CONSUMER_KEY + messageId, String.valueOf(System.currentTimeMillis()), CONSUMER_TIME, CONSUMER_TIME_UNIT);
            log.info("AbstractConsumer.confirmConsumed,messageId={},result={}", messageId, result);
        } catch (Exception e) {
            log.error("AbstractConsumer.confirmConsumed,exception", e);
        }
    }

    /**
     * 检查是否被消费
     *
     * @param messageId
     * @return
     */
    @SneakyThrows
    protected boolean checkConsumed(String messageId) {

        try {
            boolean exits = !org.springframework.util.StringUtils.isEmpty(getMqRedisHelper().opsForValue().get(CONSUMER_KEY + messageId));
            log.info("AbstractConsumer.checkConsumed,messageId={},consumed={}", messageId, exits);
            return exits;
        } catch (Exception e) {
            log.warn("AbstractConsumer.checkConsumed.redis not init,place check [mq.redis.url] config");
        }
        return false;
    }


    /**
     * 重置消息的消费
     *
     * @param idempotentKey
     */
    protected void resetConsumed(String idempotentKey) {
        try {
            getMqRedisHelper().delete(CONSUMER_KEY + idempotentKey);
            log.info("AbstractConsumer.resetConsumed,idempotentKey={}", idempotentKey);
        } catch (Exception e) {
            log.error("AbstractConsumer.resetConsumed,exception", e);
        }
    }


    protected Object objectToClass(Object data, ConsumerListener listener) {

        try {
            //当前对象泛型获取
            Type[] genericInterfaces = listener.getClass().getGenericInterfaces();

            if (genericInterfaces.length == 0) {
                //父类泛型获取
                genericInterfaces = listener.getClass().getSuperclass().getGenericInterfaces();
            }

            //泛型类型获取
            Type type = ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];

            //支持没有类型的数据
            if (type == null) {
                return data;
            }

            //支持简单类型
            String[] simpleType = {String.class.getTypeName(), Long.class.getTypeName(), Integer.class.getTypeName()};

            if (Arrays.stream(simpleType).anyMatch(item -> item.equals(type.getTypeName()))) {
                return data;
            }

            return JSON.parseObject(JSON.parse(JSON.toJSONString(data)).toString(), type);

        } catch (Exception e) {
            log.error("AbstractConsumer.objectToClass,exception", e);
            return data;
        }
    }


    /**
     * 获取自定义主键
     *
     * @param config
     * @param msgId
     * @param data
     * @return
     */
    protected String getIdempotentKey(ConsumerListenerConfig config, String msgId, Object data) {
        if (StringUtils.isEmpty(config.getIdempotentKey())) {
            return msgId;
        }

        try {
            JSONObject dataObj = (JSONObject) JSON.toJSON(data);
            return dataObj.getString(config.getIdempotentKey());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }
}
