package io.ihankun.framework.mongoplus.toolkit;

import io.ihankun.framework.mongoplus.enums.IdTypeEnum;
import io.ihankun.framework.mongoplus.incrementer.id.IdWorker;

import java.io.Serializable;

/**
 * @author hankun
 **/
public class Generate {


    public static Serializable generateId(IdTypeEnum idTypeEnum){
        if (idTypeEnum.getKey() == IdTypeEnum.ASSIGN_UUID.getKey()){
            return IdWorker.get32UUID();
        }
        if (idTypeEnum.getKey() == IdTypeEnum.ASSIGN_ULID.getKey()){
            return IdWorker.get26ULID();
        }
        if (idTypeEnum.getKey() == IdTypeEnum.ASSIGN_ID.getKey()){
            return IdWorker.getId();
        }
        return null;
    }


}
