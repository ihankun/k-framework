//package io.ihankun.framework.db.limit.queue;
//
//import cn.hutool.core.collection.ConcurrentHashSet;
//import com.alibaba.nacos.common.utils.ConcurrentHashSet;
//import io.ihankun.framework.db.limit.element.RateSnapshotElement;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class RateSnapshotQueue {
//
//    /**
//     * 缓存队列
//     * key= businessKey
//     * value=队列情况
//     */
//    private final Map<String, Set<RateSnapshotElement>> queue = new ConcurrentHashMap<>(1);
//
//    /**
//     * 追加队列元素
//     *
//     * @param businessKey
//     * @param element
//     */
//    public void push(String businessKey, RateSnapshotElement element) {
//        queue.computeIfAbsent(businessKey, v -> new ConcurrentHashSet<>()).add(element);
//    }
//
//
//    /**
//     * 移除队列元素
//     *
//     * @param businessKey
//     * @param element
//     */
//    public void remove(String businessKey, RateSnapshotElement element) {
//        Set<RateSnapshotElement> set = queue.get(businessKey);
//        if (!CollectionUtils.isEmpty(set)) {
//            set.remove(element);
//        }
//    }
//
//    /**
//     * 当前队列快照情况
//     *
//     * @return
//     */
//    public Map<String, Set<RateSnapshotElement>> snapshot() {
//        return queue.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }
//}
