package io.ihankun.framework.commons.async.callback;


import io.ihankun.framework.commons.async.wrapper.WorkerWrapper;

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
