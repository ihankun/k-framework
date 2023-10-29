package io.ihankun.framework.db.exceptions;

/**
 * @author hankun
 */
public class DbException extends RuntimeException{

    public DbException(String message) {
        super(message);
    }

    public DbException(Throwable throwable) {
        super(throwable);
    }

    public DbException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
