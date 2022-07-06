package com.example.mypractice.commons.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.mypractice.model.database.User;
import com.example.mypractice.model.web.UserToken;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * token的生成器
 *
 * @author 张贝易
 */
@Component
public class JwtGenerator {
    /**
     * 过期时间，毫秒数
     */
    private static final long EXPIRE_TIME = 30 * 60 * 1000;

    /**
     * 私钥
     */
    private static final String SECRET = "ONLINE_MUSIC";

    /**
     * 用User对象创建token
     *
     * @param user 描述User的信息
     * @return
     */
    public String createToken(User user) {
        UserToken userToken = new UserToken(user.getId(), user.getUserName(), user.getIdentity(), null);
        return createToken(userToken);
    }

    /**
     * 用自定义的用户token创建token字符串,如果过期时间为空，就创建新的
     *
     * @param token 保存用户信息的token
     * @return
     */
    public String createToken(UserToken token) {

        //私钥设置
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        //没有过期时间，创建一个
        if (token.getExpire() == null) {
            token.setExpire(new Date(System.currentTimeMillis() + EXPIRE_TIME));
        }
        //头部信息
        Map<String, Object> header = new HashMap<String, Object>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        //创建token
        return JWT.create()
                .withHeader(header)
                .withClaim(UserToken.ID, token.getId())
                .withClaim(UserToken.USER_NAME, token.getUserName())
                .withClaim(UserToken.IDENTITY, token.getIdentity())
                .withClaim(UserToken.EXPIRE, token.getExpire())
                .withExpiresAt(token.getExpire())
                .sign(algorithm);
    }

    /**
     * 将老的用户Token更新，重置过期时间
     *
     * @param userToken
     * @return
     */
    public String updateUserToken(UserToken userToken) {
        //设置过期时间为null，防止更新失败
        userToken.setExpire(null);
        return createToken(userToken);
    }

    public String updateUserToken(String userToken) throws Exception {
        UserToken tokenData = getUserTokenData(userToken);
        return updateUserToken(tokenData);
    }

    /**
     * 获取token字符串中的数据
     *
     * @param token header中的用户token字符串
     * @return
     * @throws Exception
     */
    public UserToken getUserTokenData(String token) throws Exception {
        DecodedJWT decodeToken = JWT.decode(token);
        UserToken userToken = new UserToken(decodeToken.getClaim(UserToken.ID).asLong(),
                decodeToken.getClaim(UserToken.USER_NAME).asString(),
                decodeToken.getClaim(UserToken.IDENTITY).asInt(),
                decodeToken.getClaim(UserToken.EXPIRE).asDate());
        return userToken;
    }

    /**
     * 验证用户token是否有效
     *
     * @param token 前端传递的token
     * @return
     */
    public boolean checkUserToken(String token) {
        UserToken tokenData = null;
        String newToken = null;
        try {
            //获取token中信息
            tokenData = getUserTokenData(token);
            //尝试用token中的信息和后端的密钥创建新的token
            newToken = createToken(tokenData);
        } catch (Exception e) {
            return false;
        }

        //确定没有过期
        if (tokenData.getExpire().getTime() >= System.currentTimeMillis()) {
            //比较新旧token来确定token是否被篡改
            return Objects.equals(newToken, token);
        } else {
            return false;
        }
    }

    /**
     * 验证用户token是否有效
     *
     * @param token 前端传递的token
     * @param tokenData 保存token信息的tokenData
     * @return
     */
    public boolean checkUserToken(String token,UserToken tokenData) {
        String newToken = null;
        try {
            //尝试用token中的信息和后端的密钥创建新的token
            newToken = createToken(tokenData);
        } catch (Exception e) {
            return false;
        }

        //确定没有过期
        if (tokenData.getExpire().getTime() >= System.currentTimeMillis()) {
            //比较新旧token来确定token是否被篡改
            return Objects.equals(newToken, token);
        } else {
            return false;
        }
    }
}
