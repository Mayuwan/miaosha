package com.mayuwan.miaosha.intercepter;


import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.AccessKey;
import com.mayuwan.miaosha.common.redis.KeyPrefix;
import com.mayuwan.miaosha.common.redis.MiaoshaKey;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = resolveUser(request,response);
        UserContext.set(user);
        if(user == null){
            render(response,RespBaseVo.NOT_LOGIN);
            return false;
        }
       if(handler instanceof HandlerMethod){
           HandlerMethod handlerMethod = (HandlerMethod)handler;
           AccessFilter accessFilter = handlerMethod.getMethodAnnotation(AccessFilter.class);
           if(accessFilter == null ) return true;
           int maxCount = accessFilter.maxCount();
           int seconds = accessFilter.seconds();
           boolean needLogin = accessFilter.needLogin();

           String key = request.getRequestURI();
           if(needLogin){
               key+="_"+user.getId();
           }
           KeyPrefix keyPrefix = AccessKey.withExpire(seconds);
           Integer cnt = redisService.get(keyPrefix,key,Integer.class);
           if(cnt == null){
               redisService.set(keyPrefix,key,1);
           }
           else if(cnt > maxCount){
               render(response,RespBaseVo.TOO_MUCH_REQUEST);
               return false;
           }
           else{
               redisService.incr(keyPrefix,key);
           }
       }
       return true;
    }

    private void render(HttpServletResponse response, RespBaseVo msg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream =  response.getOutputStream();
        String str = JSON.toJSONString(msg);
        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }


    private User resolveUser(HttpServletRequest request, HttpServletResponse response){
        String loginToken = getCookieToken(request);
        String paraToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);//兼容其他情况
        if(StringUtils.isBlank(loginToken) && StringUtils.isBlank(paraToken)){
            return null;
        }
        String token = StringUtils.isNotBlank(loginToken) ? loginToken : paraToken;
        User user = userService.getByToken(response,token);
        return user;
    }
    private String getCookieToken(HttpServletRequest request) {
        if(request.getCookies() == null || request.getCookies().length <= 0){
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(UserService.COOKIE_NAME_TOKEN)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
