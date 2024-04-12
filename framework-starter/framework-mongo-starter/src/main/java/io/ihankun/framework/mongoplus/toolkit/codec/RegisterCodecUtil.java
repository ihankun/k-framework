package io.ihankun.framework.mongoplus.toolkit.codec;

import io.ihankun.framework.mongoplus.cache.codec.CodecCache;
import io.ihankun.framework.mongoplus.cache.codec.CodecRegistryCache;
import io.ihankun.framework.mongoplus.codec.GenericCodec;
import io.ihankun.framework.mongoplus.toolkit.ClassTypeUtil;
import io.ihankun.framework.mongoplus.toolkit.CollUtil;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.*;
import java.util.stream.Collectors;

public class RegisterCodecUtil {

    public static List<CodecRegistry> codecRegistryList = new ArrayList<>();

    public static <T> CodecRegistry registerCodec(T t){
        return registerCodec(ClassTypeUtil.getAllClass(t));
    }

    public static <T> CodecRegistry registerCodec(Map<?,?> map){
        if (map == null){
            return CodecRegistries.fromRegistries(codecRegistryList);
        }
        return registerCodec(new HashSet<Class<?>>(){{
            map.values().forEach(m -> {
                if (m instanceof List){
                    System.out.println("找到集合了找到集合了");
                    List<?> list = (List<?>) m;
                    if (CollUtil.isEmpty(list)){
                        return;
                    }
                    addAll(ClassTypeUtil.getAllClass(list.get(0)));
                } else if (m instanceof Map){
                    registerCodec((Map<?, ?>) m);
                }else {
                    addAll(ClassTypeUtil.getAllClass(m));
                }
            });
        }});
    }

    public static CodecRegistry registerCodec(Set<Class<?>> fieldClasses){
        if (CollUtil.isNotEmpty(CodecRegistryCache.getCodecRegistry())){
            codecRegistryList.addAll(CodecRegistryCache.getCodecRegistry());
        }
        fieldClasses = fieldClasses.stream().filter(clazz -> !CodecCache.codecMap.containsKey(clazz)).collect(Collectors.toSet());
        fieldClasses.parallelStream().forEach(clazz -> {
            Codec<?> codec = new GenericCodec<>(clazz);
            CodecCache.codecMap.put(clazz,codec);
            codecRegistryList.add(CodecRegistries.fromCodecs(codec));
        });
        return CodecRegistries.fromRegistries(codecRegistryList);
    }

    public static CodecRegistry getCodecCacheAndDefault(){
        codecRegistryList.addAll(CodecRegistryCache.codecRegistryList);
        return CodecRegistries.fromRegistries(codecRegistryList);
    }

}
