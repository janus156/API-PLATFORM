package cn.api.apiinterface.Util;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.api.apiinterface.common.RedisConstants.LOGIN_USER_KEY;
import static cn.api.apiinterface.common.RedisConstants.LOGIN_USER_TTL;


public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate redisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate=redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        String key=LOGIN_USER_KEY+token;

        //如果查到的user空，返回
        Map<Object, Object> userMap = redisTemplate.opsForHash()
                .entries(key);
        if (userMap.isEmpty()){
            return true;
        }

        // 4. 刷新 Token 有效期
        redisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

}
