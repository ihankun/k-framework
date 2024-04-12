package io.ihankun.framework.db.mapper;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.ihankun.framework.core.base.BasePO;
import io.ihankun.framework.core.exception.BusinessException;
import io.ihankun.framework.db.error.CommonDbErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hankun
 */
public interface KunBaseMapper<T extends BasePO> extends BaseMapper<T> {

    Pattern PATTERN = Pattern.compile("[A-Z]");

    /**
     * 保存
     */
    default int save(T entity) {
        entity.init();
        return insert(entity);
    }

    /**
     * 保存
     */
    @Transactional(rollbackFor = Exception.class)
    default void save(Collection<T> entitys) {
        Assert.notEmpty(entitys, "error: entityList must not be empty");
        entitys.forEach(BasePO::init);
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        try (SqlSession sqlSession = SqlHelper.sqlSessionBatch(currentModelClass())) {
            int i = 0;
            for (T entity : entitys) {
                sqlSession.insert(sqlStatement, entity);
                if (i >= 1 && i % 50 == 0) {
                    sqlSession.flushStatements();
                }
                i++;
            }
            sqlSession.flushStatements();
        }
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    default int update(Collection<T> entitys) {
        Assert.notEmpty(entitys, "error: entityList must not be empty");
        entitys.forEach(BasePO::update);
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
        int result = 0;
        try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(currentModelClass())) {
            int i = 0;
            for (T anEntityList : entitys) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                result = result + batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % 50 == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return entitys.size();
    }

    /**
     * 批量删除
     */
    default int delete(List<T> entities) {
        AtomicInteger result = new AtomicInteger();
        entities.forEach(entity -> result.addAndGet(delete(entity)));
        return result.get();
    }

    /**
     * 获取 SqlStatement
     */
    default String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 反射获取PO
     */
    default Class<T> currentModelClass() {
        Type[] genericInterfaces = AopUtils.getTargetClass(this).getGenericInterfaces();

        //mapper接口类型
        Class mapperClass = (Class) genericInterfaces[0];

        //获取PO类型
        return (Class) ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    /**
     * 根据主键获取记录
     */
    default T getById(Long id) {
        //return selectOne(getWrapper(getInstance(id)));
        return selectById(id);
    }

    /**
     * 获取PO实例对象o
     */
    default T getInstance(Long id) {
        T instance = null;
        try {
            Type[] genericInterfaces = AopUtils.getTargetClass(this).getGenericInterfaces();

            //mapper接口类型
            Class mapperClass = (Class) genericInterfaces[0];

            //获取PO类型
            Class cls = (Class) ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            instance = (T) cls.newInstance();
            List<Field> fields = Arrays.asList(cls.getDeclaredFields());
            for (Field field : fields) {
                if (field.getAnnotation(TableId.class) == null) {
                    continue;
                }
                field.setAccessible(true);
                field.set(instance, id);
            }
        } catch (Exception e) {
            throw BusinessException.build(CommonDbErrorCode.INSTANCE_PO_ERROR, e.getMessage());
        }
        return instance;
    }

    /**
     * 更新
     */
    default int update(T entity) {
        entity.update();
        //return update(entity, getWrapper(entity));
        return updateById(entity);
    }


    /**
     * 删除
     */
    default int delete(T entity) {
        Pk pk = getPk(entity);
        Map<String, Object> map = new HashMap<>(4);
        map.put(pk.getName(), pk.getValue());
        return deleteByMap(map);
    }

    /**
     * 删除
     */
    default int delete(Long id) {
        return delete(getInstance(id));
    }

    /**
     * 获取wrapper
     */
    default Wrapper<T> getWrapper(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        //wrapper.eq(BasePO.ORG_ID, getOrgId(entity));
        Pk pk = getPk(entity);
        wrapper.eq(pk.getName(), pk.getValue());
        return wrapper;
    }

    /**
     * 获取机构id
     */
    default Long getOrgId(T entity) {
//        if (entity.getHisOrgId() != null) {
//            return entity.getHisOrgId();
//        }
//        LoginUserInfo user = LoginUserContext.getLoginUserInfo();
//        if (user != null && user.getOrgId() != null) {
//            return user.getOrgId();
//        }
        return BasePO.DEFAULT_ID;
    }

    /**
     * 获取实体的主键字段机字段值
     */
    default Pk getPk(T entity) {
        Pk pk = null;
        Class<? extends BasePO> cls = entity.getClass();
        List<Field> fields = Arrays.asList(cls.getDeclaredFields());
        for (Field field : fields) {
            if (field.getAnnotation(TableId.class) == null) {
                continue;
            }
            field.setAccessible(true);
            pk = new Pk();

            //将驼峰命名的属性转为列名
            Matcher matcher = PATTERN.matcher(field.getName());
            StringBuffer pkName = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(pkName, "_" + matcher.group(0).toLowerCase());
            }
            matcher.appendTail(pkName);
            pk.setName(pkName.toString());
            try {
                pk.setValue((Long) field.get(entity));
            } catch (IllegalAccessException e) {
                throw BusinessException.build(CommonDbErrorCode.GET_PK_ERROR, e.getMessage());
            }
        }
        if (pk == null) {
            throw BusinessException.build(CommonDbErrorCode.PK_SET_ERROR, cls.getName());
        }
        return pk;
    }

    /**
     * po主键信息类
     */
    @Setter
    @Getter
    public static class Pk {

        /**
         * 主键名称
         */
        private String name;

        /**
         * 主键值
         */
        private Long value;
    }
}
