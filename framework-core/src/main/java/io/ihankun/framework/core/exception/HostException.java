package io.ihankun.framework.core.exception;

import java.io.IOException;

/**
 * @author hankun
 */
public class HostException extends RuntimeException{

    public HostException(IOException cause) {
        super(cause);
    }
}
