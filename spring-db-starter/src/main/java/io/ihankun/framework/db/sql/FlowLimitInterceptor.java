package io.ihankun.framework.db.sql;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import io.ihankun.framework.common.id.IdGenerator;
import io.ihankun.framework.db.events.SqlExecEvent;
import io.ihankun.framework.db.exceptions.SqlFlowControlException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;

/**
 * @author hankun
 */
@Component
@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class FlowLimitInterceptor implements Interceptor, ApplicationEventPublisherAware {

    @Resource
    private FlowControlService flowControlService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MappedStatement mappedStatement;
        try {
            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            if (mappedStatement != null) {
                String sqlId = mappedStatement.getId();
                Long id = IdGenerator.ins().generator();
                log.info("FlowLimitInterceptor.publish.sql.exec.event,operateId={},sql={}", id, sqlId);
                applicationEventPublisher.publishEvent(SqlExecEvent.buildSqlEvent(this, sqlId, id));
                String reason = flowControlService.getReason(sqlId);
                if (reason != null) {
                    log.error("FlowLimitInterceptor.intercept,sqlId={}", sqlId);
                    throw new SqlFlowControlException("sql熔断，因为规则【" + reason + "】，sqlId=" + sqlId);
                }
            }
        } catch (SqlFlowControlException e) {
            throw e;
        } catch (Throwable e) {
            log.error("FlowLimitInterceptor.intercept.get.info.fail,e=!", e);
            return invocation.proceed();
        }

        return invocation.proceed();
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
