package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.common.redis.UserKey;
import com.mayuwan.miaosha.dao.UserMapper;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.exceptions.GlobalException;
import com.mayuwan.miaosha.utils.Md5Util;
import com.mayuwan.miaosha.utils.UUIDUtil;
import com.mayuwan.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisService redisServic;


    public static final String COOKIE_NAME_TOKEN = "token";

    public boolean login(LoginVo vo, HttpServletResponse response,User redisUser){
        String mobile = vo.getMobile();
        String formPass = vo.getPassword();
        //验证手机号是否存在
        User user = userMapper.getById(Long.valueOf(mobile));
        if(user == null){
            throw new GlobalException(RespBaseVo.MOBILE_UNEXIST);
        }
        //验证密码
        String pass = Md5Util.formPassToDBPass(formPass,user.getSalt());
        if(!user.getPassword().equals(pass)){
            throw new GlobalException( RespBaseVo.PASSWORD_ERROR);
        }
        if(redisUser == null){
            //生成cookie
            String token = UUIDUtil.uuid();
            addCookie(response,user,token);
        }

        //前端页面跳转

        return true;
    }

    private void addCookie(HttpServletResponse response,User user,String token){
        redisServic.set(UserKey.getByToken,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(UserKey.getByToken.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public User getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        User user =  redisServic.get(UserKey.getByToken,token,User.class);
        //延长token有效期
        if(user != null){
            addCookie(response,user,token);
        }
        return user;
    }
}
