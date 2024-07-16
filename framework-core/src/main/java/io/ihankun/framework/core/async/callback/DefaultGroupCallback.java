package io.ihankun.framework.core.async.callback;


import io.ihankun.framework.core.async.wrapper.WorkerWrapper;

import java.util.List;

/**
 * @author hankun
 *
 */
public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {

    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {

    }
}
