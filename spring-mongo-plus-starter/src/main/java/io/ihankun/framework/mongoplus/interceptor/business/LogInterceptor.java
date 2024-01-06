package io.ihankun.framework.mongoplus.interceptor.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.ihankun.framework.mongoplus.cache.global.OrderCache;
import io.ihankun.framework.mongoplus.cache.global.PropertyCache;
import io.ihankun.framework.mongoplus.interceptor.Interceptor;
import io.ihankun.framework.mongoplus.model.command.CommandFailed;
import io.ihankun.framework.mongoplus.model.command.CommandStarted;
import io.ihankun.framework.mongoplus.model.command.CommandSucceeded;

import java.util.Objects;

/**
 * Mongo拦截器，这里可以打印日志
 * @author hankun
 * @date 2023/11/22 10:54
*/
public class LogInterceptor implements Interceptor {

    private String formattingStatement(String statement){
        return PropertyCache.format ? JSON.toJSONString(JSONObject.parse(statement), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat) : statement;
    }

    @Override
    public void commandStarted(CommandStarted commandStarted) {
        System.out.println(commandStarted.getCommandName()+" Statement Execution ==> ");
        System.out.println(formattingStatement(commandStarted.getCommand()));
    }

    @Override
    public void commandSucceeded(CommandSucceeded commandSucceeded) {
        if (Objects.equals(commandSucceeded.getCommandName(), "find") || Objects.equals(commandSucceeded.getCommandName(), "aggregate")){
            System.out.println(commandSucceeded.getCommandName()+" results of execution ==> ");
            System.out.println(commandSucceeded.getResponse().getDocument("cursor").get("firstBatch").asArray().getValues().size());
        } else if (Objects.equals(commandSucceeded.getCommandName(), "insert") || Objects.equals(commandSucceeded.getCommandName(), "delete") || Objects.equals(commandSucceeded.getCommandName(),"update")) {
            System.out.println(commandSucceeded.getCommandName()+" results of execution ==> ");
            System.out.println(commandSucceeded.getResponse().get("n").asInt32().getValue());
        }
    }

    @Override
    public void commandFailed(CommandFailed commandFailed) {
        String commandName = commandFailed.getCommandName();
        Throwable throwable = commandFailed.getThrowable();
        System.out.println("error ==> : " + commandName + ", " + throwable.getMessage());
    }

    @Override
    public int getOrder() {
        return OrderCache.LOG_ORDER;
    }
}
