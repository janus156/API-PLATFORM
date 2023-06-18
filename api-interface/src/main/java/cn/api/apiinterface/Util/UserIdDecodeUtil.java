package cn.api.apiinterface.Util;

import io.jsonwebtoken.Claims;

public class UserIdDecodeUtil {

    public static String getUserId(String token){
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        return userid;
    }
}
