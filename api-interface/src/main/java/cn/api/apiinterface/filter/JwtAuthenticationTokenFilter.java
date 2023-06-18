package cn.api.apiinterface.filter;

import cn.api.apiinterface.Util.JwtUtil;
import cn.api.apiinterface.Util.UserHolder;
import cn.api.apiinterface.model.entity.LoginUser;
import cn.api.apiinterface.model.entity.User;
import cn.api.apiinterface.model.entity.UserDTO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import static cn.api.apiinterface.common.RedisConstants.LOGIN_USER_KEY;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息
        String redisKey = LOGIN_USER_KEY+ userid;

        log.info("尝试登录,redis key{}",redisKey);

        String str = redisTemplate.opsForValue().get(redisKey);
        LoginUser loginUser = JSONUtil.toBean(str, LoginUser.class);
        //将CacheObject转换为loginUser
        if(Objects.isNull(loginUser)){
            log.error("登录失败,redis key{}",redisKey);
            throw new RuntimeException("用户未登录");
        }
        //存入UserHolder
        User user = loginUser.getUser();
        UserDTO userDTO = BeanUtil.toBean(user, UserDTO.class);
        UserHolder.saveUser(userDTO);
        //存入SecurityContextHolder
        // 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }

}
