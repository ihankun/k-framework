package io.ihankun.framework.poi.handler.inter;

/**
 * 接口自定义处理类
 *
 * @author hankun
 */
public interface IReadHandler<T> {
    /**
     * 处理解析对象
     * @param t
     */
    public void handler(T t);


    /**
     * 处理完成之后的业务
     */
    public void doAfterAll();

}
