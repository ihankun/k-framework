package io.ihankun.framework.mongoplus.conditions.interfaces;

import io.ihankun.framework.mongoplus.support.SFunction;

import java.io.Serializable;

public interface Update<Children, T> extends Serializable {

    Children set(boolean condition, SFunction<T,Object> column, Object value);

    Children set(SFunction<T,Object> column, Object value);

    Children set(boolean condition, String column, Object value);

    Children set(String column, Object value);

}
