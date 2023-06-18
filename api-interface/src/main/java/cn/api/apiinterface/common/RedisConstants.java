package cn.api.apiinterface.common;

public class RedisConstants {
    /**
     * 登录前缀、以及过期时间
     */

    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 30L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final String LOCK_SHOP_KEY = "lock:";


    public static final String NONCE="nonce";

}
