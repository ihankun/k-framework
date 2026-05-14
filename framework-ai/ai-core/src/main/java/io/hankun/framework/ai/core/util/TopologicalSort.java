package io.hankun.framework.ai.core.util;

import java.util.*;

/**
 * @description:
 * @className: TopologicalSort
 * @createAt: 2025/9/29 17:24
 * @author: hankun
 */
public record TopologicalSort<T>(Map<T, List<T>> adjacencyList) {

    public List<T> topologicalSortKahn() {
        // 计算每个顶点的入度
        Map<T, List<T>> inDegree = new HashMap<>();
        for (Map.Entry<T, List<T>> entry : adjacencyList.entrySet()) {
            inDegree.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
            for (T neighbor : entry.getValue()) {
                inDegree.computeIfAbsent(neighbor, k -> new ArrayList<>()).add(entry.getKey());
            }
        }
        // 将入度为0的顶点加入队列
        Queue<T> queue = new LinkedList<>();
        for (Map.Entry<T, List<T>> entry : inDegree.entrySet()) {
            if (entry.getValue().isEmpty()) {
                queue.add(entry.getKey());
            }
        }
        List<T> result = new ArrayList<>();
        // 处理队列中的顶点
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);

            // 减少相邻顶点的入度
            for (T neighbor : adjacencyList.get(vertex)) {
                List<T> neighborInDegree = inDegree.get(neighbor);
                neighborInDegree.remove(vertex);
                if (neighborInDegree.isEmpty()) {
                    queue.add(neighbor);
                }
            }
        }
        // 检查是否存在环
        if (result.size() != adjacencyList.size()) {
            throw new IllegalArgumentException("图中存在环，无法进行拓扑排序");
        }
        return result;
    }

    public static class Builder<T> {
        private final Map<T, List<T>> adjacencyList = new HashMap<>();

        public Builder<T> addEdge(T source, T destination) {
            adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
            adjacencyList.computeIfAbsent(destination, k -> new ArrayList<>());
            return this;
        }

        public TopologicalSort<T> build() {
            return new TopologicalSort<>(adjacencyList);
        }
    }
}
