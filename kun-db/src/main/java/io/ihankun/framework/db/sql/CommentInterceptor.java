package io.ihankun.framework.db.sql;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.context.LoginUserInfo;
import io.ihankun.framework.common.utils.SpringHelpers;
import io.ihankun.framework.db.config.DbConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Connection;

/**
 * 功能描述:
 * @author: hankun
 * @date: 2023/9/27
 */
@Component
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class CommentInterceptor implements Interceptor {
    @Resource
    private DbConfig config;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {


        try {

            boolean comment = config.isComment();
            if (!comment) {
                return invocation.proceed();
            }


            StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());

            String applicationName = SpringHelpers.context().getEnvironment().getProperty("spring.application.name", "default");

            LoginUserInfo userInfo = LoginUserContext.get();
            String deviceIp = userInfo == null ? "" : (StrUtil.isEmpty(userInfo.getDeviceIp()) ? "" : userInfo.getDeviceIp());
            String userId = userInfo == null ? "" : (userInfo.getUserId() == null ? "" : userInfo.getUserId().toString());
            String userName = userInfo == null ? "" : (StrUtil.isEmpty(userInfo.getUserName()) ? "" : userInfo.getUserName());

            //格式：/*SQL_COMMENT_START#服务名^客户端IP^用户ID^用户名称#MSUN_COMMENT_END*/
            String sql = String.format("/*SQL_COMMENT_START#%s^%s^%s^%s#SQL_COMMENT_END*/\n", applicationName, deviceIp, userId, userName);

            sql = sql + statementHandler.getBoundSql().getSql();

            // 使用反射设置修改后的 SQL 语句
            Field sqlField = statementHandler.getBoundSql().getClass().getDeclaredField("sql");
            sqlField.setAccessible(true);
            sqlField.set(statementHandler.getBoundSql(), sql);

            // 继续执行后续操作
            return invocation.proceed();

        } catch (Exception e) {
            return invocation.proceed();
        }

    }
}
