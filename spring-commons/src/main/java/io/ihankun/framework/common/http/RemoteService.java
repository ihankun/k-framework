package io.ihankun.framework.common.http;


import io.ihankun.framework.common.exception.impl.CommonErrorCode;
import io.ihankun.framework.common.exception.BusinessException;
import io.ihankun.framework.common.response.ResponseResult;

/**
 * @author hankun
 */
public class RemoteService {

    /**
     * 调用函数
     *
     * @param result   远程接口的返回结果
     * @param callBack 回调接口
     * @return ResponseResult的数据
     */
    public static <T> T invoke(ResponseResult<T> result, RemoteCallBack<T> callBack) {
        if (result == null) {
            callBack.onFailure(BusinessException.build(CommonErrorCode.RESULT_NULL));
            return null;
        }
        if (!result.isSuccess()) {
            callBack.onFailure(BusinessException.build(result.convert()));
            return null;
        }
        callBack.onSuccess(result.getData());
        return result.getData();
    }
}
