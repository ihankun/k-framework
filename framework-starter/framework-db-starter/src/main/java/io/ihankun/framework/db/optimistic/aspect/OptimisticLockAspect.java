package io.ihankun.framework.db.optimistic.aspect;

import io.ihankun.framework.core.exception.BusinessException;
import io.ihankun.framework.core.optimistic.Lock;
import io.ihankun.framework.core.optimistic.LockId;
import io.ihankun.framework.core.optimistic.LockParam;
import io.ihankun.framework.core.optimistic.LockVersion;
import io.ihankun.framework.db.error.CommonDbErrorCode;
import io.ihankun.framework.db.optimistic.dao.OptimisticLockMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 版本控制切面
 * @author hankun
 */
@Component
@Aspect
@Slf4j
@Order
public class OptimisticLockAspect {

    @Resource
    private OptimisticLockMapper lockService;

    @Pointcut("@annotation(io.ihankun.framework.core.optimistic.Lock)")
    public void lockPointcut() {

    }

    @Around("lockPointcut()")
    public Object around(ProceedingJoinPoint point) {
        return execute(point);
    }

    private Object execute(ProceedingJoinPoint point) {
        log.info("进入版本控制锁处理切面,当前所处事务:{}", TransactionSynchronizationManager.getCurrentTransactionName());
        try {

            //获取id、version
            ParameterHandler parameterHandler = new ParameterHandler(point).invoke();
            Integer version = parameterHandler.getVersion();
            Long id = parameterHandler.getId();
            String idField = parameterHandler.getIdField();

            //若id为空说明是新增操作，不需要做处理
            if (ObjectUtils.isEmpty(id)) {
                return point.proceed(point.getArgs());
            }

            //版本为空时默认为0
            if (version == null) {
                log.info("version is null,初始化乐观锁=0");
                version = 0;
            }

            //获取表名
            LockInfo lockInfo = getTable(point);

            //id查询条件
            String idCondition = String.format("%s = %s", idField, id);
            int update = lockService.update(lockInfo.getValue(), idCondition, version);
            if (update < 1) {
                throw BusinessException.build(CommonDbErrorCode.LOCK_UPDATE_ERROR, lockInfo.getMessage());
            }

            //业务操作
            return point.proceed(point.getArgs());
        } catch (RuntimeException e) {

            //此处捕获自定义异常又抛出的意义在于，防止在下一个catch中被捕获改为“系统错误”
            throw e;
        } catch (Throwable throwable) {

            //对于所有意料之外的异常，打印日志，并抛出“系统错误”
            log.error("乐观锁异常:{}", throwable);
            throw BusinessException.build(CommonDbErrorCode.LOCK_EX, throwable.getMessage());
        }
    }

    /**
     * 获取乐观锁注解中的参数(表名)
     */
    private LockInfo getTable(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        LockInfo info = new LockInfo();
        info.setValue(method.getAnnotation(Lock.class).value());
        info.setMessage(method.getAnnotation(Lock.class).message());
        return info;
    }

    /**
     * Lock注解的属性映射
     */
    @Getter
    @Setter
    private static class LockInfo {

        /**
         * 表名
         */
        private String value;

        /**
         * 校验失败的提示信息
         */
        private String message;
    }

    /**
     * 获取参数内部类
     */
    @Getter
    private static class ParameterHandler {

        /**
         * 连接点
         */
        private final ProceedingJoinPoint point;

        /**
         * 乐观锁标识
         */
        private Integer version;

        /**
         * 主键标识
         */
        private Long id;

        /**
         * 主键列名称
         */
        private String idField;

        public ParameterHandler(ProceedingJoinPoint point) {
            this.point = point;
        }

        public ParameterHandler invoke() throws IllegalAccessException {

            //获取方法参数列表
            Object[] args = point.getArgs();

            //获取方法参数签名
            MethodSignature signature = (MethodSignature) point.getSignature();

            //方法参数签名类型
            Parameter[] parameters = signature.getMethod().getParameters();

            //遍历方法参数类型列表
            for (Parameter parameter : parameters) {
                //只有标有LockParam注解的类，才可以执行
                if (parameter.getAnnotation(LockParam.class) == null) {
                    continue;
                }

                //参数的类型
                Class<?> paramClazz = parameter.getType();

                //获取类型所对应的参数对象
                Object arg = Arrays.stream(args).filter(a -> a != null && paramClazz.isAssignableFrom(a.getClass())).findFirst().orElse(null);

                //得到参数的所有成员变量
                List<Field> fieldList = new ArrayList<>();

                Class<?> paramClazzTmp = paramClazz;

                //遍历paramClazzTmp及其所有父类，并收集所有field
                while (paramClazzTmp != null) {
                    fieldList.addAll(Arrays.asList(paramClazzTmp.getDeclaredFields()));
                    paramClazzTmp = paramClazzTmp.getSuperclass();
                }
                for (Field field : fieldList) {
                    field.setAccessible(true);

                    //获取并设置id属性的值和列名
                    getAndSetIdInfo(field, arg);

                    //获取标识乐观锁的属性
                    getAndSetVersionInfo(field, arg);
                }
            }
            return this;
        }

        /**
         * 获取标识乐观锁的属性
         */
        private void getAndSetVersionInfo(Field field, Object arg) throws IllegalAccessException {
            LockVersion lockVersion = field.getAnnotation(LockVersion.class);
            if (lockVersion != null) {
                Object fieldValue = field.get(arg);
                version = (Integer) fieldValue;
                field.set(arg, null);
            }
        }

        /**
         * 获取并设置id属性的值和列名
         */
        private void getAndSetIdInfo(Field field, Object arg) throws IllegalAccessException {
            LockId lockId = field.getAnnotation(LockId.class);
            if (lockId != null) {
                Object fieldValue = field.get(arg);

                //主键的值
                id = (Long) fieldValue;

                //主键的列名称
                idField = lockId.value();
            }
        }
    }
}
