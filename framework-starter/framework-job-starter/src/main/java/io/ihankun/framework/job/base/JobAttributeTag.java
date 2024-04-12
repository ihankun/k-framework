package io.ihankun.framework.job.base;

/**
 * @author hankun
 */
public class JobAttributeTag {
    /**
     * 简单JOB
     */
    public static final String JOB_SIMPLE = "SIMPLE";

    /**
     * 流式JOB
     */
    public static final String JOB_DATAFLOW = "DATAFLOW";
    /**
     * 脚本类型
     */
    public static final String JOB_SCRIPT = "SCRIPT";

    /**
     * 流式job(支持分片)类
     */
    public static final String ENTITY_ABSTRACTMOREITEMJOB = "AbstractMoreItemJob";
    /**
     * 流式job(不支持分片)类
     */
    public static final String ENTITY_ABSTRACTONEITEMJOB = "AbstractOneItemJob";

    /**
     * 简单job
     */
    public static final String ENTITY_ABSTRACTSIMPLEJOB = "AbstractSimpleJob";
}
