package io.ihankun.framework.mongoplus.convert;

public interface CollectionNameConvert {

    <T> String convert(Class<T> entityClass);

}
