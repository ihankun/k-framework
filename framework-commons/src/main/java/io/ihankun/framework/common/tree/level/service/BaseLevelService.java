package io.ihankun.framework.common.tree.level.service;


import io.ihankun.framework.common.error.impl.CommonErrorCode;
import io.ihankun.framework.common.tree.level.IBaseLevelDTO;
import io.ihankun.framework.common.base.BaseService;
import io.ihankun.framework.common.exception.BusinessException;
import io.ihankun.framework.common.tree.level.IBaseLevelVO;
import io.ihankun.framework.common.tree.level.enums.LevelEnum;

/**
 * @author hankun
 */
public interface BaseLevelService extends BaseService {

    /**
     * 设置级别
     */
    default void setLevel(IBaseLevelDTO levelDTO) {
        if (levelDTO == null) {
            return;
        }

        //如果是保存的话
        if (levelDTO.getId() == null) {

            //有上级信息的话
            if (levelDTO.getParentId() != null) {

                //查询上级信息
                IBaseLevelVO parent = getBaseLevelVoById(levelDTO.getParentId());
                levelDTO.setLevel(parent.getLevel() + 1);
                return;
            }

            //没有上级的话设置为一级
            levelDTO.setLevel(LevelEnum.FIRST.getLevel());
            return;
        }

        //更新操作 且 没有上级信息的话
        if (levelDTO.getParentId() == null) {

            //查询数据库中的权限信息
            IBaseLevelVO oldRole = getBaseLevelVoById(levelDTO.getId());

            //当前角色如果之前有上级，后续将上级删掉的话，级别设置为1
            if (oldRole.getParentId() != null) {
                levelDTO.setLevel(LevelEnum.FIRST.getLevel());
            }
            return;
        }

        //更新操作 且 有上级的话
        IBaseLevelVO parent = getBaseLevelVoById(levelDTO.getParentId());
        if (parent == null) {
            throw BusinessException.build(CommonErrorCode.UP_LEVEL_NOT_FOUND);
        }
        levelDTO.setLevel(parent.getLevel() + 1);
    }

    /**
     * 根据id获取BaseLevelVo
     */
    IBaseLevelVO getBaseLevelVoById(Long id);
}
