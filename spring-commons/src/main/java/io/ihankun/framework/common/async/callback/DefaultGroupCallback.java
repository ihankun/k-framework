package io.ihankun.framework.common.async.callback;


import io.ihankun.framework.common.async.wrapper.WorkerWrapper;

import java.util.List;

/**
 * @author hankun wrote on 2019-12-27
 * @version 1.0
 */
public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {

    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {

    }
}
