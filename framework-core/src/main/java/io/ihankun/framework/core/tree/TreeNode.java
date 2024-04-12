package io.ihankun.framework.core.tree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hankun
 */
@ApiModel
@Data
public class TreeNode implements Serializable {

    private static final long serialVersionUID = -5495475224276786762L;

    /**
     * 节点id
     */
    @ApiModelProperty("节点id")
    private Long id;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String label;

    /**
     * 子节点
     */
    @ApiModelProperty("子节点集合")
    private List<TreeNode> children;

    public TreeNode() {

    }

    public TreeNode(Long id, String label) {
        this.id = id;
        this.label = label;
    }
}
