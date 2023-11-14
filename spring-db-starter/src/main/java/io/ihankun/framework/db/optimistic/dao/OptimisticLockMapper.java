package io.ihankun.framework.db.optimistic.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author hankun
 */
@Repository
public interface OptimisticLockMapper extends BaseMapper {

    /**
     * 更新版本
     *
     * @param tableName 表名
     * @param condition 主键条件
     * @param version   乐观锁标识
     * @return 更新成功的记录数
     */
    @Update("update ${tableName} set version = version + 1 where version = #{version} and ${condition}")
    int update(@Param("tableName") String tableName, @Param("condition") String condition,
               @Param("version") int version);
}
