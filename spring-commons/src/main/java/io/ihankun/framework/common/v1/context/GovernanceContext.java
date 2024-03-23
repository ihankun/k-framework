package io.ihankun.framework.common.v1.context;

import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 服务治理
 * @author hankun
 */
public class GovernanceContext {

    /**获取当前规则md5值*/
    public static final String URL_GOVERN_INFO_MD5 = "/govering/md5";
    /**获取服务鉴权规则*/
    public static final String URL_GOVERN_RULE_LIST = "/govering/fetch/";
    /**记录交易调用链路信息*/
    public static final String URL_GOVERN_TRADE_RECORD = "/govering/record";


    /**上游服务名称*/
    public static final String UPSTREAM_SERVER_NAME = "Upstream";

    /**鉴权规则MD5值*/
    public final static String AUTH_RULES_MD5 = "GOVERN_MD5";
    /**uri白名单key*/
    public final static String URI_WHILE_LIST_KEY = "urilist";
    /**服务白名单key*/
    public final static String SERVER_WHILE_LIST_KEY = "whilelist";

    /**中台白名单key,中台白名单中的服务，允许访问当前服务所有接口*/
    public final static String MIDDLE_WHILE_LIST_KEY = "middlelist";

    /**入口授权策略规则key*/
    public final static String POLICY_RULES_IN_KEY = "AUTH_POLICY_IN_RULES";

    /**授权类型*/
    /**允许访问*/
    public final static String POLICY_RULES_TYPE_ALLOW = "allow";
    /**拒绝访问*/
    public final static String POLICY_RULES_TYPE_REFUSE = "refuse";

    /**鉴权规则缓存*/
    public static ConcurrentHashMap rulesMap = new ConcurrentHashMap(16);

    /**记录请求信息*/
    public static final BlockingQueue<JSONObject> RECORD_QUEUE = new LinkedBlockingQueue<>(200);
    /**用于重复请求过滤，例如for循环请求*/
    public static Set<String> recordSet = new HashSet<>(16);
}
