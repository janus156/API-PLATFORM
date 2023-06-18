package cn.api.apiinterface.Util;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

public class GlobalIdUtils {

    public static String getOnlyId(StringRedisTemplate redisTemplate, String prefix){
        if (StrUtil.isBlank(prefix)){
            return "-1";
        }
        Long value = redisTemplate.opsForValue().increment(prefix, 1L);
        return String.valueOf(value);
    }
}