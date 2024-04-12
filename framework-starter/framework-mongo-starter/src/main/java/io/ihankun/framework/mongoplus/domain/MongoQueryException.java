package io.ihankun.framework.mongoplus.domain;

/**
 * @author hankun
 * 查询异常
 * @since 2023-02-10 13:37
 **/
public class MongoQueryException extends RuntimeException {

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MongoQueryException(String message){
        super();
        this.message = message;
    }

    public MongoQueryException(){
        super();
    }

}
