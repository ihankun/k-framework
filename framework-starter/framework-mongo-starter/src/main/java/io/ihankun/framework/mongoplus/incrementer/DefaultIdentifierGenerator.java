package io.ihankun.framework.mongoplus.incrementer;

import io.ihankun.framework.mongoplus.incrementer.id.Sequence;

import java.net.InetAddress;

/**
 * 默认生成器
 * @reference mp
 * @author hankun
 * @date 2023/8/11 1:10
*/
public class DefaultIdentifierGenerator implements IdentifierGenerator {

    private final Sequence sequence;

    /**
     * 共享默认单例
     * @author hankun
     * @date 2023/8/11 1:10
    */
    @Deprecated
    public DefaultIdentifierGenerator() {
        this.sequence = new Sequence(null);
    }

    public DefaultIdentifierGenerator(InetAddress inetAddress) {
        this.sequence = new Sequence(inetAddress);
    }

    public DefaultIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public DefaultIdentifierGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long nextId(Object entity) {
        return sequence.nextId();
    }

    public static DefaultIdentifierGenerator getInstance() {
        return DefaultInstance.INSTANCE;
    }

    private static class DefaultInstance {

        public static final DefaultIdentifierGenerator INSTANCE = new DefaultIdentifierGenerator();

    }

}
