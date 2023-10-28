package io.ihankun.framework.common.exception;

import java.io.IOException;

public class HostException extends RuntimeException{

    public HostException(IOException cause) {
        super(cause);
    }
}
