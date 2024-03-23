package io.ihankun.framework.spring.server.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.ihankun.framework.common.v1.context.GovernanceContext;
import io.ihankun.framework.spring.server.utils.HttpConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class GovernanceRuleParser {

    /**临时存储鉴权规则MD5值*/
    private String ruleTmpMd5 = null;

    @Value("${spring.application.name}")
    public String curServerAppName;

    @Resource
    private GovernanceAuthProp governanceAuthProp;

    protected static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.
                    Builder().namingPattern("kun-governance-rule-%d").build());

    @PostConstruct
    public void init(){

        log.info("获取服务治理鉴权规则,服务端地址:{},动态校验开关:{}",governanceAuthProp.getHost(),governanceAuthProp.isAuthflag());
        //延迟1秒开始执行，默认每隔30秒执行一次定时任务
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try{
                fetchAuthValue();
            }catch (Exception e){
                log.error("获取服务治理鉴权规则异常，{}毫秒后重新获取",governanceAuthProp.getDelay(),e);
            }

        }, governanceAuthProp.getInitialDelay(), governanceAuthProp.getDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * md5校验
     * @return
     */
    public boolean md5Check() {
        String url = governanceAuthProp.getHost()+GovernanceContext.URL_GOVERN_INFO_MD5;
        Map<String, Object> param = new HashMap<>(2);
        param.put("md5",ruleTmpMd5);
        param.put("serverName",curServerAppName);
        String message = HttpConnectionUtil.doPost(url, JSONObject.toJSONString(param));
        JSONObject md5Json = JSONObject.parseObject(message);
        String newMd5 = md5Json.getString("md5");
        boolean result = md5Json.getBooleanValue("result");
        if(result){
            return true;
        }
        ruleTmpMd5 = newMd5;
        return false;
    }

    /**
     * 拉取鉴权规则信息
     */
    public void fetchAuthValue() {

        //拉取鉴权规则动态开为false时，不进行规则校验
        if(!governanceAuthProp.isAuthflag()){
            return;
        }

        //MD5值相同，不需要更新授权规则
        if(md5Check()){
            return;
        }

        //url动态更新后，使用最新地址
        String url = governanceAuthProp.getHost()+GovernanceContext.URL_GOVERN_RULE_LIST+curServerAppName;
        String ruleResult = null;
        try{
            ruleResult = HttpConnectionUtil.doGet(url);
            GovernanceContext.rulesMap = JSON.parseObject(ruleResult, ConcurrentHashMap.class);
        }catch (Exception e){
            log.error("获取鉴权规则信息异常：{}，url:{}",ruleResult,url,e);
            //异常时，清空当前存储MD5值，用于重新获取规则
            ruleTmpMd5 = null;
        }
    }
}
