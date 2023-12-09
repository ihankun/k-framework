package io.ihankun.framework.mongoplus.plugin.transactional;

import io.ihankun.framework.mongoplus.annotation.transactional.MongoTransactional;
import io.ihankun.framework.mongoplus.cache.global.MongoClientCache;
import io.ihankun.framework.mongoplus.context.MongoTransactionContext;
import io.ihankun.framework.mongoplus.context.MongoTransactionStatus;
import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hankun
 **/
public class MongoTransactionalAspect implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MongoTransactionalAspect.class);

    public MongoTransactionalAspect(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    private MongoClient mongoClient;

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (mongoClient == null){
            mongoClient = MongoClientCache.mongoClient;
        }
        AtomicReference<Object> invoke = new AtomicReference<>();
        Optional.ofNullable(inv.method().getAnnotation(MongoTransactional.class)).map(mongoTransactional -> {
            //开启事务
            startTransaction();
            try {
                invoke.set(inv.invoke());
                //提交事务
                commitTransaction();
                return invoke;
            } catch (Throwable e) {
                logger.error("Mongo Execute Error,Rolling back soon");
                //回滚
                rollbackTransaction();
                throw new RuntimeException(e);
            }
        });
        return invoke.get();
    }

    /**
     * 事务开启
     * @author hankun
     * @date 2023/7/30 18:15
     */
    private void startTransaction() {
        //获取线程中的session
        ClientSession session = MongoTransactionContext.getClientSessionContext();
        if (session == null) {
            session = mongoClient.startSession(ClientSessionOptions.builder().causallyConsistent(true).build());
            session.startTransaction();
            MongoTransactionStatus status = new MongoTransactionStatus(session);
            MongoTransactionContext.setTransactionStatus(status);
        }
        // 每个被切到的方法都引用加一
        MongoTransactionContext.getMongoTransactionStatus().incrementReference();
        if (logger.isDebugEnabled()) {
            logger.debug("Mongo transaction created, Thread:{}, session hashcode:{}", Thread.currentThread().getName(), session.hashCode());
        }
    }

    /**
     * 事务提交
     * @author hankun
     * @date 2023/7/30 18:15
     */
    private void commitTransaction() {
        MongoTransactionStatus status = MongoTransactionContext.getMongoTransactionStatus();
        if (status == null) {
            logger.warn("no session to commit.");
            return;
        }
        status.decrementReference();
        if (status.readyCommit()) {
            ClientSession clientSession = status.getClientSession();
            if (clientSession.hasActiveTransaction()){
                clientSession.commitTransaction();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Mongo transaction committed, Thread:{}, session hashcode:{}", Thread.currentThread().getName(), status.getClientSession().hashCode());
        }
    }

    /**
     * 事务回滚
     * @author hankun
     * @date 2023/7/30 18:16
     */
    private void rollbackTransaction() {
        MongoTransactionStatus status = MongoTransactionContext.getMongoTransactionStatus();
        if (status == null) {
            logger.warn("no session to rollback.");
            return;
        }
        // 清空计数器
        status.clearReference();
        ClientSession clientSession = status.getClientSession();
        if (clientSession.hasActiveTransaction()){
            clientSession.abortTransaction();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Mongo transaction rolled back, Thread:{}, session hashcode:{}", Thread.currentThread().getName(), status.getClientSession().hashCode());
        }
    }

    private void closeSession() {
        MongoTransactionStatus status = MongoTransactionContext.getMongoTransactionStatus();
        if (status == null) {
            logger.warn("no session to rollback.");
            return;
        }
        if (status.readyClose()) {
            try {
                ClientSession clientSession = status.getClientSession();
                if (clientSession.hasActiveTransaction()){
                    clientSession.close();
                }
            } finally {
                // 确保清理线程变量时不会被打断
                MongoTransactionContext.clear();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Mongo transaction closed, Thread:{}, session hashcode:{}", Thread.currentThread().getName(), status.getClientSession().hashCode());
        }
    }
}
