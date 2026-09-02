package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
//统一拦截用户端发送的请求并进行jwt校验
public class JwtTokenUserInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源}
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1.从头开始进行获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        try {
            log.info("jwt令牌校验:{}", token);


            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户的id:", userId);
            BaseContext.setCurrentId(userId);

            //放行
            return true;

        } catch (Exception ex) {

            //不通过响应401
            response.setStatus(401);
            return false;

        }
    }
}
