package io.ihankun.framework.common.utils.token;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.common.context.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author hankun
 */
@Slf4j
public class JwtTokenUtil {

    /**
     * token分隔符
     */
    private static final String SPLIT = "@@@";

    /**
     * token分隔后长度
     */
    private static final int SPLIT_LENGTH = 2;

    private static final String SECRET = "jwt-key";

    /**
     * 解析token
     *
     * @param authorization token字符串
     * @return
     */
    public static final LoginUserInfo parseToken(String authorization) {
        if (StringUtils.isEmpty(authorization)) {
            throw new RuntimeException("authorization is null");
        }
        String[] split = authorization.split(SPLIT);
        if (split.length != SPLIT_LENGTH) {
            throw new RuntimeException("authorization格式错误");
        }
        String token = split[0];
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token is null");
        }
        String userInfoStr = split[1];
        if (StringUtils.isEmpty(userInfoStr)) {
            throw new RuntimeException("用户信息不存在");
        }
        JwtHelper.decodeAndVerify(token, new MacSigner(SECRET));
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        try {
            String decode = new String(Base64.getDecoder().decode(userInfoStr.replaceAll(" ", "+").getBytes()), "UTF-8");
            loginUserInfo = JSON.parseObject(decode, LoginUserInfo.class);
        } catch (UnsupportedEncodingException e) {
            log.error("解析用户信息报错：", e);
        }
        return loginUserInfo;
    }
}
