package io.ihankun.framework.mongoplus.model.command;

/**
 * @author hankun
 * @project mongo-plus
 * @description 基础命令
 * @date 2023-11-22 14:17
 **/
public class BaseCommand {

    /**
     * 命令名称
     * @author hankun
     * @date 2023/11/22 14:21
    */
    private String commandName;

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public BaseCommand(String commandName) {
        this.commandName = commandName;
    }

    public BaseCommand() {
    }
}
