package io.ihankun.framework.common.v1.exception;

import java.io.IOException;

/**
 * @author hankun
 */
public class HostException extends RuntimeException{

    public HostException(IOException cause) {
        super(cause);
    }
}
