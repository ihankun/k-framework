package io.ihankun.framework.mongoplus.interceptor;

import io.ihankun.framework.mongoplus.domain.MongoPlusInterceptorException;
import io.ihankun.framework.mongoplus.model.command.CommandFailed;
import io.ihankun.framework.mongoplus.model.command.CommandStarted;
import io.ihankun.framework.mongoplus.model.command.CommandSucceeded;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInterceptor implements CommandListener {

    private final MongoPlusInterceptor mongoPlusInterceptor = new MongoPlusInterceptor();

    private final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    @Override
    public void commandStarted(CommandStartedEvent event) {
        try {
            mongoPlusInterceptor.commandStarted(new CommandStarted(event.getCommandName(),event.getCommand(),event.getCommand().toJson(),event));
        }catch (Exception e){
            logger.error("interceptor error: ",e);
            throw new MongoPlusInterceptorException(e);
        }
    }

    @Override
    public void commandSucceeded(CommandSucceededEvent event) {
        try {
            mongoPlusInterceptor.commandSucceeded(new CommandSucceeded(event.getCommandName(),event.getResponse(),event));
        }catch (Exception e){
            logger.error("interceptor error: ",e);
            throw new MongoPlusInterceptorException(e);
        }
    }

    @Override
    public void commandFailed(CommandFailedEvent event) {
        try {
            mongoPlusInterceptor.commandFailed(new CommandFailed(event.getCommandName(),event.getThrowable(),event));
        }catch (Exception e){
            logger.error("interceptor error: ",e);
            throw new MongoPlusInterceptorException(e);
        }
    }
}
