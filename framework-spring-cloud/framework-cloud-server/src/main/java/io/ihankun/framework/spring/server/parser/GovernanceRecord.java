package io.ihankun.framework.spring.server.parser;

import com.alibaba.fastjson.JSONObject;
import io.ihankun.framework.core.context.GovernanceContext;
import io.ihankun.framework.spring.server.utils.HttpConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class GovernanceRecord {

    /**每次上送50条数据*/
    private Integer MAX_BATCH_SIZE = 50;

    @Resource
    private GovernanceAuthProp governanceAuthProp;

    protected static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.
                    Builder().namingPattern("kun-govern-record-%d").build());

    @PostConstruct
    public void init(){

        //延迟1秒开始执行，默认每隔10秒执行一次定时任务
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try{
                //记录交易链路信息开关为false时，不提交链路信息
                if(governanceAuthProp.isRecordflag()){
                    while (true) {
                        if (GovernanceContext.RECORD_QUEUE.isEmpty()) {
                            GovernanceContext.recordSet.clear();
                            break;
                        }
                        List<JSONObject> recordDataList = new ArrayList<>();
                        GovernanceContext.RECORD_QUEUE.drainTo(recordDataList, MAX_BATCH_SIZE);
                        postRecode(recordDataList);
                    }
                }

            }catch (Exception e){
                GovernanceContext.recordSet.clear();
                log.error("记录交易链路信息异常，10 秒后重新执行，该报错不影响正常业务！！！",e);
            }

        }, governanceAuthProp.getInitialDelay(), 10000, TimeUnit.MILLISECONDS);
    }

    private void postRecode(List<JSONObject> recordDataList) {
        try {
            String url = governanceAuthProp.getHost()+GovernanceContext.URL_GOVERN_TRADE_RECORD;
            HttpConnectionUtil.doPost(url,JSONObject.toJSONString(recordDataList));
        } catch (Exception e) {
            log.error("记录交易链路信息异常,不影响正常交易",e);
        }
    }
}
