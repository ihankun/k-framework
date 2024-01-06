package io.ihankun.framework.mongoplus.interceptor.business;

import io.ihankun.framework.mongoplus.cache.global.OrderCache;
import io.ihankun.framework.mongoplus.interceptor.Interceptor;
import io.ihankun.framework.mongoplus.model.command.CommandFailed;
import io.ihankun.framework.mongoplus.model.command.CommandStarted;
import io.ihankun.framework.mongoplus.model.command.CommandSucceeded;
import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 防止全表更新和删除的拦截器
 *
 * @author hankun
 **/
public class BlockAttackInnerInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(BlockAttackInnerInterceptor.class);

    @Override
    public void commandStarted(CommandStarted commandStarted) {
        if ("update".equals(commandStarted.getCommandName()) || "delete".equals(commandStarted.getCommandName())) {
            BsonValue filter = commandStarted.getCommandDocument().get(commandStarted.getCommandName() + "s").asArray().get(0).asDocument().get("q");
            if (filter == null || filter.asDocument().isEmpty()) {
                logger.error("Prohibition of collection {} operation",commandStarted.getCommandName());
                throw new IllegalArgumentException("Prohibition of collection " + commandStarted.getCommandName() +" operation");
            }
        }
    }

    @Override
    public void commandSucceeded(CommandSucceeded commandSucceeded) {
        //不做任何操作
    }

    @Override
    public void commandFailed(CommandFailed commandFailed) {
        //不做任何操作
    }

    @Override
    public int getOrder() {
        return OrderCache.BLOCK_ATTACK_INNER_ORDER;
    }
}
