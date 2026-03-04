package io.hankun.framework.gateway.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ClientRequestInfo {

    private String signType;

    private Long timestamp;

    private ClientUserInfo userInfo;

    private String userInfoStr;

    private String token;

    private String body;
}
