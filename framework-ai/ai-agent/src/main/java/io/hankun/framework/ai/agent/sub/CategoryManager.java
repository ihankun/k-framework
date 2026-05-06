package io.hankun.framework.ai.agent.sub;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: CategoryManager
 * @createAt: 2025/9/28 09:13
 * @author: hankun
 */
@Component
public class CategoryManager {

    private final Map<String, CategoryNode> categoryMap = new HashMap<>();

    public CategoryManager(List<CategoryNode> categoryNodes) {
        for (CategoryNode categoryNode : categoryNodes) {
            categoryMap.put(categoryNode.getName(), categoryNode);
        }
    }

    public CategoryNode getCategory(String categoryName) {
        return categoryMap.get(categoryName);
    }

    public List<CategoryNode> getCategories(List<String> categoryNames) {
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        List<CategoryNode> categories = new ArrayList<>(categoryNames.size());
        for (String categoryName : categoryNames) {
            CategoryNode categoryNode = categoryMap.get(categoryName);
            if (categoryNode == null) {
                continue;
            }
            categories.add(categoryNode);
        }
        return categories;
    }
}
