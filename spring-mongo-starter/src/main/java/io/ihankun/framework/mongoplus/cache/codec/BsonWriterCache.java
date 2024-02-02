package io.ihankun.framework.mongoplus.cache.codec;

import org.bson.BsonWriter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hankun
 **/
public class BsonWriterCache {

    public static Map<Class<?>, BsonWriter> bsonWriterMap = new ConcurrentHashMap<>();

}
