package io.ihankun.framework.spring.server.filter.thread;

import com.alibaba.fastjson.JSON;
import feign.RequestTemplate;
import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.context.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * @author hankun
 */
@Slf4j
@Component
public class KunLoginUserThreadLocal implements IKunThreadLocalFilter {

    Base64.Encoder encoder = Base64.getEncoder();
    Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public String name() {
        return "login-user";
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public String writeHeader(RequestTemplate template) {
        //从上下文中取出用户信息，放置到Http请求Header中进行透传
        LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo != null) {
            log.info("LoginUserThreadLocal.writeHeader,loginUser={}", userInfo);
            String encode = new String(encoder.encode(userInfo.toJson().getBytes()));
            template.header(LoginUserContext.LOGIN_USER_KEY, encode);
        }
        return org.apache.commons.lang3.StringUtils.EMPTY;
    }

    @Override
    public String readHeader(HttpServletRequest request) {
        //用户登录信息获取
        String loginUserInfoEncoder = request.getHeader(LoginUserContext.LOGIN_USER_KEY);
        if (!StringUtils.isEmpty(loginUserInfoEncoder)) {
            String loginUserInfoJson = new String(decoder.decode(loginUserInfoEncoder.getBytes()));
            LoginUserInfo userInfo = JSON.parseObject(loginUserInfoJson, LoginUserInfo.class);
            LoginUserContext.mock(userInfo);
            log.info("LoginUserThreadLocal.readHeader,loginUser={}", userInfo);
        }
        return org.apache.commons.lang3.StringUtils.EMPTY;
    }

    @Override
    public void clearThreadLocal() {
        LoginUserContext.clear();
    }
}
