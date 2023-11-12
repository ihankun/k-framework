package io.ihankun.framework.common.context;

import io.ihankun.framework.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hankun
 */
@Data
public class LoginUserInfo extends BaseEntity {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("账号ID")
    private Long accountId;

    @ApiModelProperty("用户名称")
    private String accountName;

    @ApiModelProperty("登录后Token")
    private String token;

    @ApiModelProperty("登录后秘钥")
    private String secret;

    @ApiModelProperty("设备地址")
    private String deviceMac;

    @ApiModelProperty("设备IP")
    private String deviceIp;

    @ApiModelProperty("机构ID")
    private Long orgId;
}
