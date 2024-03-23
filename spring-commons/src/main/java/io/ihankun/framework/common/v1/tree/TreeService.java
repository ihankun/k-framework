package io.ihankun.framework.common.v1.tree;

import io.ihankun.framework.common.v1.base.BaseService;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author hankun
 */
public interface TreeService extends BaseService {

    /**
     * 递归构建树
     * @param node 节点
     */
    default void buildTree(TreeNode node) {

        //1.查询所有下级权限，下级权限为空的话直接返回
        List<TreeNode> subNodes = findSubNodesById(node.getId());
        if (CollectionUtils.isEmpty(subNodes)) {
            return;
        }

        //2.关联tree和下级权限
        node.setChildren(subNodes);

        //3.遍历下级权限中的每个子权限，递归构建其子权限
        node.getChildren().forEach(this::buildTree);
    }

    /**
     * 根据id查询所有子节点信息
     * @param id 节点id
     * @return 子节点信息
     */
    List<TreeNode> findSubNodesById(Long id);
}
