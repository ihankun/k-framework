package io.ihankun.framework.mongoplus.conditions.update;

import com.mongodb.client.ClientSession;

/**
 * 修改方法定义
 * @author hankun
 * @date 2023/6/24/024 2:58
*/
public interface ChainUpdate {

    boolean update();

    boolean update(ClientSession clientSession);

    boolean remove();

    boolean remove(ClientSession clientSession);

}
