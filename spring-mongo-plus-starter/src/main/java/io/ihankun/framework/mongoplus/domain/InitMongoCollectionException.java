package io.ihankun.framework.mongoplus.domain;

/**
 * @Description: 初始化连接异常
 * @BelongsProject: mongo
 * @BelongsPackage: io.ihankun.framework.mongoplus.domain
 * @Author: hankun
 * @CreateTime: 2023-02-18 15:09
 * @Version: 1.0
 */
public class InitMongoCollectionException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InitMongoCollectionException(String message){
        super();
        this.message = message;
    }

    public InitMongoCollectionException(){
        super();
    }
}
